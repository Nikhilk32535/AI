package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendButton;
    ProgressBar progressBar;

    List<Message> messageList;
    MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        // Initialize views
        recyclerView = findViewById(R.id.recyclerview);
        messageEditText = findViewById(R.id.editmassage);
        sendButton = findViewById(R.id.sendbutton);
        progressBar = findViewById(R.id.processbar);

        // Initialize message list
        messageList = new ArrayList<>();
        messageList.add(new Message("👋👋 Welcome to Nikhil's AI Assistant! I'm here to help. Ask any question!",
                Message.SENT_BY_BOT));

        // Setup adapter and layout manager
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Send button click
        sendButton.setOnClickListener(v -> {
            if (!isConnectedToInternet()) {
                Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            String question = messageEditText.getText().toString().trim();
            if (question.isEmpty()) {
                Toast.makeText(this, "Please enter your message.", Toast.LENGTH_SHORT).show();
                return;
            }

            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            hideKeyboard();
            callApi(question);
        });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    void addResponse(String response) {
        runOnUiThread(() -> {
            int lastIndex = messageList.size() - 1;
            if (lastIndex >= 0 && messageList.get(lastIndex).getMassage().equals("Typing...")) {
                messageList.remove(lastIndex);
                messageAdapter.notifyItemRemoved(lastIndex);
            }

            messageList.add(new Message(response, Message.SENT_BY_BOT));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    void setProgressBar(boolean show) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            sendButton.setVisibility(show ? View.GONE : View.VISIBLE);
        });
    }

    void callApi(String question) {
        setProgressBar(true);
        addToChat("Typing...", Message.SENT_BY_BOT);

        GenerativeModel gm = new GenerativeModel("gemini-pro", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(question).build();
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {

            @Override
            public void onSuccess(GenerateContentResponse result) {
                addResponse(result.getText());
                setProgressBar(false);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                addResponse("❌ Failed to get a response. Please try again.");
                setProgressBar(false);
            }
        }, executor);
    }

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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
