package controller;

import model.Category;
import model.Product;
import service.CategoryService;
import service.ProductService;
import service.impl.CategoryServiceImpl;
import service.impl.ProductServiceImpl;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class ProductUI extends JPanel {
    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private JTable tblProduct;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtPrice, txtDescription, txtImage, txtSearch;
    private JComboBox<Category> cmbCategory;
    private JComboBox<String> cmbStatus;
    private JLabel lblImagePreview;
    private JLabel lblDescription;
    private final Map<Integer, Product> productMap = new HashMap<>();

    public ProductUI() {
        initComponents();
        loadCategories();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(18, 18));
        setBackground(UIStyle.BG);
        setBorder(new EmptyBorder(28, 32, 28, 32));
        JLabel title = new JLabel("商品管理");
        title.setFont(UIStyle.TITLE);
        title.setForeground(UIStyle.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(18, 18));
        center.setBackground(UIStyle.BG);
        center.add(createFormPanel(), BorderLayout.WEST);
        center.add(createTablePanel(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(370, 0));
        form.setBorder(UIStyle.formBorder("商品資料"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        txtId = UIStyle.textField(); txtId.setEditable(false); txtId.setText("自動產生"); txtId.setForeground(UIStyle.MUTED);
        txtName = UIStyle.textField();
        cmbCategory = new JComboBox<>(); cmbCategory.setFont(UIStyle.FONT);
        UIStyle.styleComboBox(cmbCategory);
        txtPrice = UIStyle.textField();
        txtDescription = UIStyle.textField();
        txtImage = UIStyle.textField();
        cmbStatus = new JComboBox<>(new String[]{"販售中", "停售"}); cmbStatus.setFont(UIStyle.FONT);
        UIStyle.styleComboBox(cmbStatus);

        int row = 0;
        row = addField(form, c, row, "商品編號：", txtId);
        row = addField(form, c, row, "商品名稱：*", txtName);
        row = addField(form, c, row, "分類：", cmbCategory);
        row = addField(form, c, row, "價格：*", txtPrice);
        row = addDescriptionField(form, c, row);
        row = addField(form, c, row, "圖片檔名", txtImage);
        lblImagePreview = new JLabel("圖片預覽", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(130, 90));
        lblImagePreview.setOpaque(true);
        lblImagePreview.setBackground(new Color(250, 248, 244));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(new Color(220, 210, 200)));
        lblImagePreview.setFont(UIStyle.FONT);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        form.add(lblImagePreview, c);
        row++;
        row = addField(form, c, row, "狀態：", cmbStatus);

        JPanel buttons = new JPanel(new GridLayout(2, 3, 8, 8));
        buttons.setBackground(Color.WHITE);
        JButton btnAdd = UIStyle.primaryButton("新增");
        JButton btnUpdate = UIStyle.primaryButton("修改");
        JButton btnDelete = UIStyle.lightButton("刪除");
        JButton btnClear = UIStyle.lightButton("清空");
        JButton btnRefresh = UIStyle.lightButton("重新整理");
        JButton btnExit = UIStyle.lightButton("離開");
        buttons.add(btnAdd); buttons.add(btnUpdate); buttons.add(btnDelete);
        buttons.add(btnClear); buttons.add(btnRefresh); buttons.add(btnExit);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        form.add(buttons, c);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); clearForm(); loadProducts(); });
        btnExit.addActionListener(e -> exitToDashboard());
        cmbCategory.addActionListener(e -> updateDescriptionVisibility());
        updateDescriptionVisibility();
        return form;
    }


    private int addDescriptionField(JPanel panel, GridBagConstraints c, int row) {
        lblDescription = UIStyle.label("套餐描述：");
        c.gridx = 0; c.gridy = row; c.gridwidth = 2; c.weightx = 1.0;
        c.insets = new Insets(8, 4, 3, 4);
        panel.add(lblDescription, c);
        c.gridy = row + 1;
        c.insets = new Insets(0, 4, 8, 4);
        panel.add(txtDescription, c);
        return row + 2;
    }

    private boolean isComboCategorySelected() {
        Object item = cmbCategory.getSelectedItem();
        if (item instanceof Category) {
            return "套餐".equals(((Category) item).getCategoryName());
        }
        return false;
    }

    private void updateDescriptionVisibility() {
        boolean visible = isComboCategorySelected();
        if (lblDescription != null) lblDescription.setVisible(visible);
        if (txtDescription != null) {
            txtDescription.setVisible(visible);
            if (!visible) txtDescription.setText("");
        }
        revalidate();
        repaint();
    }

    private int addField(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.gridwidth = 2; c.weightx = 1.0;
        c.insets = new Insets(8, 4, 3, 4);
        panel.add(UIStyle.label(label), c);
        c.gridy = row + 1;
        c.insets = new Insets(0, 4, 8, 4);
        panel.add(field, c);
        return row + 2;
    }

    private JPanel createTablePanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(UIStyle.BG);
        JPanel search = new JPanel(new BorderLayout(8, 8));
        search.setBackground(UIStyle.BG);
        txtSearch = UIStyle.textField();
        JButton btnSearch = UIStyle.primaryButton("搜尋");
        JButton btnRefresh = UIStyle.lightButton("重新整理");
        JPanel btns = new JPanel(new GridLayout(1, 2, 8, 0));
        btns.setBackground(UIStyle.BG);
        btns.add(btnSearch); btns.add(btnRefresh);
        search.add(UIStyle.label("搜尋商品："), BorderLayout.WEST);
        search.add(txtSearch, BorderLayout.CENTER);
        search.add(btns, BorderLayout.EAST);
        p.add(search, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"編號", "名稱", "分類", "價格", "圖片", "狀態"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProduct = new JTable(tableModel);
        UIStyle.styleTable(tblProduct);
        UIStyle.setColumnWidths(tblProduct, 70, 160, 100, 90, 150, 80);
        tblProduct.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
        tblProduct.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblProduct.getSelectedRow() >= 0) {
                    fillFormFromSelectedRow();
                    txtName.requestFocusInWindow();
                }
            }
        });
        p.add(UIStyle.cleanScrollPane(tblProduct), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> loadProducts(productService.searchProducts(txtSearch.getText())));
        txtSearch.addActionListener(e -> loadProducts(productService.searchProducts(txtSearch.getText())));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); clearForm(); loadProducts(); });
        return p;
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        for (Category c : categoryService.getAllCategories()) cmbCategory.addItem(c);
    }

    private void loadProducts() { loadProducts(productService.getAllProducts()); }

    private void loadProducts(List<Product> products) {
        tableModel.setRowCount(0);
        productMap.clear();
        for (Product p : products) {
            productMap.put(p.getProductId(), p);
            tableModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getCategoryName(), p.getPrice(), p.getImage(), p.getStatus()});
        }
    }

    private void fillFormFromSelectedRow() {
        int row = tblProduct.getSelectedRow();
        if (row < 0) return;
        int r = tblProduct.convertRowIndexToModel(row);
        txtId.setBackground(Color.WHITE);
        txtId.setForeground(UIStyle.TEXT);
        txtId.setText(String.valueOf(tableModel.getValueAt(r, 0)));
        txtName.setText(String.valueOf(tableModel.getValueAt(r, 1)));
        selectCategory(String.valueOf(tableModel.getValueAt(r, 2)));
        txtPrice.setText(String.valueOf(tableModel.getValueAt(r, 3)));
        Product selected = productMap.get(Integer.parseInt(String.valueOf(tableModel.getValueAt(r, 0))));
        txtDescription.setText(selected != null && selected.getDescription() != null ? selected.getDescription() : "");
        txtImage.setText(String.valueOf(tableModel.getValueAt(r, 4)));
        updateImagePreview();
        cmbStatus.setSelectedItem(String.valueOf(tableModel.getValueAt(r, 5)));
        updateDescriptionVisibility();
    }

    private void selectCategory(String categoryName) {
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            if (cmbCategory.getItemAt(i).getCategoryName().equals(categoryName)) {
                cmbCategory.setSelectedIndex(i);
                return;
            }
        }
    }

    private Product readProduct() throws NumberFormatException {
        Product p = new Product();
        String idText = txtId.getText().trim();
        if (!idText.isEmpty() && idText.matches("\\d+")) p.setProductId(Integer.parseInt(idText));
        p.setProductName(txtName.getText().trim());
        Category category = (Category) cmbCategory.getSelectedItem();
        if (category != null) {
            p.setCategoryId(category.getCategoryId());
            p.setCategoryName(category.getCategoryName());
        }
        p.setPrice(Integer.parseInt(txtPrice.getText().trim()));
        p.setDescription(isComboCategorySelected() ? txtDescription.getText().trim() : "");
        p.setImage(txtImage.getText().trim());
        p.setStatus(String.valueOf(cmbStatus.getSelectedItem()));
        return p;
    }

    private void addProduct() {
        try {
            if (!UIStyle.confirm(this, "確定要新增此商品嗎？")) return;
            String productName = txtName.getText().trim();
            productService.addProduct(readProduct());
            UIStyle.showInfo(this, "新增商品成功");
            loadProducts();
            selectProductByName(productName);
            clearForm();
            txtName.requestFocusInWindow();
        } catch (NumberFormatException ex) { UIStyle.showError(this, "價格必須是數字"); }
        catch (Exception ex) { UIStyle.showError(this, ex.getMessage()); }
    }

    private void updateProduct() {
        try {
            if (txtId.getText().trim().isEmpty() || !txtId.getText().trim().matches("\\d+")) { UIStyle.showError(this, "請先選擇商品"); return; }
            if (!UIStyle.confirm(this, "確定要修改此商品資料嗎？")) return;
            productService.updateProduct(readProduct());
            UIStyle.showInfo(this, "修改商品成功");
            clearForm(); loadProducts();
        } catch (NumberFormatException ex) { UIStyle.showError(this, "價格必須是數字"); }
        catch (Exception ex) { UIStyle.showError(this, ex.getMessage()); }
    }

    private void deleteProduct() {
        try {
            if (txtId.getText().trim().isEmpty() || !txtId.getText().trim().matches("\\d+")) { UIStyle.showError(this, "請先選擇商品"); return; }
            if (!UIStyle.confirm(this, "確定要刪除此商品嗎？")) return;
            productService.deleteProduct(Integer.parseInt(txtId.getText().trim()));
            UIStyle.showInfo(this, "刪除商品成功");
            clearForm(); loadProducts();
        } catch (Exception ex) { UIStyle.showError(this, ex.getMessage()); }
    }

    private void selectProductByName(String productName) {
        if (productName == null || productName.isEmpty()) return;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 1);
            if (productName.equals(String.valueOf(value))) {
                int viewRow = tblProduct.convertRowIndexToView(i);
                tblProduct.setRowSelectionInterval(viewRow, viewRow);
                tblProduct.scrollRectToVisible(tblProduct.getCellRect(viewRow, 0, true));
                return;
            }
        }
    }

    private void updateImagePreview() {
        if (lblImagePreview == null) return;
        String fileName = txtImage.getText().trim();
        if (fileName.isEmpty()) {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("圖片預覽");
            return;
        }
        URL url = getClass().getResource("/images/foods/" + fileName);
        if (url == null) {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("未找到圖片");
            return;
        }
        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage().getScaledInstance(120, 80, Image.SCALE_SMOOTH);
        lblImagePreview.setText("");
        lblImagePreview.setIcon(new ImageIcon(image));
    }

    private void exitToDashboard() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainUI) {
            ((MainUI) window).showDashboard();
        }
    }

    private void clearForm() {
        txtId.setBackground(new Color(238, 238, 238));
        txtId.setForeground(UIStyle.MUTED);
        txtId.setText("自動產生"); txtName.setText(""); txtPrice.setText(""); txtDescription.setText(""); txtImage.setText("");
        if (lblImagePreview != null) { lblImagePreview.setIcon(null); lblImagePreview.setText("圖片預覽"); }
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        cmbStatus.setSelectedItem("販售中"); tblProduct.clearSelection();
        updateDescriptionVisibility();
    }
}
