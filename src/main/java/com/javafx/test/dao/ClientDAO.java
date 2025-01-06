package com.javafx.test.dao;

import com.javafx.test.config.DatabaseConnection;
import com.javafx.test.entities.Client;
import com.javafx.test.entities.Commande;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class ClientDAO implements Dao<Client> {
    private final Connection connection = DatabaseConnection.getConnection();
    private final CommandeDAO commandeDAO;

    public ClientDAO() {
        this.commandeDAO = new CommandeDAO();
    }

    @Override
    public boolean create(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getTelephone());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    client.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Client read(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Adjusted to match the fields in the Client constructor
                Client client = new Client(
                        rs.getString("nom"),       // Assuming nom is the client's last name
                        rs.getString("prenom"),    // Assuming prenom is the client's first name
                        rs.getString("email"),     // Client's email
                        rs.getString("telephone")  // Client's phone number
                );
                client.setId(rs.getInt("id"));

                // Load the commandes for this client
                List<Commande> commandes = commandeDAO.findByClientId(id);
                for (Commande commande : commandes) {
                    client.ajouterCommande(commande);
                }

                return client;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public boolean update(Client client) {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getTelephone());
            pstmt.setInt(5, client.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        // D'abord supprimer les commandes associées
        String deleteCommandesSql = "DELETE FROM commandes WHERE client_id = ?";
        String deleteClientSql = "DELETE FROM clients WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtCommandes = conn.prepareStatement(deleteCommandesSql);
                 PreparedStatement pstmtClient = conn.prepareStatement(deleteClientSql)) {

                pstmtCommandes.setInt(1, id);
                pstmtCommandes.executeUpdate();

                pstmtClient.setInt(1, id);
                boolean result = pstmtClient.executeUpdate() > 0;

                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }


    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
                client.setTelephone(rs.getString("telephone"));

                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public List<Client> findByNom(String nom) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE nom LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
                client.setTelephone(rs.getString("telephone"));

                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}
