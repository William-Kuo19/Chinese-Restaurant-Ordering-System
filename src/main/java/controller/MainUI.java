package controller;

import model.Dashboard;
import model.Member;
import service.DashboardService;
import service.impl.DashboardServiceImpl;
import util.LoginSession;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainUI extends JFrame {
    private JLabel lblClock;
    private JPanel contentPanel;
    private final DashboardService dashboardService = new DashboardServiceImpl();

    public MainUI() {
        initComponents();
        startClock();
    }

    private void initComponents() {
        UIStyle.applyFrame(this, "首頁", 1320, 820);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.BG);
        setContentPane(root);

        root.add(createSidebar(), BorderLayout.WEST);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIStyle.BG);
        root.add(contentPanel, BorderLayout.CENTER);
        showDashboard();
    }

    private JPanel createSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(270, 820));
        side.setBackground(UIStyle.SIDEBAR);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(30, 22, 30, 22));

        JLabel logo = new JLabel("<html><b>中式餐廳</b><br/><span style='font-size:12px'>點餐管理系統</span></html>");
        logo.setForeground(Color.WHITE);
        logo.setFont(UIStyle.BRAND_TITLE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(logo);
        side.add(Box.createVerticalStrut(30));

        addMenu(side, "🏠  首頁", e -> showDashboard());
        if (LoginSession.isAdmin()) {
            addMenu(side, "👥  會員管理", e -> showPanel(new MemberUI(), "會員管理"));
            addMenu(side, "🍜  商品管理", e -> showPanel(new ProductUI(), "商品管理"));
            addMenu(side, "🛒  點餐", e -> showPanel(new ShoppingUI(this::showDashboard), "點餐"));
            addMenu(side, "🧾  訂單查詢", e -> showPanel(new OrderUI(false), "訂單查詢"));
        } else {
            addMenu(side, "🛒  點餐", e -> showPanel(new ShoppingUI(this::showDashboard), "點餐"));
            addMenu(side, "🧾  我的訂單", e -> showPanel(new OrderUI(true), "我的訂單"));
        }
        side.add(Box.createVerticalGlue());
        JButton btnLogout = sidebarButton("🚪  登出");
        btnLogout.addActionListener(e -> {
            LoginSession.logout();
            new LoginUI().setVisible(true);
            dispose();
        });
        side.add(btnLogout);
        return side;
    }

    private void addMenu(JPanel side, String text, java.awt.event.ActionListener listener) {
        JButton btn = sidebarButton(text);
        btn.addActionListener(listener);
        side.add(btn);
        side.add(Box.createVerticalStrut(10));
    }

    private JButton sidebarButton(String text) {
        JButton btn = UIStyle.sidebarButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void showPanel(JPanel panel, String title) {
        setTitle("中式餐館點餐管理系統－" + title);
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showDashboard() {
        setTitle("中式餐館點餐管理系統－首頁");
        contentPanel.removeAll();
        contentPanel.add(createDashboard(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createDashboard() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(UIStyle.BG);
        main.setBorder(new EmptyBorder(32, 36, 32, 36));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIStyle.BG);
        JLabel title = new JLabel("營運總覽");
        title.setFont(UIStyle.TITLE);
        title.setForeground(UIStyle.TEXT);
        top.add(title, BorderLayout.WEST);
        lblClock = new JLabel();
        lblClock.setFont(UIStyle.FONT_BOLD);
        lblClock.setForeground(UIStyle.TEXT);
        top.add(lblClock, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);

        Dashboard d = dashboardService.getDashboard();
        JPanel center = new JPanel(new GridLayout(2, 2, 18, 18));
        center.setBackground(UIStyle.BG);
        center.add(card("💰 今日營業額", "$ " + d.getTodayRevenue()));
        center.add(card("🧾 今日訂單", d.getTodayOrderCount() + " 筆"));
        center.add(card("🍜 商品總數", d.getProductCount() + " 項"));
        center.add(card("👥 會員總數", d.getMemberCount() + " 人"));
        main.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 18, 0));
        bottom.setBackground(UIStyle.BG);
        bottom.add(loginInfoCard());
        bottom.add(latestOrdersCard());
        main.add(bottom, BorderLayout.SOUTH);
        return main;
    }

    private JPanel card(String title, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIStyle.CARD);
        p.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(UIStyle.CARD_SHADOW, 1, true), new EmptyBorder(24, 28, 24, 28)));
        JLabel t = new JLabel(title);
        t.setFont(UIStyle.FONT_BOLD);
        t.setForeground(UIStyle.MUTED);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Microsoft JhengHei", Font.BOLD, 34));
        v.setForeground(UIStyle.TEXT);
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel loginInfoCard() {
        Member m = LoginSession.getCurrentUser();
        JPanel p = new JPanel(new GridLayout(4, 1, 0, 4));
        p.setBackground(UIStyle.CARD);
        p.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(UIStyle.CARD_SHADOW, 1, true), new EmptyBorder(18, 24, 18, 24)));
        p.add(UIStyle.label("目前登入者：" + (m == null ? "" : m.getUsername())));
        p.add(UIStyle.label("身分：" + (m != null && "ADMIN".equalsIgnoreCase(m.getRole()) ? "管理員" : "一般會員")));
        LocalDateTime time = LoginSession.getLoginTime();
        p.add(UIStyle.label("登入時間：" + (time == null ? "" : time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")))));
        p.add(UIStyle.label("今日日期：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))));
        return p;
    }

    private JPanel latestOrdersCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIStyle.CARD);
        p.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(UIStyle.CARD_SHADOW, 1, true), new EmptyBorder(18, 24, 18, 24)));
        JLabel title = new JLabel("📋 最新訂單");
        title.setFont(UIStyle.FONT_BOLD);
        title.setForeground(UIStyle.TEXT);
        p.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"訂單編號", "類型", "桌號", "金額"}, 0);
        List<String[]> rows = dashboardService.getLatestOrders();
        for (String[] r : rows) model.addRow(r);
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        UIStyle.setColumnWidths(table, 180, 80, 80, 90);
        p.add(UIStyle.cleanScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            if (lblClock != null) lblClock.setText("目前時間：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        timer.start();
    }
}
