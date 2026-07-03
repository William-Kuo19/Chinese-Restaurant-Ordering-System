package service;

import java.util.List;
import model.Product;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsForOrdering(String categoryName, String keyword);
    List<String> getActiveCategoryNamesForOrdering();
    void addProduct(Product product) throws Exception;
    void updateProduct(Product product) throws Exception;
    void deleteProduct(int productId) throws Exception;
    int countProducts();
}
