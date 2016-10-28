package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.QRCodeBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.R;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;

public class QrcodeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;
    int numOfPdfs = 0;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        recyclerView = (RecyclerView) findViewById(R.id.qrcodeList);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 300);

        dbHelper = new DBHelper(QrcodeActivity.this);
        numOfPdfs = dbHelper.getAllQRCodes().size();
        if (numOfPdfs > 0) {
            mAdapter = new CustomAdapter(dbHelper.getAllQRCodes());

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), columns);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Currently there are no scanned qrcodes in device.");

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
        ArrayList<QRCodeBean> qrCodes;
        int i = 0;

        public CustomAdapter(ArrayList<QRCodeBean> qrCodes) {
            this.qrCodes = qrCodes;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.qr_code_layout, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            viewHolder.qrCodeTextView.setText("Code: " + qrCodes.get(i).getBarcode());
            viewHolder.invigilatorCode.setText("Scanned by: " + qrCodes.get(i).getInvigilatorId());
            viewHolder.scanTime.setText("Scan time: " + qrCodes.get(i).getCaptureTime());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("QRCODEACTIVITY", String.valueOf(qrCodes.get(i).getBarcodeId()));
                    Intent intent = new Intent(QrcodeActivity.this, DisplayCopiesActivity.class);
                    intent.putExtra("barcode", qrCodes.get(i).getBarcodeId() + "");
                    intent.putExtra("barcodeValue", qrCodes.get(i).getBarcode() + "");
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return qrCodes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView qrCodeTextView, invigilatorCode, scanTime;


            public ViewHolder(View itemView) {
                super(itemView);
                qrCodeTextView = (TextView) itemView.findViewById(R.id.code);
                invigilatorCode = (TextView) itemView.findViewById(R.id.scannedBy);
                scanTime = (TextView) itemView.findViewById(R.id.scanTime);
            }

        }
    }

}
