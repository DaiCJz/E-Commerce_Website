package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
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

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private Amount amount;
    @Autowired
    private CartDetail cartDetail;
    public void addProductToCart(int cartId, int productId, int quantity ) {
        // 找到或創建一個新的購物車
        Cart cart = cartRepository.findByUserId(cartId); 
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(cartId);
            cartRepository.save(cart);
        }

            // 創建購物車:先驗證該商品是否已經在購物車內
        CartDetail byCartAndProductId = cartDetailRepository.findByCartAndProductId(cart,productId);
        if(byCartAndProductId==null){
            cartDetail.setCartId(cart.getCartId());
            cartDetail.setProductId(productId);
            cartDetail.setQuantity(quantity);
            cartDetailRepository.save(cartDetail);
        }else{
            cartDetail.setCartId(byCartAndProductId.getCart().getCartId());
            cartDetail.setProductId(byCartAndProductId.getProductId());
            cartDetail.setQuantity(byCartAndProductId.getQuantity()+1);
            cartDetail.setCartDetailId(byCartAndProductId.getCartDetailId());
            cartDetailRepository.save(cartDetail);
        }
        
    }


    //透過id分頁查詢購物車內商品
    public List<Object> getProductsByUserId(int userId) {
        // 先尋找該用戶是否擁有購物車
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // 搜索所有購物車內容
            List<CartDetail> details = cartDetailRepository.findAllByCart(cart);
    
            // 提取所有 productId
            List<Integer> productIds = details.stream()
                                            .map(CartDetail::getProductId)
                                            .collect(Collectors.toList());
        
            // 使用分頁查詢產品
            PageRequest pageRequest = PageRequest.of(0, 5);
           Page<Product> pageResult=productRepository.findByIdIn(productIds, pageRequest);
           List<Object> result = new ArrayList<>();
           result.add(0,pageResult);
           result.add(1,details);
            return result;
        }
        // 防止 null pointer 錯誤
        return new ArrayList<>();
    }
    

    //透過id 搜尋所有購物車內的商品
    public List<Object> getAllProductsByUserId(int userId) {
        // 先尋找該用戶是否擁有購物車
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // 搜索所有購物車內容
            List<CartDetail> details = cartDetailRepository.findAllByCart(cart);
    
            // 提取所有 productId
            List<Integer> productIds = details.stream()
                                            .map(CartDetail::getProductId)
                                            .collect(Collectors.toList());
        
            List<Product> products =productRepository.findByIdIn(productIds);
            List<Object> result = new ArrayList<>();
            result.add(0,products);
            result.add(1,details);
            return result;
        }
        // 防止 null pointer 錯誤
        return new ArrayList<>();
    }

    //更新數量
    public boolean updateQuantity(int userId,int cartDetailId ,int product_id, int quantity){
        
        if(cartRepository.findByUserId(userId)!=null){
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCartDetailId(cartDetailId);
            cartDetail.setProductId(product_id);
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
        Product p =  productRepository.findByProductId(c.getProductId());
            
            totalAmount+=(c.getQuantity()*p.getPrice());
            //獲得折扣
            totalDiscount+=(c.getQuantity()*p.getPrice()*(p.getDiscount()*0.01));
        }
        //設定總計
        amount.setTotal(totalAmount);
        amount.setTotalDiscount(totalDiscount);
        amount.setFinalAmount(totalAmount-totalDiscount);


        return ResponseEntity.ok(amount);
    }
    
}

    