package dao.impl;

import dao.OrderDetailDao;
import model.OrderDetail;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailDao {
    @Override
    public void insertBatch(Connection conn, int orderId, List<OrderDetail> details) throws Exception {
        String sql = "INSERT INTO order_detail(order_id, product_id, qty, price, subtotal) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderDetail d : details) {
                ps.setInt(1, orderId);
                ps.setInt(2, d.getProductId());
                ps.setInt(3, d.getQty());
                ps.setInt(4, d.getPrice());
                ps.setInt(5, d.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<OrderDetail> findByOrderId(int orderId) throws Exception {
        String sql = "SELECT od.detail_id, od.order_id, od.product_id, p.product_name, od.qty, od.price, od.subtotal " +
                     "FROM order_detail od JOIN product p ON p.product_id = od.product_id " +
                     "WHERE od.order_id = ? ORDER BY od.detail_id";
        List<OrderDetail> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setDetailId(rs.getInt("detail_id"));
                    d.setOrderId(rs.getInt("order_id"));
                    d.setProductId(rs.getInt("product_id"));
                    d.setProductName(rs.getString("product_name"));
                    d.setQty(rs.getInt("qty"));
                    d.setPrice(rs.getInt("price"));
                    d.setSubtotal(rs.getInt("subtotal"));
                    list.add(d);
                }
            }
        }
        return list;
    }
}
