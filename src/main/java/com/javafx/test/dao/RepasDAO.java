package com.javafx.test.dao;

import com.javafx.test.config.DatabaseConnection;
import com.javafx.test.entities.Commande;
import com.javafx.test.entities.PlatPrincipal;
import com.javafx.test.entities.Repas;
import com.javafx.test.entities.Supplement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepasDAO {
    private final Connection connection = DatabaseConnection.getConnection();

    // Version modifiée de create() qui accepte uniquement un Repas
    public boolean create(Repas repas) {
        // Créez une commande par défaut si nécessaire
        Commande commande = new Commande();  // Vous pouvez adapter la création de la commande selon votre logique
        return create(repas, commande);  // Appel de la méthode create existante avec Commande
    }

    // Version originale de create() qui accepte Repas et Commande
    public boolean create(Repas repas, Commande commande) {
        String sql = "INSERT INTO repas (plat_principal_id, total, commande_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, repas.getPlatPrincipal().getId());
            pstmt.setDouble(2, repas.getTotal());
            pstmt.setInt(3, commande.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    repas.setId(rs.getInt(1));

                    // Ajouter les suppléments
                    for (Supplement supplement : repas.getSupplements()) {
                        addSupplementToRepas(repas.getId(), supplement);
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lire un Repas par ID
    public Repas read(int repasId) {
        String sql = "SELECT * FROM repas WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repasId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlatPrincipalDAO platPrincipalDAO = new PlatPrincipalDAO();
                PlatPrincipal platPrincipal = platPrincipalDAO.read(rs.getInt("plat_principal_id"));

                Repas repas = new Repas(platPrincipal);
                repas.setId(rs.getInt("id"));
                repas.setTotal(rs.getDouble("total"));

                // Charger les suppléments
                repas.setSupplements(getSupplementsForRepas(repas.getId()));

                return repas;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Mettre à jour un Repas
    public boolean update(Repas repas) {
        String sql = "UPDATE repas SET plat_principal_id = ?, total = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repas.getPlatPrincipal().getId());
            pstmt.setDouble(2, repas.getTotal());
            pstmt.setInt(3, repas.getId());

            if (pstmt.executeUpdate() > 0) {
                // Réinitialiser et ajouter les nouveaux suppléments
                deleteSupplementsForRepas(repas.getId());
                for (Supplement supplement : repas.getSupplements()) {
                    addSupplementToRepas(repas.getId(), supplement);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprimer les suppléments d'un repas
    private boolean deleteSupplementsForRepas(int repasId) {
        String sql = "DELETE FROM repas_supplements WHERE repas_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repasId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ajouter un supplément à un repas
    private boolean addSupplementToRepas(int repasId, Supplement supplement) {
        String sql = "INSERT INTO repas_supplements (repas_id, supplement_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repasId);
            pstmt.setInt(2, supplement.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Charger les suppléments associés à un repas
    private List<Supplement> getSupplementsForRepas(int repasId) {
        List<Supplement> supplements = new ArrayList<>();
        String sql = "SELECT * FROM repas_supplements WHERE repas_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repasId);
            ResultSet rs = pstmt.executeQuery();

            SupplementDAO supplementDAO = new SupplementDAO();
            while (rs.next()) {
                Supplement supplement = supplementDAO.read(rs.getInt("supplement_id"));
                supplements.add(supplement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplements;
    }

    // Trouver les Repas associés à une Commande via son ID
    public List<Repas> findByCommande(Commande commande) {
        List<Repas> repasList = new ArrayList<>();
        String sql = "SELECT * FROM repas WHERE commande_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commande.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PlatPrincipalDAO platPrincipalDAO = new PlatPrincipalDAO();
                PlatPrincipal platPrincipal = platPrincipalDAO.read(rs.getInt("plat_principal_id"));

                Repas repas = new Repas(platPrincipal);
                repas.setId(rs.getInt("id"));
                repas.setTotal(rs.getDouble("total"));
                repas.setSupplements(getSupplementsForRepas(repas.getId()));

                repasList.add(repas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repasList;
    }

    // Supprimer tous les repas associés à une Commande
    public boolean deleteByCommande(Commande commande) {
        String sql = "DELETE FROM repas WHERE commande_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, commande.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
