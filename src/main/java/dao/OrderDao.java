package dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import model.Order;

public interface OrderDao {
    int countTodayOrders();
    int sumTodayRevenue();
    List<String[]> findLatestOrders(int limit);
    int insert(Connection conn, Order order) throws Exception;

    List<Order> findByCriteria(String orderNo, String dineType, String status,
                               LocalDate startDate, LocalDate endDate,
                               Integer memberId) throws Exception;
}
