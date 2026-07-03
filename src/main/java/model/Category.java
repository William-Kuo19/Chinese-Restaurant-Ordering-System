package model;

public class Category {
    private Integer categoryId;
    private String categoryName;
    private Integer sortOrder;

    public Category() {}

    public Category(Integer categoryId, String categoryName, Integer sortOrder) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.sortOrder = sortOrder;
    }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    @Override
    public String toString() { return categoryName; }
}
