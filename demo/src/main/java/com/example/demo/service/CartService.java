package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Amount;
import com.example.demo.model.Cart;
import com.example.demo.model.CartDetail;
import com.example.demo.model.Product;
import com.example.demo.repository.CartDetailRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
   
   
    public void addProductToCart(int cartId, Product  product, int quantity ) {
        // 找到或創建一個新的購物車
        Cart cart = cartRepository.findByUserId(cartId); 
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(cartId);
            cartRepository.save(cart);
        }

            // 創建購物車:先驗證該商品是否已經在購物車內
        CartDetail byCartAndProductId = cartDetailRepository.findByCartAndProduct(cart,product);
        if(byCartAndProductId==null){
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCartId(cart.getCartId());
            cartDetail.setProduct(product);
            cartDetail.setQuantity(quantity);
            cartDetailRepository.save(cartDetail);
        }else{
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCartId(byCartAndProductId.getCart().getCartId());
            cartDetail.setProduct(byCartAndProductId.getProduct());
            cartDetail.setQuantity(byCartAndProductId.getQuantity()+1);
            cartDetail.setCartDetailId(byCartAndProductId.getCartDetailId());
            cartDetailRepository.save(cartDetail);
        }
        
    }


    //透過id分頁查詢購物車內商品
    public Page<CartDetail> getProductsByUserId(int userId) {
        // 先尋找該用戶是否擁有購物車
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // 搜索所有購物車內容
            // List<CartDetail> details = cartDetailRepository.findAllByCart(cart);
    
            // 提取所有 productId
            // List<Integer> productIds = details.stream()
            //                                 .map(CartDetail::getProductId)
            //                                 .collect(Collectors.toList());
        
            // 使用分頁查詢產品
            PageRequest pageRequest = PageRequest.of(0, 5);
        //    Page<Product> pageResult=productRepository.findByIdIn(productIds, pageRequest);
        //    List<Object> result = new ArrayList<>();
        //    result.add(0,pageResult);
        //    result.add(1,details);
            Page<CartDetail> pageResult = cartDetailRepository.findAllByCart(cart,pageRequest);
            return pageResult;
        }
        // 防止 null pointer 錯誤
        return null;
    }
    

    //透過id 搜尋所有購物車內的商品
    public List<CartDetail> getAllProductsByUserId(int userId) {
        // 先尋找該用戶是否擁有購物車
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // 搜索所有購物車內容
            List<CartDetail> details = cartDetailRepository.findAllByCart(cart);
    
            // // 提取所有 productId
            // List<Integer> productIds = details.stream()
            //                                 .map(CartDetail::getProductId)
            //                                 .collect(Collectors.toList());
        
            // List<Product> products =productRepository.findByIdIn(productIds);
            // List<Object> result = new ArrayList<>();
            // result.add(0,products);
            // result.add(1,details);
            return details;
        }
        // 防止 null pointer 錯誤
        return new ArrayList<>();
    }

    //更新數量
    public boolean updateQuantity(int userId,int cartDetailId ,int product_id, int quantity){
        
        if(cartRepository.findByUserId(userId)!=null){
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCartDetailId(cartDetailId);
            Product byId  = productRepository.findById(product_id);
            cartDetail.setProduct(byId);
            cartDetail.setQuantity(quantity);
            cartDetail.setCart(cartRepository.findByUserId(userId));
            cartDetailRepository.save(cartDetail);
            return true;
        }
        return  false;
    }
    

    //刪除購物車內商品
    public ResponseEntity<String> deleteProduct(int userId,int cartDetailId,int productId){
        
        if(cartRepository.findByUserId(userId)!=null){
    
                cartDetailRepository.deleteById(cartDetailId);

        }
        return ResponseEntity.ok("delete success!");
    }



    //計算金額
    public ResponseEntity<Amount> getTotalAmountById(int id){
        //獲得該購物車
        Cart cart = cartRepository.findByUserId(id);
        //獲得購物車詳細內容
        List<CartDetail> cartDetails = cartDetailRepository.findAllByCart(cart);
        System.out.println("cartdetails"+cartDetails);
        //總價格
        double totalAmount = 0;
        double totalDiscount =0;
        for (CartDetail c : cartDetails) {
            //獲得product來獲得價格
        Product p =  productRepository.findByProductId(c.getProduct().getProduct_id());
            
            totalAmount+=(c.getQuantity()*p.getPrice());
            //獲得折扣
            totalDiscount+=(c.getQuantity()*p.getPrice()*(p.getDiscount()*0.01));
        }
        //設定總計
        Amount amount = new Amount();
        amount.setTotal(Math.round(totalAmount));
        amount.setTotalDiscount(Math.round(totalDiscount));
        amount.setFinalAmount(Math.round(totalAmount-totalDiscount));


        return ResponseEntity.ok(amount);
    }
    

    //刪除購物車內的所有商品
    @Transactional
    public ResponseEntity<String> deleteAllProduct(int userId){
        if(cartRepository.findByUserId(userId)!=null){
            Cart cart = cartRepository.findByUserId(userId);
            List<CartDetail> cartDetails = cartDetailRepository.findAllByCart(cart);
            //購買後設定數量
        for (CartDetail cartDetail : cartDetails) {
            Product product = cartDetail.getProduct();
            Integer quantity = cartDetail.getQuantity();
            int stock = product.getStock();
            product.setStock(stock-quantity);
            productRepository.save(product);
        }
            cartDetailRepository.deleteByCart(cart);
        }
        return ResponseEntity.ok("delete success!");
    }

}

    