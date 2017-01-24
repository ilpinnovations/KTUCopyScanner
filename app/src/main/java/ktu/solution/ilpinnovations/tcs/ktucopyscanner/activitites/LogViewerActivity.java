package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.LogBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.R;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.LogManager;

public class LogViewerActivity extends AppCompatActivity {
    private static final String TAG = LogViewerActivity.class.getSimpleName();

    private TextView logView;
    private AppCompatSpinner spinner;

    private LogManager manager;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        manager = new LogManager(getApplicationContext());
        // generating logview log
        String log = "Launching Log Viewer!";
        manager.appendData(log);

        spinner = (AppCompatSpinner) findViewById(R.id.log_spinner);
        logView = (TextView) findViewById(R.id.log_textview);

        spinner.setOnItemSelectedListener(itemSelectedListener);

        DBHelper db = new DBHelper(getApplicationContext());
        ArrayList<LogBean> logbeanList = db.getAllLogs();

        ArrayList<String> spinnerCategories = new ArrayList<>();

        for (LogBean logBean: logbeanList){
            spinnerCategories.add(logBean.getFileName());
        }

        Collections.reverse(spinnerCategories);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerCategories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // On selecting a spinner item
            String fileName = adapterView.getItemAtPosition(i).toString();
            Log.i(TAG, "Spinner onItemSelected received file name: " + fileName);

            String log = manager.getLog(fileName);
            logView.setText(log);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
}
