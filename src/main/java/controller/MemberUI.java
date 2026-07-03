package controller;

import model.Member;
import service.MemberService;
import service.impl.MemberServiceImpl;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MemberUI extends JPanel {
    private final MemberService memberService = new MemberServiceImpl();
    private JTable tblMember;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtUsername, txtName, txtPhone, txtEmail, txtSearch;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JComboBox<String> cmbRole;

    public MemberUI() {
        initComponents();
        loadMembers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(18, 18));
        setBackground(UIStyle.BG);
        setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel title = new JLabel("會員管理");
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
        form.setPreferredSize(new Dimension(340, 0));
        form.setBorder(UIStyle.formBorder("會員資料"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        txtId = UIStyle.textField();
        txtId.setText("自動產生");
        UIStyle.styleReadOnlyField(txtId);
        txtUsername = UIStyle.textField();
        txtPassword = UIStyle.passwordField();
        txtPassword.setEchoChar('●');
        chkShowPassword = new JCheckBox("顯示密碼");
        chkShowPassword.setFont(UIStyle.FONT);
        chkShowPassword.setForeground(UIStyle.TEXT);
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.addActionListener(e -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '●'));
        txtName = UIStyle.textField();
        txtPhone = UIStyle.textField();
        txtEmail = UIStyle.textField();
        cmbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        cmbRole.setFont(UIStyle.FONT);
        UIStyle.styleComboBox(cmbRole);

        int row = 0;
        row = addField(form, c, row, "會員編號：", txtId);
        row = addField(form, c, row, "帳號：*", txtUsername);
        row = addField(form, c, row, "密碼：*", txtPassword);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2; c.weightx = 1.0;
        c.insets = new Insets(0, 4, 8, 4);
        form.add(chkShowPassword, c);
        row++;
        row = addField(form, c, row, "姓名：*", txtName);
        row = addField(form, c, row, "電話：", txtPhone);
        row = addField(form, c, row, "Email：", txtEmail);
        row = addField(form, c, row, "權限：", cmbRole);

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

        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); clearForm(); loadMembers(); });
        btnExit.addActionListener(e -> exitToDashboard());
        return form;
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
        search.add(UIStyle.label("搜尋會員："), BorderLayout.WEST);
        search.add(txtSearch, BorderLayout.CENTER);
        search.add(btns, BorderLayout.EAST);
        p.add(search, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"編號", "帳號", "密碼", "姓名", "電話", "Email", "權限"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblMember = new JTable(tableModel);
        UIStyle.styleTable(tblMember);
        UIStyle.setColumnWidths(tblMember, 70, 120, 120, 100, 120, 190, 80);
        tblMember.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
        tblMember.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblMember.getSelectedRow() >= 0) {
                    fillFormFromSelectedRow();
                    txtName.requestFocusInWindow();
                }
            }
        });
        p.add(UIStyle.cleanScrollPane(tblMember), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> loadMembers(memberService.searchMembers(txtSearch.getText())));
        txtSearch.addActionListener(e -> loadMembers(memberService.searchMembers(txtSearch.getText())));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); clearForm(); loadMembers(); });
        return p;
    }

    private void loadMembers() { loadMembers(memberService.getAllMembers()); }

    private void loadMembers(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{m.getMemberId(), m.getUsername(), m.getPassword(), m.getName(), m.getPhone(), m.getEmail(), m.getRole()});
        }
    }

    private void fillFormFromSelectedRow() {
        int row = tblMember.getSelectedRow();
        if (row < 0) return;
        int r = tblMember.convertRowIndexToModel(row);
        txtId.setBackground(Color.WHITE);
        txtId.setForeground(UIStyle.TEXT);
        txtId.setText(String.valueOf(tableModel.getValueAt(r, 0)));
        txtUsername.setText(String.valueOf(tableModel.getValueAt(r, 1)));
        txtPassword.setText(String.valueOf(tableModel.getValueAt(r, 2)));
        txtName.setText(String.valueOf(tableModel.getValueAt(r, 3)));
        txtPhone.setText(String.valueOf(tableModel.getValueAt(r, 4)));
        txtEmail.setText(String.valueOf(tableModel.getValueAt(r, 5)));
        cmbRole.setSelectedItem(String.valueOf(tableModel.getValueAt(r, 6)));
    }

    private Member readMember() {
        Member m = new Member();
        String idText = txtId.getText().trim();
        if (!idText.isEmpty() && idText.matches("\\d+")) m.setMemberId(Integer.parseInt(idText));
        m.setUsername(txtUsername.getText().trim());
        String password = new String(txtPassword.getPassword()).trim();
        m.setPassword(password.isEmpty() ? "1234" : password);
        m.setName(txtName.getText().trim());
        m.setPhone(txtPhone.getText().trim());
        m.setEmail(txtEmail.getText().trim());
        m.setRole(String.valueOf(cmbRole.getSelectedItem()));
        return m;
    }

    private void addMember() {
        try {
            if (!UIStyle.confirm(this, "確定要新增此會員嗎？")) return;
            String username = txtUsername.getText().trim();
            memberService.addMember(readMember());
            UIStyle.showInfo(this, "新增會員成功");
            loadMembers();
            selectMemberByUsername(username);
            clearForm();
            txtUsername.requestFocusInWindow();
        } catch (Exception ex) { UIStyle.showError(this, ex.getMessage()); }
    }

    private void updateMember() {
        try {
            if (txtId.getText().trim().isEmpty() || !txtId.getText().trim().matches("\\d+")) { UIStyle.showError(this, "請先選擇會員"); return; }
            if (!UIStyle.confirm(this, "確定要修改此會員資料嗎？")) return;
            memberService.updateMember(readMember());
            UIStyle.showInfo(this, "修改會員成功");
            clearForm(); loadMembers();
        } catch (Exception ex) { UIStyle.showError(this, ex.getMessage()); }
    }

    private void deleteMember() {
        try {
            if (txtId.getText().trim().isEmpty() || !txtId.getText().trim().matches("\\d+")) { UIStyle.showError(this, "請先選擇會員"); return; }
            if (!UIStyle.confirm(this, "確定要刪除此會員嗎？")) return;
            memberService.deleteMember(Integer.parseInt(txtId.getText().trim()));
            UIStyle.showInfo(this, "刪除會員成功");
            clearForm(); loadMembers();
        } catch (Exception ex) { UIStyle.showError(this, ex.getMessage()); }
    }

    private void selectMemberByUsername(String username) {
        if (username == null || username.isEmpty()) return;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 1);
            if (username.equals(String.valueOf(value))) {
                int viewRow = tblMember.convertRowIndexToView(i);
                tblMember.setRowSelectionInterval(viewRow, viewRow);
                tblMember.scrollRectToVisible(tblMember.getCellRect(viewRow, 0, true));
                return;
            }
        }
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
        txtId.setText("自動產生");
        txtUsername.setText(""); txtPassword.setText(""); txtName.setText("");
        txtPhone.setText(""); txtEmail.setText(""); cmbRole.setSelectedItem("USER");
        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar('●');
        tblMember.clearSelection();
    }
}
