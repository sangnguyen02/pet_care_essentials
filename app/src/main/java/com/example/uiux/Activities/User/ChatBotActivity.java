package com.example.uiux.Activities.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiux.Adapters.ChatAdapter;
import com.example.uiux.Model.ChatMessage;
import com.example.uiux.Model.Supplies;
import com.example.uiux.R;
import com.example.uiux.Utils.ResponseParts;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;

//public class ChatBotActivity extends AppCompatActivity {
//    private RecyclerView rv_chat_messages;
//    private ImageView img_back_chat_bot;
//    private ChatAdapter chatAdapter;
//    private List<ChatMessage> chatMessages;
//    private EditText etMessageInput;
//    private ImageButton btnSend;
//    private GenerativeModelFutures model;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_chat_bot);
//        initWidget();
//
//        initGenerativeModel();
//
//        // Send Message
////        btnSend.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String userMessage = etMessageInput.getText().toString().trim();
////                if (!userMessage.isEmpty()) {
////                    chatMessages.add(new ChatMessage(userMessage, true)); // User message
////                    chatMessages.add(new ChatMessage("This is a reply from bot.", false)); // Bot reply
////                    chatAdapter.notifyDataSetChanged();
////                    rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
////                    etMessageInput.setText(""); // Clear input field
////                }
////            }
////        });
//        btnSend.setOnClickListener(v -> {
//            String userMessage = etMessageInput.getText().toString().trim();
//            if (!userMessage.isEmpty()) {
//                sendMessageToBot(userMessage);
//                etMessageInput.setText(""); // Clear input field
//            }
//        });
//    }
//
//    private void initGenerativeModel() {
//        GenerativeModel gm = new GenerativeModel(
//                /* modelName */ "gemini-1.5-flash",
//                /* apiKey */ "AIzaSyCczBLARX9JUmi1AUlbDMEU0CEZ2MQQSlE");
//        model = GenerativeModelFutures.from(gm);
//    }
//
//    void initWidget() {
//        img_back_chat_bot =findViewById(R.id.img_back_chat_bot);
//        img_back_chat_bot.setOnClickListener(view -> finish());
//        rv_chat_messages = findViewById(R.id.rv_chat_messages);
//        etMessageInput = findViewById(R.id.et_message_input);
//        btnSend = findViewById(R.id.btn_send_message);
//
//        // Setup RecyclerView
//        chatMessages = new ArrayList<>();
//        chatAdapter = new ChatAdapter(this, chatMessages);
//        rv_chat_messages.setLayoutManager(new LinearLayoutManager(this));
//        rv_chat_messages.setAdapter(chatAdapter);
//    }
//
//    private void sendMessageToBot(String userMessage) {
//        // Add user's message to the chat
//        chatMessages.add(new ChatMessage(userMessage, true));
//        chatAdapter.notifyDataSetChanged();
//        rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//
//        // Prepare content for Gemini API
//        Content content = new Content.Builder().addText(userMessage).build();
//
//        // Call Gemini API
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//        Futures.addCallback(
//                response,
//                new FutureCallback<GenerateContentResponse>() {
//                    @Override
//                    public void onSuccess(GenerateContentResponse result) {
//                        // Get response text from Gemini
//                        String botReply = result.getText();
//
//                        // Update chat with bot's reply
//                        runOnUiThread(() -> {
//                            chatMessages.add(new ChatMessage(botReply, false));
//                            chatAdapter.notifyDataSetChanged();
//                            rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        t.printStackTrace();
//                        runOnUiThread(() -> {
//                            chatMessages.add(new ChatMessage("Error: Unable to get response from bot.", false));
//                            chatAdapter.notifyDataSetChanged();
//                            rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//                        });
//                    }
//                },
//                this.getMainExecutor());
//    }
//}

