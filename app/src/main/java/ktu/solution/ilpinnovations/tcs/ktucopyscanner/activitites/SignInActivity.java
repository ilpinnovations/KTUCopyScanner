package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.FormDataBean;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();

        userBean = ManageSharedPreferences.getUserDetails(SignInActivity.this);

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

                LogManager manager = new LogManager(getApplicationContext());
                String file_name = manager.createLog(userBean.getExamId(), userBean.getLecturerId());

                Log.i(TAG, "External Storage Dir: " + Environment.getExternalStorageDirectory().getPath() + "\nStorage: " + Environment.getDataDirectory().getPath());

                ManageSharedPreferences.saveUserDetails(SignInActivity.this, userBean);
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
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
            return false;
        } else if (formDataBean.getLecturerName().length() == 0) {
            lecturerNameEditText.setError(AppConstants.INCORRECTLECTURERNAME);
            return false;
        } else if (formDataBean.getHallNumber().length() == 0) {
            hallNumberEditText.setError(AppConstants.INCORRECTHALLNUMBER);
            return false;
        } else if (formDataBean.getExamId().length() == 0) {
            examIdEditText.setError(AppConstants.INCORRECTEXAMID);
            return false;
        } else if (!(formDataBean.getPassword().length() > 0 && formDataBean.getPassword().equals("admin"))) {
            passwordEditText.setError(AppConstants.INCORRECTPASSWORD);
            return false;
        }
        return true;
    }
}
