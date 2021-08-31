package sample.models;

import java.util.Date;

public class LaundryItem {
    private Integer id, payStatus, categoryId;
    private Float totalAmount, amountPaid, amountChange, weight, unitPrice, amount;
    private String customer, status, remarks, categoryName;
    private Date dateCreated;

    public LaundryItem(int id, String customer, String categoryName, float weight, float amountPaid, float totalAmount, String status, Date dateCreated) {
        this.id = id;
        this.categoryName = categoryName;
        this.customer = customer;
        this.weight = weight;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public float getAmountPaid() {
        return amountPaid;
    }

    public float getAmountChange() {
        return amountChange;
    }

    public String getCustomer() {
        return customer;
    }

    public String getRemarks() {
        return remarks;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Float getWeight() {
        return weight;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public Float getAmount() {
        return amount;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
