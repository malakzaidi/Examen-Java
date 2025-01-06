package com.javafx.test.entities;

import java.util.HashMap;
import java.util.Map;

public class PlatPrincipal {
    private int id;
    private String nom;
    private double prixBase;
    private Map<Ingredient, Double> ingredients; // Ingrédient et sa quantité

    public PlatPrincipal(String nom, double prixBase) {
        this.nom = nom;
        this.prixBase = prixBase;
        this.ingredients = new HashMap<>();
    }

    public void ajouterIngredient(Ingredient ingredient, double quantite) {
        ingredients.put(ingredient, quantite);
    }

    public double calculerPrix() {
        double prixIngredients = ingredients.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrixUnitaire() * e.getValue())
                .sum();
        return prixBase + prixIngredients;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getPrixBase() { return prixBase; }
    public Map<Ingredient, Double> getIngredients() { return ingredients; }

    public void setId(int platId) {
        this.id = platId;
    }
}

