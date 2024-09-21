package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.OrderStatus;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;



@RestController
@RequestMapping("/api/checkout")
public class OrderController {
    
    @Autowired
    CartService cartService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @PostMapping("/ecpayCheckout")
    public ResponseEntity<String> ecpayCheckout(@RequestParam String orderId){
         String aioCheckOutALLForm = orderService.ecpayCheckout(orderId);
        return new ResponseEntity<String>(aioCheckOutALLForm,HttpStatus.OK);
    }

    @PostMapping("/ecpayReturn")
    public ResponseEntity<String> handleEcpayReturn(@RequestParam Map<String, String> params) {
        // 處理 ECPay 回傳的資料
        String tradeStatus = params.get("RtnCode"); // 回傳狀態碼
        String tradeMessage = params.get("RtnMsg"); // 回傳訊息
        String orderId = params.get("MerchantTradeNo"); // 商家訂單編號
    
        if ("1".equals(tradeStatus)) {
            // 成功狀態碼為 "1"
            System.out.println("交易成功: " + tradeMessage);
            // 更新訂單狀態
            Order order = orderRepository.findByOrderId(orderId);
            order.setStatus(OrderStatus.fromValue("paid"));
            orderRepository.save(order);
            //更新購物車狀態
            Integer userId = order.getUser().getUserId();
            System.out.println(userId);
            cartService.deleteAllProduct(userId );

    
            //跳轉回買家中心HTML
            String redirectHtml = "<html><body>"
                + "<script type='text/javascript'>"
                + "alert('交易成功，即將返回會員中心');"
                + "window.location.href = 'http://localhost:3000/BuyerCenter';"
                + "</script>"
                + "</body></html>";
    
            return ResponseEntity.ok(redirectHtml);
        } else {
            // 交易失敗
            System.out.println("交易失敗: " + tradeMessage);
    
            //  跳轉的 HTML
            String redirectHtml = "<html><body>"
                + "<script type='text/javascript'>"
                + "alert('交易失敗，即將返回會員中心');"
                + "window.location.href = 'http://localhost:3000/BuyerCenter';"
                + "</script>"
                + "</body></html>";
            
            return ResponseEntity.ok(redirectHtml);
        }
    }
    
        @PostMapping("/createOrder")
        public ResponseEntity<String> createOrder(@RequestBody Order formData){
            return orderService.createOrder(formData);
        }

        @GetMapping("/getOrders")
        public Page<Order> searchOrders(
            @RequestParam int userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        // 創建分頁請求對象
        PageRequest pageable = PageRequest.of(page, size);
        return orderService.searchOrders(userId,pageable);
    }

        @GetMapping("/getOrderDetails")
        public List<OrderDetail> getOrderDetails(@RequestParam String orderId){
        return orderService.getOrderDetails(orderId);
    }
        //賣家中心獲得所有訂單
        @GetMapping("/getAllOrders")
        public Page<Order> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        // 創建分頁請求對象
        PageRequest pageable = PageRequest.of(page, size);
        return orderService.getAllOrders(pageable);
    }
}   