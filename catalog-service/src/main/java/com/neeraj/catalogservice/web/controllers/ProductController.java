package com.neeraj.catalogservice.web.controllers;

import com.neeraj.catalogservice.domain.PagedResult;
import com.neeraj.catalogservice.domain.Product;
import com.neeraj.catalogservice.domain.ProductNotFoundException;
import com.neeraj.catalogservice.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    PagedResult<Product> getAllProducts(@RequestParam(name = "page", defaultValue = "1") int pageNo) {
        return productService.getAllProducts(pageNo);
    }

    @GetMapping("/{code}")
    ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode(code));
    }
}
