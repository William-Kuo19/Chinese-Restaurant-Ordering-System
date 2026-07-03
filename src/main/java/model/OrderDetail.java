package model;

public class OrderDetail {
    private Integer detailId;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private Integer qty;
    private Integer price;
    private Integer subtotal;

    public OrderDetail() {}

    public OrderDetail(Integer productId, String productName, Integer qty, Integer price) {
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.subtotal = qty * price;
    }

    public Integer getDetailId() { return detailId; }
    public void setDetailId(Integer detailId) { this.detailId = detailId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; updateSubtotal(); }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; updateSubtotal(); }
    public Integer getSubtotal() { return subtotal; }
    public void setSubtotal(Integer subtotal) { this.subtotal = subtotal; }
    private void updateSubtotal() { if (qty != null && price != null) subtotal = qty * price; }
}
