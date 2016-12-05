package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

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

    public String createLog(String centreCode, String invigilatorCode){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String fileName = "log_" + centreCode + "_" + invigilatorCode + "_" + ts;

        Log.i(TAG, "FileName: " + fileName);

        return fileName;
    }
}