//public class ChatBotActivity extends AppCompatActivity {
//    private RecyclerView rv_chat_messages;
//    private ImageView img_back_chat_bot;
//    private ChatAdapter chatAdapter;
//    private List<ChatMessage> chatMessages;
//    private EditText etMessageInput;
//    private ImageButton btnSend;
//    private GenerativeModelFutures model;
//
//    // List to store supplies from Firebase Realtime Database
//    private List<Supplies> supplies;
//    private List<String> suggestedSuppliesIds = new ArrayList<>();
//
//    private DatabaseReference supplyRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
//        setContentView(R.layout.activity_chat_bot);
//        supplyRef = FirebaseDatabase.getInstance().getReference("Supplies");
//        initWidget();
//
//        // Load supplies from Firebase Realtime Database (replace with your actual loading logic)
//        loadSuppliesFromFirebase();
//
//        initGenerativeModel();
//
//        sendWelcomeMessage();
//
//        btnSend.setOnClickListener(v -> {
//            String userMessage = etMessageInput.getText().toString().trim();
//            if (!userMessage.isEmpty()) {
//                sendMessageToBot(userMessage);
//                etMessageInput.setText(""); // Clear input field
//            }
//        });
//    }
//
//    private void initGenerativeModel() {
//        GenerativeModel gm = new GenerativeModel(
//                /* modelName */ "gemini-1.5-flash",
//                /* apiKey */ "AIzaSyCczBLARX9JUmi1AUlbDMEU0CEZ2MQQSlE");
//        model = GenerativeModelFutures.from(gm);
//    }
//
//    void initWidget() {
//        img_back_chat_bot = findViewById(R.id.img_back_chat_bot);
//        img_back_chat_bot.setOnClickListener(view -> finish());
//        rv_chat_messages = findViewById(R.id.rv_chat_messages);
//        etMessageInput = findViewById(R.id.et_message_input);
//        btnSend = findViewById(R.id.btn_send_message);
//
//        // Setup RecyclerView
//        chatMessages = new ArrayList<>();
//        chatAdapter = new ChatAdapter(this, chatMessages);
//        rv_chat_messages.setLayoutManager(new LinearLayoutManager(this));
//        rv_chat_messages.setAdapter(chatAdapter);
//    }
//
//    private void sendWelcomeMessage() {
//        // Gửi tin nhắn chào mừng khách hàng
//        String welcomeMessage = "Welcome to our customer service. How can I assist you with product suggestions today?";
//        chatMessages.add(new ChatMessage(welcomeMessage, false)); // Tin nhắn từ bot
//        chatAdapter.notifyDataSetChanged();
//        rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//    }
//
//    private void sendMessageToBot(String userMessage) {
//        // Add user's message to the chat
//        chatMessages.add(new ChatMessage(userMessage, true));
//        chatAdapter.notifyDataSetChanged();
//        rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//
//        // Prepare prompt for Gemini
//        String prompt = "I have a list of supplies: " + getSuppliesJson() +
//                ". Please only provide 1 supplies_id of the suitable product for " + userMessage + "with structure supplies_id: value";
//
//        // Call Gemini API
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(new Content.Builder().addText(prompt).build());
//        Futures.addCallback(
//                response,
//                new FutureCallback<GenerateContentResponse>() {
//                    @Override
//                    public void onSuccess(GenerateContentResponse result) {
//                        // Get response text from Gemini
//                        String botReply = result.getText();
//                        Log.e("Bot Response", botReply);
//
//                        String supplyId = extractSupplyIdFromResponse(botReply);
//                        Supplies recommendedSupply = getSupplyById(supplyId);
//
//                        // Update chat with bot's reply
//                        runOnUiThread(() -> {
////                            chatMessages.add(new ChatMessage("I recommend: " + botReply, false));
//                            if (recommendedSupply != null) {
////                                chatMessages.add(new ChatMessage("I recommend: " + botReply, false));
//                                String reply = "I think this is what you want: " + recommendedSupply.getName();
////                                        "\n Mã sản phẩm là " + recommendedSupply.getSupplies_id()
////                                        "\n Sản phẩm này " + recommendedSupply.getDescription();
//                                chatMessages.add(new ChatMessage(reply, false, recommendedSupply.getSupplies_id(), recommendedSupply.getImageUrls().get(0).toString()));
//                            } else {
//                                chatMessages.add(new ChatMessage("I couldn't find a suitable supply. Please give me more detail information", false));
//                            }
//                            chatAdapter.notifyDataSetChanged();
//                            rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        t.printStackTrace();
//                        runOnUiThread(() -> {
//                            chatMessages.add(new ChatMessage("Error: Unable to get response from bot.", false));
//                            chatAdapter.notifyDataSetChanged();
//                            rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
//                        });
//                    }
//                },
//                this.getMainExecutor());
//    }
//
//    // Helper methods to extract supply_id and retrieve supply information
//    private String extractSupplyIdFromResponse(String response) {
//        // Implement logic to extract the supply_id from Gemini's response
//        // Example: Assuming the response is "supply_id: 123"
//        if (response.contains("supplies_id: ")) {
//            return response.substring(response.indexOf("supplies_id: ") + "supplies_id: ".length()).trim();
//        }
//        return null;
//    }
//
//    private Supplies getSupplyById(String supplyId) {
////        Log.e("Supply ID", supplyId);
//        if (supplies != null) {
//            for (Supplies supply : supplies) {
//                if (supply.getSupplies_id().equals(supplyId)) {
//                    return supply;
//                }
//            }
//        }
//        return null;
//    }
//
//    // Method to convert the supplies list to JSON string
//    private String getSuppliesJson() {
//        Gson gson = new Gson();
//        return gson.toJson(supplies);
//    }
//
//    // Method to load supplies from Firebase Realtime Database (replace with your actual logic)
//    private void loadSuppliesFromFirebase() {
//        supplies = new ArrayList<>(); // Khởi tạo danh sách supplies
//        supplyRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                supplies.clear(); // Xóa danh sách cũ
//                for (DataSnapshot supplySnapshot : dataSnapshot.getChildren()) {
//                    Supplies supply = supplySnapshot.getValue(Supplies.class);
//                    supplies.add(supply);
//                }
//                // Cập nhật UI hoặc thực hiện các thao tác khác sau khi nhận dữ liệu
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Xử lý lỗi
//            }
//        });
//    }
//}

