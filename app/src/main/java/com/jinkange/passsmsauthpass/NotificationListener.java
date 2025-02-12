package com.jinkange.passsmsauthpass;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationListener extends NotificationListenerService {

    private static final String SERVER_URL = "http://59.25.230.179:51791/?code="; // GET 요청 보낼 서버

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        Log.d(TAG, "onNotificationRemoved ~ " +
                " packageName: " + sbn.getPackageName() +
                " id: " + sbn.getId());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Bundle extras = sbn.getNotification().extras;

        CharSequence title = "";

        if (extras.getCharSequence(Notification.EXTRA_TITLE) == null) {
            title = "";
        } else {
            title = extras.getCharSequence(Notification.EXTRA_TITLE);
        }

        CharSequence text = "";

        if (extras.getCharSequence(Notification.EXTRA_TEXT) == null) {
            text = "";
        } else {
            text = extras.getCharSequence(Notification.EXTRA_TEXT);
        }
        //KT일때 문자(com.samsung.android.messaging) , LG일때 (com.android.cellbroadcastreceiver)
        if (sbn.getPackageName().equals("com.samsung.android.messaging") || sbn.getPackageName().equals("com.android.cellbroadcastreceiver")
                || sbn.getPackageName().equals("com.android.messaging") || sbn.getPackageName().equals("com.lge.message")
                || sbn.getPackageName().equals("com.htc.sense.messaging") || sbn.getPackageName().equals("com.motorola.messaging")) {

            if (text.toString().contains("한국모바일인증")) {
                String extractedCode = extractSixDigitCode(text.toString());
                if (extractedCode != null) {
                    Log.d("SmsReceiver", "추출된 인증번호: " + extractedCode);
                    sendHttpRequest(SERVER_URL + extractedCode);
                }
            }

            Log.d("myLog", "pac name " + sbn.getPackageName());
            Log.d("myLog", "title " + title);
            Log.d("myLog", "text " + text);

        } else {
            if (sbn.getPackageName().equals("com.kakao.talk")) { //카카오톡일때
            }

        }
    }

    // 📌 HTTP GET 요청 함수
    private void sendHttpRequest(String urlString) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("SmsReceiver", "HTTP 응답 코드: " + responseCode);
                Log.d("SmsReceiver", "응답 내용: " + response.toString());

            } catch (Exception e) {
                Log.e("SmsReceiver", "HTTP 요청 실패", e);
            }
        }).start();
    }

    // 📌 문자에서 6자리 숫자만 추출하는 정규식 함수
    private String extractSixDigitCode(String message) {
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b"); // 6자리 연속된 숫자
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // 6자리 숫자가 없으면 null 반환
    }

}