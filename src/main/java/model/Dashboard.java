package model;

public class Dashboard {
    private int memberCount;
    private int productCount;
    private int todayOrderCount;
    private int todayRevenue;

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
    public int getTodayOrderCount() { return todayOrderCount; }
    public void setTodayOrderCount(int todayOrderCount) { this.todayOrderCount = todayOrderCount; }
    public int getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(int todayRevenue) { this.todayRevenue = todayRevenue; }
}
