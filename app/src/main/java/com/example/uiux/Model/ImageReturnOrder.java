package com.example.uiux.Model;

public class ImageReturnOrder {
    private String img_return_id;
    private String info_return_id;
    private String img1;
    private String img2;
    private String img3;

    public ImageReturnOrder() {
    }

    public ImageReturnOrder(String img_return_id, String info_return_id, String img1, String img2, String img3) {
        this.img_return_id = img_return_id;
        this.info_return_id = info_return_id;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
    }

    public String getImg_return_id() {
        return img_return_id;
    }

    public void setImg_return_id(String img_return_id) {
        this.img_return_id = img_return_id;
    }

    public String getInfo_return_id() {
        return info_return_id;
    }

    public void setInfo_return_id(String info_return_id) {
        this.info_return_id = info_return_id;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }
}
