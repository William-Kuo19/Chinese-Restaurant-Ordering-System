package controller;

/**
 * Chinese Restaurant Ordering System
 *
 * Class : ShoppingUI
 * Description : 點餐主畫面，包含商品分類、商品清單、購物車與結帳流程；套用 Professional POS UI。
 *
 * Author : 郭元吉
 */

import model.Member;
import model.Order;
import model.OrderDetail;
import model.Product;
import service.OrderService;
import service.ProductService;
import service.impl.OrderServiceImpl;
import service.impl.ProductServiceImpl;
import util.LoginSession;
import util.PdfUtil;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.text.NumberFormat;

public class ShoppingUI extends JPanel {
    private static final String ORDER_DETAIL_TITLE = "訂單明細";
    private static final List<String> POS_CATEGORY_ORDER = Arrays.asList("全部", "套餐", "主食", "麵類", "小菜", "湯品", "飲料", "甜點");

    private final ProductService productService = new ProductServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();

    private JTable tblProduct;
    private JTable tblCart;
    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbTableNo;
    private JTextArea txtRemark;
    private JPanel categoryPanel;
    private JLabel lblTotal;
    private JLabel lblSelectedItem;
    private JLabel lblSelectedQty;
    private final NumberFormat currencyFormat = NumberFormat.getIntegerInstance();
    private JRadioButton rdoDineIn;
    private JRadioButton rdoTakeOut;

    private String currentCategory = "全部";
    private final List<OrderDetail> cart = new ArrayList<>();
    private final Runnable onExit;

    public ShoppingUI() {
        this(null);
    }

