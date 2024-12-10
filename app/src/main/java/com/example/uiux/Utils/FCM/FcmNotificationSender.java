package com.example.uiux.Utils.FCM;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.browser.trusted.sharing.ShareTarget;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender {
    private final String userFcmToken;
    private final String title;
    private final String body;
    private final Context context;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/petcareessentials-ede77/messages:send";
    private String orderId;
    public FcmNotificationSender(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    // constructor cho trường hợp notification chứa order id
    public FcmNotificationSender(String userFcmToken, String title, String body, String orderId, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.orderId = orderId;
        this.context = context;
    }

    public void sendNotification() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObj = new JSONObject();
//            mainObj.put("to", userFcmToken);
            JSONObject notificationObject = new JSONObject();

            // chứa OrderID để dẫn vào màn hình chi tiết
            JSONObject dataObject = new JSONObject();

            notificationObject.put("title", title);
            notificationObject.put("body", body);

            dataObject.put("order_id", orderId);
            Log.e("FcmNotificationSender", "Data Object: " + dataObject.toString());

//            notificationObject.put("sound", "alarm.mp3");
//            notificationObject.put("android_channel_id", "Orde_Status");
            messageObj.put("token", userFcmToken);
            messageObj.put("notification", notificationObject);
            messageObj.put("data", dataObject);  // Thêm dataObject vào messageObj



            mainObj.put("message", messageObj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, reponse -> {
                // code run got response

            }, volleyError -> {
                // code run got error

            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    AccessToken accessToken = new AccessToken();
                    String accessKey = accessToken.getAccessToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "Bearer " + accessKey);
                    return header;

                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
