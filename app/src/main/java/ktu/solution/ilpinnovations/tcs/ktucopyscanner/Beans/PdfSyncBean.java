package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

/**
 * Created by 1115394 on 12/6/2016.
 */
public class PdfSyncBean {
    private static final String TAG = PdfSyncBean.class.getSimpleName();

    private String barcode,
            pdfName,
            sizeOfPdf,
            invigilatorID,
            timestamp;

    private boolean isPdfSynced;

    public PdfSyncBean() {
    }

    public PdfSyncBean(String barcode, String pdfName, String sizeOfPdf, String invigilatorID, String invigilatorName, String timestamp, boolean isPdfSynced) {
        this.barcode = barcode;
        this.pdfName = pdfName;
        this.sizeOfPdf = sizeOfPdf;
        this.invigilatorID = invigilatorID;
        this.timestamp = timestamp;
        this.isPdfSynced = isPdfSynced;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public void setIsPdfSynced(boolean isPdfSynced) {
        this.isPdfSynced = isPdfSynced;
    }

    public void setSizeOfPdf(String sizeOfPdf) {
        this.sizeOfPdf = sizeOfPdf;
    }

    public void setInvigilatorID(String invigilatorID) {
        this.invigilatorID = invigilatorID;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getPdfName() {
        return pdfName;
    }

    public boolean getIsPdfSynced() {
        return isPdfSynced;
    }

    public String getSizeOfPdf() {
        return sizeOfPdf;
    }

    public String getInvigilatorID() {
        return invigilatorID;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
