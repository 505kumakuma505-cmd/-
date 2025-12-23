package com.kogo.pixlstore;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    private List<AppItem> appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupApps();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        appList = new ArrayList<>();
        appAdapter = new AppAdapter(appList, this::onAppItemClick);
        recyclerView.setAdapter(appAdapter);
    }

    private void setupApps() {
        // Fortnite
        AppItem fortnite = new AppItem(
            "Fortnite",
            "com.epicgames.fortnite",
            "バトルロワイヤルゲーム - 120fps対応版",
            R.drawable.fortnite_icon,
            getAppStatus("com.epicgames.fortnite"),
            "https://www.epicgames.com/fortnite/en-US/mobile/android/get-started"
        );

        // VoiD FX
        AppItem voidfx = new AppItem(
            "VoiD FX",
            "com.kogo.voidfx",
            "Fortnite FPS設定ツール",
            R.drawable.voidfx_icon,
            getAppStatus("com.kogo.voidfx"),
            ""
        );

        appList.add(fortnite);
        appList.add(voidfx);
        appAdapter.notifyDataSetChanged();
    }

    private String getAppStatus(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            return "インストール済み v" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "未インストール";
        }
    }

    private void onAppItemClick(AppItem app) {
        Intent intent = new Intent(this, AppDetailActivity.class);
        intent.putExtra("app_name", app.getName());
        intent.putExtra("package_name", app.getPackageName());
        intent.putExtra("description", app.getDescription());
        intent.putExtra("status", app.getStatus());
        intent.putExtra("download_url", app.getDownloadUrl());
        startActivity(intent);
    }

    public void showManualUrlDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("手動APK追加");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("アプリ名");
        layout.addView(nameInput);

        final EditText urlInput = new EditText(this);
        urlInput.setHint("APK URL");
        layout.addView(urlInput);

        builder.setView(layout);

        builder.setPositiveButton("追加", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String url = urlInput.getText().toString().trim();
            
            if (!name.isEmpty() && !url.isEmpty()) {
                AppItem customApp = new AppItem(
                    name,
                    "custom." + name.toLowerCase().replace(" ", ""),
                    "手動追加アプリ",
                    R.drawable.ic_android,
                    "未インストール",
                    url
                );
                appList.add(customApp);
                appAdapter.notifyDataSetChanged();
                Toast.makeText(this, "アプリが追加されました", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "すべての項目を入力してください", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("キャンセル", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // アプリ状態を更新
        for (int i = 0; i < appList.size(); i++) {
            AppItem app = appList.get(i);
            app.setStatus(getAppStatus(app.getPackageName()));
        }
        appAdapter.notifyDataSetChanged();
    }
}