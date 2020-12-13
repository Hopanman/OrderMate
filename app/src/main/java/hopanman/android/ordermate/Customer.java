package hopanman.android.ordermate;

public class Customer {
    private String customerEmail;
    private String customerName;
    private String customerTel;

    public Customer() {}

    public Customer(String customerEmail, String customerName, String customerTel) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerTel = customerTel;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }
}
