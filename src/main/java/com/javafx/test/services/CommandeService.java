package com.javafx.test.services;


import com.javafx.test.dao.CommandeDAO;
import com.javafx.test.entities.*;

import java.util.List;

public class CommandeService {
    private final CommandeDAO commandeDAO;
    private final ClientService clientService;
    private final RepasService repasService;

    public CommandeService() {
        this.commandeDAO = new CommandeDAO();
        this.clientService = new ClientService();
        this.repasService = new RepasService();
    }

    public boolean creerCommande(Client client, List<Repas> repas) {
        Commande commande = new Commande(client);
        for (Repas r : repas) {
            commande.ajouterRepas(r);
        }
        return commandeDAO.create(commande);
    }

    public Commande ajouterRepasACommande(Commande commande, PlatPrincipal plat) {
        Repas nouveauRepas = new Repas(plat);
        commande.ajouterRepas(nouveauRepas);
        commandeDAO.update(commande);
        return commande;
    }

    public Commande ajouterSupplementARepas(Commande commande, Repas repas, Supplement supplement) {
        repas.ajouterSupplement(supplement);
        commande.calculerTotal();
        commandeDAO.update(commande);
        return commande;
    }
}