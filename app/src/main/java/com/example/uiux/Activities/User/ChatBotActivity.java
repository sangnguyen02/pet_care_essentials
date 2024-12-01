package com.example.uiux.Activities.User;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.ChatAdapter;
import com.example.uiux.Model.ChatMessage;
import com.example.uiux.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity {
    private RecyclerView rv_chat_messages;
    private ImageView img_back_chat_bot;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText etMessageInput;
    private ImageButton btnSend;
    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);
        initWidget();

        initGenerativeModel();

        // Send Message
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userMessage = etMessageInput.getText().toString().trim();
//                if (!userMessage.isEmpty()) {
//                    chatMessages.add(new ChatMessage(userMessage, true)); // User message
//                    chatMessages.add(new ChatMessage("This is a reply from bot.", false)); // Bot reply
//                    chatAdapter.notifyDataSetChanged();
//                    rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//                    etMessageInput.setText(""); // Clear input field
//                }
//            }
//        });
        btnSend.setOnClickListener(v -> {
            String userMessage = etMessageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessageToBot(userMessage);
                etMessageInput.setText(""); // Clear input field
            }
        });
    }

    private void initGenerativeModel() {
        GenerativeModel gm = new GenerativeModel(
                /* modelName */ "gemini-1.5-flash",
                /* apiKey */ "AIzaSyCczBLARX9JUmi1AUlbDMEU0CEZ2MQQSlE");
        model = GenerativeModelFutures.from(gm);
    }

    void initWidget() {
        img_back_chat_bot =findViewById(R.id.img_back_chat_bot);
        img_back_chat_bot.setOnClickListener(view -> finish());
        rv_chat_messages = findViewById(R.id.rv_chat_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send_message);

        // Setup RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        rv_chat_messages.setLayoutManager(new LinearLayoutManager(this));
        rv_chat_messages.setAdapter(chatAdapter);
    }

    private void sendMessageToBot(String userMessage) {
        // Add user's message to the chat
        chatMessages.add(new ChatMessage(userMessage, true));
        chatAdapter.notifyDataSetChanged();
        rv_chat_messages.scrollToPosition(chatMessages.size() - 1);

        // Prepare content for Gemini API
        Content content = new Content.Builder().addText(userMessage).build();

        // Call Gemini API
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        // Get response text from Gemini
                        String botReply = result.getText();

                        // Update chat with bot's reply
                        runOnUiThread(() -> {
                            chatMessages.add(new ChatMessage(botReply, false));
                            chatAdapter.notifyDataSetChanged();
                            rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        runOnUiThread(() -> {
                            chatMessages.add(new ChatMessage("Error: Unable to get response from bot.", false));
                            chatAdapter.notifyDataSetChanged();
                            rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
                        });
                    }
                },
                this.getMainExecutor());
    }
}