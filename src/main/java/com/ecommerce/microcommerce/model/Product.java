package com.ecommerce.microcommerce.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
//@JsonIgnoreProperties(value = {"prixAchat", "id"})
//@JsonFilter("monFiltreDynamique")

@SequenceGenerator(name="seq", initialValue=4)
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq")
    //@JsonIgnore
    private int id;

    @Length(min=3, max=30)
    private String nom;

    //@Min(value = 1, message = "You need to raise the price, you're not going to make any profit like this...")
    private int prix;

    //information que nous ne souhaitons pas exposer
    private int prixAchat;

    //constructeur par défaut
    public Product() {
    }

    //constructeur pour nos tests
    public Product(int id, String nom, int prix, int prixAchat) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.prixAchat = prixAchat;
    }

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

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(int prixAchat) {
        this.prixAchat = prixAchat;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                '}';
    }
}