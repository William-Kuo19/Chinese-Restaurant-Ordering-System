package util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.Order;
import model.OrderDetail;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfUtil {
    private PdfUtil() {}

    /**
     * 讓使用者選擇 PDF 儲存位置。
     */
    public static File choosePdfFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("選擇 PDF 儲存位置");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF 檔案 (*.pdf)", "pdf"));

        String defaultName = "訂單_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        chooser.setSelectedFile(new File(defaultName));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        return file;
    }

    /**
     * ShoppingUI 使用的 PDF 匯出方法。
     */
    public static void exportOrder(File file, String orderNo, List<OrderDetail> details, int total) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Font titleFont = createFont(18, Font.BOLD);
        Font normalFont = createFont(12, Font.NORMAL);

        Paragraph title = new Paragraph("中式餐館點餐管理系統", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("訂單編號：" + orderNo, normalFont));
        document.add(new Paragraph(" "));

        addDetailTable(document, details, normalFont);

        document.add(new Paragraph(" "));
        Paragraph totalParagraph = new Paragraph("總金額：NT$ " + total, titleFont);
        totalParagraph.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(totalParagraph);
        document.close();
    }

    /**
     * 依 Order 物件匯出 PDF。
     */
    public static void exportOrder(File file, Order order, List<OrderDetail> details) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        Font titleFont = createFont(18, Font.BOLD);
        Font normalFont = createFont(12, Font.NORMAL);

        Paragraph title = new Paragraph("中式餐館點餐管理系統", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("訂單編號：" + order.getOrderNo(), normalFont));
        document.add(new Paragraph("用餐方式：" + safe(order.getDineType())
                + (order.getTableNo() == null || order.getTableNo().isEmpty() ? "" : "　桌號：" + order.getTableNo()), normalFont));
        if (order.getRemark() != null && !order.getRemark().trim().isEmpty()) {
            document.add(new Paragraph("備註：" + order.getRemark(), normalFont));
        }
        document.add(new Paragraph(" "));

        addDetailTable(document, details, normalFont);

        document.add(new Paragraph(" "));
        Paragraph total = new Paragraph("總金額：NT$ " + order.getTotal(), titleFont);
        total.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(total);
        document.close();
    }

    private static void addDetailTable(Document document, List<OrderDetail> details, Font normalFont) throws Exception {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});
        table.addCell(new Paragraph("品項", normalFont));
        table.addCell(new Paragraph("數量", normalFont));
        table.addCell(new Paragraph("單價", normalFont));
        table.addCell(new Paragraph("小計", normalFont));

        for (OrderDetail d : details) {
            table.addCell(new Paragraph(safe(d.getProductName()), normalFont));
            table.addCell(new Paragraph(String.valueOf(d.getQty()), normalFont));
            table.addCell(new Paragraph("NT$ " + d.getPrice(), normalFont));
            table.addCell(new Paragraph("NT$ " + d.getSubtotal(), normalFont));
        }
        document.add(table);
    }

    /**
     * 修正中文 PDF 字型問題。
     *
     * 原本的 MHei-Medium 需要額外的 iText Asian 字型套件，
     * 若 Maven 沒有載入該套件，就會出現：
     * Font 'MHei-Medium' with 'UniCNS-UCS2-H' is not recognized.
     *
     * 這裡改用 Windows / macOS / Linux 常見中文字型，並使用 IDENTITY_H + EMBEDDED，
     * 可正常輸出繁體中文 PDF。
     */
    private static Font createFont(float size, int style) throws Exception {
        String[] fonts = {
                "C:/Windows/Fonts/msjh.ttc,0",       // Microsoft JhengHei
                "C:/Windows/Fonts/msjhbd.ttc,0",     // Microsoft JhengHei Bold
                "C:/Windows/Fonts/mingliu.ttc,0",    // MingLiU
                "C:/Windows/Fonts/simsun.ttc,0",     // SimSun
                "/System/Library/Fonts/PingFang.ttc,0",
                "/Library/Fonts/Arial Unicode.ttf",
                "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc,0"
        };

        for (String fontPath : fonts) {
            if (fontExists(fontPath)) {
                BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new Font(baseFont, size, style);
            }
        }

        // 最後保底：若沒有中文字型，至少避免程式中斷；但中文可能無法正常顯示。
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        return new Font(baseFont, size, style);
    }

    private static boolean fontExists(String fontPath) {
        String path = fontPath;
        int commaIndex = fontPath.indexOf(',');
        if (commaIndex >= 0) {
            path = fontPath.substring(0, commaIndex);
        }
        return new File(path).exists();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
