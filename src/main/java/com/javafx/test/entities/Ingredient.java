package com.javafx.test.entities;

import java.util.ArrayList;
import java.util.List;

public class Ingredient {
    private int id;
    private String nom;
    private double prixUnitaire;
    private String unite;

    public Ingredient(String nom, double prixUnitaire, String unite) {
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.unite = unite;
    }
    // Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public String getUnite() { return unite; }

    public void setId(int id) {
        this.id = id;
    }
}


