package com.javafx.test.entities;

public class Supplement {
    private int id;
    private String nom;
    private double prix;

    public Supplement(String nom, double prix) {
        this.nom = nom;
        this.prix = prix;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getPrix() { return prix; }
    public int setId(int id) { return this.id = id; }
}

