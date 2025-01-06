package com.javafx.test.services;


import com.javafx.test.dao.ClientDAO;
import com.javafx.test.entities.Client;

import java.util.List;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public boolean createClient(Client client) {
        // Validation des données
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client est requis");
        }
        if (client.getEmail() == null || !client.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email invalide");
        }

        return clientDAO.create(client);
    }

    public Client getClient(int id) {
        return clientDAO.read(id);
    }

    public boolean updateClient(Client client) {
        // Validation des données
        if (client.getId() <= 0) {
            throw new IllegalArgumentException("ID client invalide");
        }

        return clientDAO.update(client);
    }

    public boolean deleteClient(int id) {
        return clientDAO.delete(id);
    }

    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }
}