package kr.ac.kpu.block.smared;


public class LedgerContent {
    public String price;
    public String paymemo;
    public String useItem;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUseItem() {
        return useItem;
    }

    public String getPaymemo() {
        return paymemo;
    }

    public void setPaymemo(String paymemo) {
        this.paymemo = paymemo;
    }

    public void setUseItem(String useItem) {
        this.useItem = useItem;

    }

    public LedgerContent() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)

    }
}


