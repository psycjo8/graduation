package kr.ac.kpu.block.smared;


public class Ledger {
    public String year;
    public String month;
    public String day;
    public String classfy;
    public String times;
    public String price;
    public String paymemo;
    public String useItem;



    public Ledger() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
        this.year = null;
        this.month = null;
        this.day = null;
        this.classfy = null;
        this.times = null;

    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String time) {
        this.times = time;
    }

    public String getClassfy() {
        return classfy;
    }

    public void setClassfy(String classfy) {
        this.classfy = classfy;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaymemo() {
        return paymemo;
    }

    public void setPaymemo(String payMemo) {
        this.paymemo = payMemo;
    }

    public String getUseItem() {
        return useItem;
    }

    public void setUseItem(String useItem) {
        this.useItem = useItem;
    }
}
