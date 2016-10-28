package ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans;

/**
 * Created by 1007546 on 13-09-2016.
 */
public class FormDataBean {
    private String lecturerId, lecturerName, hallNumber, examId, password;

    public FormDataBean(String lecturerId, String lecturerName, String hallNumber, String examId, String password) {
        this.lecturerId = lecturerId;
        this.lecturerName = lecturerName;
        this.hallNumber = hallNumber;
        this.examId = examId;
        this.password = password;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public String getExamId() {
        return examId;
    }

    public String getPassword() {
        return password;
    }
}
