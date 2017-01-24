package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.CenterStats;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.ImageBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.PdfSyncBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.QRCodeBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.Beans.UserBean;
import ktu.solution.ilpinnovations.tcs.ktucopyscanner.db.DBHelper;

/**
 * Created by 1007546 on 23-09-2016.
 */

public class GeneratePDFAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private ServiceResponse mServiceResponse;
    private ArrayList<ImageBean> images;
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);

    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    DBHelper dbHelper;

    public GeneratePDFAsyncTask(Context context, ServiceResponse mServiceResponse) {
        this.mServiceResponse = mServiceResponse;
        this.context = context;
    }


    @Override
    protected String doInBackground(Void... voids) {
        String len = "";
        Document document = new Document();
        dbHelper = new DBHelper(context);
        ArrayList<QRCodeBean> qrCodes = dbHelper.getAllQRCodes();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/exam/data/copies";
            File dir = new File(path);

            if (dir.exists()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
                dir.delete();
            }

            dir.mkdirs();


            Log.d("PDFCreator", "PDF Path: " + path);
            int i = 0;
            for (QRCodeBean qrCodeBean : qrCodes) {
                ++i;
                HeaderFooterPageEvent event = new HeaderFooterPageEvent();

                document = new Document(PageSize.A4.rotate(), 20f, 20f, 20f, 20f);

                images = dbHelper.getAllCopiesByQrCode(qrCodeBean.getBarcodeId());

                File file = new File(dir, qrCodeBean.getBarcode() + "_" + "_" + qrCodeBean.getInvigilatorId() + ".pdf");
                FileOutputStream fOut = new FileOutputStream(file);

                PdfWriter.getInstance(document, fOut).setPageEvent(event);
                document.open();
                addMetaData(document);
                addTitlePage(document, qrCodeBean.getBarcode());
                addContent(document, images);

//                PdfSyncBean bean = new PdfSyncBean();
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//                String ts = formatter.format(new Date());
//
//                bean.setTimestamp(ts);
//                bean.setInvigilatorID(qrCodeBean.getInvigilatorId());
//                bean.setIsPdfSynced(false);
//                bean.setPdfName(qrCodeBean.getBarcode() + "_" + "_" + qrCodeBean.getInvigilatorId() + ".pdf");
//                bean.setBarcode(qrCodeBean.getBarcode());
//                bean.setSizeOfPdf(String.valueOf(file.length()));
//
//                DBHelper db = new DBHelper(context);
//                int id = db.insertPdfSync(bean);

                document.close();
            }



            ManageSharedPreferences.saveNumPdfs(context, i);
            len = String.valueOf(dir.length());

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            //Toast.makeText(context, "Operation failed please try again later.", Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
        return len;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        mServiceResponse.onServiceResponse(string);
    }

    public interface ServiceResponse {
        void onServiceResponse(String serviceResponse);
    }


    //##############################################################################################################################

    private void addMetaData(Document document) {
        document.addTitle("Exam Copies");
        document.addCreator("ILP Innovations");
    }

    private void addTitlePage(Document document, String copyCode)
            throws DocumentException {
        UserBean userBean = ManageSharedPreferences.getUserDetails(context);

        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Exam id: " + userBean.getExamId(), catFont));
        addEmptyLine(preface, 1);

        preface.add(new Paragraph(
                "Report generated by: " + userBean.getLecturerId() + ", on: " + new Date(), smallBold));
        addEmptyLine(preface, 3);

        preface.add(new Paragraph(
                "Exam paper id : " + copyCode, smallBold));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph(
                "Enclosing barcode id : " + images.get(0).getEndBarcodeId(), smallBold));

        addEmptyLine(preface, 1);
        preface.add(new Paragraph(
                "PDF Generated on : " + Timmings.getCurrentTime(), smallBold));

        addEmptyLine(preface, 1);
        document.add(preface);
        document.newPage();
    }

    private void addContent(Document document, ArrayList<ImageBean> scannedCopies) throws DocumentException {
        try {

            for (ImageBean scannedCopy : scannedCopies) {

                byte[] decodedBytes = Base64.decode(scannedCopy.getImages(), 0);
                Bitmap img = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                Image image = Image.getInstance(stream.toByteArray());

                image.scaleToFit(PageSize.A4.rotate());


                document.add(image);
                document.newPage();



            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }



}
