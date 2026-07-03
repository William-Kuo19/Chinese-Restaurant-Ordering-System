package dao;

import java.util.List;
import model.Product;

public interface ProductDao {
    List<Product> findAll();
    List<Product> search(String keyword);
    List<Product> findForOrdering(String categoryName, String keyword);
    List<String> findActiveCategoryNamesForOrdering();
    boolean insert(Product product);
    boolean update(Product product);
    boolean delete(int productId);
    int countAll();
}