public class ChatBotActivity extends AppCompatActivity {
    private RecyclerView rv_chat_messages;
    private ImageView img_back_chat_bot;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText etMessageInput;
    private ImageButton btnSend;
    private GenerativeModelFutures model;

    private List<Supplies> supplies;
    private DatabaseReference supplyRef;

    private List<String> suggestedSuppliesIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        setContentView(R.layout.activity_chat_bot);
        supplyRef = FirebaseDatabase.getInstance().getReference("Supplies");
        initWidget();

        loadSuppliesFromFirebase();

        suggestedSuppliesIds = new ArrayList<>();

        initGenerativeModel();

        sendWelcomeMessage();

        btnSend.setOnClickListener(v -> {
            String userMessage = etMessageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessageToBot(userMessage);
                etMessageInput.setText("");
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
        img_back_chat_bot = findViewById(R.id.img_back_chat_bot);
        img_back_chat_bot.setOnClickListener(view -> finish());
        rv_chat_messages = findViewById(R.id.rv_chat_messages);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSend = findViewById(R.id.btn_send_message);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        rv_chat_messages.setLayoutManager(new LinearLayoutManager(this));
        rv_chat_messages.setAdapter(chatAdapter);
    }

    private void sendWelcomeMessage() {
        String welcomeMessage = "Welcome to PetCare Essentials. How can I assist you with product suggestions today?";
        chatMessages.add(new ChatMessage(welcomeMessage, false));
        chatAdapter.notifyDataSetChanged();
        rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
    }

    private void sendMessageToBot(String userMessage) {
        // Add user's message to the chat
        chatMessages.add(new ChatMessage(userMessage, true));
        chatAdapter.notifyDataSetChanged();
        rv_chat_messages.scrollToPosition(chatMessages.size() - 1);

        String prompt = "I have a list of supplies: " + getSuppliesJson() +
                ". The user has already received suggestions for the following supplies: " + getSuggestedSuppliesJson() +
                ". Please only provide 1 supplies_id of a suitable product for " + userMessage + " that has not been suggested before." +
                "You will response the user with structure like that: supplies_id: value then break line and few lines description of the suggested supply" +
                "If the user ask for the another same supply you still response according to the structure I told you but the supply has not already received suggestions and" +
                " the suggest supply is same type or category with the previous supply";

        // Call Gemini API
        ListenableFuture<GenerateContentResponse> response = model.generateContent(new Content.Builder().addText(prompt).build());
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String botReply = result.getText();
                        ResponseParts parts = extractSupplyIdAndDescriptionFromResponse(botReply);
                        Log.e("Bot Reply: ", botReply);

                        Log.e("Supply ID extracted: ", parts.getSuppliesId());

                        if (isSupplySuggestedBefore(parts.getSuppliesId())) {
                            runOnUiThread(() -> {
                                String followUpMessage = "I noticed I suggested this product earlier. Could you provide more details or preferences?";
                                chatMessages.add(new ChatMessage(followUpMessage, false));
                                chatAdapter.notifyDataSetChanged();
                                rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
                            });
                        } else {
                            addSuggestedSupply(parts.getSuppliesId());

                            Supplies recommendedSupply = getSupplyById(parts.getSuppliesId());

                            runOnUiThread(() -> {
                                if (recommendedSupply != null) {
                                    String reply = "Here is my recommendation: " + recommendedSupply.getName() + "\n" + "\n" +
                                            parts.getDescription();
                                    chatMessages.add(new ChatMessage(reply, false, recommendedSupply.getSupplies_id(), recommendedSupply.getImageUrls().get(0).toString()));
//                                    chatMessages.add(new ChatMessage(parts.getDescription(), false));

                                } else {
//                                    chatMessages.add(new ChatMessage(botReply, false));
                                    chatMessages.add(new ChatMessage("I couldn't find a suitable supply. Please give me more detail information", false));
                                }
                                chatAdapter.notifyDataSetChanged();
                                rv_chat_messages.scrollToPosition(chatMessages.size() - 1);
                            });
                        }
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

private ResponseParts extractSupplyIdAndDescriptionFromResponse(String response) {
    int suppliesIdStartIndex = response.indexOf("supplies_id: ");
    if (suppliesIdStartIndex != -1) {
        suppliesIdStartIndex += "supplies_id: ".length();
        int suppliesIdEndIndex = response.indexOf("\n", suppliesIdStartIndex);
        if (suppliesIdEndIndex == -1) {
            suppliesIdEndIndex = response.length();
        }

        String suppliesId = response.substring(suppliesIdStartIndex, suppliesIdEndIndex).trim();

        String description = response.substring(suppliesIdEndIndex).trim();

        return new ResponseParts(suppliesId, description);
    }

    return new ResponseParts(null, response.trim());
}


    private boolean isSupplySuggestedBefore(String supplyId) {
        return suggestedSuppliesIds.contains(supplyId);
    }

    private void addSuggestedSupply(String supplyId) {
        suggestedSuppliesIds.add(supplyId);
    }

    private Supplies getSupplyById(String supplyId) {
        if (supplies != null) {
            for (Supplies supply : supplies) {
                if (supply.getSupplies_id().equals(supplyId)) {
                    return supply;
                }
            }
        }
        return null;
    }

    private String getSuppliesJson() {
        Gson gson = new Gson();
        return gson.toJson(supplies);
    }

    private String getSuggestedSuppliesJson() {
        Gson gson = new Gson();
        return gson.toJson(suggestedSuppliesIds);
    }

    private void loadSuppliesFromFirebase() {
        supplies = new ArrayList<>();
        supplyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                supplies.clear(); // Xóa danh sách cũ
                for (DataSnapshot supplySnapshot : dataSnapshot.getChildren()) {
                    Supplies supply = supplySnapshot.getValue(Supplies.class);
                    supplies.add(supply);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}