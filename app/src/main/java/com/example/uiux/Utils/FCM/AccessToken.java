package com.example.uiux.Utils.FCM;

import android.util.Log;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AccessToken {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"petcareessentials-ede77\",\n" +
                    "  \"private_key_id\": \"b5292f33339b67350f291b9701fe7b33ab0fa0ef\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDwViWXTtAaFOu6\\nsGlERKtbm+Z16KQSOW18A88Fi5UnGhjU0w5MSzGuQs91YtkGaq2EXAUioZsBhnAy\\nOao/F3+o07hX4tc4YK5AiI9Y4SE6MNix0ySyQAOXR8LJsWwCc2xXCoS4s7zzTh7S\\nAK5RwdXBMaJ32f+5u4WWYtCbIaKgLvfRMydpCMsGs2RSspim4h4dHkXtiQtAF0oz\\nw9poY/00qsL2XhnC3o/aglHrKBm9BYbVYENtAQF3hBpt5BYF2G8Mbm/h87dEH+M1\\nCptSK69VCVG7vH2tVK9dg61qEuWZyOUZ85bUAaf3l7f0JImShdPNY/L2YNvB34th\\nZNic/hIbAgMBAAECggEAJbF1ItZvB/ApCnYZhGX6tBru2aY7eUo9dBpK0TVTlrbe\\nlx5k2y1d5NVAoUn3/Kwle6FNAuHypBoYJaBqpYuDb5CJFGuU0phDLAbe6JMieXRo\\nv/b8BeLJTWQpViW36Wh94Oba6vbxlv01Ez2dGWB7rFmjAAYWN+SDrpLf4by0O0fk\\nrnC8DWt796dNugf+OZ5Q+mwbvZi6zu65SZwn775PALFdIFGPOPXR/y1fMvpvm3Xi\\nQzzUq2Ljc6nAF2BmYKCwLc3rVsjeLCnA6CWgHTfCEOCMd2IuE+NFb/9qfv7MbAzI\\n8OMJ605LMZPP9TGrfx4yMuGYZY/Jg8E32ROp9giXvQKBgQD9VxQeJfQDAdMMmFmn\\ntBvErIkXeu+0xpslfr9QVGajRuaKATR32bVJpFnTEbjwApROxUBmV850wPaGCHxp\\n+hwt41Sciw5HjiEeaBZQq348KCs8zVqu6k9LqSpckT2PEpGyqeuIdT0wgWi0+LuE\\npYXqKF1DLiurlAI/+NIuc/5/dQKBgQDy3B4NdHUFtWsWJ1HZVZb5nnul+wloBFyr\\nMXBYatT/MeOCZufEPmPEliNa/Ojz9UJD1AXp2rdEaJ2zQH50fdRrI/FckYIZMqpy\\nUC9gHJ/xjyhYDjVwHa8X4gGCM39jYjZJxUYj2xUNGi3tZoqwGZomXUnaKASYEAIk\\nFiHwBH8pTwKBgEymrTKV/ydtxOJ5sh64svIl1fDhxGco9EY6/D2c29UpHZXB2ZMt\\nrtlSPc9L/G26Cg9vRRXyGUhgHfbd2G7kOuRzdOVMeYyS51hg2+eai7491R83bumf\\ngVB+JZAObda9IpIxMU9/UX08mmavH2V6A3OetOEfthza0ndynH1roSQFAoGBAILS\\nbeHjlNIRavERFzcSsvQeGg9MDkIY725MgG8whTI+xoZNNpXw4b9Y/QybZkVcHWj6\\nQoE5jzVrgV+44yWPTqi0QOUTgNhgAY74gLyuTr5J1nqwlLqjUtlzpkpjwUBg/Et6\\n2BCiiFF5g7n5XcYE1JPL9udsXSUoom6b+YYYdgNjAoGAU4MWko0SbrLDuagdJy5p\\n56NFp55bU4n/PKOkeXouwSn+0T7oGd3m2C0mPRcQn9m31cLiFL18QwX0DLTUU+Dg\\nXs5jOL38QFPy8iYmwI0llkGdq6P2zlkBNldtW/1U8GcapTq8YSZgYRU9Vj1sryBe\\noHHKch6gSSE05h96ChEmIok=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-c0vkk@petcareessentials-ede77.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"111415826597212617802\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-c0vkk%40petcareessentials-ede77.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";

            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();


        } catch (IOException e) {
            Log.e("Error", "" + e.getMessage());
            return null;
        }
    }
}
