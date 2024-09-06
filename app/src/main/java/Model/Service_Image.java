package Model;

public class Service_Image {
    private String service_img_id;
    private String service_id;
    private String image;

    public Service_Image() {
    }

    public Service_Image(String service_img_id, String service_id, String image) {
        this.service_img_id = service_img_id;
        this.service_id = service_id;
        this.image = image;
    }

    public String getService_img_id() {
        return service_img_id;
    }

    public void setService_img_id(String service_img_id) {
        this.service_img_id = service_img_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
