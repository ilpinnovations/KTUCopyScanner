package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.LogManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by 1007546 on 13-09-2016.
 */
public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private LogManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new LogManager(getApplicationContext());
        // generating home activity log
        String log = "Launching QR Code Scanner activity!";
        manager.appendData(log);

        qrScanner();
    }

    public void qrScanner() {
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();           // Stop camera on pause
        }

    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)


        Intent intent = new Intent();
        intent.putExtra("MESSAGE", rawResult.getText());
        setResult(2, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
