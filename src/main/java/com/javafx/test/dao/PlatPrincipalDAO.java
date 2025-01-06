package com.javafx.test.dao;

import com.javafx.test.config.DatabaseConnection;
import com.javafx.test.entities.Ingredient;
import com.javafx.test.entities.PlatPrincipal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlatPrincipalDAO implements Dao<PlatPrincipal> {
    private final Connection connection = DatabaseConnection.getConnection();
    private final IngredientDAO ingredientDAO;

    public PlatPrincipalDAO() {
        this.ingredientDAO = new IngredientDAO();
    }

    @Override
    public boolean create(PlatPrincipal platPrincipal) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO plats_principaux (nom, prix_base) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, platPrincipal.getNom());
            pstmt.setDouble(2, platPrincipal.getPrixBase());

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int platId = rs.getInt(1);
                    platPrincipal.setId(platId);

                    // Insérer les relations avec les ingrédients
                    for (Map.Entry<Ingredient, Double> entry : platPrincipal.getIngredients().entrySet()) {
                        insertPlatIngredientRelation(platId, entry.getKey().getId(), entry.getValue());
                    }

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;
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

    private void insertPlatIngredientRelation(int platId, int ingredientId, double quantite) throws SQLException {
        String sql = "INSERT INTO plats_ingredients (plat_id, ingredient_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, platId);
            pstmt.setInt(2, ingredientId);
            pstmt.setDouble(3, quantite);
            pstmt.executeUpdate();
        }
    }

    @Override
    public PlatPrincipal read(int id) {
        String sql = "SELECT * FROM plats_principaux WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );
                plat.setId(rs.getInt("id"));

                // Récupérer les ingrédients et leurs quantités
                Map<Ingredient, Double> ingredients = findIngredientsForPlat(id);
                for (Map.Entry<Ingredient, Double> entry : ingredients.entrySet()) {
                    plat.ajouterIngredient(entry.getKey(), entry.getValue());
                }

                return plat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<Ingredient, Double> findIngredientsForPlat(int platId) {
        Map<Ingredient, Double> ingredients = new HashMap<>();
        String sql = "SELECT * FROM plats_ingredients WHERE plat_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, platId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = ingredientDAO.read(rs.getInt("ingredient_id"));
                if (ingredient != null) {
                    ingredients.put(ingredient, rs.getDouble("quantite"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    @Override
    public boolean update(PlatPrincipal platPrincipal) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE plats_principaux SET nom = ?, prix_base = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, platPrincipal.getNom());
            pstmt.setDouble(2, platPrincipal.getPrixBase());
            pstmt.setInt(3, platPrincipal.getId());

            if (pstmt.executeUpdate() > 0) {
                // Mettre à jour les ingrédients
                deletePlatIngredientRelations(platPrincipal.getId());
                for (Map.Entry<Ingredient, Double> entry : platPrincipal.getIngredients().entrySet()) {
                    insertPlatIngredientRelation(platPrincipal.getId(), entry.getKey().getId(), entry.getValue());
                }

                conn.commit();
                return true;
            }

            conn.rollback();
            return false;
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

    private void deletePlatIngredientRelations(int platId) throws SQLException {
        String sql = "DELETE FROM plats_ingredients WHERE plat_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, platId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Supprimer d'abord les relations avec les ingrédients
            deletePlatIngredientRelations(id);

            // Puis supprimer le plat
            String sql = "DELETE FROM plats_principaux WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            boolean result = pstmt.executeUpdate() > 0;
            conn.commit();
            return result;
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

    public List<PlatPrincipal> findAll() {
        List<PlatPrincipal> plats = new ArrayList<>();
        String sql = "SELECT * FROM plats_principaux";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );
                plat.setId(rs.getInt("id"));

                // Récupérer les ingrédients
                Map<Ingredient, Double> ingredients = findIngredientsForPlat(plat.getId());
                for (Map.Entry<Ingredient, Double> entry : ingredients.entrySet()) {
                    plat.ajouterIngredient(entry.getKey(), entry.getValue());
                }

                plats.add(plat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plats;
    }
}