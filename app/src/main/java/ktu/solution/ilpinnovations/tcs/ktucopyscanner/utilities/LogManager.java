package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.LogBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.UserBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;

/**
 * Created by 1115394 on 12/5/2016.
 */
public class LogManager {
    private static final String TAG = LogManager.class.getSimpleName();
    private Context mContext;

    private String BASE_PATH;
    public LogManager(Context context){
        this.mContext = context;
        BASE_PATH  = Environment.getDataDirectory().getPath() + File.separator +
                "data" + File.separator +
                mContext.getPackageName() + File.separator +
                "log" + File.separator;

        Log.i(TAG, "BASE_PATH " + BASE_PATH);
    }

    public LogBean createLog(String centreCode, String invigilatorCode){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        String fileName = "log_" + centreCode + "_" + invigilatorCode + "_" + ts;
        boolean isUploaded = false;
        boolean isCreated = false;

        LogBean logBean = new LogBean(fileName, isUploaded, ts);
        Log.i(TAG, "FileName: " + fileName);

        try {
            // this will create a new name everytime and unique
            File root = new File(BASE_PATH);
            // if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs();// this will create folder.
            }

            File file = new File(root, fileName);  // file path to save
            isCreated = file.createNewFile();
            Log.i(TAG, "File generated with name: " + fileName + " inside root dir: " + BASE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
//            result.setText(e.getMessage().toString());
        }

        if (isCreated){
            ManageSharedPreferences.saveLogFileName(mContext, fileName);

            DBHelper db = new DBHelper(mContext);
            int i = db.insertLog(logBean);

            return logBean;
        }else {
            Log.i(TAG, "Error creating file!");
            return null;
        }
    }

    public boolean appendData(String log){
        String fileName = ManageSharedPreferences.getLogFileName(mContext);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String date = df.format(new Date());

        UserBean userBean = ManageSharedPreferences.getUserDetails(mContext);

        String writableData = date + "\t\t" + log + "\n";
        Log.i(TAG, "Writable Data: " + writableData);

        try {
            // this will create a new name everytime and unique
            File root = new File(BASE_PATH);
            File filepath = new File(root, fileName);  // file path to save

            FileWriter writer = new FileWriter(filepath, true);
            writer.append(writableData);
            writer.flush();
            writer.close();
//            Log.i(TAG, "File generated with name: " + fileName + " inside root dir: " + BASE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean appendData(String log, boolean flag){
        String fileName = ManageSharedPreferences.getLogFileName(mContext);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String date = df.format(new Date());

        UserBean userBean = ManageSharedPreferences.getUserDetails(mContext);

        String writableData = "\n\n" + date + "\t\t" + log + "\n";
        Log.i(TAG, "Writable Data: " + writableData);

        try {
            // this will create a new name everytime and unique
            File root = new File(BASE_PATH);
            File filepath = new File(root, fileName);  // file path to save

            FileWriter writer = new FileWriter(filepath, true);
            writer.append(writableData);
            writer.flush();
            writer.close();
//            Log.i(TAG, "File generated with name: " + fileName + " inside root dir: " + BASE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean appendData(String fileName, String data){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String date = df.format(new Date());

        String writableData = date + "\t\t" + data + "\n";
        Log.i(TAG, "Writable Data: " + writableData);

        try {
            // this will create a new name everytime and unique
            File root = new File(BASE_PATH);
            File filepath = new File(root, fileName);  // file path to save
            FileWriter writer = new FileWriter(filepath, true);
            writer.append(writableData);
            writer.flush();
            writer.close();
//            Log.i(TAG, "File generated with name: " + fileName + " inside root dir: " + BASE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    public String getLog(String fileName){
        // Base directory
        File root = new File(BASE_PATH);

        // Log file
        File logFile = new File(root, fileName);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(logFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }

    public void syncLog(String fileName){
        DBHelper db = new DBHelper(mContext);
        ArrayList<LogBean> beanList = db.getAllLocalLogs();

        if (beanList != null){
            for (LogBean bean: beanList){
                db.updateLogUploadStatus(bean, true);
            }
        }else {
            Log.e(TAG, "Logs are already synced!");
        }
    }
}
