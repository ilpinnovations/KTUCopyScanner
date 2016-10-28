package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

/**
 * Created by 1007546 on 13-09-2016.
 */
public class UserBean {

    private String lecturerId;
    private String lecturerName;
    private String hallNumber;
    private String examId;

    public UserBean() {
    }

    public UserBean(String lecturerId, String lecturerName, String hallNumber, String examId) {
        this.lecturerId = lecturerId;
        this.lecturerName = lecturerName;
        this.hallNumber = hallNumber;
        this.examId = examId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(String hallNumber) {
        this.hallNumber = hallNumber;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}
