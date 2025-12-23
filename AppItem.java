package com.kogo.pixlstore;

public class AppItem {
    private String name;
    private String packageName;
    private String description;
    private int iconResource;
    private String status;
    private String downloadUrl;

    public AppItem(String name, String packageName, String description, 
                   int iconResource, String status, String downloadUrl) {
        this.name = name;
        this.packageName = packageName;
        this.description = description;
        this.iconResource = iconResource;
        this.status = status;
        this.downloadUrl = downloadUrl;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getIconResource() { return iconResource; }
    public void setIconResource(int iconResource) { this.iconResource = iconResource; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}