    public ShoppingUI(Runnable onExit) {
        this.onExit = onExit;
        initComponents();
        loadCategories();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(18, 18));
        setBackground(UIStyle.BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("🛒 POS 點餐作業");
        title.setFont(UIStyle.TITLE);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createProductPanel(), createCartPanel());
        split.setResizeWeight(0.58);
        split.setDividerSize(8);
        split.setBorder(null);
        split.setOneTouchExpandable(false);
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.58));
        add(split, BorderLayout.CENTER);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(UIStyle.BG);
        panel.setMinimumSize(new Dimension(520, 420));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(UIStyle.BG);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(UIStyle.BG);
        searchPanel.add(UIStyle.sectionTitle("商品選擇"), BorderLayout.WEST);
        txtSearch = UIStyle.textField();
        txtSearch.addActionListener(e -> loadProducts());
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        JButton btnSearch = UIStyle.secondaryButton("搜尋");
        btnSearch.addActionListener(e -> loadProducts());
        searchPanel.add(btnSearch, BorderLayout.EAST);
        top.add(searchPanel, BorderLayout.NORTH);

        categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        categoryPanel.setBackground(UIStyle.BG);
        JScrollPane categoryScroll = new JScrollPane(
                categoryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        categoryScroll.setBorder(null);
        categoryScroll.setPreferredSize(new Dimension(0, 44));
        categoryScroll.getHorizontalScrollBar().setUnitIncrement(16);
        top.add(categoryScroll, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        productModel = createProductTableModel(false);
        tblProduct = new JTable(productModel);
        UIStyle.styleTable(tblProduct);
        tblProduct.setRowHeight(34);
        tblProduct.setIntercellSpacing(new Dimension(0, 4));
        tblProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applyProductTableColumnWidths(false);
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSelectedProductToCartOne();
                }
            }
        });
        JScrollPane productScroll = new JScrollPane(
                tblProduct,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        productScroll.getVerticalScrollBar().setUnitIncrement(18);
        productScroll.getHorizontalScrollBar().setUnitIncrement(18);
        productScroll.setBorder(new javax.swing.border.LineBorder(UIStyle.BORDER, 1, true));
        panel.add(productScroll, BorderLayout.CENTER);

        JLabel hint = UIStyle.label("操作提示：雙擊商品即可加入 1 份；數量請在右側訂單明細調整。");
        hint.setForeground(UIStyle.MUTED);
        panel.add(hint, BorderLayout.SOUTH);
        return panel;
    }

    private JComponent createCartPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(UIStyle.formBorder("🧾 " + ORDER_DETAIL_TITLE));
        content.setPreferredSize(new Dimension(440, 760));

        cartModel = new DefaultTableModel(new Object[]{"品項", "數量", "單價", "小計"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCart = new JTable(cartModel);
        UIStyle.styleTable(tblCart);
        UIStyle.setColumnWidths(tblCart, 170, 60, 80, 90);
        tblCart.setIntercellSpacing(new Dimension(0, 3));
        tblCart.setAutoCreateRowSorter(false);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCart.setFillsViewportHeight(true);
        tblCart.setRowHeight(30);
        alignNumberColumns(tblCart);
        tblCart.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedCartInfo();
        });

        JScrollPane cartScroll = new JScrollPane(
                tblCart,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        cartScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartScroll.setPreferredSize(new Dimension(420, 245));
        cartScroll.setMinimumSize(new Dimension(420, 210));
        cartScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        content.add(cartScroll);
        content.add(Box.createVerticalStrut(10));

        JComponent qtyPanel = createQuantityPanel();
        qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(qtyPanel);
        content.add(Box.createVerticalStrut(8));

        lblTotal = new JLabel("應付金額：NT$ 0", SwingConstants.CENTER);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTotal.setFont(UIStyle.BIG_NUMBER);
        lblTotal.setForeground(UIStyle.PRIMARY);
        lblTotal.setOpaque(true);
        lblTotal.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(UIStyle.SECONDARY, 1, true), new EmptyBorder(10, 0, 10, 0)));
        lblTotal.setPreferredSize(new Dimension(420, 58));
        lblTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        content.add(lblTotal);

        JComponent orderInfo = createOrderInfoPanel();
        orderInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(orderInfo);
        content.add(Box.createVerticalStrut(8));

        JComponent buttons = createActionButtons();
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(buttons);

        JScrollPane wrapper = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        wrapper.setBorder(null);
        wrapper.setPreferredSize(new Dimension(460, 0));
        wrapper.setMinimumSize(new Dimension(430, 420));
        wrapper.getVerticalScrollBar().setUnitIncrement(18);
        return wrapper;
    }

    private JPanel createQuantityPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(UIStyle.formBorder("修改數量"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

        lblSelectedItem = UIStyle.label("目前餐點：未選擇");
        lblSelectedItem.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        lblSelectedItem.setBorder(new EmptyBorder(2, 4, 4, 4));
        panel.add(lblSelectedItem, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 4));
        controls.setBackground(Color.WHITE);
        JButton btnMinus = UIStyle.lightButton("-");
        JButton btnPlus = UIStyle.lightButton("+");
        btnMinus.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        btnPlus.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        btnMinus.setPreferredSize(new Dimension(58, 42));
        btnPlus.setPreferredSize(new Dimension(58, 42));
        lblSelectedQty = new JLabel("0", SwingConstants.CENTER);
        lblSelectedQty.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
        lblSelectedQty.setOpaque(true);
        lblSelectedQty.setBackground(Color.WHITE);
        lblSelectedQty.setBorder(new javax.swing.border.LineBorder(UIStyle.BORDER, 1, true));
        lblSelectedQty.setPreferredSize(new Dimension(76, 42));

        btnMinus.addActionListener(e -> decreaseSelectedCartQty());
        btnPlus.addActionListener(e -> increaseSelectedCartQty());
        controls.add(btnMinus);
        controls.add(lblSelectedQty);
        controls.add(btnPlus);
        panel.add(controls, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOrderInfoPanel() {
        JPanel orderInfo = new JPanel();
        orderInfo.setLayout(new BoxLayout(orderInfo, BoxLayout.Y_AXIS));
        orderInfo.setBackground(Color.WHITE);
        orderInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        typePanel.setBackground(Color.WHITE);
        rdoDineIn = new JRadioButton("內用", true);
        rdoTakeOut = new JRadioButton("外帶");
        rdoDineIn.setFont(UIStyle.FONT);
        rdoTakeOut.setFont(UIStyle.FONT);
        rdoDineIn.setBackground(Color.WHITE);
        rdoTakeOut.setBackground(Color.WHITE);
        ButtonGroup group = new ButtonGroup();
        group.add(rdoDineIn);
        group.add(rdoTakeOut);
        rdoDineIn.addActionListener(e -> setTableNoEnabled(true));
        rdoTakeOut.addActionListener(e -> setTableNoEnabled(false));
        typePanel.add(rdoDineIn);
        typePanel.add(rdoTakeOut);
        orderInfo.add(typePanel);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(UIStyle.label("桌號："));
        cmbTableNo = new JComboBox<>();
        for (int i = 1; i <= 20; i++) cmbTableNo.addItem(String.format("A%02d", i));
        UIStyle.styleComboBox(cmbTableNo);
        cmbTableNo.setPreferredSize(new Dimension(130, 34));
        tablePanel.add(cmbTableNo);
        orderInfo.add(tablePanel);

        orderInfo.add(UIStyle.label("備註："));
        txtRemark = new JTextArea(2, 20);
        txtRemark.setFont(UIStyle.FONT);
        txtRemark.setLineWrap(true);
        txtRemark.setWrapStyleWord(true);
        txtRemark.setToolTipText("例如：少辣、不加蔥、外帶不用餐具");
        txtRemark.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(UIStyle.INPUT_BORDER, 1, true), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane remarkScroll = new JScrollPane(txtRemark);
        remarkScroll.setPreferredSize(new Dimension(410, 48));
        remarkScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        orderInfo.add(remarkScroll);
        JLabel hint = UIStyle.label("例如：少辣 / 不加蔥 / 外帶不用餐具");
        hint.setForeground(UIStyle.MUTED);
        orderInfo.add(hint);
        return orderInfo;
    }

    private JPanel createActionButtons() {
        JPanel buttons = new JPanel(new BorderLayout(8, 8));
        buttons.setBackground(Color.WHITE);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        JPanel top = new JPanel(new GridLayout(1, 4, 8, 8));
        top.setBackground(Color.WHITE);
        JButton btnRemove = UIStyle.dangerButton("刪除品項");
        btnRemove.addActionListener(e -> removeSelectedCartItem());
        JButton btnClear = UIStyle.lightButton("清空訂單");
        btnClear.addActionListener(e -> clearCart());
        JButton btnPdf = UIStyle.woodButton("列印");
        btnPdf.addActionListener(e -> exportPdfOnly());
        JButton btnExit = UIStyle.lightButton("離開");
        btnExit.addActionListener(e -> exitShopping());
        top.add(btnRemove);
        top.add(btnClear);
        top.add(btnPdf);
        top.add(btnExit);

        JButton btnCheckout = UIStyle.primaryButton("結帳");
        btnCheckout.setFont(UIStyle.CHECKOUT_BUTTON);
        btnCheckout.setPreferredSize(UIStyle.CHECKOUT_BUTTON_SIZE);
        btnCheckout.setToolTipText("送出目前訂單並建立訂單資料");
        btnCheckout.addActionListener(e -> checkout());

        buttons.add(top, BorderLayout.CENTER);
        buttons.add(btnCheckout, BorderLayout.SOUTH);
        return buttons;
    }

    private void loadCategories() {
        categoryPanel.removeAll();
        List<String> categories = new ArrayList<>();
        categories.add("全部");
        try {
            for (String category : productService.getActiveCategoryNamesForOrdering()) {
                if (category != null && category.trim().length() > 0 && !categories.contains(category.trim())) {
                    categories.add(category.trim());
                }
            }
        } catch (Exception ex) {
            UIStyle.showError(this, "載入商品分類失敗：" + ex.getMessage());
        }

        categories.sort(Comparator
                .comparingInt((String c) -> categoryOrderIndex(c))
                .thenComparing(String::compareTo));

        if (!categories.contains(currentCategory)) currentCategory = "全部";
        for (String category : categories) addCategoryButton(category);
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private int categoryOrderIndex(String categoryName) {
        int index = POS_CATEGORY_ORDER.indexOf(categoryName);
        return index >= 0 ? index : POS_CATEGORY_ORDER.size();
    }

    private void addCategoryButton(String categoryName) {
        JButton btn = categoryName.equals(currentCategory) ? UIStyle.primaryButton(categoryName) : UIStyle.lightButton(categoryName);
        btn.setFont(new Font("Microsoft JhengHei", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(Math.max(82, btn.getPreferredSize().width + 16), 34));
        btn.addActionListener(e -> {
            currentCategory = ((JButton) e.getSource()).getText();
            loadCategories();
            loadProducts();
        });
        categoryPanel.add(btn);
    }

    private void loadProducts() {
        boolean showDescription = "套餐".equals(currentCategory);
        productModel = createProductTableModel(showDescription);
        tblProduct.setModel(productModel);
        applyProductTableColumnWidths(showDescription);
        UIStyle.styleTable(tblProduct);
        tblProduct.setRowHeight(34);
        tblProduct.setIntercellSpacing(new Dimension(0, 4));

        List<Product> list = productService.getProductsForOrdering(currentCategory, txtSearch == null ? "" : txtSearch.getText());
        for (Product p : list) {
            if (showDescription) {
                productModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getCategoryName(), p.getPrice(), p.getDescription(), p.getStatus()});
            } else {
                productModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getCategoryName(), p.getPrice(), p.getStatus()});
            }
        }
    }

    private DefaultTableModel createProductTableModel(boolean showDescription) {
        Object[] columns = showDescription
                ? new Object[]{"商品編號", "商品名稱", "分類", "單價", "商品描述", "狀態"}
                : new Object[]{"商品編號", "商品名稱", "分類", "單價", "狀態"};

        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void applyProductTableColumnWidths(boolean showDescription) {
        if (tblProduct == null || tblProduct.getColumnModel().getColumnCount() == 0) return;
        tblProduct.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblProduct.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblProduct.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblProduct.getColumnModel().getColumn(3).setPreferredWidth(70);
        if (showDescription) {
            tblProduct.getColumnModel().getColumn(4).setPreferredWidth(260);
            tblProduct.getColumnModel().getColumn(5).setPreferredWidth(70);
        } else {
            tblProduct.getColumnModel().getColumn(4).setPreferredWidth(70);
        }
    }

    private void addSelectedProductToCartOne() {
        int row = tblProduct.getSelectedRow();
        if (row < 0) {
            UIStyle.showError(this, "請先選擇商品");
            return;
        }
        int modelRow = tblProduct.convertRowIndexToModel(row);
        int productId = Integer.parseInt(productModel.getValueAt(modelRow, 0).toString());
        String productName = productModel.getValueAt(modelRow, 1).toString();
        int price = Integer.parseInt(productModel.getValueAt(modelRow, 3).toString());

        int index = findCartIndexByProductId(productId);
        if (index >= 0) {
            OrderDetail d = cart.get(index);
            d.setQty(d.getQty() + 1);
            refreshCart(index);
        } else {
            cart.add(new OrderDetail(productId, productName, 1, price));
            refreshCart(cart.size() - 1);
        }
    }

    private int findCartIndexByProductId(int productId) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProductId() == productId) return i;
        }
        return -1;
    }

    private void increaseSelectedCartQty() {
        int index = getSelectedCartModelIndex();
        if (index < 0) {
            UIStyle.showError(this, "請先選擇要修改的餐點");
            return;
        }
        OrderDetail d = cart.get(index);
        d.setQty(d.getQty() + 1);
        refreshCart(index);
    }

    private void decreaseSelectedCartQty() {
        int index = getSelectedCartModelIndex();
        if (index < 0) {
            UIStyle.showError(this, "請先選擇要修改的餐點");
            return;
        }
        OrderDetail d = cart.get(index);
        if (d.getQty() <= 1) {
            if (UIStyle.confirm(this, "是否刪除此商品？")) {
                cart.remove(index);
                refreshCart(Math.min(index, cart.size() - 1));
            }
            return;
        }
        d.setQty(d.getQty() - 1);
        refreshCart(index);
    }

    private int getSelectedCartModelIndex() {
        int row = tblCart.getSelectedRow();
        if (row < 0) return -1;
        return tblCart.convertRowIndexToModel(row);
    }

    private void updateSelectedCartInfo() {
        int index = getSelectedCartModelIndex();
        if (index < 0 || index >= cart.size()) {
            lblSelectedItem.setText("目前餐點：未選擇");
            lblSelectedQty.setText("0");
            return;
        }
        OrderDetail d = cart.get(index);
        lblSelectedItem.setText("目前餐點：" + d.getProductName());
        lblSelectedQty.setText(String.valueOf(d.getQty()));
    }

    private void removeSelectedCartItem() {
        int index = getSelectedCartModelIndex();
        if (index < 0) {
            UIStyle.showError(this, "請先選擇要刪除的品項");
            return;
        }
        if (UIStyle.confirm(this, "確定要刪除此品項嗎？")) {
            cart.remove(index);
            refreshCart(Math.min(index, cart.size() - 1));
        }
    }

    private void clearCart() {
        if (!cart.isEmpty() && UIStyle.confirm(this, "確定要清空訂單嗎？")) {
            cart.clear();
            refreshCart(-1);
        }
    }

    private void refreshCart() {
        refreshCart(-1);
    }

    private void refreshCart(int selectIndex) {
        cartModel.setRowCount(0);
        for (OrderDetail d : cart) {
            cartModel.addRow(new Object[]{d.getProductName(), d.getQty(), money(d.getPrice()), money(d.getSubtotal())});
        }
        cartModel.fireTableDataChanged();
        updateTotal();
        if (selectIndex >= 0 && selectIndex < cart.size()) {
            tblCart.setRowSelectionInterval(selectIndex, selectIndex);
            tblCart.scrollRectToVisible(tblCart.getCellRect(selectIndex, 0, true));
        } else {
            tblCart.clearSelection();
            updateSelectedCartInfo();
        }
        tblCart.revalidate();
        tblCart.repaint();
    }

    private void updateTotal() {
        lblTotal.setText("應付金額：" + money(calcTotal()));
        updateSelectedCartInfo();
    }

    private String money(Integer amount) {
        int value = amount == null ? 0 : amount;
        return "NT$ " + currencyFormat.format(value);
    }

    private int calcTotal() {
        int total = 0;
        for (OrderDetail d : cart) total += d.getSubtotal();
        return total;
    }

    private Order buildOrder() {
        Member m = LoginSession.getCurrentUser();
        Order order = new Order();
        order.setMemberId(m == null ? null : m.getMemberId());
        order.setDineType(rdoDineIn.isSelected() ? "內用" : "外帶");
        order.setTableNo(rdoDineIn.isSelected() ? String.valueOf(cmbTableNo.getSelectedItem()) : null);
        order.setRemark(txtRemark.getText().trim());
        order.setTotal(calcTotal());
        return order;
    }

    private void checkout() {
        if (cart.isEmpty()) {
            UIStyle.showError(this, "訂單明細沒有商品，無法結帳");
            return;
        }
        if (rdoDineIn.isSelected() && cmbTableNo.getSelectedItem() == null) {
            UIStyle.showError(this, "內用請選擇桌號");
            return;
        }

        if (!showCheckoutDetailDialog()) return;

        try {
            Order order = buildOrder();
            String orderNo = orderService.checkout(order, new ArrayList<>(cart));
            order.setOrderNo(orderNo);
            UIStyle.showInfo(this, "結帳成功，訂單編號：" + orderNo);

            if (UIStyle.confirm(this, "是否列印訂單？")) {
                File file = PdfUtil.choosePdfFile(this);
                if (file != null) {
                    PdfUtil.exportOrder(file, order, new ArrayList<>(cart));
                    UIStyle.showInfo(this, "訂單已建立並列印。\n檔案位置：\n" + file.getAbsolutePath());
                }
            }

            cart.clear();
            refreshCart();
            txtRemark.setText("");
        } catch (Exception ex) {
            UIStyle.showError(this, ex.getMessage());
        }
    }

    private boolean showCheckoutDetailDialog() {
        final boolean[] confirmed = {false};
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "訂單明細", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(14, 14));
        dialog.getContentPane().setBackground(UIStyle.BG);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Color.WHITE);
        content.setBorder(UIStyle.formBorder("訂單明細確認"));

        JLabel title = new JLabel("請確認訂單明細", SwingConstants.CENTER);
        title.setFont(UIStyle.TITLE);
        title.setForeground(UIStyle.TEXT);
        content.add(title, BorderLayout.NORTH);

        DefaultTableModel detailModel = new DefaultTableModel(new Object[]{"品項", "數量", "單價", "小計"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (OrderDetail d : cart) {
            detailModel.addRow(new Object[]{d.getProductName(), d.getQty(), money(d.getPrice()), money(d.getSubtotal())});
        }
        JTable detailTable = new JTable(detailModel);
        UIStyle.styleTable(detailTable);
        UIStyle.setColumnWidths(detailTable, 260, 80, 100, 110);
        detailTable.setIntercellSpacing(new Dimension(0, 3));
        detailTable.setAutoCreateRowSorter(false);
        detailTable.setRowHeight(32);
        alignNumberColumns(detailTable);
        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        scrollPane.setPreferredSize(new Dimension(520, 260));
        content.add(scrollPane, BorderLayout.CENTER);

        JLabel total = new JLabel("應付金額：" + money(calcTotal()), SwingConstants.RIGHT);
        total.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
        total.setForeground(UIStyle.TEXT);
        total.setBorder(new EmptyBorder(8, 0, 0, 0));
        content.add(total, BorderLayout.SOUTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setBackground(UIStyle.BG);
        buttons.setBorder(new EmptyBorder(0, 0, 16, 0));
        JButton btnBack = UIStyle.lightButton("返回修改");
        JButton btnConfirm = UIStyle.primaryButton("確認結帳");
        btnBack.setPreferredSize(new Dimension(126, 38));
        btnConfirm.setPreferredSize(new Dimension(126, 38));
        dialog.getRootPane().setDefaultButton(btnConfirm);
        dialog.getRootPane().registerKeyboardAction(
                e -> { confirmed[0] = false; dialog.dispose(); },
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        btnBack.addActionListener(e -> {
            confirmed[0] = false;
            dialog.dispose();
        });
        btnConfirm.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        buttons.add(btnBack);
        buttons.add(btnConfirm);

        dialog.add(content, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(620, 460));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return confirmed[0];
    }

    private void exportPdfOnly() {
        if (cart.isEmpty()) {
            UIStyle.showError(this, "訂單明細沒有商品，無法列印");
            return;
        }
        try {
            Order order = buildOrder();
            order.setOrderNo("尚未結帳");
            File file = PdfUtil.choosePdfFile(this);
            if (file != null) {
                PdfUtil.exportOrder(file, order, new ArrayList<>(cart));
                UIStyle.showInfo(this, "列印成功");
            }
        } catch (Exception ex) {
            UIStyle.showError(this, "列印失敗：" + ex.getMessage());
        }
    }

    private void setTableNoEnabled(boolean enabled) {
        cmbTableNo.setEnabled(enabled);
        if (enabled && cmbTableNo.getSelectedItem() == null) cmbTableNo.setSelectedIndex(0);
    }

    private void exitShopping() {
        if (!cart.isEmpty() && !UIStyle.confirm(this, "目前訂單尚未結帳，確定要離開嗎？")) return;
        if (onExit != null) onExit.run();
    }

    private void alignNumberColumns(JTable table) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(right);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(3).setCellRenderer(right);
    }
}
