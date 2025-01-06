package com.javafx.test.dao;

import com.javafx.test.config.DatabaseConnection;
import com.javafx.test.entities.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class IngredientDAO implements Dao<Ingredient> {
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public boolean create(Ingredient ingredient) {
        String sql = "INSERT INTO ingredients (nom, prix_unitaire, unite) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ingredient.getNom());
            pstmt.setDouble(2, ingredient.getPrixUnitaire());
            pstmt.setString(3, ingredient.getUnite());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ingredient.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Ingredient read(int id) {
        String sql = "SELECT * FROM ingredients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire"),
                        rs.getString("unite")
                );
                ingredient.setId(rs.getInt("id"));
                return ingredient;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Ingredient ingredient) {
        String sql = "UPDATE ingredients SET nom = ?, prix_unitaire = ?, unite = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ingredient.getNom());
            pstmt.setDouble(2, ingredient.getPrixUnitaire());
            pstmt.setString(3, ingredient.getUnite());
            pstmt.setInt(4, ingredient.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM ingredients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire"),
                        rs.getString("unite")
                );
                ingredient.setId(rs.getInt("id"));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public List<Ingredient> findByPlatPrincipalId(int platPrincipalId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT i.*, pi.quantite FROM ingredients i " +
                "JOIN plats_ingredients pi ON i.id = pi.ingredient_id " +
                "WHERE pi.plat_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, platPrincipalId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire"),
                        rs.getString("unite")
                );
                ingredient.setId(rs.getInt("id"));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }
}