package service.impl;

import dao.OrderDao;
import dao.OrderDetailDao;
import dao.impl.OrderDaoImpl;
import dao.impl.OrderDetailDaoImpl;
import exception.OrderException;
import model.Order;
import model.OrderDetail;
import service.OrderService;
import util.DBConnection;
import util.OrderNoUtil;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao = new OrderDaoImpl();
    private final OrderDetailDao detailDao = new OrderDetailDaoImpl();

    @Override
    public String checkout(Order order, List<OrderDetail> details) throws Exception {
        if (order == null) throw new Exception("訂單資料不可空白");
        if (details == null || details.isEmpty()) throw new Exception("請先加入商品到訂單明細");
        if (order.getMemberId() == null) throw new Exception("尚未登入，無法建立訂單");
        if (order.getOrderNo() == null || order.getOrderNo().trim().isEmpty()) order.setOrderNo(OrderNoUtil.generate());
        if (order.getDineType() == null || order.getDineType().trim().isEmpty()) order.setDineType("內用");
        if ("內用".equals(order.getDineType()) && (order.getTableNo() == null || order.getTableNo().trim().isEmpty())) {
            throw new Exception("內用請選擇桌號");
        }
        int total = 0;
        for (OrderDetail d : details) total += d.getSubtotal();
        order.setTotal(total);
        order.setStatus("待製作");

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int orderId = orderDao.insert(conn, order);
                order.setOrderId(orderId);
                detailDao.insertBatch(conn, orderId, details);
                conn.commit();
                return order.getOrderNo();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Order> findByCriteria(String orderNo, String dineType, String status,
                                      LocalDate startDate, LocalDate endDate,
                                      Integer memberId) throws OrderException {
        try {
            return orderDao.findByCriteria(orderNo, dineType, status, startDate, endDate, memberId);
        } catch (Exception e) {
            throw new OrderException("查詢訂單失敗", e);
        }
    }

    @Override
    public List<OrderDetail> findDetailsByOrderId(int orderId) throws OrderException {
        try {
            return detailDao.findByOrderId(orderId);
        } catch (Exception e) {
            throw new OrderException("查詢訂單明細失敗", e);
        }
    }
}
