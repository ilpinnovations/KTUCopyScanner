package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

/**
 * Created by 1007546 on 20-09-2016.
 */
public class QRCodeBean {
    private int barcodeId;
    private String barcode;
    private String captureTime;
    private String invigilatorId;

    public QRCodeBean() {
    }

    public QRCodeBean(int barcodeId, String barcode, String captureTime, String invigilatorId) {
        this.barcodeId = barcodeId;
        this.barcode = barcode;
        this.captureTime = captureTime;
        this.invigilatorId = invigilatorId;
    }

    public QRCodeBean(String barcode, String captureTime, String invigilatorId) {
        this.barcode = barcode;
        this.captureTime = captureTime;
        this.invigilatorId = invigilatorId;
    }

    public int getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(int barcodeId) {
        this.barcodeId = barcodeId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    public String getInvigilatorId() {
        return invigilatorId;
    }

    public void setInvigilatorId(String invigilatorId) {
        this.invigilatorId = invigilatorId;
    }
}
