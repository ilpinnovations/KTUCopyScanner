package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

import java.util.ArrayList;

/**
 * Created by 1007546 on 20-09-2016.
 */
public class ScannedCopiesBean {
    private long barcodeId;
    private ArrayList<String> encodedImage;
    private String scanTime;
    private String enclosingBarcodeId;

    public ScannedCopiesBean() {
    }

    public ScannedCopiesBean(long barcodeId, ArrayList<String> encodedImage, String scanTime, String enclosingBarcodeId) {
        this.barcodeId = barcodeId;
        this.encodedImage = encodedImage;
        this.scanTime = scanTime;
        this.enclosingBarcodeId = enclosingBarcodeId;
    }

    public long getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(long barcodeId) {
        this.barcodeId = barcodeId;
    }

    public ArrayList<String> getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(ArrayList<String> encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public String getEnclosingBarcodeId() {
        return enclosingBarcodeId;
    }

    public void setEnclosingBarcodeId(String enclosingBarcodeId) {
        this.enclosingBarcodeId = enclosingBarcodeId;
    }
}
