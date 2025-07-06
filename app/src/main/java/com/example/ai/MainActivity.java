package com.example.ai;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ProgressBar progressBar;

    // Data & Adapter
    private final List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    // Executor for background tasks
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Customize status bar color
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.botMessageBg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Setup toolbar with custom MaterialToolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Title is shown in layout
        }

        // Initialize UI elements
        initViews();

        // Initial bot welcome message
        addToChat("👋👋 Welcome to Nikhil's AI Assistant! I'm here to help. Ask any question!",
                Message.SENT_BY_BOT);
    }

    /**
     * Initialize view references and configure RecyclerView and adapter.
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview);
        messageEditText = findViewById(R.id.editmassage);
        sendButton = findViewById(R.id.sendbutton);
        progressBar = findViewById(R.id.processbar);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // New messages come at bottom
        recyclerView.setLayoutManager(layoutManager);

        sendButton.setOnClickListener(v -> {
            if (!isConnectedToInternet()) {
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

    /**
     * Add a message to the RecyclerView chat list.
     */
    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    /**
     * Show bot response in the chat.
     */
    private void addResponse(String response) {
        runOnUiThread(() -> {
            removeTypingPlaceholder();
            addToChat(response, Message.SENT_BY_BOT);
        });
    }

    /**
     * Show/hide loading indicator.
     */
    private void setProgressBar(boolean show) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            sendButton.setVisibility(show ? View.GONE : View.VISIBLE);
        });
    }

    /**
     * Call Gemini API to get AI-generated response.
     */
    private void callApi(String question) {
        setProgressBar(true);
        addToChat("Typing...", Message.SENT_BY_BOT);

        String apiKey = BuildConfig.GEMINI_API_KEY;
        GenerativeModel gm = new GenerativeModel("gemini-2.5-pro", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(question).build();
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
                Log.e("AI Response", "Error: ", t);
                addResponse("❌ Failed to get a response.");
                setProgressBar(false);
            }
        }, executor);
    }

    /**
     * Remove "Typing..." placeholder if exists.
     */
    private void removeTypingPlaceholder() {
        int lastIndex = messageList.size() - 1;
        if (lastIndex >= 0 && messageList.get(lastIndex).getMassage().equals("Typing...")) {
            messageList.remove(lastIndex);
            messageAdapter.notifyItemRemoved(lastIndex);
        }
    }

    /**
     * Check for internet connectivity.
     */
    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnectedOrConnecting();
            }
        }
        return false;
    }

    /**
     * Hide keyboard after sending message.
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
