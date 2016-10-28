package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

/**
 * Created by 1007546 on 23-09-2016.
 */
public class DisplayImagesAsyncTask extends AsyncTask<String, Bitmap, Void> {
    private Context context;
    ImageView imageView;

    public DisplayImagesAsyncTask(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }


    @Override
    protected Void doInBackground(String... strings) {
        byte[] decodedBytes = Base64.decode(strings[0], 0);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        publishProgress(decodedImage);
        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        imageView.setImageBitmap(values[0]);
    }
}
