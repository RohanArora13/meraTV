package channel.price.calculator.meratv;

public class SingleItemModel {
    private int uidc;
    private String name;
    private double price;
    private String sdhd;

    // to store whether item is selected or not in recycler view
    private boolean isSelected = false;

    public SingleItemModel(int uidc, String name, double price, String sdhd) {
        this.uidc = uidc;
        this.name = name;
        this.price = price;
        this.sdhd = sdhd;
    }

    public int getUidc() {
        return uidc;
    }

    public void setUidc(int uidc) {
        this.uidc = uidc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSdhd() {
        return sdhd;
    }

    public void setSdhd(String sdhd) {
        this.sdhd = sdhd;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void update_price(){

    }
}
