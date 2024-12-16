package com.example.uiux.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiux.Activities.SupplyDetailActivity;
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
            holder.itemView.setOnLongClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", message.getMessage());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            });

        } else {
            String imageUrl = message.getImageUrl();
            String supplyId = message.getSupplyId();
            ((BotViewHolder) holder).bind(message.getMessage(), imageUrl);
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, SupplyDetailActivity.class);
                intent.putExtra("supply_id", supplyId);
                context.startActivity(intent);
            });
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
        private ImageView imgRecommendation;
        private LinearLayout linear_bot_recommendation;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message_bot);
            imgRecommendation = itemView.findViewById(R.id.img_bot_recommendation);
            linear_bot_recommendation = itemView.findViewById(R.id.linear_bot_recommendation);
        }

        public void bind(String message, String imageUrl) {
            tvMessage.setText(message);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                linear_bot_recommendation.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .error(R.drawable.product_sample)
                        .into(imgRecommendation);
            } else {
                linear_bot_recommendation.setVisibility(View.GONE);
            }
        }
    }
}
