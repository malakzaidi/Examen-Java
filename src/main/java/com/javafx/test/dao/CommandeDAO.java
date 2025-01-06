package com.javafx.test.dao;

import com.javafx.test.config.DatabaseConnection;
import com.javafx.test.entities.Client;
import com.javafx.test.entities.Commande;
import com.javafx.test.entities.PlatPrincipal;
import com.javafx.test.entities.Repas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    private final Connection connection = DatabaseConnection.getConnection();

    // Create a new Commande
    public boolean create(Commande commande) {
        String sql = "INSERT INTO commande (client_id, date_commande, total) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, commande.getClient().getId());  // Assume Client has a method getId()
            pstmt.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            pstmt.setDouble(3, commande.getTotal());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    commande.setId(rs.getInt(1));  // Set the generated ID to the Commande

                    // Adding the repas to the commande
                    for (Repas repas : commande.getRepas()) {
                        addRepasToCommande(commande.getId(), repas);
                    }

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get a Commande by ID
    public Commande read(int commandeId) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ClientDAO clientDAO = new ClientDAO();
                Client client = clientDAO.read(rs.getInt("client_id"));

                Commande commande = new Commande(client);
                commande.setId(rs.getInt("id"));
                commande.setDateCommande(rs.getDate("date_commande"));
                commande.setTotal(rs.getDouble("total"));

                // Load the related repas for this commande
                commande.setRepas(findRepasByCommandeId(commande.getId()));

                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add a repas to the commande
    private boolean addRepasToCommande(int commandeId, Repas repas) {
        String sql = "INSERT INTO repas (plat_principal_id, total, commande_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repas.getPlatPrincipal().getId());
            pstmt.setDouble(2, repas.getTotal());
            pstmt.setInt(3, commandeId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Find all repas associated with a commande by its ID
    private List<Repas> findRepasByCommandeId(int commandeId) {
        List<Repas> repasList = new ArrayList<>();
        String sql = "SELECT * FROM repas WHERE commande_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PlatPrincipalDAO platPrincipalDAO = new PlatPrincipalDAO();
                PlatPrincipal platPrincipal = platPrincipalDAO.read(rs.getInt("plat_principal_id"));

                Repas repas = new Repas(platPrincipal);
                repas.setId(rs.getInt("id"));
                repas.setTotal(rs.getDouble("total"));

                repasList.add(repas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repasList;
    }

    // Update a Commande
    public boolean update(Commande commande) {
        String sql = "UPDATE commande SET client_id = ?, date_commande = ?, total = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commande.getClient().getId());
            pstmt.setDate(2, new java.sql.Date(commande.getDateCommande().getTime()));
            pstmt.setDouble(3, commande.getTotal());
            pstmt.setInt(4, commande.getId());

            if (pstmt.executeUpdate() > 0) {
                // Update the repas list
                deleteRepasForCommande(commande.getId());
                for (Repas repas : commande.getRepas()) {
                    addRepasToCommande(commande.getId(), repas);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a Commande by ID
    public boolean delete(int commandeId) {
        String sql = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);

            if (pstmt.executeUpdate() > 0) {
                // Delete all repas associated with the commande
                deleteRepasForCommande(commandeId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete all repas associated with a Commande
    private boolean deleteRepasForCommande(int commandeId) {
        String sql = "DELETE FROM repas WHERE commande_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Find all commandes associated with a client by their ID
    public List<Commande> findByClientId(int clientId) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE client_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ClientDAO clientDAO = new ClientDAO();
                Client client = clientDAO.read(rs.getInt("client_id"));

                Commande commande = new Commande(client);
                commande.setId(rs.getInt("id"));
                commande.setDateCommande(rs.getDate("date_commande"));
                commande.setTotal(rs.getDouble("total"));

                // Load the related repas for this commande
                commande.setRepas(findRepasByCommandeId(commande.getId()));

                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

}
