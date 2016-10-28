package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

import java.io.Serializable;


public class ImageBean implements Serializable {
    private String images;
    private String endBarcodeId;

    public ImageBean(String images, String endBarcodeId) {
        this.images = images;
        this.endBarcodeId = endBarcodeId;
    }

    public ImageBean() {
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getEndBarcodeId() {
        return endBarcodeId;
    }

    public void setEndBarcodeId(String endBarcodeId) {
        this.endBarcodeId = endBarcodeId;
    }
}
