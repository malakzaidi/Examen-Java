package com.javafx.test.entities;

import java.util.ArrayList;
import java.util.List;

public class Repas {
    private int id;
    private PlatPrincipal platPrincipal;  // Objet PlatPrincipal
    private List<Supplement> supplements;  // Liste d'objets Supplement
    private double total;

    public PlatPrincipal getPlatPrincipal() { return platPrincipal; }
    public List<Supplement> getSupplements() { return supplements; }
    public double getTotal() { return total; }
    public Repas(PlatPrincipal platPrincipal) {
        this.platPrincipal = platPrincipal;
        this.supplements = new ArrayList<>();
        calculerTotal();
    }

    public void ajouterSupplement(Supplement supplement) {
        this.supplements.add(supplement);
        calculerTotal();
    }

    public double calculerTotal() {
        this.total = this.platPrincipal.calculerPrix() +
                this.supplements.stream()
                        .mapToDouble(Supplement::getPrix)
                        .sum();
        return 0;
    }
    public void setSupplements(List<Supplement> supplements) {
        this.supplements = supplements;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double setTotal(double total) {
        this.total = total;
        return total;
    }
    }

