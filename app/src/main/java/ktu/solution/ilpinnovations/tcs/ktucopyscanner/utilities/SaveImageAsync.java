package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ScannedCopiesBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;

/**
 * Created by abhi on 3/29/2016.
 */
public class SaveImageAsync extends AsyncTask<ScannedCopiesBean, Void, String> {
    private Context context;
    private ServiceResponse mServiceResponse;

    public SaveImageAsync(Context context, ServiceResponse mServiceResponse) {
        this.mServiceResponse = mServiceResponse;
        this.context = context;
    }

    @Override
    protected String doInBackground(ScannedCopiesBean... scannedCopiesBean) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<String> images = new ArrayList<>();
        for (String imagePath : scannedCopiesBean[0].getEncodedImage()) {
            Log.d("Save Image Async", imagePath);
            Bitmap bitmap = null;
            File f = new File(imagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            images.add(encodeToBase64(bitmap));
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exam/data/copies/temp";
        File dir = new File(path);

        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScannedCopiesBean sc = new ScannedCopiesBean(scannedCopiesBean[0].getBarcodeId(), images, scannedCopiesBean[0].getScanTime(), scannedCopiesBean[0].getEnclosingBarcodeId());
        return String.valueOf(dbHelper.insertCopyImages(sc));

    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        mServiceResponse.onServiceResponse(string);
    }

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            System.gc();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        }
        return temp;
    }

    public interface ServiceResponse {
        void onServiceResponse(String serviceResponse);
    }


}
