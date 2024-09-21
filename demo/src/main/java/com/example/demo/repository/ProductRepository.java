package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

        //關鍵字搜索
        Page<Product> findByNameContaining(@Param("keyword") String keyword,PageRequest pageable);

        //更新以及儲存（Jpa的save方法會檢查資料的主鍵是否存在，如果存在將會進行更新，不存在則儲存一個新的。
        @Modifying
        @Transactional
        Product save(Product updateProduct);

        Product findById(int product_id);
        //刪除 
        @Modifying
        @Transactional
        void deleteById(int productId);

        Product findByProductId(Integer productId);
        
        //賣家頁面透過名稱以及產品id靈活搜尋產品
            @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:productId IS NULL OR p.productId = :productId)")
            Page<Product> findByNameContainingAndProductId(
            @Param("name") String name, 
            @Param("productId") Integer productId, 
            PageRequest pageable);


            //分頁搜索有id在productIds裡的商品
            @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
            Page<Product> findByIdIn(@Param("productIds") List<Integer> productIds, PageRequest pageRequest);


            //搜索全部有id在productIds裡的商品
            @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
            List<Product>findByIdIn(@Param("productIds") List<Integer> productIds);


        }



