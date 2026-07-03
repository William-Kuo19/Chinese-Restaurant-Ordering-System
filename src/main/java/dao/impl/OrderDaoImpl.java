package dao.impl;

import dao.OrderDao;
import model.Order;
import util.DBConnection;
import util.LogUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    @Override
    public int countTodayOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_time >= CURDATE() AND order_time < DATE_ADD(CURDATE(), INTERVAL 1 DAY)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LogUtil.error("count today orders failed", e);
        }
        return 0;
    }

    @Override
    public int sumTodayRevenue() {
        String sql = "SELECT COALESCE(SUM(total),0) FROM orders WHERE order_time >= CURDATE() AND order_time < DATE_ADD(CURDATE(), INTERVAL 1 DAY)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LogUtil.error("sum today revenue failed", e);
        }
        return 0;
    }

    @Override
    public List<String[]> findLatestOrders(int limit) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT order_no, dine_type, COALESCE(table_no,'') table_no, total FROM orders ORDER BY order_time DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{rs.getString("order_no"), rs.getString("dine_type"), rs.getString("table_no"), String.valueOf(rs.getInt("total"))});
                }
            }
        } catch (SQLException e) {
            LogUtil.error("latest orders failed", e);
        }
        return list;
    }

    @Override
    public int insert(Connection conn, Order order) throws Exception {
        String sql = "INSERT INTO orders(order_no, member_id, dine_type, table_no, remark, total, status) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderNo());
            ps.setInt(2, order.getMemberId());
            ps.setString(3, order.getDineType());
            ps.setString(4, order.getTableNo());
            ps.setString(5, order.getRemark());
            ps.setInt(6, order.getTotal());
            ps.setString(7, order.getStatus() == null ? "待製作" : order.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("新增訂單失敗，無法取得訂單編號");
    }

    @Override
    public List<Order> findByCriteria(String orderNo, String dineType, String status,
                                      LocalDate startDate, LocalDate endDate,
                                      Integer memberId) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT order_id, order_no, member_id, dine_type, table_no, remark, total, status, order_time " +
                "FROM orders WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (orderNo != null && !orderNo.trim().isEmpty()) {
            sql.append(" AND order_no LIKE ?");
            params.add("%" + orderNo.trim() + "%");
        }
        if (dineType != null && !dineType.trim().isEmpty() && !"全部".equals(dineType)) {
            sql.append(" AND dine_type = ?");
            params.add(dineType);
        }
        if (status != null && !status.trim().isEmpty() && !"全部".equals(status)) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (startDate != null) {
            sql.append(" AND order_time >= ?");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }
        if (endDate != null) {
            sql.append(" AND order_time < ?");
            params.add(Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));
        }
        if (memberId != null) {
            sql.append(" AND member_id = ?");
            params.add(memberId);
        }
        sql.append(" ORDER BY order_time DESC");

        List<Order> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setOrderNo(rs.getString("order_no"));
                    o.setMemberId(rs.getInt("member_id"));
                    o.setDineType(rs.getString("dine_type"));
                    o.setTableNo(rs.getString("table_no"));
                    o.setRemark(rs.getString("remark"));
                    o.setTotal(rs.getInt("total"));
                    o.setStatus(rs.getString("status"));
                    Timestamp time = rs.getTimestamp("order_time");
                    if (time != null) o.setOrderTime(time.toLocalDateTime());
                    list.add(o);
                }
            }
        }
        return list;
    }
}
