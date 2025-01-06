package com.javafx.test.entities;

import java.util.ArrayList;
import java.util.List;

public class Client {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private List<Commande> commandes;

    // Constructeur par défaut
    public Client() {
        commandes = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Client(String nom, String prenom, String email, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.commandes = new ArrayList<>();
    }

    // Constructeur avec id
    public Client(int id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.commandes = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }

    // Méthodes supplémentaires
    public void ajouterCommande(Commande commande) {
        this.commandes.add(commande);
    }

    public void supprimerCommande(Commande commande) {
        this.commandes.remove(commande);
    }

    public Commande getCommandeById(int id) {
        for (Commande commande : commandes) {
            if (commande.getId() == id) {
                return commande;
            }
        }
        return null;
    }

    public boolean aCommande(int id) {
        for (Commande commande : commandes) {
            if (commande.getId() == id) {
                return true;
            }
        }
        return false;
    }

    // Méthode pour afficher les informations du client
    public void afficherInfosClient() {
        System.out.println("Client ID: " + id);
        System.out.println("Nom: " + nom);
        System.out.println("Prénom: " + prenom);
        System.out.println("Email: " + email);
        System.out.println("Téléphone: " + telephone);
        System.out.println("Commandes: ");
        for (Commande commande : commandes) {
            System.out.println(" - Commande ID: " + commande.getId() + " Date: " + commande.getDateCommande());
        }
    }

    // Override de la méthode toString
    @Override
    public String toString() {
        return "Client{id=" + id + ", nom='" + nom + "', prenom='" + prenom + "', email='" + email + "', telephone='" + telephone + "'}";
    }
}
