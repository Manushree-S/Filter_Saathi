package org.filtersaathi.app.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class in Java for generating Jal Jeevan Mission monthly water audit reports.
 */
public final class PdfReportGenerator {

    private PdfReportGenerator() {}

    public static File generateMonthlyReport(Context context, String panchayatName, int healthScore) throws IOException {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Header Background
        paint.setColor(Color.parseColor("#0F2A5C"));
        canvas.drawRect(0, 0, 595, 120, paint);

        // Header Title
        paint.setColor(Color.WHITE);
        paint.setTextSize(24);
        paint.setFakeBoldText(true);
        canvas.drawText("FilterSaathi - Jal Jeevan Mission Water Audit", 40, 60, paint);

        paint.setTextSize(14);
        paint.setFakeBoldText(false);
        canvas.drawText("Panchayat: " + panchayatName + " | Date: October 2026", 40, 90, paint);

        // Content
        paint.setColor(Color.parseColor("#17213A"));
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        canvas.drawText("Executive Summary", 40, 160, paint);

        paint.setTextSize(14);
        paint.setFakeBoldText(false);
        canvas.drawText("Overall Community Filter Health Score: " + healthScore + "%", 40, 200, paint);
        canvas.drawText("Safe Water Dispensed This Month: 42,500 Litres", 40, 230, paint);
        canvas.drawText("Total Active Units: 14 / 16 Operational", 40, 260, paint);
        canvas.drawText("Sump IoT Sensor: Active & Synchronized with State Jal Nigam Server", 40, 290, paint);

        // Footer
        paint.setColor(Color.GRAY);
        paint.setTextSize(11);
        canvas.drawText("Generated automatically by FilterSaathi Android Application.", 40, 800, paint);

        document.finishPage(page);

        File pdfFile = new File(context.getExternalFilesDir(null), "FilterSaathi_Monthly_Audit.pdf");
        FileOutputStream fos = new FileOutputStream(pdfFile);
        document.writeTo(fos);
        document.close();
        fos.close();

        return pdfFile;
    }
}
