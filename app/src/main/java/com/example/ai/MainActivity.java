package com.example.ai;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai.Utility.ChatExportPdfUtils;
import com.example.ai.Utility.ChatExportUtils;
import com.example.ai.Utility.Utils;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private final List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.botMessageBg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initViews();

        addToChat("\uD83D\uDC4B\uD83D\uDC4B Welcome to Nikhil's AI Assistant! I'm here to help. Ask any question!",
                Message.SENT_BY_BOT);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview);
        messageEditText = findViewById(R.id.editmassage);
        sendButton = findViewById(R.id.sendbutton);
        progressBar = findViewById(R.id.processbar);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        sendButton.setOnClickListener(v -> {
            if (!Utils.isConnectedToInternet(this)) {
                Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            String userMessage = messageEditText.getText().toString().trim();
            if (userMessage.isEmpty()) {
                Toast.makeText(this, "Please enter your message.", Toast.LENGTH_SHORT).show();
                return;
            }

            addToChat(userMessage, Message.SENT_BY_ME);
            messageEditText.setText("");
            hideKeyboard();
            callApi(userMessage);
        });
    }

    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
            messageList.add(new Message(message, sentBy, timestamp));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    private void addResponse(String response) {
        runOnUiThread(() -> {
            removeTypingPlaceholder();
            addToChat(response, Message.SENT_BY_BOT);
        });
    }

    private void setProgressBar(boolean show) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            sendButton.setVisibility(show ? View.GONE : View.VISIBLE);
        });
    }

    private void callApi(String question) {
        setProgressBar(true);
        addToChat("Typing...", Message.SENT_BY_BOT);

        String apiKey = BuildConfig.GEMINI_API_KEY;
        GenerativeModel gm = new GenerativeModel("gemini-2.5-pro", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String prompt = "Reply concisely. Only give simple answer which is human understandable.\n\nUser: " + question;
        Content content = new Content.Builder().addText(prompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String reply = result.getText();
                addResponse(reply);
                setProgressBar(false);
            }

            @Override
            public void onFailure(Throwable t) {
                addResponse("❌ Failed to get a response.");
                setProgressBar(false);
            }
        }, executor);
    }

    private void removeTypingPlaceholder() {
        int lastIndex = messageList.size() - 1;
        if (lastIndex >= 0 && messageList.get(lastIndex).getMassage().equals("Typing...")) {
            messageList.remove(lastIndex);
            messageAdapter.notifyItemRemoved(lastIndex);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showExportOptions(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.export_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.export_txt) {
                ChatExportUtils.exportChatAsTxt(this, messageList);
            } else if (id == R.id.export_pdf) {
                ChatExportPdfUtils.exportChatAsPdf(this, messageList);
            }

            return true;
        });

        popup.show();
    }
}
