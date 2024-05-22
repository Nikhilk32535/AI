package com.example.ai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.JsonToken;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
RecyclerView recyclerView;
TextView welcometext;
EditText massageedittext;
ImageButton sendmassage;
List<Message> messageList;
MessageAdapter messageAdapter;
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
        welcometext=findViewById(R.id.welcommassage);
        massageedittext=findViewById(R.id.editmassage);
        sendmassage=findViewById(R.id.sendbutton);

   //Setup Message Adapter
        messageAdapter=new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager lln=new LinearLayoutManager(this);
        lln.setStackFromEnd(true);
        recyclerView.setLayoutManager(lln);


        sendmassage.setOnClickListener((v)->{
            String question=massageedittext.getText().toString().trim();
           // Toast.makeText(this,question,Toast.LENGTH_LONG).show();
            addToChat(question,Message.SENT_BY_ME);
            massageedittext.setText("");
            callApi(question);
            welcometext.setText("");
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
    void callApi(String question){
        // okhttp setup
        messageList.add(new Message("Typing....",Message.SENT_BY_BOT));
        JSONObject jsonbody=new JSONObject();
        try {
            jsonbody.put("model","gpt-3.5-turbo");
            JSONArray messagearray=new JSONArray();
            JSONObject obj=new JSONObject();
            obj.put("role","user");
            obj.put("content",question);
            messagearray.put(obj);
            jsonbody.put("messages",messagearray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body=RequestBody.create(jsonbody.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer $OPENAI_API")
                .post(body)
                .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        addResponse("Kuch galat hua hai check karo :-"+e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        JSONObject jsonObject=null;
                        try {
                            jsonObject=new JSONObject(response.body().string());
                            JSONArray jsonArray=jsonObject.getJSONArray("choices");
                                String result = jsonArray.getJSONObject(0)
                                                 .getJSONObject("message")
                                                    .getString("content");
                                addResponse(result.trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        addResponse("Kuch galat hua hai Karan ye hai:-"+ response.body().string());
                    }
                    }
                });

    }


}
