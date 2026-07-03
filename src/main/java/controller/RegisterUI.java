package controller;

import model.Member;
import service.MemberService;
import service.impl.MemberServiceImpl;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterUI extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private final MemberService memberService = new MemberServiceImpl();

    public RegisterUI(JFrame owner) {
        super(owner, "中式餐館點餐管理系統－註冊", true);
        initComponents();
    }

    private void initComponents() {
        setSize(520, 560);
        setLocationRelativeTo(getOwner());
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.BG);
        root.setBorder(new EmptyBorder(24, 34, 24, 34));
        setContentPane(root);

        JLabel title = new JLabel("會員註冊");
        title.setFont(UIStyle.TITLE);
        title.setForeground(UIStyle.TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(12, 1, 0, 8));
        form.setBackground(UIStyle.BG);
        root.add(form, BorderLayout.CENTER);

        txtUsername = UIStyle.textField();
        txtPassword = UIStyle.passwordField();
        txtConfirm = UIStyle.passwordField();
        txtName = UIStyle.textField();
        txtPhone = UIStyle.textField();
        txtEmail = UIStyle.textField();

        form.add(UIStyle.label("帳號")); form.add(txtUsername);
        form.add(UIStyle.label("密碼")); form.add(txtPassword);
        form.add(UIStyle.label("確認密碼")); form.add(txtConfirm);
        form.add(UIStyle.label("姓名")); form.add(txtName);
        form.add(UIStyle.label("電話")); form.add(txtPhone);
        form.add(UIStyle.label("Email")); form.add(txtEmail);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setBackground(UIStyle.BG);
        JButton btnSave = UIStyle.primaryButton("建立帳號");
        JButton btnCancel = UIStyle.lightButton("取消");
        buttons.add(btnSave);
        buttons.add(btnCancel);
        root.add(buttons, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> doRegister());
        btnCancel.addActionListener(e -> dispose());
    }

    private void doRegister() {
        try {
            Member m = new Member();
            m.setUsername(txtUsername.getText().trim());
            m.setPassword(new String(txtPassword.getPassword()).trim());
            m.setName(txtName.getText().trim());
            m.setPhone(txtPhone.getText().trim());
            m.setEmail(txtEmail.getText().trim());
            memberService.register(m, new String(txtConfirm.getPassword()).trim());
            JOptionPane.showMessageDialog(this, "註冊成功，請返回登入");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "註冊失敗", JOptionPane.ERROR_MESSAGE);
        }
    }
}
