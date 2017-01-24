package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.LogBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;

/**
 * Created by 1115394 on 12/5/2016.
 */
public class UploadLogsOnServer extends AsyncTask<Void, Void, Void>{

    private ServiceResponse mServiceResponse;
    private String TAG = UploadLogsOnServer.class.getSimpleName();
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://ktu-copy-scanne.appspot.com");
    String BASE_PATH;


    public UploadLogsOnServer(Context context, ServiceResponse mServiceResponse) {
        this.mServiceResponse = mServiceResponse;
        this.context = context;

        BASE_PATH  = Environment.getDataDirectory().getPath() + File.separator +
                "data" + File.separator +
                context.getPackageName() + File.separator +
                "log" + File.separator;

        Log.i(TAG, "BASE_PATH " + BASE_PATH);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "Inside do in background");

        final DBHelper db = new DBHelper(context);
        ArrayList<LogBean> logBeanArrayList = db.getAllLocalLogs();
        ArrayList<LogBean> temp = db.getAllLogs();
        logBeanArrayList.add(temp.get(temp.size()-1));

        for (final LogBean bean : logBeanArrayList) {
            Uri file = Uri.fromFile(new File(BASE_PATH + bean.getFileName()));

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();


            UploadTask uploadTask = storageRef.child("logs/" + file.getLastPathSegment()).putFile(file, metadata);
            Log.d(TAG, "Uploading Logs");
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                    Log.d(TAG, "Progress: " + progress);
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                    Log.d(TAG, "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "Upload failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    Log.d("Upload Data success", String.valueOf(downloadUrl));
                    db.updateLogUploadStatus(bean, true);
                }
            });

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mServiceResponse.onServiceResponse("success");
    }


    public interface ServiceResponse {
        void onServiceResponse(String serviceResponse);
    }
}
