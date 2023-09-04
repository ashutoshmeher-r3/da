package com.r3.developers.samples.digitalassets.workflows;

import net.corda.v5.base.types.MemberX500Name;

import java.util.UUID;

public class AssetDetail {

    private String assetId;
    private String owner;
    private  String title;
    private  String description;
    private  String artist;

    public AssetDetail() {
    }

    public AssetDetail(String assetId, String owner, String title, String description, String artist) {
        this.assetId = assetId;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.artist = artist;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
