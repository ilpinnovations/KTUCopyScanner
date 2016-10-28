package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

/**
 * Created by 1007546 on 23-09-2016.
 */
public class LoadImageAsyncTask extends AsyncTask<String, File, Void> {
    private Context context;
    ImageView imageView;

    public LoadImageAsyncTask(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }


    @Override
    protected Void doInBackground(String... strings) {
        File f = new File(strings[0]);
        publishProgress(f);
        return null;
    }

    @Override
    protected void onProgressUpdate(File... file) {
        super.onProgressUpdate(file);
        Uri imageUri = Uri.fromFile(file[0]);
        Glide.with(context)
                .load(imageUri)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(imageView);
        System.gc();
    }

}
