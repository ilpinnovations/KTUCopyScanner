package ktu.solution.ilpinnovations.tcs.ktucopyscanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ImageBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.QRCodeBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ScannedCopiesBean;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "KTUScanned.db";
    public static final String TABLE_NAME_BARCODE = "barcodes";
    public static final String TABLE_NAME_COPIES = "copies";

    public static final String COLUMN_BARCODES_ID = "id";
    public static final String COLUMN_BARCODES_VALUE = "barcode";
    public static final String COLUMN_BARCODES_SCANTIME = "scan_time";
    public static final String COLUMN_BARCODES_SCANNEDBY = "invigilator_id";
    public static final String COLUMN_BARCODES_ISPDFGENERATED = "is_pdfgenerated";
    public static final String COLUMN_BARCODES_PDFGENERATIONTIME = "pdf_generation_time";

    public static final String COLUMN_COPIES_ID = "id";
    public static final String COLUMN_COPIES_BARCODE_ID = "barcode_id";
    public static final String COLUMN_COPIES_ENDBARCODE = "ending_barcode";
    public static final String COLUMN_COPIES_IMAGE = "image";
    public static final String COLUMN_COPIES_IMAGE_SCANTIME = "scan_time";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 8);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME_BARCODE + "( "
                        + COLUMN_BARCODES_ID + " integer primary key, "
                        + COLUMN_BARCODES_VALUE + " text UNIQUE, "
                        + COLUMN_BARCODES_SCANTIME + " text, "
                        + COLUMN_BARCODES_ISPDFGENERATED + " boolean, "
                        + COLUMN_BARCODES_PDFGENERATIONTIME + " text, "
                        + COLUMN_BARCODES_SCANNEDBY + " text ) "
        );

        db.execSQL(
                "create table " + TABLE_NAME_COPIES + "( "
                        + COLUMN_COPIES_ID + " integer primary key, "
                        + COLUMN_COPIES_BARCODE_ID + " integer REFERENCES " + TABLE_NAME_BARCODE + " (" + COLUMN_BARCODES_ID + "), "
                        + COLUMN_COPIES_IMAGE + " text, "
                        + COLUMN_COPIES_ENDBARCODE + " text, "
                        + COLUMN_COPIES_IMAGE_SCANTIME + " text ) "

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BARCODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COPIES);
        onCreate(db);
    }

    public int insertBarcode(QRCodeBean qrCodeBean) {
        int id = 0;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_BARCODES_VALUE, qrCodeBean.getBarcode());
        contentValues.put(COLUMN_BARCODES_SCANTIME, qrCodeBean.getCaptureTime());
        contentValues.put(COLUMN_BARCODES_SCANNEDBY, qrCodeBean.getInvigilatorId());
        db.insert(TABLE_NAME_BARCODE, null, contentValues);

        cursor = db.rawQuery("SELECT " + COLUMN_BARCODES_ID + " FROM " + TABLE_NAME_BARCODE + " WHERE " + COLUMN_BARCODES_VALUE + " =? ", new String[]{qrCodeBean.getBarcode()});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_BARCODES_ID));
        }

        db.close();
        return id;
    }

    public long insertCopyImages(ScannedCopiesBean scannedCopiesBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long barcode = scannedCopiesBean.getBarcodeId();
        String scamTime = scannedCopiesBean.getScanTime();
        String enclosingBarcode = scannedCopiesBean.getEnclosingBarcodeId();

        for (String s : scannedCopiesBean.getEncodedImage()) {
            contentValues.put(COLUMN_COPIES_BARCODE_ID, barcode);
            contentValues.put(COLUMN_COPIES_IMAGE, s);
            contentValues.put(COLUMN_COPIES_IMAGE_SCANTIME, scamTime);
            contentValues.put(COLUMN_COPIES_ENDBARCODE, enclosingBarcode);

            db.insert(TABLE_NAME_COPIES, null, contentValues);
        }
        db.close();
        return new DBHelper(context).numberOfRowsInCOPIESTABLE();
    }


    public int numberOfRowsInBARCODETABLE() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_BARCODE);
        return numRows;
    }

    public int numberOfRowsInCOPIESTABLE() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_COPIES);
        return numRows;
    }


    public ArrayList<QRCodeBean> getAllQRCodes() {
        ArrayList<QRCodeBean> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_BARCODE, null);
        res.moveToFirst();

        if (res != null && res.getCount() > 0) {
            while (!res.isAfterLast()) {
                int barcodeId = res.getInt(res.getColumnIndex(COLUMN_BARCODES_ID));
                String barcodeValue = res.getString(res.getColumnIndex(COLUMN_BARCODES_VALUE));
                String captureTime = res.getString(res.getColumnIndex(COLUMN_BARCODES_SCANTIME));
                String capturedBy = res.getString(res.getColumnIndex(COLUMN_BARCODES_SCANNEDBY));

                QRCodeBean qrCodeBean = new QRCodeBean(barcodeId, barcodeValue, captureTime, capturedBy);
                array_list.add(qrCodeBean);
                res.moveToNext();
            }
        }
        res.close();
        db.close();
        return array_list;
    }

    public ArrayList<ImageBean> getAllCopiesByQrCode(int qrcode) {
        ArrayList<ImageBean> images = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_COPIES + " where " + COLUMN_COPIES_BARCODE_ID + " = " + qrcode, null);
        res.moveToFirst();
        if (res != null && res.getCount() > 0) {
            while (!res.isAfterLast()) {
                String imageValue = res.getString(res.getColumnIndex(COLUMN_COPIES_IMAGE));
                String imageEnclosingId = res.getString(res.getColumnIndex(COLUMN_COPIES_ENDBARCODE));
                ImageBean imageBean = new ImageBean(imageValue, imageEnclosingId);
                images.add(imageBean);
                res.moveToNext();
            }
        }
        res.close();
        db.close();
        return images;
    }

    public ArrayList<Long> getAllCopies() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Long> barcodes = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_COPIES, null);
        res.moveToFirst();
        if (res != null && res.getCount() > 0) {
            while (!res.isAfterLast()) {
                long barcodeId = res.getLong(res.getColumnIndex(COLUMN_COPIES_BARCODE_ID));
                barcodes.add(barcodeId);
                res.moveToNext();
            }
        }
        res.close();
        db.close();
        return barcodes;
    }

}
