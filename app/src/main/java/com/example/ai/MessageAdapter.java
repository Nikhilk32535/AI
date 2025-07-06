package com.example.ai;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView, rightChatTextView;
        TextView leftTimestamp, rightTimestamp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.leftChatLayout);
            rightChatLayout = itemView.findViewById(R.id.rightChatLayout);
            leftChatTextView = itemView.findViewById(R.id.leftChatTextView);
            rightChatTextView = itemView.findViewById(R.id.rightChatTextView);
            leftTimestamp = itemView.findViewById(R.id.leftTimestamp);
            rightTimestamp = itemView.findViewById(R.id.rightTimestamp);
        }

        void bindLongClick(Context context, String messageText) {
            View.OnLongClickListener listener = v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("Choose an option");
                builder.setItems(new CharSequence[]{"Copy", "Share"}, (dialog, which) -> {
                    if (which == 0) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Message", messageText);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, messageText);
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                    }
                });
                builder.show();
                return true;
            };

            leftChatTextView.setOnLongClickListener(listener);
            rightChatTextView.setOnLongClickListener(listener);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items, parent, false);
        return new MyViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        Context context = holder.itemView.getContext();
        Markwon markwon = Markwon.create(context);

        if (message.getSendby().equals(Message.SENT_BY_ME)) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            markwon.setMarkdown(holder.rightChatTextView, message.getMassage());  // Use markdown for user too
            holder.rightTimestamp.setText(timestamp);
            holder.bindLongClick(context, message.getMassage());
        } else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            markwon.setMarkdown(holder.leftChatTextView, message.getMassage());
            holder.leftTimestamp.setText(timestamp);
            holder.bindLongClick(context, message.getMassage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
