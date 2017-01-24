package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.FormDataBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.LogBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.UserBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.R;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.AppConstants;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.LogManager;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.ManageSharedPreferences;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();

    private EditText lecturerIdEditText, lecturerNameEditText, hallNumberEditText, examIdEditText, passwordEditText;
    private Button submitButton;
    private UserBean userBean;

    private LogManager manager;
    private boolean FLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        manager = new LogManager(getApplicationContext());
        userBean = ManageSharedPreferences.getUserDetails(SignInActivity.this);

        String date = ManageSharedPreferences.getLogDate(getApplicationContext());

        if (!date.equalsIgnoreCase(generateCurrentDate())){
            Log.i(TAG, "Current Date: " + generateCurrentDate() + " saved date: " + date);
            if (userBean.getLecturerName().equals("") || userBean == null){
                FLAG = true;
            }else {
                LogBean logBean = manager.createLog(userBean.getExamId(), userBean.getLecturerId());
                ManageSharedPreferences.saveLogDate(getApplicationContext(), generateCurrentDate());
            }
        }

        if (!FLAG){
            String logStart = "LOG START";
            manager.appendData(logStart, true);

            String log = "Application Started!";
            manager.appendData(log);
        }

        initializeViews();

        if (!userBean.getLecturerName().equals("")) {
            manageAlreadyLoggedIn();
        }

        submitButton.setOnClickListener(new SubmitButtonHandler());
    }

    private void manageAlreadyLoggedIn() {
        lecturerIdEditText.setText(userBean.getLecturerId());
        lecturerNameEditText.setText(userBean.getLecturerName());
        hallNumberEditText.setText(userBean.getHallNumber());
        examIdEditText.setText(userBean.getExamId());
    }


    private void initializeViews() {
        lecturerIdEditText = (EditText) findViewById(R.id.lecturerId);
        lecturerNameEditText = (EditText) findViewById(R.id.lecturerName);
        hallNumberEditText = (EditText) findViewById(R.id.hallNo);
        examIdEditText = (EditText) findViewById(R.id.examId);
        passwordEditText = (EditText) findViewById(R.id.password);
        submitButton = (Button) findViewById(R.id.submitButton);
    }

    private class SubmitButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FormDataBean formDataBean = populateFormData();

            if (dataValid(formDataBean)) {
                UserBean userBean = new UserBean();
                userBean.setLecturerId(formDataBean.getLecturerId());
                userBean.setLecturerName(formDataBean.getLecturerName());
                userBean.setHallNumber(formDataBean.getHallNumber());
                userBean.setExamId(formDataBean.getExamId());

                generateLog(userBean);

                ManageSharedPreferences.saveUserDetails(SignInActivity.this, userBean);
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void generateLog(UserBean userBean){
        if (FLAG){
            LogBean logBean = manager.createLog(userBean.getExamId(), userBean.getLecturerId());
            ManageSharedPreferences.saveLogDate(getApplicationContext(), generateCurrentDate());
        }
        String invigilatorID = "Invigilator ID: " + userBean.getLecturerId();
        manager.appendData(invigilatorID);

        String invigilatorName = "Invigilator Name: " + userBean.getLecturerName();
        manager.appendData(invigilatorName);

        String log = "User attempting to sign-in the system!";
        manager.appendData(log);
    }

    private String generateCurrentDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return format.format(new Date());
    }

    private FormDataBean populateFormData() {
        String lecturerId = lecturerIdEditText.getText().toString().trim();
        String lecturerName = lecturerNameEditText.getText().toString().trim();
        String hallNumber = hallNumberEditText.getText().toString().trim();
        String examId = examIdEditText.getText().toString();
        String password = passwordEditText.getText().toString().trim();

        FormDataBean formDataBean = new FormDataBean(lecturerId, lecturerName, hallNumber, examId, password);
        return formDataBean;
    }

    private boolean dataValid(FormDataBean formDataBean) {
        if (formDataBean.getLecturerId().length() == 0) {
            lecturerIdEditText.setError(AppConstants.INCORRECTLECTURERID);
            String log = "Invigilator ID field left blank!";
            manager.appendData(log);
            return false;
        } else if (formDataBean.getLecturerName().length() == 0) {
            lecturerNameEditText.setError(AppConstants.INCORRECTLECTURERNAME);
            String log = "Invigilator Name field left blank!";
            manager.appendData(log);
            return false;
        } else if (formDataBean.getHallNumber().length() == 0) {
            hallNumberEditText.setError(AppConstants.INCORRECTHALLNUMBER);
            String log = "Hall number field left blank!";
            manager.appendData(log);
            return false;
        } else if (formDataBean.getExamId().length() == 0) {
            examIdEditText.setError(AppConstants.INCORRECTEXAMID);
            String log = "Exam ID field left blank!";
            manager.appendData(log);
            return false;
        } else if (!(formDataBean.getPassword().length() > 0 && formDataBean.getPassword().equals("admin"))) {
            passwordEditText.setError(AppConstants.INCORRECTPASSWORD);
            String log = "Provided incorrect password while login!";
            manager.appendData(log);
            return false;
        }
        return true;
    }
}
