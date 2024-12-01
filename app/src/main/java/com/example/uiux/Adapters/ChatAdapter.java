package com.example.uiux.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Model.ChatMessage;
import com.example.uiux.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT = 2;

    private List<ChatMessage> chatMessages;
    private Context context;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        // Xác định loại View dựa vào sender (User hoặc Bot)
        if (chatMessages.get(position).isUser()) {
            return TYPE_USER;
        } else {
            return TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == TYPE_USER) {
            View userView = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(userView);
        } else {
            View botView = inflater.inflate(R.layout.item_chat_bot, parent, false);
            return new BotViewHolder(botView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (holder.getItemViewType() == TYPE_USER) {
            ((UserViewHolder) holder).bind(message.getMessage());
        } else {
            ((BotViewHolder) holder).bind(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // ViewHolder cho User Message
    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message_user);
        }

        public void bind(String message) {
            tvMessage.setText(message);
        }
    }

    // ViewHolder cho Bot Message
    static class BotViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message_bot);
        }

        public void bind(String message) {
            tvMessage.setText(message);
        }
    }
}
