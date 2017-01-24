package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

/**
 * Created by 1115394 on 12/5/2016.
 */
public class LogBean {
    private static final String TAG = LogBean.class.getSimpleName();

    private String fileName;
    private boolean isUploaded;
    private String creationTimestamp;

    public LogBean(){

    }

    public LogBean(String fileName, boolean isUploaded, String creationTimestamp){
        this.fileName = fileName;
        this.creationTimestamp = creationTimestamp;
        this.isUploaded = isUploaded;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public boolean getIsUploaded() {
        return isUploaded;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getFileName() {
        return fileName;
    }
}
