package com.kogo.pixlstore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class VoidFxActivity extends AppCompatActivity {

    private Spinner fpsSpinner;
    private Switch showFpsSwitch;
    private Switch dlssSwitch;
    private Switch cosmeticStreamingSwitch;
    private Switch antiAliasingSwitch;
    private Switch vsyncSwitch;
    private Switch videoPlaybackSwitch;
    private Switch motionBlurSwitch;
    private Switch grassSwitch;
    private TextView warningText;
    private Button applyButton;

    private SharedPreferences preferences;
    
    // FPSモード設定
    private final String[] fpsOptions = {"30 FPS", "60 FPS", "120 FPS", "144 FPS", "165 FPS", "180 FPS", "200 FPS", "240 FPS", "360 FPS"};
    private final String[] fpsModes = {"Mode_30Fps", "Mode_60Fps", "Mode_120Fps", "Mode_144Fps", "Mode_165Fps", "Mode_180Fps", "Mode_200Fps", "Mode_240Fps", "Mode_360Fps"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_fx);

        initializeViews();
        setupSpinners();
        loadSettings();
    }

    private void initializeViews() {
        fpsSpinner = findViewById(R.id.fpsSpinner);
        showFpsSwitch = findViewById(R.id.showFpsSwitch);
        dlssSwitch = findViewById(R.id.dlssSwitch);
        cosmeticStreamingSwitch = findViewById(R.id.cosmeticStreamingSwitch);
        antiAliasingSwitch = findViewById(R.id.antiAliasingSwitch);
        vsyncSwitch = findViewById(R.id.vsyncSwitch);
        videoPlaybackSwitch = findViewById(R.id.videoPlaybackSwitch);
        motionBlurSwitch = findViewById(R.id.motionBlurSwitch);
        grassSwitch = findViewById(R.id.grassSwitch);
        warningText = findViewById(R.id.warningText);
        applyButton = findViewById(R.id.applyButton);

        preferences = getSharedPreferences("VoidFxSettings", MODE_PRIVATE);

        // 警告テキスト設定
        warningText.setText("⚠️ 重要な注意事項 ⚠️\n" +
                          "• 設定適用後は、ゲーム内の設定メニューを開かないでください\n" +
                          "• 設定がリセットされる可能性があります\n" +
                          "• 本機能の使用によるアカウントBANは自己責任です");

        applyButton.setOnClickListener(this::onApplySettings);
    }

    private void setupSpinners() {
        ArrayAdapter<String> fpsAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, fpsOptions);
        fpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fpsSpinner.setAdapter(fpsAdapter);
    }

    private void loadSettings() {
        // 保存された設定をロード
        int fpsIndex = preferences.getInt("fps_index", 2); // デフォルト120fps
        fpsSpinner.setSelection(fpsIndex);

        showFpsSwitch.setChecked(preferences.getBoolean("show_fps", true));
        dlssSwitch.setChecked(preferences.getBoolean("dlss", false));
        cosmeticStreamingSwitch.setChecked(preferences.getBoolean("cosmetic_streaming", true));
        antiAliasingSwitch.setChecked(preferences.getBoolean("anti_aliasing", false));
        vsyncSwitch.setChecked(preferences.getBoolean("vsync", false));
        videoPlaybackSwitch.setChecked(preferences.getBoolean("video_playback", true));
        motionBlurSwitch.setChecked(preferences.getBoolean("motion_blur", false));
        grassSwitch.setChecked(preferences.getBoolean("grass", false));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("fps_index", fpsSpinner.getSelectedItemPosition());
        editor.putBoolean("show_fps", showFpsSwitch.isChecked());
        editor.putBoolean("dlss", dlssSwitch.isChecked());
        editor.putBoolean("cosmetic_streaming", cosmeticStreamingSwitch.isChecked());
        editor.putBoolean("anti_aliasing", antiAliasingSwitch.isChecked());
        editor.putBoolean("vsync", vsyncSwitch.isChecked());
        editor.putBoolean("video_playback", videoPlaybackSwitch.isChecked());
        editor.putBoolean("motion_blur", motionBlurSwitch.isChecked());
        editor.putBoolean("grass", grassSwitch.isChecked());
        editor.apply();
    }

    private void onApplySettings(View view) {
        // 設定確認ダイアログ
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("設定の適用");
        builder.setMessage("選択した設定をFortniteに適用しますか？\n\n" +
                          "適用後は必ずゲーム内設定メニューを開かないでください。");

        builder.setPositiveButton("適用", (dialog, which) -> {
            applyFortniteSettings();
        });

        builder.setNegativeButton("キャンセル", null);
        builder.show();
    }

    private void applyFortniteSettings() {
        try {
            // GameUserSettings.iniファイルのパスを検索
            String fortniteSettingsPath = findFortniteSettingsFile();
            
            if (fortniteSettingsPath == null) {
                showErrorDialog("Fortniteの設定ファイルが見つかりませんでした。\n\n" +
                              "1. Fortniteがインストールされているか確認してください\n" +
                              "2. Fortniteを一度起動してログインしてください\n" +
                              "3. 再度お試しください");
                return;
            }

            // 設定を適用
            modifyGameUserSettings(fortniteSettingsPath);
            saveSettings();

            // 成功メッセージ
            AlertDialog.Builder successBuilder = new AlertDialog.Builder(this);
            successBuilder.setTitle("設定適用完了");
            successBuilder.setMessage("✅ Fortniteの設定が正常に変更されました！\n\n" +
                                    "⚠️ 重要：\n" +
                                    "• ゲーム内の設定メニューを開かないでください\n" +
                                    "• 設定がリセットされてしまいます\n\n" +
                                    "選択したFPS: " + fpsOptions[fpsSpinner.getSelectedItemPosition()]);
            
            successBuilder.setPositiveButton("OK", null);
            successBuilder.show();

        } catch (Exception e) {
            showErrorDialog("設定の適用中にエラーが発生しました：\n" + e.getMessage());
        }
    }

    private String findFortniteSettingsFile() {
        // 一般的なFortnite設定ファイルのパスを検索
        String[] possiblePaths = {
            "/data/data/com.epicgames.fortnite/files/UE4Game/FortniteGame/FortniteGame/Saved/Config/Android/GameUserSettings.ini",
            "/storage/emulated/0/Android/data/com.epicgames.fortnite/files/UE4Game/FortniteGame/FortniteGame/Saved/Config/Android/GameUserSettings.ini",
            "/sdcard/Android/data/com.epicgames.fortnite/files/UE4Game/FortniteGame/FortniteGame/Saved/Config/Android/GameUserSettings.ini"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.canRead() && file.canWrite()) {
                return path;
            }
        }

        return null;
    }

    private void modifyGameUserSettings(String filePath) throws IOException {
        File settingsFile = new File(filePath);
        
        // ファイルを読み込み
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        
        // 設定を変更
        Map<String, String> settings = new HashMap<>();
        
        // FPS設定
        String selectedFpsMode = fpsModes[fpsSpinner.getSelectedItemPosition()];
        settings.put("MobileFPSMode", selectedFpsMode);
        
        // その他の設定
        settings.put("bShowFPS", showFpsSwitch.isChecked() ? "True" : "False");
        settings.put("bEnableDLSSFrameGeneration", dlssSwitch.isChecked() ? "True" : "False");
        settings.put("CosmeticStreamingEnabled", cosmeticStreamingSwitch.isChecked() ? "CodeSet_Enabled" : "CodeSet_Disabled");
        settings.put("bUseVSync", vsyncSwitch.isChecked() ? "True" : "False");
        settings.put("bAllowVideoPlayback", videoPlaybackSwitch.isChecked() ? "True" : "False");
        settings.put("bMotionBlur", motionBlurSwitch.isChecked() ? "True" : "False");
        settings.put("bShowGrass", grassSwitch.isChecked() ? "True" : "False");
        
        // アンチエイリアシング設定
        if (antiAliasingSwitch.isChecked()) {
            settings.put("FortAntiAliasingMethod", "TSREpic");
        } else {
            settings.put("FortAntiAliasingMethod", "None");
        }

        // 設定を適用
        for (Map.Entry<String, String> setting : settings.entrySet()) {
            String key = setting.getKey();
            String value = setting.getValue();
            
            String pattern = key + "=([^\r\n]*)";
            String replacement = key + "=" + value;
            
            if (content.contains(key + "=")) {
                content = content.replaceAll(pattern, replacement);
            } else {
                // 設定が存在しない場合は追加
                int insertPos = content.indexOf("[/Script/FortniteGame.FortGameUserSettings]");
                if (insertPos != -1) {
                    insertPos = content.indexOf("\n", insertPos) + 1;
                    content = content.substring(0, insertPos) + key + "=" + value + "\n" + content.substring(insertPos);
                }
            }
        }

        // ファイルに書き戻し
        try (FileWriter writer = new FileWriter(settingsFile)) {
            writer.write(content);
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("エラー");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public void showHelpDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("VoiD FX ヘルプ");
        builder.setMessage("🎮 使用方法：\n\n" +
                          "1️⃣ 端末設定で120Hz表示を有効にする\n" +
                          "2️⃣ Fortniteをインストール・起動してログイン\n" +
                          "3️⃣ 希望のFPS設定を選択\n" +
                          "4️⃣ グラフィック設定を調整\n" +
                          "5️⃣ 「設定を適用」をタップ\n" +
                          "6️⃣ Fortniteを起動してプレイ\n\n" +
                          "⚠️ ゲーム内設定メニューは開かないでください！");
        
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}