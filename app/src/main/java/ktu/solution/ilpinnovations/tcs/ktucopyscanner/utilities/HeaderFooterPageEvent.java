package ktu.solution.ilpinnovations.tcs.ktucopyscanner.utilities;

/**
 * Created by 1007546 on 04-10-2016.
 */

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    protected Phrase watermark = new Phrase("KTU Confidential", new Font(Font.FontFamily.HELVETICA, 20, Font.NORMAL, BaseColor.LIGHT_GRAY));

    public void onStartPage(PdfWriter writer, Document document) {
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("KTU Confidentail Document"), 30, 800, 0);
//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("KTU Confidentail Document"), 550, 800, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
//        PdfContentByte canvas = writer.getDirectContent();
//        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 298, 221, 45);
//        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 458, 381, 45);

    }

}
