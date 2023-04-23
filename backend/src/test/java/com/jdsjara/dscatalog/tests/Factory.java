package com.jdsjara.dscatalog.tests;

import java.time.Instant;

import com.jdsjara.dscatalog.dto.ProductDTO;
import com.jdsjara.dscatalog.entities.Category;
import com.jdsjara.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, null, Instant.now());
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
}
