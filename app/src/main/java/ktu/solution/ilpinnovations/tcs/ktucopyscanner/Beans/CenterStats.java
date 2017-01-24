package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

/**
 * Created by 1115394 on 12/6/2016.
 */
public class CenterStats {

    private String center_id;
    private int pdf_count;

    public CenterStats() {
    }

    public CenterStats(String center_id, int pdf_count) {
        this.center_id = center_id;
        this.pdf_count = pdf_count;
    }

    public String getCenter_id() {
        return center_id;
    }

    public int getPdf_count() {
        return pdf_count;
    }


    public void setCenter_id(String center_id) {
        this.center_id = center_id;
    }

    public void setPdf_count(int pdf_count) {
        this.pdf_count = pdf_count;
    }
}
