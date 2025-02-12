package com.jinkange.passsmsauthpass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TARGET_NUMBER = "16001522"; // 감지할 번호 (필요 없으면 제거 가능)
    private static final String SERVER_URL = "http://59.25.230.179:51791/?code="; // GET 요청 보낼 서버
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SmsReceiver", "ㅁㄴㅇㅁㄴㅇㅁㄴㅇㅁㄴㅇㅁㄴㅇㅁㄴㅇ");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        Log.d("SmsReceiver", "문자왔어용 {}"+sms.getOriginatingAddress());
                        String sender = sms.getOriginatingAddress(); // 발신자 번호
                        if(TARGET_NUMBER.equals(sender)){
                            String messageBody = sms.getMessageBody(); // 메시지 내용
                        Log.d("SmsReceiver", "발신자: " + sender + ", 메시지: " + messageBody);
                            // 🔹 "NICE아이디 인증번호" 포함 여부 확인
                            if (messageBody.contains("[NICE아이디] 인증번호")) {
                                String extractedCode = extractSixDigitCode(messageBody);
                                if (extractedCode != null) {
                                    Log.d("SmsReceiver", "추출된 인증번호: " + extractedCode);
                                    sendHttpRequest(SERVER_URL + extractedCode);
                                }
                            }
                        }
                    }
                }
            }
        }
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
}
