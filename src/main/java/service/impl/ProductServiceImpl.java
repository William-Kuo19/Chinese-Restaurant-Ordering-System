package service.impl;

import dao.ProductDao;
import dao.impl.ProductDaoImpl;
import exception.ProductException;
import model.Product;
import service.ProductService;
import util.Validator;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    private final ProductDao productDao = new ProductDaoImpl();

    @Override
    public List<Product> getAllProducts() { return productDao.findAll(); }

    @Override
    public List<Product> searchProducts(String keyword) { return Validator.isBlank(keyword) ? productDao.findAll() : productDao.search(keyword); }

    @Override
    public List<Product> getProductsForOrdering(String categoryName, String keyword) {
        return productDao.findForOrdering(categoryName, keyword);
    }

    @Override
    public List<String> getActiveCategoryNamesForOrdering() {
        return productDao.findActiveCategoryNamesForOrdering();
    }

    @Override
    public void addProduct(Product product) throws Exception {
        validate(product);
        if (!productDao.insert(product)) throw new ProductException("新增商品失敗");
    }

    @Override
    public void updateProduct(Product product) throws Exception {
        validate(product);
        if (product.getProductId() == null) throw new ProductException("請先選擇商品");
        if (!productDao.update(product)) throw new ProductException("修改商品失敗");
    }

    @Override
    public void deleteProduct(int productId) throws Exception {
        if (!productDao.delete(productId)) throw new ProductException("刪除商品失敗，可能已有訂單資料關聯");
    }

    @Override
    public int countProducts() { return productDao.countAll(); }

    private void validate(Product p) throws ProductException {
        if (Validator.isBlank(p.getProductName())) throw new ProductException("商品名稱不可空白");
        if (p.getCategoryId() == null || p.getCategoryId() <= 0) throw new ProductException("請選擇商品分類");
        if (p.getPrice() == null || p.getPrice() < 0) throw new ProductException("商品價格不可小於 0");
        if (!"販售中".equals(p.getStatus()) && !"停售".equals(p.getStatus())) throw new ProductException("商品狀態錯誤");
    }
}
