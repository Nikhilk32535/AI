package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
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
EditText massageedittext;
ImageButton sendmassage;
List<Message> messageList;
MessageAdapter messageAdapter;
ProgressBar progressBar;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60,TimeUnit.SECONDS)
            .build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList=new ArrayList<>();

        recyclerView=findViewById(R.id.recyclerview);
        massageedittext=findViewById(R.id.editmassage);
        sendmassage=findViewById(R.id.sendbutton);
        progressBar=findViewById(R.id.processbar);

        messageList.add(new Message("👋👋 Welcome to Nikhil's AI Assistant! I'm here to help. " +
                "Ask any question, and I'll provide answers. Let's chat! ",Message.SENT_BY_BOT));
   //Setup Message Adapter
        messageAdapter=new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager lln=new LinearLayoutManager(this);
        lln.setStackFromEnd(true);
        recyclerView.setLayoutManager(lln);


        sendmassage.setOnClickListener((v)-> {
            checkInternetConnection();
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        messageList.add(new Message(message,sentBy));
        messageAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }



    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    void setProgressBar(Boolean bar) {
        runOnUiThread(()->{
            if (bar) {
                progressBar.setVisibility(View.VISIBLE);
                sendmassage.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                sendmassage.setVisibility(View.VISIBLE);
            }
        });
    }

    void callApi(String question){
        setProgressBar(true);
        messageList.add(new Message("Typing....",Message.SENT_BY_BOT));
        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ "API KEY");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(question)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                addResponse(resultText);
                setProgressBar(false);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                setProgressBar(false);
            }
        }, executor);
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

       if (!isConnected) {
            // Internet is not available, show a notification
          Toast.makeText(this,"Check Internet Connection",Toast.LENGTH_LONG).show();
        }
       else {

           String question = massageedittext.getText().toString().trim();
           // Toast.makeText(this,question,Toast.LENGTH_LONG).show();
           if (question.isEmpty()) {
               Toast.makeText(this, "Can't be empty. ", Toast.LENGTH_SHORT).show();
           } else {

               addToChat(question, Message.SENT_BY_ME);
               massageedittext.setText("");
               callApi(question);
           }
       }
    }
}