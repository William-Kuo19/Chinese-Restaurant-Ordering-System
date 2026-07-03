package dao.impl;

import dao.CategoryDao;
import model.Category;
import util.DBConnection;
import util.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, sort_order FROM category ORDER BY sort_order, category_id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getInt("sort_order")));
        } catch (SQLException e) { LogUtil.error("findAll category failed", e); }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM category";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { LogUtil.error("count category failed", e); }
        return 0;
    }
}
