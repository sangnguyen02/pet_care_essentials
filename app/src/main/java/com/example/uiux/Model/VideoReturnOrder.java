package com.example.uiux.Model;

public class VideoReturnOrder {
    private  String video_return_id;
    private  String info_return_id;
    private  String unboxing_video;
    private String product_video;

    public VideoReturnOrder() {
    }

    public VideoReturnOrder(String video_return_id, String info_return_id, String unboxing_video, String product_video) {
        this.video_return_id = video_return_id;
        this.info_return_id = info_return_id;
        this.unboxing_video = unboxing_video;
        this.product_video = product_video;
    }

    public String getVideo_return_id() {
        return video_return_id;
    }

    public void setVideo_return_id(String video_return_id) {
        this.video_return_id = video_return_id;
    }

    public String getInfo_return_id() {
        return info_return_id;
    }

    public void setInfo_return_id(String info_return_id) {
        this.info_return_id = info_return_id;
    }

    public String getUnboxing_video() {
        return unboxing_video;
    }

    public void setUnboxing_video(String unboxing_video) {
        this.unboxing_video = unboxing_video;
    }

    public String getProduct_video() {
        return product_video;
    }

    public void setProduct_video(String product_video) {
        this.product_video = product_video;
    }
}
