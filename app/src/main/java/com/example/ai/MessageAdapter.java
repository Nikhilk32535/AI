package com.example.ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    List<Message> messageList;
    public MessageAdapter(List<Message> messageList) {
        this.messageList=messageList;   }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatview= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items,null);
        MyViewHolder myViewHolder=new MyViewHolder(chatview);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message=messageList.get(position);
        if(message.getSendby().equals(Message.SENT_BY_ME)){
            holder.leftchatview.setVisibility(View.GONE);
            holder.rightchatview.setVisibility(View.VISIBLE);
            holder.rightchattextview.setText(message.getMassage());
        }
        else{
            holder.rightchatview.setVisibility(View.GONE);
            holder.leftchatview.setVisibility(View.VISIBLE);
            holder.leftchattextview.setText(message.getMassage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftchatview,rightchatview;
        TextView leftchattextview,rightchattextview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftchatview=itemView.findViewById(R.id.leftchatview);
            rightchatview=itemView.findViewById(R.id.rightchatview);
            leftchattextview=itemView.findViewById(R.id.leftchattextview);
            rightchattextview=itemView.findViewById(R.id.rightchattextview);

        }
    }

}
