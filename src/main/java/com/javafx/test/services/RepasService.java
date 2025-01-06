package com.javafx.test.services;


import com.javafx.test.dao.RepasDAO;
import com.javafx.test.entities.Repas;
import com.javafx.test.entities.Supplement;

public class RepasService {
    private final RepasDAO repasDAO;

    public RepasService() {
        this.repasDAO = new RepasDAO();
    }

    public boolean createRepas(Repas repas) {
        // Validation
        if (repas.getPlatPrincipal() == null) {
            throw new IllegalArgumentException("Plat principal requis");
        }

        // Calcul du total du repas
        double total = repas.getPlatPrincipal().calculerPrix();
        total += repas.getSupplements().stream()
                .mapToDouble(Supplement::getPrix)
                .sum();
        repas.setTotal(total);

        return repasDAO.create(repas);
    }

    public boolean ajouterSupplement(int repasId, Supplement supplement) {
        Repas repas = repasDAO.read(repasId);
        if (repas == null) {
            throw new IllegalArgumentException("Repas non trouvé");
        }

        repas.ajouterSupplement(supplement);
        repas.setTotal(repas.calculerTotal());

        return repasDAO.update(repas);
    }
}
