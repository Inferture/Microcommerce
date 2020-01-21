package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
    Product findById(int id);

    List<Product> findAllByOrderByNomAsc();
    List<Product> findByPrixLessThan(int prixLimit);
    List<Product> findByNomLike(String recherche);

    @Query(value = "SELECT p FROM Product p WHERE prix > :prixLimit")
    List<Product>  chercherUnProduitCher(@Param("prixLimit") int prix);



}