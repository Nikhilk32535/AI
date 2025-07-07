// ChatExportPdfUtils.java
package com.example.ai.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.example.ai.Message;
import com.example.ai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ChatExportPdfUtils {

    public static void exportChatAsPdf(Context context, List<Message> messageList) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(14f);
        int y = 50;
        int pageHeight = 1120;
        int pageWidth = 792;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Optional background image
        Bitmap bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf_bg);
        Bitmap scaledBg = Bitmap.createScaledBitmap(bg, pageWidth, pageHeight, false);
        canvas.drawBitmap(scaledBg, 0, 0, null);

        paint.setColor(Color.BLACK);
        canvas.drawText("══════════════════════════", 40, y, paint); y += 25;
        canvas.drawText("🧠 Nikhil's AI Chat Export", 40, y, paint); y += 25;
        canvas.drawText("══════════════════════════", 40, y, paint); y += 40;

        for (Message msg : messageList) {
            String sender = msg.getSendby().equals(Message.SENT_BY_ME) ? "👤 You" : "🤖 Bot";
            String fullText = sender + " - " + msg.getTimestamp() + "\n" + msg.getMassage();
            for (String line : fullText.split("\n")) {
                canvas.drawText(line, 40, y, paint);
                y += 25;
                if (y > pageHeight - 50) {
                    pdfDocument.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    canvas.drawBitmap(scaledBg, 0, 0, null);
                    y = 50;
                }
            }
            y += 30;
        }

        pdfDocument.finishPage(page);

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "AIChat_" + System.currentTimeMillis() + ".pdf";
            File file = new File(dir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            pdfDocument.writeTo(out);
            out.close();

            Toast.makeText(context, "✅ PDF saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "❌ Failed to export PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }
}
