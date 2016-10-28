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

/**
 * Created by 1007546 on 26-09-2016.
 */

public class UploadDataOnServer extends AsyncTask<Void, Void, Void> {
    private ServiceResponse mServiceResponse;
    private String TAG = "UploadDataOnServer";
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://ktu-copy-scanne.appspot.com");


    public UploadDataOnServer(Context context, ServiceResponse mServiceResponse) {
        this.mServiceResponse = mServiceResponse;
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "Inside do in background");
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exam/data/copies/";
        File dir = new File(path);
        String[] files = dir.list();
        int i = 0;
        for (String f : files) {
            ++i;
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exam/data/copies/";
            Log.d(TAG, "File: " + path);
            Uri file = Uri.fromFile(new File(path + f));

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/pdf")
                    .build();


            UploadTask uploadTask = storageRef.child("exam_pdfs/" + file.getLastPathSegment()).putFile(file, metadata);
            Log.d(TAG, "Uploading task");
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
                }
            });

        }

        ManageSharedPreferences.saveNumPdfsSynced(context, i);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mServiceResponse.onServiceResponse("sucess");
    }


    public interface ServiceResponse {
        void onServiceResponse(String serviceResponse);
    }
}
