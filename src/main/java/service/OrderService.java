package service;

import java.time.LocalDate;
import java.util.List;
import model.Order;
import model.OrderDetail;
import exception.OrderException;

public interface OrderService {
    String checkout(Order order, List<OrderDetail> details) throws Exception;

    List<Order> findByCriteria(String orderNo, String dineType, String status,
                               LocalDate startDate, LocalDate endDate,
                               Integer memberId) throws OrderException;

    List<OrderDetail> findDetailsByOrderId(int orderId) throws OrderException;
}
