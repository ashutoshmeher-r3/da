package com.r3.developers.samples.digitalassets.workflows;

public class ApproveArtWorkFlowArgs {
    private String assetRef;
    private String action; // APPROVE/ REJECR

    public ApproveArtWorkFlowArgs(String assetRef, String action) {
        this.assetRef = assetRef;
        this.action = action;
    }

    public String getAssetRef() {
        return assetRef;
    }

    public void setAssetRef(String assetRef) {
        this.assetRef = assetRef;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
