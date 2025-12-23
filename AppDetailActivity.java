package com.kogo.pixlstore;

import android.app.DownloadManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class AppDetailActivity extends AppCompatActivity {

    private String packageName;
    private String downloadUrl;
    private String appName;
    private Button actionButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private long downloadId = -1;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadId) {
                progressBar.setVisibility(View.GONE);
                actionButton.setEnabled(true);
                actionButton.setText("インストール");
                statusText.setText("ダウンロード完了 - インストール準備完了");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        initializeViews();
        loadAppData();
        updateUI();

        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void initializeViews() {
        ImageView appIcon = findViewById(R.id.appIcon);
        TextView appNameText = findViewById(R.id.appName);
        TextView appDescription = findViewById(R.id.appDescription);
        statusText = findViewById(R.id.statusText);
        actionButton = findViewById(R.id.actionButton);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        appName = intent.getStringExtra("app_name");
        packageName = intent.getStringExtra("package_name");
        downloadUrl = intent.getStringExtra("download_url");

        appNameText.setText(appName);
        appDescription.setText(intent.getStringExtra("description"));
        statusText.setText(intent.getStringExtra("status"));

        // アイコン設定
        if ("Fortnite".equals(appName)) {
            appIcon.setImageResource(R.drawable.fortnite_icon);
        } else if ("VoiD FX".equals(appName)) {
            appIcon.setImageResource(R.drawable.voidfx_icon);
        } else {
            appIcon.setImageResource(R.drawable.ic_android);
        }

        actionButton.setOnClickListener(this::onActionButtonClick);
    }

    private void loadAppData() {
        // アプリの詳細情報をロード
    }

    private void updateUI() {
        boolean isInstalled = isAppInstalled(packageName);
        File apkFile = getApkFile();

        if (isInstalled) {
            actionButton.setText("起動");
            actionButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (apkFile.exists()) {
            actionButton.setText("インストール");
            actionButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            actionButton.setText("ダウンロード");
            actionButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        }
    }

    private void onActionButtonClick(View view) {
        boolean isInstalled = isAppInstalled(packageName);
        File apkFile = getApkFile();

        if (isInstalled) {
            launchApp();
        } else if (apkFile.exists()) {
            installApp(apkFile);
        } else {
            if (downloadUrl.isEmpty()) {
                showVoidFxInfo();
            } else {
                downloadApp();
            }
        }
    }

    private void launchApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(this, "アプリを起動できませんでした", Toast.LENGTH_SHORT).show();
        }
    }

    private void installApp(File apkFile) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(this, "com.kogo.pixlstore.provider", apkFile);
            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installIntent.setData(apkUri);
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            startActivity(installIntent);
        } else {
            Uri apkUri = Uri.fromFile(apkFile);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);
        }
    }

    private void downloadApp() {
        progressBar.setVisibility(View.VISIBLE);
        actionButton.setEnabled(false);
        statusText.setText("ダウンロード中...");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        
        request.setTitle(appName + " ダウンロード");
        request.setDescription("APKファイルをダウンロード中...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadId = downloadManager.enqueue(request);
    }

    private void showVoidFxInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("VoiD FX について");
        builder.setMessage("VoiD FXは本アプリに統合されています。\n\n" +
                          "使用方法：\n" +
                          "1. まずFortniteをインストール\n" +
                          "2. Fortniteを起動してログイン\n" +
                          "3. メニューからVoiD FX設定を開く\n" +
                          "4. FPS設定を変更\n" +
                          "5. 設定を適用");
        
        builder.setPositiveButton("VoiD FX設定を開く", (dialog, which) -> {
            Intent intent = new Intent(this, VoidFxActivity.class);
            startActivity(intent);
        });
        
        builder.setNegativeButton("閉じる", null);
        builder.show();
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private File getApkFile() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(downloadsDir, appName + ".apk");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException e) {
            // レシーバーが登録されていない場合
        }
    }
}