package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Product;
import com.example.demo.model.myData;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }


    // 處理關鍵字搜索的 GET 請求方法

   @GetMapping("/search")
public Page<Product> searchProducts(
        @RequestParam("keyword") String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
    
    // 創建分頁請求對象
    PageRequest pageable = PageRequest.of(page, size);
    Page<Product> searchProducts = productService.searchProducts(keyword, pageable);
    
    // 過濾掉庫存為 0 的產品
    List<Product> filteredProducts = searchProducts
            .stream()
            .filter(s -> s.getStock() != 0)
            .collect(Collectors.toList());
    
    // 使用 PageImpl 將 List 轉換為 Page
    return new PageImpl<>(filteredProducts, pageable, filteredProducts.size());
}

       //更新產品資訊
        @PatchMapping("/updateProduct")
        public Product updateProduct(@RequestBody Product updatedProduct){
            return productService.updateProduct(updatedProduct);
        }
      // 刪除產品
    @DeleteMapping("/deleteProduct")
    public void deleteProduct(@RequestParam("productId") int productId) {
        productService.deleteProduct(productId);
    }

    //賣家產品頁面透過名稱或者編號搜索產品（靈活搜索）
    @PostMapping("/SearchByNameAndId")
    public Page<Product> findByNameAndProductId(@RequestBody myData data) {
       

        return productService.findByNameContainingAndProductId(data);
    }
    

    @PostMapping("/AddProduct")
    public Product AddProduct(@RequestBody Product newProduct) {
            return productService.AddProduct(newProduct);
    }
    

  @PostMapping("/uploadImage")
  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    return productService.uploadImage(file);
}

};







