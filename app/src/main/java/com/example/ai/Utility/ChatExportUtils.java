package com.example.ai.Utility;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.ai.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ChatExportUtils {

    public static void exportChatAsTxt(Context context, List<Message> messageList) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "AIChat_" + System.currentTimeMillis() + ".txt";
            File file = new File(dir, fileName);
            FileOutputStream out = new FileOutputStream(file);

            // Header
            StringBuilder chatText = new StringBuilder();
            chatText.append("═══════════════════════════════════════\n");
            chatText.append("🧠 Nikhil's AI Chat Export\n");
            chatText.append("═══════════════════════════════════════\n\n");

            for (Message msg : messageList) {
                String sender = msg.getSendby().equals(Message.SENT_BY_ME) ? "👤 You" : "🤖 Bot";
                chatText.append(sender)
                        .append(" [").append(msg.getTimestamp()).append("]:\n")
                        .append(msg.getMassage()).append("\n\n");
            }

            out.write(chatText.toString().getBytes());
            out.close();

            Toast.makeText(context, "TXT exported to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "❌ Failed to export TXT", Toast.LENGTH_SHORT).show();
        }
    }
}
