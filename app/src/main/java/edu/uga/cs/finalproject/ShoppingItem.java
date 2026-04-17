package edu.uga.cs.finalproject;

public class ShoppingItem {

    private String itemName;
    private String itemCount;

    public ShoppingItem() {
    }

    public ShoppingItem(String itemName, String itemCount) {
        this.itemName = itemName;
        this.itemCount = itemCount;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }
}