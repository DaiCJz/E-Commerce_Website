package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {

    List<OrderDetail> findAllByOrder(Order order);

    
} 