package com.r3.developers.samples.digitalassets.workflows;

import net.corda.v5.base.types.MemberX500Name;

public class PlaceArtFlowArgs {

    private String assetId;
    private String symbol;
    private int amount;
    private MemberX500Name issuer;

    public PlaceArtFlowArgs() {
    }

    public PlaceArtFlowArgs(String assetId, String symbol, int amount, MemberX500Name issuer) {
        this.assetId = assetId;
        this.symbol = symbol;
        this.amount = amount;
        this.issuer = issuer;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getAmount() {
        return amount;
    }

    public MemberX500Name getIssuer() {
        return issuer;
    }
}
