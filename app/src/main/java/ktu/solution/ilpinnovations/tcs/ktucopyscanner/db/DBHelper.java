package ktu.solution.ilpinnovations.tcs.ktucopyscanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.CenterStats;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ImageBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.LogBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.PdfSyncBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.QRCodeBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ScannedCopiesBean;


public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "KTUScanned.db";
    public static final String TABLE_NAME_BARCODE = "barcodes";
    public static final String TABLE_NAME_COPIES = "copies";
    public static final String TABLE_NAME_LOG = "log";
    public static final String TABLE_NAME_PDF_SYNC = "pdf_sync";
    public static final String TABLE_NAME_CENTER_STATS = "center_stats";

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

    public static final String COLUMN_LOG_ID = "id";
    public static final String COLUMN_LOG_FILE_NAME = "file_name";
    public static final String COLUMN_LOG_IS_UPLOADED = "is_uploaded";
    public static final String COLUMN_LOG_CREATION_TIMESTAMP = "creation_timestamp";

    public static final String COLUMN_PDF_SYNC_ID = "id";
    public static final String COLUMN_PDF_SYNC_BARCODE = "barcode";
    public static final String COLUMN_PDF_SYNC_PDF_NAME = "pdf_name";
    public static final String COLUMN_PDF_SYNC_IS_SYNCED = "is_pdf_synced";
    public static final String COLUMN_PDF_SYNC_SIZE_OF_PDF = "size_of_pdf";
    public static final String COLUMN_PDF_SYNC_INVIGILATOR_ID = "invigilator_id";
    public static final String COLUMN_PDF_SYNC_TIMESTAMP = "timestamp";

    public static final String COLUMN_CENTER_STATS_CENTER_CODE = "center_id";
    public static final String COLUMN_CENTER_STATS_PDF_COUNT = "pdf_count";
    public static final String COLUMN_CENTER_STATS_IS_SYNCED = "is_synced";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 12);
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

        db.execSQL(
                "create table " + TABLE_NAME_LOG + "( "
                        + COLUMN_LOG_ID + " INTEGER PRIMARY KEY, "
                        + COLUMN_LOG_FILE_NAME + " TEXT UNIQUE, "
                        + COLUMN_LOG_IS_UPLOADED + " INTEGER DEFAULT 0, "
                        + COLUMN_LOG_CREATION_TIMESTAMP + " TEXT ) "
        );

        db.execSQL(
                "create table " + TABLE_NAME_PDF_SYNC + "( "
                        + COLUMN_PDF_SYNC_ID + " integer primary key, "
                        + COLUMN_PDF_SYNC_BARCODE + " text, "
                        + COLUMN_PDF_SYNC_PDF_NAME + " text, "
                        + COLUMN_PDF_SYNC_SIZE_OF_PDF + " text, "
                        + COLUMN_PDF_SYNC_IS_SYNCED + " integer default 0, "
                        + COLUMN_PDF_SYNC_INVIGILATOR_ID + " text, "
                        + COLUMN_PDF_SYNC_TIMESTAMP + " text )"
        );

        db.execSQL(
                "create table " + TABLE_NAME_CENTER_STATS + "( "
                        + COLUMN_CENTER_STATS_CENTER_CODE + " TEXT PRIMARY KEY, "
                        + COLUMN_CENTER_STATS_PDF_COUNT + " INTEGER DEFAULT 0, "
                        + COLUMN_CENTER_STATS_IS_SYNCED + " INTEGER DEFAULT 0 ) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BARCODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COPIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PDF_SYNC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CENTER_STATS);
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

        cursor.close();
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

    public int insertLog(LogBean logBean) {
        int id = 0;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int isUploaded = logBean.getIsUploaded() ? 1 : 0;

        contentValues.put(COLUMN_LOG_FILE_NAME, logBean.getFileName());
        contentValues.put(COLUMN_LOG_IS_UPLOADED, isUploaded);
        contentValues.put(COLUMN_LOG_CREATION_TIMESTAMP, logBean.getCreationTimestamp());
        db.insert(TABLE_NAME_LOG, null, contentValues);

        cursor = db.rawQuery("DELETE FROM " + TABLE_NAME_CENTER_STATS, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_LOG_ID));
        }

        cursor.close();
        db.close();
        return id;
    }

    public boolean updateLogUploadStatus(LogBean logBean, boolean uploadStatus){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int isUploaded = uploadStatus ? 1 : 0;

        Log.i(TAG, "new upload status: " + isUploaded);
        contentValues.put(COLUMN_LOG_IS_UPLOADED, isUploaded);
        db.update(TABLE_NAME_LOG, contentValues, COLUMN_LOG_FILE_NAME + "=?" , new String[]{logBean.getFileName()});

        db.close();
        return true;
    }

    public ArrayList<LogBean> getAllLogs(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<LogBean> logBeanList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_LOG,
                new String[]{COLUMN_LOG_FILE_NAME, COLUMN_LOG_IS_UPLOADED, COLUMN_LOG_CREATION_TIMESTAMP},
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                LogBean logBean = new LogBean();
                logBean.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOG_FILE_NAME)));
                logBean.setIsUploaded(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOG_IS_UPLOADED)) == 1);
                logBean.setCreationTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOG_CREATION_TIMESTAMP)));
                logBeanList.add(logBean);
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return logBeanList;
    }

    public ArrayList<LogBean> getAllLocalLogs(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<LogBean> logBeanList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_LOG,
                new String[]{COLUMN_LOG_FILE_NAME, COLUMN_LOG_IS_UPLOADED, COLUMN_LOG_CREATION_TIMESTAMP},
                COLUMN_LOG_IS_UPLOADED + "=?",
                new String[]{"0"},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                LogBean logBean = new LogBean();
                logBean.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOG_FILE_NAME)));
                logBean.setIsUploaded(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOG_IS_UPLOADED)) == 1);
                logBean.setCreationTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOG_CREATION_TIMESTAMP)));
                logBeanList.add(logBean);
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return logBeanList;
    }

    public int insertPdfSync(PdfSyncBean syncBean) {
        int id = 0;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int isUploaded = syncBean.getIsPdfSynced() ? 1 : 0;

        contentValues.put(COLUMN_PDF_SYNC_BARCODE, syncBean.getBarcode());
        contentValues.put(COLUMN_PDF_SYNC_PDF_NAME, syncBean.getPdfName());
        contentValues.put(COLUMN_PDF_SYNC_SIZE_OF_PDF, syncBean.getSizeOfPdf());
        contentValues.put(COLUMN_PDF_SYNC_IS_SYNCED, isUploaded);
        contentValues.put(COLUMN_PDF_SYNC_INVIGILATOR_ID, syncBean.getInvigilatorID());
        contentValues.put(COLUMN_PDF_SYNC_TIMESTAMP, syncBean.getTimestamp());
        db.insert(TABLE_NAME_PDF_SYNC, null, contentValues);

        cursor = db.rawQuery("SELECT " + COLUMN_PDF_SYNC_ID + " FROM " + TABLE_NAME_PDF_SYNC + " WHERE " + COLUMN_PDF_SYNC_PDF_NAME + " =? ", new String[]{syncBean.getPdfName()});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_PDF_SYNC_ID));
        }

        cursor.close();
        db.close();
        return id;
    }

    public boolean updatePdfSyncStatus(PdfSyncBean syncBean, boolean uploadStatus){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int isUploaded = uploadStatus ? 1 : 0;

        Log.i(TAG, "new upload status: " + isUploaded);
        contentValues.put(COLUMN_PDF_SYNC_IS_SYNCED, isUploaded);
        db.update(TABLE_NAME_PDF_SYNC, contentValues, null , null);

        db.close();
        return true;
    }

    public ArrayList<PdfSyncBean> getAllPdfSync(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PdfSyncBean> syncBeanList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_PDF_SYNC,
                new String[]{COLUMN_PDF_SYNC_BARCODE,
                        COLUMN_PDF_SYNC_PDF_NAME,
                        COLUMN_PDF_SYNC_SIZE_OF_PDF,
                        COLUMN_PDF_SYNC_IS_SYNCED,
                        COLUMN_PDF_SYNC_INVIGILATOR_ID,
                        COLUMN_PDF_SYNC_TIMESTAMP},
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                PdfSyncBean syncBean = new PdfSyncBean();
                syncBean.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_BARCODE)));
                syncBean.setPdfName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_PDF_NAME)));
                syncBean.setSizeOfPdf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_SIZE_OF_PDF)));
                syncBean.setIsPdfSynced(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_IS_SYNCED)) == 1);
                syncBean.setInvigilatorID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_INVIGILATOR_ID)));
                syncBean.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_TIMESTAMP)));

                syncBeanList.add(syncBean);
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return syncBeanList;
    }

    public ArrayList<PdfSyncBean> getAllLocalPdfSync(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PdfSyncBean> syncBeanList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_PDF_SYNC,
                new String[]{COLUMN_PDF_SYNC_BARCODE,
                        COLUMN_PDF_SYNC_PDF_NAME,
                        COLUMN_PDF_SYNC_SIZE_OF_PDF,
                        COLUMN_PDF_SYNC_IS_SYNCED,
                        COLUMN_PDF_SYNC_INVIGILATOR_ID,
                        COLUMN_PDF_SYNC_TIMESTAMP},
                COLUMN_PDF_SYNC_IS_SYNCED + "=?",
                new String[]{"0"},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                PdfSyncBean syncBean = new PdfSyncBean();
                syncBean.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_BARCODE)));
                syncBean.setPdfName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_PDF_NAME)));
                syncBean.setSizeOfPdf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_SIZE_OF_PDF)));
                syncBean.setIsPdfSynced(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_IS_SYNCED)) == 1);
                syncBean.setInvigilatorID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_INVIGILATOR_ID)));
                syncBean.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_SYNC_TIMESTAMP)));

                syncBeanList.add(syncBean);
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return syncBeanList;
    }

    public boolean insertCenterStats(String centerId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor c = db.rawQuery("SELECT " + COLUMN_CENTER_STATS_PDF_COUNT + " FROM " + TABLE_NAME_CENTER_STATS + " WHERE " + COLUMN_CENTER_STATS_CENTER_CODE + " =?;",new String[]{centerId});
        if (c.getCount() == 1)
        {
            c.moveToFirst();
            int count = c.getInt(c.getColumnIndexOrThrow(COLUMN_CENTER_STATS_PDF_COUNT));
            Log.i(TAG, "current count for: " + centerId + " is: " + count);
            count++;
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_CENTER_STATS_PDF_COUNT, count);
            String where = COLUMN_CENTER_STATS_CENTER_CODE + " = '" + centerId + "'";
//            Cursor c2 = db.rawQuery("UPDATE " + TABLE_NAME_CENTER_STATS + " SET " + COLUMN_CENTER_STATS_PDF_COUNT + " =? WHERE " + COLUMN_CENTER_STATS_CENTER_CODE + " =?;",new String[]{String.valueOf(count), centerId});
//            c2.close();
            db.update(TABLE_NAME_CENTER_STATS, cv, where, null);
        }else if (c.getCount() == 0){
            contentValues.put(COLUMN_CENTER_STATS_CENTER_CODE, centerId);
            contentValues.put(COLUMN_CENTER_STATS_PDF_COUNT, 1);
            contentValues.put(COLUMN_CENTER_STATS_IS_SYNCED, 0);
            db.insert(TABLE_NAME_CENTER_STATS, null, contentValues);
        }

        c.close();
        db.close();
        return true;
    }

    public boolean resetLocalStats(){
        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "UPDATE " + TABLE_NAME_CENTER_STATS + " SET " + COLUMN_CENTER_STATS_PDF_COUNT + " = '0'";
//        Log.i(TAG, query);
//        Cursor c2 = db.rawQuery(query,null);
//        c2.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CENTER_STATS_PDF_COUNT, 0);
        db.update(TABLE_NAME_CENTER_STATS, contentValues, null, null);
        db.close();
        return true;
    }

    public ArrayList<CenterStats> getStats(){
        ArrayList<CenterStats> statList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_CENTER_STATS,
                new String[]{COLUMN_CENTER_STATS_CENTER_CODE, COLUMN_CENTER_STATS_PDF_COUNT},
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                CenterStats bean = new CenterStats();
                bean.setCenter_id(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CENTER_STATS_CENTER_CODE)));
                bean.setPdf_count(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CENTER_STATS_PDF_COUNT)));
                statList.add(bean);
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();

        return statList;
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
