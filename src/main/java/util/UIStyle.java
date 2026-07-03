package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

/**
 * Chinese Restaurant Ordering System - CIS UI Design
 *
 * Description : 統一管理系統品牌色彩、字型、按鈕、表格、表單與 Dialog 樣式。
 * 原則：只做 UI/CIS 優化，不新增功能、不改 MVC/DAO 架構。
 */
public class UIStyle {
    // CIS Color Palette
    public static final Color BG = new Color(246, 244, 239);              // 暖白背景
    public static final Color CARD = Color.WHITE;                         // 卡片白
    public static final Color SIDEBAR = new Color(38, 45, 48);            // Loft 深灰
    public static final Color SIDEBAR_HOVER = new Color(52, 61, 64);
    public static final Color PRIMARY = new Color(178, 34, 34);           // 中國紅
    public static final Color PRIMARY_HOVER = new Color(139, 26, 26);
    public static final Color SECONDARY = new Color(200, 155, 60);        // 金色
    public static final Color SECONDARY_HOVER = new Color(176, 132, 42);
    public static final Color WOOD = new Color(122, 82, 48);              // 木質棕
    public static final Color WOOD_HOVER = new Color(150, 101, 61);
    public static final Color TABLE_HEADER = new Color(93, 64, 55);       // 深咖啡
    public static final Color BORDER = new Color(216, 210, 200);
    public static final Color INPUT_BORDER = new Color(190, 180, 168);
    public static final Color TEXT = new Color(43, 43, 43);
    public static final Color MUTED = new Color(110, 110, 110);
    public static final Color SELECTION = new Color(245, 229, 190);
    public static final Color CARD_SHADOW = new Color(232, 226, 216);
    public static final Color TOTAL_BG = new Color(255, 248, 232);
    public static final Color SUCCESS = new Color(76, 175, 80);
    public static final Color WARNING = new Color(251, 140, 0);
    public static final Color ERROR = new Color(229, 57, 53);

    // Typography
    public static final String FONT_FAMILY = "Microsoft JhengHei UI";
    public static final Font FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font PAGE_TITLE = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font BIG_NUMBER = new Font(FONT_FAMILY, Font.BOLD, 32);
    public static final Font BRAND_TITLE = new Font(FONT_FAMILY, Font.BOLD, 30);
    public static final Font SIDE_MENU = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font CHECKOUT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Dimension NORMAL_BUTTON_SIZE = new Dimension(106, 38);
    public static final Dimension CHECKOUT_BUTTON_SIZE = new Dimension(420, 56);

    private UIStyle() {}

    public static void applyFrame(JFrame frame, String title, int width, int height) {
        applyGlobalStyle();
        frame.setTitle("中式餐館點餐管理系統－" + title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void applyGlobalStyle() {
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT_BOLD);
        UIManager.put("OptionPane.background", BG);
        UIManager.put("Panel.background", BG);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Label.font", FONT);
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Table.font", FONT_SMALL);
        UIManager.put("TableHeader.font", FONT_BOLD);
    }

    public static JButton primaryButton(String text) {
        return button(text, PRIMARY, PRIMARY_HOVER, Color.WHITE, PRIMARY, true);
    }

    public static JButton secondaryButton(String text) {
        return button(text, SECONDARY, SECONDARY_HOVER, Color.WHITE, SECONDARY, true);
    }

    public static JButton woodButton(String text) {
        return button(text, WOOD, WOOD_HOVER, Color.WHITE, WOOD, true);
    }

    public static JButton lightButton(String text) {
        return button(text, CARD, new Color(252, 249, 243), WOOD, WOOD, false);
    }

    public static JButton dangerButton(String text) {
        return button(text, CARD, new Color(255, 244, 244), ERROR, ERROR, false);
    }

    private static JButton button(String text, Color bg, Color hoverBg, Color fg, Color border, boolean filled) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setMargin(new Insets(6, 14, 6, 14));
        btn.setPreferredSize(new Dimension(Math.max(NORMAL_BUTTON_SIZE.width, btn.getPreferredSize().width + 14), NORMAL_BUTTON_SIZE.height));
        btn.setBorder(new CompoundBorder(new LineBorder(border, 1, true), new EmptyBorder(7, 16, 7, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    public static JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT);
        lbl.setForeground(TEXT);
        return lbl;
    }

    public static JLabel mutedLabel(String text) {
        JLabel lbl = label(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(MUTED);
        return lbl;
    }

    public static JLabel pageTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(PAGE_TITLE);
        lbl.setForeground(TEXT);
        return lbl;
    }



    public static JPanel professionalCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(new LineBorder(CARD_SHADOW, 1, true), new EmptyBorder(18, 20, 18, 20)));
        return panel;
    }

    public static JPanel totalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TOTAL_BG);
        panel.setBorder(new CompoundBorder(new LineBorder(SECONDARY, 1, true), new EmptyBorder(12, 16, 12, 16)));
        return panel;
    }

    public static JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(SIDE_MENU);
        btn.setForeground(new Color(245, 245, 245));
        btn.setBackground(SIDEBAR);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(210, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR); }
        });
        return btn;
    }

    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        lbl.setForeground(TABLE_HEADER);
        return lbl;
    }

    public static JScrollPane cleanScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(new LineBorder(BORDER, 1, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(16, 18, 16, 18)));
        return panel;
    }

    public static JTextField textField() {
        JTextField txt = new JTextField();
        txt.setFont(FONT);
        txt.setForeground(TEXT);
        txt.setBackground(CARD);
        txt.setCaretColor(TEXT);
        txt.setPreferredSize(new Dimension(210, 36));
        txt.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER, 1, true), new EmptyBorder(7, 10, 7, 10)));
        return txt;
    }

    public static JPasswordField passwordField() {
        JPasswordField txt = new JPasswordField();
        txt.setFont(FONT);
        txt.setForeground(TEXT);
        txt.setBackground(CARD);
        txt.setCaretColor(TEXT);
        txt.setPreferredSize(new Dimension(210, 36));
        txt.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER, 1, true), new EmptyBorder(7, 10, 7, 10)));
        return txt;
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT);
        comboBox.setForeground(TEXT);
        comboBox.setBackground(CARD);
        comboBox.setPreferredSize(new Dimension(210, 36));
        comboBox.setBorder(new LineBorder(INPUT_BORDER, 1, true));
    }

    public static javax.swing.border.Border formBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER, 1, true),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_BOLD,
                TABLE_HEADER
        );
        return new CompoundBorder(titledBorder, new EmptyBorder(16, 18, 18, 18));
    }

    public static void styleReadOnlyField(JTextField txt) {
        txt.setEditable(false);
        txt.setBackground(new Color(238, 238, 238));
        txt.setForeground(MUTED);
    }

    public static void showInfo(java.awt.Component parent, String message) {
        applyGlobalStyle();
        JOptionPane.showMessageDialog(parent, message, "訊息", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(java.awt.Component parent, String message) {
        applyGlobalStyle();
        JOptionPane.showMessageDialog(parent, message, "錯誤", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(java.awt.Component parent, String message) {
        applyGlobalStyle();
        return JOptionPane.showConfirmDialog(parent, message, "確認", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void setColumnWidths(JTable table, int... widths) {
        if (table == null || widths == null) return;
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            if (widths[i] > 0) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_SMALL);
        table.setRowHeight(34);
        table.setForeground(TEXT);
        table.setBackground(CARD);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(230, 226, 218));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setBorder(new LineBorder(BORDER, 1, true));
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setForeground(Color.WHITE);
        header.setBackground(TABLE_HEADER);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
    }
}
