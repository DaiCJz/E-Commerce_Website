package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cart;
import com.example.demo.model.CartDetail;
import com.example.demo.model.Product;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
     Page<CartDetail> findAllByCart(Cart cart,PageRequest pageRequest);

     CartDetail findByCartAndProduct(Cart cart, Product product);

     List<CartDetail> findAllByCart(Cart cart);

     // void deleteAllByCart(Cart cart);

     void deleteByCart(Cart cart);
}
