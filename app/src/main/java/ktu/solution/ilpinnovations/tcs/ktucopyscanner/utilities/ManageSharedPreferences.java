package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.UserBean;

/**
 * Created by 1007546 on 13-09-2016.
 */
public class ManageSharedPreferences {

    private static final String SHAREDPREFERENCENAME = "KTU_PREFS";
    private static final String PERSISTENTSHAREDPREFERENCE = "KTU_PERSISTENT_PREFS";
    private static final String KEY_LECTURERID = "LecturerId";
    private static final String KEY_LECTURERNAME = "LecturerName";
    private static final String KEY_HALLNUMBER = "HallNumber";
    private static final String KEY_EXAMID = "ExamId";
    private static final String KEY_NUMPDFS = "NUMPDFs";
    private static final String KEY_NUMPDFS_SYNCED = "NumPDFsSynced";
    private static final String KEY_LOG_FILE_NAME = "LogFile";
    private static final String KEY_LOG_DATE = "LogDate";
    private static final String KEY_LAST_SYNCED = "LastSynced";


    public static Boolean saveUserDetails(Context context, UserBean userBean) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_LECTURERID, userBean.getLecturerId());
        editor.putString(KEY_LECTURERNAME, userBean.getLecturerName());
        editor.putString(KEY_HALLNUMBER, userBean.getHallNumber());
        editor.putString(KEY_EXAMID, userBean.getExamId());
        editor.apply();
        return true;
    }

    public static Boolean saveNumPdfs(Context context, int numberOfPdfs) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_NUMPDFS, String.valueOf(numberOfPdfs));
        editor.apply();
        return true;

    }

    public static Boolean saveNumPdfsSynced(Context context, int numberOfPdfs) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_NUMPDFS_SYNCED, String.valueOf(numberOfPdfs));
        editor.apply();
        return true;

    }

    public static Boolean clearUserDetails(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear().apply();
        return true;
    }

    public static UserBean getUserDetails(Context context) {
        UserBean userBean = new UserBean();

        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        userBean.setLecturerId(sharedpreferences.getString(KEY_LECTURERID, ""));
        userBean.setLecturerName(sharedpreferences.getString(KEY_LECTURERNAME, ""));
        userBean.setExamId(sharedpreferences.getString(KEY_EXAMID, ""));
        userBean.setHallNumber(sharedpreferences.getString(KEY_HALLNUMBER, ""));
        return userBean;
    }

    public static String getSyncedPFS(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_NUMPDFS_SYNCED, "");
    }

    public static String getNumPFS(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_NUMPDFS, "");
    }

    public static String getLogFileName(Context context) {
        String fileName;
        SharedPreferences sharedpreferences = context.getSharedPreferences(PERSISTENTSHAREDPREFERENCE, context.MODE_PRIVATE);
        fileName = sharedpreferences.getString(KEY_LOG_FILE_NAME, "");
        return fileName;
    }

    public static Boolean saveLogFileName(Context context, String fileName) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PERSISTENTSHAREDPREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_LOG_FILE_NAME, fileName);
        editor.apply();
        return true;
    }

    public static String getLogDate(Context context) {
        String date;
        SharedPreferences sharedpreferences = context.getSharedPreferences(PERSISTENTSHAREDPREFERENCE, context.MODE_PRIVATE);
        date = sharedpreferences.getString(KEY_LOG_DATE, "");
        return date;
    }

    public static Boolean saveLogDate(Context context, String date) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PERSISTENTSHAREDPREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_LOG_DATE, date);
        editor.apply();
        return true;
    }

    public static String getLastSynced(Context context) {
        String time;
        SharedPreferences sharedpreferences = context.getSharedPreferences(PERSISTENTSHAREDPREFERENCE, context.MODE_PRIVATE);
        time = sharedpreferences.getString(KEY_LAST_SYNCED, "");
        return time;
    }

    public static Boolean saveLastSynced(Context context, String time) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PERSISTENTSHAREDPREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_LAST_SYNCED, time);
        editor.apply();
        return true;
    }
}
