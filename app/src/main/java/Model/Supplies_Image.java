package Model;

public class Supplies_Image {
    private String supplies_img_id;
    private  String supplies_id;
    private String image;

    public Supplies_Image() {
    }

    public Supplies_Image(String supplies_img_id, String supplies_id, String image) {
        this.supplies_img_id = supplies_img_id;
        this.supplies_id = supplies_id;
        this.image = image;
    }

    public String getSupplies_img_id() {
        return supplies_img_id;
    }

    public void setSupplies_img_id(String supplies_img_id) {
        this.supplies_img_id = supplies_img_id;
    }

    public String getSupplies_id() {
        return supplies_id;
    }

    public void setSupplies_id(String supplies_id) {
        this.supplies_id = supplies_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
