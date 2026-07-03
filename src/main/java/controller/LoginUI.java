package controller;

import model.Member;
import service.MemberService;
import service.impl.MemberServiceImpl;
import util.LoginSession;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginUI extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final MemberService memberService = new MemberServiceImpl();

    public LoginUI() {
        initComponents();
    }

    private void initComponents() {
        UIStyle.applyFrame(this, "登入", 900, 560);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.BG);
        setContentPane(root);

        JPanel left = new JPanel();
        left.setBackground(UIStyle.SIDEBAR);
        left.setPreferredSize(new Dimension(330, 560));
        left.setLayout(new GridBagLayout());
        JLabel title = new JLabel("中式餐廳點餐管理系統");
        title.setText("<html><div style='text-align:center;'>中式餐廳<br/><span style='font-size:20px'>點餐管理系統</span></div></html>");
        title.setForeground(Color.WHITE);
        title.setFont(UIStyle.BRAND_TITLE);
        left.add(title);
        root.add(left, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.BG);
        root.add(form, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setBackground(UIStyle.CARD);
        card.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.LineBorder(UIStyle.CARD_SHADOW, 1, true), new EmptyBorder(38, 44, 38, 44)));
        card.setLayout(new GridBagLayout());
        form.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JLabel lblTitle = new JLabel("🔐 會員登入");
        lblTitle.setFont(UIStyle.TITLE);
        lblTitle.setForeground(UIStyle.TEXT);
        card.add(lblTitle, gbc);

        gbc.gridy++;
        card.add(UIStyle.label("👤 帳號"), gbc);
        gbc.gridy++;
        txtUsername = UIStyle.textField();
        txtUsername.setColumns(22);
        card.add(txtUsername, gbc);

        gbc.gridy++;
        card.add(UIStyle.label("🔒 密碼"), gbc);
        gbc.gridy++;
        txtPassword = UIStyle.passwordField();
        card.add(txtPassword, gbc);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 12, 0));
        buttons.setBackground(Color.WHITE);
        JButton btnLogin = UIStyle.primaryButton("登入");
        JButton btnRegister = UIStyle.lightButton("註冊");
        buttons.add(btnLogin);
        buttons.add(btnRegister);

        gbc.gridy++;
        gbc.insets = new Insets(24, 0, 8, 0);
        card.add(buttons, gbc);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> new RegisterUI(this).setVisible(true));
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });
    }

    private void doLogin() {
        try {
            Member member = memberService.login(txtUsername.getText(), new String(txtPassword.getPassword()));
            LoginSession.login(member);
            JOptionPane.showMessageDialog(this, "登入成功");
            new MainUI().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "登入失敗", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
