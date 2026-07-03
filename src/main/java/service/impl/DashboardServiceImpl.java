package service.impl;

import dao.OrderDao;
import dao.impl.OrderDaoImpl;
import model.Dashboard;
import service.DashboardService;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {
    private final MemberServiceImpl memberService = new MemberServiceImpl();
    private final ProductServiceImpl productService = new ProductServiceImpl();
    private final OrderDao orderDao = new OrderDaoImpl();

    public Dashboard getDashboard() {
        Dashboard d = new Dashboard();
        d.setMemberCount(memberService.countMembers());
        d.setProductCount(productService.countProducts());
        d.setTodayOrderCount(orderDao.countTodayOrders());
        d.setTodayRevenue(orderDao.sumTodayRevenue());
        return d;
    }

    public List<String[]> getLatestOrders() { return orderDao.findLatestOrders(5); }
}
