package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ImageBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.R;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.DisplayImagesAsyncTask;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.LogManager;

public class DisplayCopiesActivity extends AppCompatActivity {
    private String TAG = "DISPLAYCOPIES";
    private RecyclerView recyclerView;
    private int barcodeId;
    private ArrayList<ImageBean> images = new ArrayList<>();
    DBHelper dbHelper;
    private TextView statusText;
    private String barcodeValue;
    private LogManager manager;

    @Override
    protected void onResume() {
        super.onResume();
        // generating home activity log
        String log = "Displaying images!";
        manager.appendData(log);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_copies);

        manager = new LogManager(getApplicationContext());

        dbHelper = new DBHelper(DisplayCopiesActivity.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(TAG, extras.getString("barcode"));
            barcodeValue = extras.getString("barcodeValue");
            barcodeId = Integer.parseInt(extras.getString("barcode"));
        }

        images = dbHelper.getAllCopiesByQrCode(barcodeId);

        recyclerView = (RecyclerView) findViewById(R.id.qrcodeList);
        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText("Code : " + barcodeValue);

        if (images.size() > 0) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new CustomAdapter(images));
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Currently there are no scanned copies in device.");

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        ArrayList<ImageBean> images;
        int i = 0;

        public CustomAdapter(ArrayList<ImageBean> images) {
            this.images = images;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_thumbnail_one, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            DisplayImagesAsyncTask displayImagesAsyncTask = new DisplayImagesAsyncTask(DisplayCopiesActivity.this, viewHolder.imageThumbnail);
            displayImagesAsyncTask.execute(images.get(i).getImages());
            viewHolder.pageNumber.setText("" + (i + 1));
        }


        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageThumbnail;
            public TextView pageNumber;

            public ViewHolder(View itemView) {
                super(itemView);
                imageThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
                pageNumber = (TextView) itemView.findViewById(R.id.pageNumber);
            }

        }
    }
}
