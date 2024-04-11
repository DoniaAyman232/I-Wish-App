package dto;

public class ItemDTO {
    private int itemId;
    private String itemName;
    private double itemPrice;
    private String itemCategory;
    private double total_contribution;

    public ItemDTO(String itemName, double itemPrice, double total_contribution) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.total_contribution = total_contribution;
    }

    public double getTotal_contribution() {
        return total_contribution;
    }

    public void setTotal_contribution(double total_contribution) {
        this.total_contribution = total_contribution;
    }

    // Default constructor
    public ItemDTO() {
    }

    // Parameterized constructor
    public ItemDTO(int itemId, String itemName, double itemPrice, String itemCategory) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
    }

    // Getter and Setter methods for each field

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    // Override toString method for better representation
    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                ", itemCategory='" + itemCategory + '\'' +
                '}';
    }
}
