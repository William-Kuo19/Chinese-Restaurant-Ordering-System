package dao;

import java.sql.Connection;
import java.util.List;
import model.OrderDetail;

public interface OrderDetailDao {
    void insertBatch(Connection conn, int orderId, List<OrderDetail> details) throws Exception;
    List<OrderDetail> findByOrderId(int orderId) throws Exception;
}
