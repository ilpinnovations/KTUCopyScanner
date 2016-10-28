package ktu.solution.ilpinnovations.tcs.ktucopyscanner.activitites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.QRCodeBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ScannedCopiesBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.UserBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.R;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.LoadImageAsyncTask;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.ManageSharedPreferences;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.SaveImageAsync;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities.Timmings;

public class CopyScannerActivity extends AppCompatActivity {

    private static final String TAG = "MYTAG";
    private PreviewImage preview;
    Camera camera;
    private int index;
    private ArrayList<String> images;
    private boolean flag = false;
    private String barcode;

    private Button mButton, mButton1, mButton2;
    private TextView statusText;
    private View view1, view2;
    private RecyclerView recyclerView;

    public String photoFileName = "photo.jpeg";

    public final String APP_TAG = "MyCustomApp";
    private String statusTextContent = "No copies scanned";
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_scanner);

        if (savedInstanceState == null) {
            images = new ArrayList<>();
        } else {
            images = savedInstanceState.getStringArrayList("images");
            flag = savedInstanceState.getBoolean("flag");
            barcode = savedInstanceState.getString("barcode");
            index = savedInstanceState.getInt("index");
            statusTextContent = savedInstanceState.getString("statusTextContent");

            Log.d("Saved Instance state", "Images taken from instance");
            Log.d("Saved Instance state", String.valueOf(flag));
            Log.d("Saved Instance state", barcode);
            Log.d("Saved Instance state", statusTextContent);
            Log.d("Saved Instance state", String.valueOf(index));


            onResume();


        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barcode = extras.getString("qrcode");
        }


        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText(statusTextContent);

        preview = new PreviewImage(this, (SurfaceView) findViewById(R.id.surfaceView));
        preview.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        preview.setKeepScreenOn(true);

        initializeViews();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                takePicture();
                mButton.setEnabled(false);
            }
        });


        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (images.size() > 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CopyScannerActivity.this);
                    alertDialogBuilder.setMessage("You have scanned " + images.size() + " copies, do you want to proceed?");

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            System.gc();
                            flag = true;
                            toggleViews();
                            displayPreviewImages();


                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CopyScannerActivity.this);
                    alertDialogBuilder.setMessage("You have not scanned any copies, please scan pages to proceed.");

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CopyScannerActivity.this);
                alertDialogBuilder.setMessage("Do you want to submit the images scanned ? This action cannot be rolled back. ");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        System.gc();
                        Intent intent = new Intent(CopyScannerActivity.this, QRCodeScannerActivity.class);
                        startActivityForResult(intent, 2);
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    private void toggleViews() {
        initializeViews();
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.VISIBLE);

    }

    private void initializeViews() {
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        mButton = (Button) findViewById(R.id.takePicture);
        mButton1 = (Button) findViewById(R.id.done);
        mButton2 = (Button) findViewById(R.id.confirm);
        recyclerView = (RecyclerView) findViewById(R.id.imagesRecyclerView);

        mButton.setFocusable(true);
        mButton.requestFocus();
    }


    private void takePicture() {
        try {
            camera.startPreview();
            preview.mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    }
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            toggleViews();
            displayPreviewImages();
        } else {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            int w = 0, h = 0;
            for (Camera.Size size : sizes) {
                if (size.width > w || size.height > h) {
                    w = size.width;
                    h = size.height;
                }
            }
            parameters.setPictureSize(w, h);
            camera.setParameters(parameters);
            setCameraDisplayOrientation(CopyScannerActivity.this, getBackFacingCameraId(), camera);
            camera.startPreview();
            preview.setCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = rotateBitmap(bitmap, 180f);
                float aspectRatio = bitmap.getWidth() /
                        (float) bitmap.getHeight();

                bitmap = BITMAP_RESIZER(bitmap, 1190, Math.round(1190 / aspectRatio));
                saveImageToDisk(bitmap);
                resetCam();
                mButton.setEnabled(true);
            } catch (Exception e) {
                Log.d("BITMAP ERROR ", "Error while  decoding bitmap");
                e.printStackTrace();
            }

        }


    };


    //---------------------------------------------------------------------------------------------------------------------------


    public Bitmap BITMAP_RESIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        System.gc();
        return scaledBitmap;

    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onBackPressed() {
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        //ArrayList<String> images;

        public CustomAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_thumbnail, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            System.gc();
            LoadImageAsyncTask loadImageAsyncTask = new LoadImageAsyncTask(CopyScannerActivity.this, viewHolder.imageThumbnail);
            loadImageAsyncTask.execute(images.get(i));

            viewHolder.editImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CopyScannerActivity.this);
                    alertDialogBuilder.setMessage("Do you want to update the image?");

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            index = i;
                            actionEditImage();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });

                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }
            });
        }


        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageThumbnail, editImageButton;


            public ViewHolder(View itemView) {
                super(itemView);
                imageThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
                editImageButton = (ImageView) itemView.findViewById(R.id.editImage);
            }

        }
    }

    private void actionEditImage() {
        System.gc();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }

    }

    public Uri getPhotoFileUri(String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }

            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }


    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode != RESULT_CANCELED) {
                Log.d(APP_TAG, "Inside onActivityResult");
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                Bitmap bitmap = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                try {
                    float aspectRatio = bitmap.getWidth() / (float) bitmap.getHeight();


                    bitmap = BITMAP_RESIZER(bitmap, 1190, Math.round(1190 / aspectRatio));
                    //bitmap = rotateBitmap(bitmap, 180f);
                    Log.d(APP_TAG, "Bitmap decoded and resized");
                    Log.d(APP_TAG, "Images arraylist size, before edit " + images.size());
                    updateImageOnDisk(bitmap, images.get(index));
                    Log.d(APP_TAG, "Images arraylist size after edit" + images.size());
                    mAdapter.notifyDataSetChanged();
                    Log.d(APP_TAG, "adapter notified");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(this, "Picture wasn't taken, please try again.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Picture wasn't taken, please try again.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == 2) {
            if (data != null) {
                DBHelper dbHelper = new DBHelper(CopyScannerActivity.this);
                final String code = data.getStringExtra("MESSAGE");
                final ProgressDialog pd = new ProgressDialog(CopyScannerActivity.this);
                pd.setMessage("Saving Images");
                pd.setIcon(R.mipmap.ic_launcher);
                pd.setCancelable(false);
                UserBean userBean = ManageSharedPreferences.getUserDetails(CopyScannerActivity.this);
                String timeStamp = Timmings.getCurrentTime();

                QRCodeBean qrCodeBean = new QRCodeBean(barcode, timeStamp, userBean.getLecturerId());
                int barcodeId = dbHelper.insertBarcode(qrCodeBean);

                Log.d(TAG + "Barcode id ", String.valueOf(barcodeId));
                Log.d(TAG + " images ", String.valueOf(images.size()));

                //Log.d("Images", String.valueOf(images));

                ScannedCopiesBean scannedCopiesBean = new ScannedCopiesBean(barcodeId, images, Timmings.getCurrentTime(), code);
                SaveImageAsync saveImageAsync = new SaveImageAsync(CopyScannerActivity.this, new SaveImageAsync.ServiceResponse() {
                    @Override
                    public void onServiceResponse(String serviceResponse) {
                        pd.dismiss();
                        Log.d(TAG, "Copy saving : " + serviceResponse);
                        finish();
                    }
                });

                saveImageAsync.execute(scannedCopiesBean);
                pd.show();
            }
        }

    }

    private void displayPreviewImages() {
        initializeViews();

        mAdapter = new CustomAdapter();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 400);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), columns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private String saveImageToDisk(Bitmap bitmap) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exam/data/copies/temp";
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        int i = images.size() + 1;
        File file = new File(dir, "img" + i + ".jpeg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            Log.d(TAG, "Bitmap saved on disk");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error while saving the bitmap");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        images.add(file.getPath());
        statusTextContent = images.size() + " copy/copies scanned.";
        Log.d(TAG, "Size of Images " + images.size());
        statusText.setText(statusTextContent);
        return file.getPath();
    }

    private String updateImageOnDisk(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("images", images);
        outState.putBoolean("flag", flag);
        outState.putInt("index", index);
        outState.putString("barcode", barcode);
        outState.putString("statusTextContent", statusTextContent);
        super.onSaveInstanceState(outState);
    }

}
