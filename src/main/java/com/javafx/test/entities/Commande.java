package com.javafx.test.entities;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {
    private int id;
    private Client client;  // Objet Client au lieu de client_id
    private List<Repas> repas;  // Liste d'objets Repas
    private Date dateCommande;
    private double total;

    // Constructor
    public Commande(Client client) {
        this.client = client;
        this.repas = new ArrayList<>();
        this.dateCommande = new Date();
    }

    public Commande() {

    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Repas> getRepas() {
        return repas;
    }

    public void setRepas(List<Repas> repas) {
        this.repas = repas;
        this.calculerTotal();  // Recalculate total after setting repas
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Other methods
    public void ajouterRepas(Repas repas) {
        this.repas.add(repas);
        this.calculerTotal();
    }

    public void calculerTotal() {
        this.total = this.repas.stream()
                .mapToDouble(Repas::getTotal)
                .sum();
    }

    public List<Repas> getRepasList() {
        return repas;
    }

    public void setRepasList(List<Repas> repas) {
        this.repas = repas;
        this.calculerTotal();  // Recalculate total after setting repas
    }
}
