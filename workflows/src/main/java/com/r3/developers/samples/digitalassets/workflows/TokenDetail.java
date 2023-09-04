package com.r3.developers.samples.digitalassets.workflows;

import com.r3.developers.samples.digitalassets.states.TokenStatus;
import net.corda.v5.crypto.SecureHash;

import java.math.BigDecimal;
import java.util.UUID;

public class TokenDetail {
    private String  assetRef;
    private String issuer;
    private String owner;
    private String symbol;
    private String amount;
    private String status;

    public TokenDetail(String assetRef, String issuer, String owner,
                       String symbol, String amount, String status) {
        this.assetRef = assetRef;
        this.issuer = issuer;
        this.owner = owner;
        this.symbol = symbol;
        this.amount = amount;
        this.status = status;
    }

    public String getAssetRef() {
        return assetRef;
    }

    public void setAssetRef(String assetRef) {
        this.assetRef = assetRef;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
