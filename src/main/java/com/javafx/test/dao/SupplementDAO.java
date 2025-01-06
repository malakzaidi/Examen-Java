package com.javafx.test.dao;

import com.javafx.test.config.DatabaseConnection;
import com.javafx.test.entities.Supplement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplementDAO implements Dao<Supplement> {
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public boolean create(Supplement supplement) {
        String sql = "INSERT INTO supplements (nom, prix) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, supplement.getNom());
            pstmt.setDouble(2, supplement.getPrix());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    supplement.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Supplement read(int id) {
        String sql = "SELECT * FROM supplements WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Supplement supplement = new Supplement(
                        rs.getString("nom"),
                        rs.getDouble("prix")
                );
                supplement.setId(rs.getInt("id"));
                return supplement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Supplement supplement) {
        String sql = "UPDATE supplements SET nom = ?, prix = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, supplement.getNom());
            pstmt.setDouble(2, supplement.getPrix());
            pstmt.setInt(3, supplement.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM supplements WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Supplement> findAll() {
        List<Supplement> supplements = new ArrayList<>();
        String sql = "SELECT * FROM supplements";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplement supplement = new Supplement(
                        rs.getString("nom"),
                        rs.getDouble("prix")
                );
                supplement.setId(rs.getInt("id"));
                supplements.add(supplement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplements;
    }

    public List<Supplement> findByRepasId(int repasId) {
        List<Supplement> supplements = new ArrayList<>();
        String sql = "SELECT s.* FROM supplements s " +
                "JOIN repas_supplements rs ON s.id = rs.supplement_id " +
                "WHERE rs.repas_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, repasId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Supplement supplement = new Supplement(
                        rs.getString("nom"),
                        rs.getDouble("prix")
                );
                supplement.setId(rs.getInt("id"));
                supplements.add(supplement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supplements;
    }
}