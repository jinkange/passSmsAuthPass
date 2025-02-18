package com.jinkange.passsmsauthpass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private EditText editPhoneNumber;
    private TextView textPhoneNumber;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_PHONE_NUMBER = "phone_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "App started");
        if (!permissionGrantred()) {
            Intent gt= new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(gt);
        }
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnShow = findViewById(R.id.btnShow);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 저장된 번호가 있으면 표시
        String savedNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, "");
        if (!savedNumber.isEmpty()) {
            textPhoneNumber.setText("저장된 번호: " + savedNumber);
        }

        btnSave.setOnClickListener(view -> {
            String phoneNumber = editPhoneNumber.getText().toString();
            sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply();
            editPhoneNumber.setText(""); // 입력 필드 초기화
        });

        btnShow.setOnClickListener(view -> {
            String savedPhone = sharedPreferences.getString(KEY_PHONE_NUMBER, "저장된 번호 없음");
            textPhoneNumber.setText("저장된 번호: " + savedPhone);
        });
    }

    private boolean permissionGrantred() {
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (sets != null && sets.contains(getPackageName())) {
            return true;
        } else {
            return false;
        }
    }
}