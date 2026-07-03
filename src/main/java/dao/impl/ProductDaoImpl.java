package dao.impl;

import dao.ProductDao;
import model.Product;
import util.DBConnection;
import util.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {
    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.category_id, c.category_name, p.price, p.description, p.image, p.status " +
                     "FROM product p JOIN category c ON p.category_id=c.category_id ORDER BY p.product_id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LogUtil.error("findAll product failed", e); }
        return list;
    }

    @Override
    public List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, p.category_id, c.category_name, p.price, p.description, p.image, p.status " +
                     "FROM product p JOIN category c ON p.category_id=c.category_id " +
                     "WHERE p.product_name LIKE ? OR c.category_name LIKE ? OR p.description LIKE ? OR p.status LIKE ? ORDER BY p.product_id";
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like); ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { LogUtil.error("search product failed", e); }
        return list;
    }


    @Override
    public List<Product> findForOrdering(String categoryName, String keyword) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.product_id, p.product_name, p.category_id, c.category_name, p.price, p.description, p.image, p.status " +
            "FROM product p JOIN category c ON p.category_id=c.category_id WHERE p.status='販售中' "
        );
        List<String> params = new ArrayList<>();
        if (categoryName != null && categoryName.trim().length() > 0 && !"全部".equals(categoryName.trim())) {
            sql.append("AND c.category_name=? ");
            params.add(categoryName.trim());
        }
        if (keyword != null && keyword.trim().length() > 0) {
            sql.append("AND (p.product_name LIKE ? OR c.category_name LIKE ? OR p.description LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        sql.append("ORDER BY CASE c.category_name WHEN '套餐' THEN 1 WHEN '主食' THEN 2 WHEN '麵類' THEN 3 WHEN '小菜' THEN 4 WHEN '湯品' THEN 5 WHEN '飲料' THEN 6 WHEN '甜點' THEN 7 ELSE 99 END, c.category_name, p.product_id");
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { LogUtil.error("findForOrdering product failed", e); }
        return list;
    }

    @Override
    public List<String> findActiveCategoryNamesForOrdering() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT c.category_name " +
                     "FROM product p JOIN category c ON p.category_id=c.category_id " +
                     "WHERE p.status='販售中' AND c.category_name IS NOT NULL AND c.category_name<>''";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("category_name"));
        } catch (SQLException e) { LogUtil.error("findActiveCategoryNamesForOrdering failed", e); }
        return list;
    }

    @Override
    public boolean insert(Product p) {
        String sql = "INSERT INTO product(product_name, category_id, price, description, image, status) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, p);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LogUtil.error("insert product failed", e); return false; }
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE product SET product_name=?, category_id=?, price=?, description=?, image=?, status=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fill(ps, p);
            ps.setInt(7, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LogUtil.error("update product failed", e); return false; }
    }

    @Override
    public boolean delete(int productId) {
        String sql = "DELETE FROM product WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LogUtil.error("delete product failed", e); return false; }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM product";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { LogUtil.error("count product failed", e); }
        return 0;
    }

    private void fill(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1, p.getProductName());
        ps.setInt(2, p.getCategoryId());
        ps.setInt(3, p.getPrice());
        ps.setString(4, p.getDescription());
        ps.setString(5, p.getImage());
        ps.setString(6, p.getStatus() == null ? "販售中" : p.getStatus());
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("category_id"),
            rs.getString("category_name"), rs.getInt("price"), rs.getString("description"),
            rs.getString("image"), rs.getString("status")
        );
    }
}
