package controller;

import exception.OrderException;
import model.Member;
import model.Order;
import model.OrderDetail;
import service.OrderService;
import service.impl.OrderServiceImpl;
import util.LoginSession;
import util.PdfUtil;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrderUI extends JPanel {
    private final OrderService orderService = new OrderServiceImpl();
    private final boolean onlyMine;

    private JTextField txtOrderNo;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JComboBox<String> cmbDineType;
    private JComboBox<String> cmbStatus;
    private JTable tblOrders;
    private DefaultTableModel orderModel;
    private List<Order> currentOrders = new ArrayList<>();

    public OrderUI() {
        this(false);
    }

    public OrderUI(boolean onlyMine) {
        this.onlyMine = onlyMine;
        initComponents();
        queryOrders();
    }

    private void initComponents() {
        setLayout(new BorderLayout(18, 18));
        setBackground(UIStyle.BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setBackground(UIStyle.BG);
        JLabel title = new JLabel(onlyMine ? "我的訂單" : "訂單查詢");
        title.setFont(UIStyle.TITLE);
        title.setForeground(UIStyle.TEXT);
        top.add(title, BorderLayout.NORTH);
        top.add(createSearchPanel(), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(UIStyle.formBorder("查詢條件"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtOrderNo = UIStyle.textField();
        txtStartDate = UIStyle.textField();
        txtEndDate = UIStyle.textField();
        txtStartDate.setToolTipText("格式：yyyy-MM-dd，例如 2026-07-03");
        txtEndDate.setToolTipText("格式：yyyy-MM-dd，例如 2026-07-03");
        cmbDineType = new JComboBox<>(new String[]{"全部", "內用", "外帶"});
        cmbStatus = new JComboBox<>(new String[]{"全部", "待製作", "製作中", "已完成", "已取消"});
        UIStyle.styleComboBox(cmbDineType);
        UIStyle.styleComboBox(cmbStatus);

        addField(panel, gbc, 0, 0, "訂單編號：", txtOrderNo);
        addField(panel, gbc, 2, 0, "用餐方式：", cmbDineType);
        addField(panel, gbc, 4, 0, "狀態：", cmbStatus);
        addField(panel, gbc, 0, 1, "開始日期：", txtStartDate);
        addField(panel, gbc, 2, 1, "結束日期：", txtEndDate);

        JButton btnToday = UIStyle.lightButton("今天");
        btnToday.addActionListener(e -> {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            txtStartDate.setText(today);
            txtEndDate.setText(today);
            queryOrders();
        });
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(btnToday, gbc);

        JButton btnQuery = UIStyle.primaryButton("查詢");
        btnQuery.addActionListener(e -> queryOrders());
        gbc.gridx = 5;
        gbc.gridy = 1;
        panel.add(btnQuery, gbc);

        txtOrderNo.addActionListener(e -> queryOrders());
        txtStartDate.addActionListener(e -> queryOrders());
        txtEndDate.addActionListener(e -> queryOrders());
        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int x, int y, String label, JComponent field) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        panel.add(UIStyle.label(label), gbc);
        gbc.gridx = x + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JScrollPane createTablePanel() {
        orderModel = new DefaultTableModel(new Object[]{"訂單編號", "用餐方式", "桌號", "狀態", "總金額", "時間", "備註"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblOrders = new JTable(orderModel);
        UIStyle.styleTable(tblOrders);
        UIStyle.setColumnWidths(tblOrders, 170, 90, 80, 90, 90, 150, 220);
        tblOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alignNumberColumn(tblOrders, 4);
        tblOrders.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openDetailDialog();
            }
        });
        return UIStyle.cleanScrollPane(tblOrders);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(UIStyle.BG);
        JButton btnDetail = UIStyle.primaryButton("查看明細");
        btnDetail.addActionListener(e -> openDetailDialog());
        JButton btnPdf = UIStyle.lightButton("重印");
        btnPdf.addActionListener(e -> reprintPdf());
        JButton btnRefresh = UIStyle.lightButton("重新整理");
        btnRefresh.addActionListener(e -> queryOrders());
        panel.add(btnDetail);
        panel.add(btnPdf);
        panel.add(btnRefresh);
        return panel;
    }

    private void queryOrders() {
        try {
            Integer memberId = null;
            if (onlyMine || !LoginSession.isAdmin()) {
                Member m = LoginSession.getCurrentUser();
                memberId = m == null ? null : m.getMemberId();
            }
            currentOrders = orderService.findByCriteria(
                    txtOrderNo == null ? "" : txtOrderNo.getText().trim(),
                    normalizeCombo(cmbDineType),
                    normalizeCombo(cmbStatus),
                    parseDate(txtStartDate == null ? "" : txtStartDate.getText().trim()),
                    parseDate(txtEndDate == null ? "" : txtEndDate.getText().trim()),
                    memberId
            );
            renderOrders();
        } catch (OrderException ex) {
            UIStyle.showError(this, ex.getMessage());
        } catch (Exception ex) {
            UIStyle.showError(this, "查詢條件錯誤：" + ex.getMessage());
        }
    }

    private void renderOrders() {
        orderModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Order o : currentOrders) {
            orderModel.addRow(new Object[]{
                    o.getOrderNo(),
                    o.getDineType(),
                    o.getTableNo() == null ? "" : o.getTableNo(),
                    o.getStatus(),
                    o.getTotal(),
                    o.getOrderTime() == null ? "" : o.getOrderTime().format(fmt),
                    o.getRemark() == null ? "" : o.getRemark()
            });
        }
    }

    private void openDetailDialog() {
        Order order = getSelectedOrder();
        if (order == null) return;
        try {
            List<OrderDetail> details = orderService.findDetailsByOrderId(order.getOrderId());
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "訂單明細 - " + order.getOrderNo(), Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new BorderLayout(12, 12));
            ((JComponent) dialog.getContentPane()).setBorder(new EmptyBorder(16, 16, 16, 16));

            JLabel header = UIStyle.label("訂單編號：" + order.getOrderNo()
                    + "　用餐方式：" + safe(order.getDineType())
                    + "　桌號：" + safe(order.getTableNo())
                    + "　備註：" + safe(order.getRemark()));
            dialog.add(header, BorderLayout.NORTH);

            DefaultTableModel detailModel = new DefaultTableModel(new Object[]{"品項", "數量", "單價", "小計"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (OrderDetail d : details) {
                detailModel.addRow(new Object[]{d.getProductName(), d.getQty(), d.getPrice(), d.getSubtotal()});
            }
            JTable tblDetail = new JTable(detailModel);
            UIStyle.styleTable(tblDetail);
            UIStyle.setColumnWidths(tblDetail, 280, 80, 100, 110);
            alignNumberColumn(tblDetail, 1);
            alignNumberColumn(tblDetail, 2);
            alignNumberColumn(tblDetail, 3);
            dialog.add(UIStyle.cleanScrollPane(tblDetail), BorderLayout.CENTER);

            JLabel total = new JLabel("總金額：NT$ " + order.getTotal(), SwingConstants.RIGHT);
            total.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
            dialog.add(total, BorderLayout.SOUTH);

            dialog.setSize(820, 460);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception ex) {
            UIStyle.showError(this, "載入訂單明細失敗：" + ex.getMessage());
        }
    }

    private void reprintPdf() {
        Order order = getSelectedOrder();
        if (order == null) return;
        try {
            List<OrderDetail> details = orderService.findDetailsByOrderId(order.getOrderId());
            File file = PdfUtil.choosePdfFile(this);
            if (file != null) {
                PdfUtil.exportOrder(file, order, details);
                UIStyle.showInfo(this, "重印成功");
            }
        } catch (Exception ex) {
            UIStyle.showError(this, "重印失敗：" + ex.getMessage());
        }
    }

    private Order getSelectedOrder() {
        int row = tblOrders.getSelectedRow();
        if (row < 0) {
            UIStyle.showError(this, "請先選擇一筆訂單");
            return null;
        }
        int modelRow = tblOrders.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= currentOrders.size()) return null;
        return currentOrders.get(modelRow);
    }

    private String normalizeCombo(JComboBox<String> combo) {
        if (combo == null || combo.getSelectedItem() == null) return "";
        String value = combo.getSelectedItem().toString();
        return "全部".equals(value) ? "" : value;
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(text.trim(), DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("日期格式需為 yyyy-MM-dd");
        }
    }

    private void alignNumberColumn(JTable table, int column) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(column).setCellRenderer(right);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
