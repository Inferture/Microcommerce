package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@Api( value="API pour les opérations CRUD (Create, Read, Update, Delete) sur les produits.")
@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;


    //Récupérer la liste des produits
    @ApiOperation(value = "Récupère la liste des produits en stock.")
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)

    public MappingJacksonValue listeProduits() {

        List<Product> produits = productDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listeDeNosFiltres);

        return produitsFiltres;
    }


    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock.")
    @GetMapping(value = "/Produits/{id}")

    public MappingJacksonValue afficherUnProduit(@PathVariable int id) {

        Product product = productDao.findById(id);

        if(product==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. :(");


        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitFiltre = new MappingJacksonValue(product);
        produitFiltre.setFilters(listeDeNosFiltres);
        return produitFiltre;
        //return productDao.findById(id);
    }

    //ajouter un produit
    @PostMapping(value = "/Produits")
    @ApiOperation(value = "Ajoute un produit à la liste.")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {

        if(product.getPrix()<=0)
        {
            throw new ProduitGratuitException("Le prix doit être strictement supérieur à 0 !");
        }


        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "test/produits/{prixLimit}")
    public MappingJacksonValue testeDeRequetes(@PathVariable int prixLimit) {
        List<Product> products = productDao.findByPrixLessThan(prixLimit);


        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(products);
        produitsFiltres.setFilters(listeDeNosFiltres);
        return produitsFiltres;
    }

    @GetMapping(value = "test/produits/like/{recherche}")
    public MappingJacksonValue testeDeRequetes(@PathVariable String recherche) {
        List<Product> products = productDao.findByNomLike("%"+recherche+"%");

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(products);
        produitsFiltres.setFilters(listeDeNosFiltres);
        return produitsFiltres;
    }

    @ApiOperation(value = "Supprime un produit de la liste.")
    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.delete(productDao.findById(id));
    }




    @ApiOperation(value = "Modifie un produit de la liste.")
    @PutMapping (value = "/Produits")
    public void updateProduit(@Valid @RequestBody Product product) {
        if(product.getPrix()<=0)
        {
            throw new ProduitGratuitException("Le prix doit être strictement supérieur à 0 !");
        }
        productDao.save(product);
    }


    @GetMapping(value = "test/produits/expensive/{prixLimit}")
    public MappingJacksonValue requetePerso(@PathVariable int prixLimit) {

        List<Product> products = productDao.chercherUnProduitCher(prixLimit);

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(products);
        produitsFiltres.setFilters(listeDeNosFiltres);
        return produitsFiltres;
    }

    @ApiOperation(value = "Récupère la liste des produits avec la marge effectuée sur le produit.")
    @GetMapping(value = "/AdminProduits")
    public Map<Product, Integer> calculerMargeProduit() {

        List<Product> products = productDao.findAll();
        Map<Product, Integer> marges = new HashMap<>();
        for(int i=0;i<products.size();i++)
        {
            marges.put(products.get(i), products.get(i).getPrix()-products.get(i).getPrixAchat());
        }
        return marges;
     }

    @ApiOperation(value = "Récupère la liste des produits en stock dans l'ordre alphabétique.")
    @GetMapping(value = "/TriProduits")
     public MappingJacksonValue trierProduitsParOrdreAlphabetique() {

         List<Product> produits = productDao.findAllByOrderByNomAsc();

         SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

         FilterProvider listeDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

         MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

         produitsFiltres.setFilters(listeDeNosFiltres);

         return produitsFiltres;
     }

}