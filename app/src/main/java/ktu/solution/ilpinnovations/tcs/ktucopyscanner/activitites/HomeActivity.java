package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.QRCodeBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.UserBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.R;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.AppConstants;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.GeneratePDFAsyncTask;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.ManageSharedPreferences;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.UploadDataOnServer;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView nameTextView, hallNumberTextView;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
    private Button logoutButton;
    private Button startScanningButton, generatePDFsButton, viewImagesButton, syncButton;
    private final String TAG = "HOME_ACTIVITY";

    private int COUNT1 = 0;
    private int COUNT = 0;
    DBHelper dbHelper;

    int numBarcodes;
    int numImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DBHelper(HomeActivity.this);
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        populateUserDetails();

        testPerformance();

        logoutButton.setOnClickListener(new LogoutHandler());
        startScanningButton.setOnClickListener(new StartScanningHandler());
        generatePDFsButton.setOnClickListener(new GeneratePDFHandler());
        viewImagesButton.setOnClickListener(new ViewImagesHandler());

        syncButton.setOnClickListener(new SyncButtonHandler());
        signInAnonymously();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    private void testPerformance() {
        ArrayList<QRCodeBean> qrCodeBean = dbHelper.getAllQRCodes();
        ArrayList<Long> imageBean = dbHelper.getAllCopies();

        Log.d(TAG, "Size of images: " + imageBean.size());
        for (QRCodeBean qr : qrCodeBean) {
            Log.d(TAG, String.valueOf(qr.getBarcodeId()));
            Log.d(TAG, String.valueOf(qr.getBarcode()));
        }

        for (Long l : imageBean) {
            Log.d(TAG, String.valueOf(l));
        }

    }

    private void populateUserDetails() {
        UserBean userBean = ManageSharedPreferences.getUserDetails(HomeActivity.this);
        nameTextView.setText("Hello invigilator : " + userBean.getLecturerId());
        hallNumberTextView.setText("Hall number : " + userBean.getHallNumber());
        tv4.setText(ManageSharedPreferences.getNumPFS(HomeActivity.this).equals("") ? "0" : ManageSharedPreferences.getNumPFS(HomeActivity.this));
        tv5.setText(ManageSharedPreferences.getSyncedPFS(HomeActivity.this).equals("") ? "0" : ManageSharedPreferences.getSyncedPFS(HomeActivity.this));
    }

    private void initializeViews() {
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        hallNumberTextView = (TextView) findViewById(R.id.hallNoTextView);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        startScanningButton = (Button) findViewById(R.id.startScanning);
        generatePDFsButton = (Button) findViewById(R.id.generatePDFs);
        viewImagesButton = (Button) findViewById(R.id.viewImages);
        syncButton = (Button) findViewById(R.id.syncButton);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);


    }


    //===========================================================================================================================

    private class LogoutHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertDialogBuilder.setTitle("Are you sure you want to logout?");

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    ManageSharedPreferences.clearUserDetails(HomeActivity.this);
                    Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setIcon(R.mipmap.ic_launcher);
            alertDialog.show();


        }
    }


    private class StartScanningHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.prompt_before_scanning);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);


            final EditText numCopiesEditText = (EditText) dialog.findViewById(R.id.numCopies);
            final EditText passwordEditText = (EditText) dialog.findViewById(R.id.password);

            Button dialogButton = (Button) dialog.findViewById(R.id.submitButton);


            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    COUNT = 0;
                    COUNT1 = 0;
                    int numOfCopies;
                    numOfCopies = Integer.parseInt(numCopiesEditText.getText().toString().trim());
                    String password = passwordEditText.getText().toString().trim();
                    COUNT = numOfCopies;
                    if (numOfCopies == 0) {
                        numCopiesEditText.setError("Please enter a value more than 0.");
                        return;
                    } else if (password.length() == 0 || !(password.equals("admin"))) {
                        passwordEditText.setError(AppConstants.INCORRECTPASSWORD);
                        return;
                    }
                    dialog.dismiss();

                    beginScanningOfCopies();
                }
            });

            dialog.show();

        }
    }

    private class GeneratePDFHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
            pd.setMessage("Generating PDF");
            pd.setIcon(R.mipmap.ic_launcher);
            pd.setCancelable(false);

            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.prompt_password_layout);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            final EditText passwordEditText = (EditText) dialog.findViewById(R.id.password);

            Button dialogButton = (Button) dialog.findViewById(R.id.submitButton);


            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String password = passwordEditText.getText().toString().trim();
                    if (password.length() == 0 || !(password.equals("admin"))) {
                        passwordEditText.setError(AppConstants.INCORRECTPASSWORD);
                        return;
                    } else {
                        GeneratePDFAsyncTask generatePDFAsyncTask = new GeneratePDFAsyncTask(HomeActivity.this, new GeneratePDFAsyncTask.ServiceResponse() {
                            @Override
                            public void onServiceResponse(String serviceResponse) {
                                Toast.makeText(HomeActivity.this, "PDFs generated successfully.", Toast.LENGTH_LONG).show();
                                pd.dismiss();
                                populateUserDetails();

                            }
                        });

                        generatePDFAsyncTask.execute();
                        dialog.dismiss();
                        pd.show();
                    }

                }
            });

            dialog.show();


        }
    }


    private class ViewImagesHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.prompt_password_layout);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            final EditText passwordEditText = (EditText) dialog.findViewById(R.id.password);

            Button dialogButton = (Button) dialog.findViewById(R.id.submitButton);


            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String password = passwordEditText.getText().toString().trim();
                    if (password.length() == 0 || !(password.equals("admin"))) {
                        passwordEditText.setError(AppConstants.INCORRECTPASSWORD);
                        return;
                    } else {
                        dialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, QrcodeActivity.class);
                        startActivity(intent);
                    }

                }
            });

            dialog.show();


        }
    }

    private class SyncButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
            pd.setMessage("Generating PDF");
            pd.setIcon(R.mipmap.ic_launcher);
            pd.setCancelable(false);

            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.prompt_password_layout);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            final EditText passwordEditText = (EditText) dialog.findViewById(R.id.password);

            Button dialogButton = (Button) dialog.findViewById(R.id.submitButton);


            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String password = passwordEditText.getText().toString().trim();
                    if (password.length() == 0 || !(password.equals("admin"))) {
                        passwordEditText.setError(AppConstants.INCORRECTPASSWORD);
                        return;
                    } else {
                        UploadDataOnServer uploadDataOnServer = new UploadDataOnServer(HomeActivity.this, new UploadDataOnServer.ServiceResponse() {
                            @Override
                            public void onServiceResponse(String serviceResponse) {
                                pd.cancel();
                                populateUserDetails();
                                Toast.makeText(HomeActivity.this, "Request is registered, files will be uploaded soon.", Toast.LENGTH_LONG).show();
                            }
                        });
                        uploadDataOnServer.execute();
                        pd.show();
                        dialog.dismiss();
                    }

                }
            });

            dialog.show();
        }
    }
    //===============================================================================================================================

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //===============================================================================================================================

    private void beginScanningOfCopies() {
        Log.d(TAG, "INSISDE START SCANNING OF COPIES");
        Log.d(TAG, "count1 " + COUNT1 + " count " + COUNT);

        if (COUNT1 < COUNT) {
            Intent intent = new Intent(HomeActivity.this, QRCodeScannerActivity.class);
            startActivityForResult(intent, 2);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (data != null) {
                final String message = data.getStringExtra("MESSAGE");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                alertDialogBuilder.setMessage("Do you want to start scanning copies for " + message + " ?");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(HomeActivity.this, CopyScannerActivity.class);
                        intent.putExtra("qrcode", message);
                        startActivityForResult(intent, 3);
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setCancelable(false);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        }

        if (requestCode == 3) {
            ++COUNT1;
            Toast.makeText(HomeActivity.this, COUNT1 + " copies scanned. ", Toast.LENGTH_SHORT).show();
            beginScanningOfCopies();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        numBarcodes = dbHelper.numberOfRowsInBARCODETABLE();
        numImages = dbHelper.numberOfRowsInCOPIESTABLE();


        tv1.setText("" + numBarcodes);
        tv2.setText("" + numBarcodes);
        tv3.setText("" + numImages);
        tv7.setText("" + numImages);


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
