package kr.ac.kpu.block.smared;


public class Ledger {
    public String paymemo;
    public String price;
    public String useItem;
    public String date;


    public Ledger() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public String getPaymemo() {
        return paymemo;
    }

    public void setPaymemo(String paymemo) {
        this.paymemo = paymemo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUseItem() {
        return useItem;
    }

    public void setUseItem(String useItem) {
        this.useItem = useItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
