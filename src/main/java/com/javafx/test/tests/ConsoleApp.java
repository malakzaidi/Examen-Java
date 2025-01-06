package com.javafx.test.tests;

import com.javafx.test.dao.ClientDAO;
import com.javafx.test.dao.CommandeDAO;
import com.javafx.test.entities.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class ConsoleApp {

    public static void main(String[] args) {
        // Création d'une commande
        Commande commande = new Commande();
        Client client = new Client();
        client.setNom("Léa Dupont");
        commande.setClient(client);

        // Ajout de repas à la commande
        commande.ajouterRepas(new Repas("Salade composée",
                List.of(new Ingredient("Laitue", 150.0, "grammes"),
                        new Ingredient("Tomate", 2.0, "unités")),
                List.of(new Supplement("Vinaigrette", 2.5),
                        new Supplement("Pain", 1.8))));

        commande.ajouterRepas(new Repas("Pizza Margherita",
                List.of(new Ingredient("Pâte", 150.0, "grammes"),
                        new Ingredient("Tomate", 100.0, "grammes"),
                        new Ingredient("Mozzarella", 80.0, "grammes")),
                List.of(new Supplement("Boisson gazeuse", 2.0))));

        // Affichage du ticket
        afficherTicket(commande);
    }

    private static void afficherTicket(Commande commande) {
        private static void afficherTicket(Commande commande) {
            System.out.println("Bienvenue " + commande.getClient().getNom());
            System.out.println("-----------------TICKET-----------------");
            System.out.println("Nom: " + commande.getClient().getNom());

            int i = 1;
            for (Repas repas : commande.getRepas()) {
                System.out.println("Repas N°:" + i + " " + repas.getPlatPrincipal().getNom());
                System.out.println("Ingrédients:");
                for (Map.Entry<Ingredient, Double> entry : repas.getPlatPrincipal().getIngredients().entrySet()) {
                    System.out.printf("%s: %.2f %s\n", entry.getKey().getNom(), entry.getValue(), entry.getKey().getUnite());
                }

                // Affichage des suppléments (à adapter selon votre structure)
                System.out.println("Suppléments:");
                for (Supplement supplement : repas.getSupplements()) {
                    System.out.printf("%s: %.2f\n", supplement.getNom(), supplement.getPrix());
                }

                System.out.println();
                i++;
            }

            // Formatage du prix total avec 2 décimales
            System.out.printf("Total: %.2f\n", commande.getTotal());
            System.out.println("----------------------------------------");
        }
        double total = calculerTotal(commande);
        System.out.printf("Total: %.2f€\n", total); // Affichage du total en euros
        System.out.println("----------------------------------------");
    }

    private static double calculerTotal(Commande commande) {
        double total = 0;
        for (Repas repas : commande.getRepas()) {
            double totalRepas = 0;
            for (Ingredient ingredient : repas.getIngredients()) {
                totalRepas += ingredient.getQuantite() * ingredient.getPrixUnitaire(); // Calcul du coût total des ingrédients
            }
            for (Supplement supplement : repas.getSupplements()) {
                totalRepas += supplement.getPrix();
            }
            total += totalRepas;
        }
        return total;
    }
}
