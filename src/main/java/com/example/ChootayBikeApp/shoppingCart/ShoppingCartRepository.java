package com.example.ChootayBikeApp.shoppingCart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    //List<ShoppingCart> findAllByUserUsername(String Username);

    List<ShoppingCart> findAllByUserFirstNameAndCompleted(String firstName, Boolean isCompleted);

    List<ShoppingCart> findAllByUserFirstNameAndCompletedOrderByDateDesc(String firstName, Boolean isCompleted);

    List<ShoppingCart> findAll();

    ShoppingCart findShoppingCartById(Long id);

    void deleteAllById(Long id);

}
