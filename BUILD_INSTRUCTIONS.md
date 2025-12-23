# PixL Store & VoiD FX 代用アプリ - ビルド手順

## 📱 **完成したアプリの機能**

### 🏪 **PixL Store機能**
- Fortnite & VoiD FX管理
- APK自動ダウンロード
- 手動URL追加機能
- アプリ状態管理
- インストール・起動機能

### ⚙️ **VoiD FX機能**
- **FPS設定**: 30/60/120/144/165/180/200/240/360fps対応
- **グラフィック設定**:
  - FPS表示 ON/OFF
  - DLSS Frame Generation
  - アンチエイリアシング
  - Vsync
  - コスメティックストリーミング
  - ビデオ再生
  - モーションブラー
  - 草表示
- **GameUserSettings.ini自動変更**
- **設定の保存・復元**

## 🔨 **ビルド手順**

### 1. 必要なソフトウェア
```bash
# Android Studio最新版をインストール
# Java 8+ (OpenJDK推奨)
# Android SDK API Level 26-34
```

### 2. プロジェクトセットアップ
```bash
# プロジェクトフォルダに移動
cd /mnt/user-data/outputs/pixl_project/pixl_store

# Android Studioでプロジェクトを開く
# File > Open > pixl_store フォルダを選択
```

### 3. 依存関係の同期
```bash
# Android Studioで以下を実行:
# Build > Make Project
# または Gradle Sync ボタンをクリック
```

### 4. APKビルド
```bash
# リリース版APKの作成:
# Build > Generate Signed Bundle/APK
# または
./gradlew assembleRelease

# デバッグ版APK (開発・テスト用):
./gradlew assembleDebug
```

### 5. 生成されるファイル
```
app/build/outputs/apk/release/app-release.apk  # リリース版
app/build/outputs/apk/debug/app-debug.apk      # デバッグ版
```

## 📋 **使用方法**

### 🚀 **初回セットアップ**
1. APKをインストール
2. 必要な権限を許可
3. PixL Storeを起動

### 🎮 **Fortnite 120fps化手順**
1. **端末設定**: 120Hz表示モードを有効
2. **Fortniteインストール**: PixL Store → Fortnite → ダウンロード
3. **初回起動**: Fortnite起動 → ログイン → ゲーム設定で60fps・低画質設定
4. **VoiD FX設定**: PixL Store → VoiD FX → 希望FPS選択 → 設定適用
5. **ゲームプレイ**: Fortnite起動 (⚠️ゲーム内設定メニューは開かない)

### ⚙️ **カスタマイズ**
- **手動APK追加**: メイン画面の「+手動追加」ボタン
- **FPS変更**: VoiD FX設定で再選択・再適用
- **設定リセット**: アプリ設定から初期化

## ⚠️ **重要な注意事項**

### 🚫 **制限事項**
- Epic Gamesの利用規約違反の可能性
- アカウントBANのリスク
- 一部デバイスでは動作しない可能性
- ゲーム内設定メニューを開くと120fps設定がリセット

### 🔒 **セキュリティ**
- 信頼できるAPKのみ使用
- 定期的な設定バックアップ推奨
- 公式Fortniteの更新に注意

### 🛠 **トラブルシューティング**
- **設定ファイルが見つからない**: Fortniteを一度起動してログイン
- **権限エラー**: アプリ設定で全ての権限を許可
- **インストール失敗**: 提供元不明のアプリを許可

## 📞 **サポート**

作成者: こうご用カスタム版
バージョン: 1.0
作成日: 2025年12月8日

**注意**: 本アプリの使用により発生する問題について、開発者は一切の責任を負いません。自己責任でご利用ください。