package com.example.animor.Utils;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.entity.Message;
import com.example.animor.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MINE = 1;
    private static final int TYPE_OTHER = 2;
    private final List<Message> messageList;
    private final long myUserId;

    public MessageAdapter(List<Message> messageList, long myUserId) {
        this.messageList = messageList;
        this.myUserId = myUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messageList.get(position);
        return msg.getFromUserId() == myUserId ? TYPE_MINE : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MINE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_mine, parent, false);
            return new MineViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_other, parent, false);
            return new OtherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messageList.get(position);
        if (holder instanceof MineViewHolder) {
            ((MineViewHolder) holder).bind(msg);
        } else if (holder instanceof OtherViewHolder) {
            ((OtherViewHolder) holder).bind(msg);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MineViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        MineViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.text_content);
        }
        void bind(Message msg) {
            textContent.setText(msg.getContent());
        }
    }

    static class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        OtherViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.text_content);
        }
        void bind(Message msg) {
            textContent.setText(msg.getContent());
        }
    }

    // Add new msg then refresh
    public void addMessage(Message msg) {
        messageList.add(msg);
        notifyItemInserted(messageList.size() - 1);
    }
}

