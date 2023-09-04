package com.r3.developers.samples.digitalassets.states;

import com.r3.developers.samples.digitalassets.contracts.DigitalAssetTokenContract;
import net.corda.v5.crypto.SecureHash;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@BelongsToContract(DigitalAssetTokenContract.class)
public class DigitalAssetToken implements ContractState {

    private UUID assetRef;
    private SecureHash issuer;
    private SecureHash owner;
    private String symbol;
    private BigDecimal amount;
    private TokenStatus status;
    public List<PublicKey> participants;

    public DigitalAssetToken(UUID assetRef, SecureHash issuer, SecureHash owner,
                             String symbol, BigDecimal amount, TokenStatus status,
                             List<PublicKey> participants) {
        this.assetRef = assetRef;
        this.issuer = issuer;
        this.owner = owner;
        this.symbol = symbol;
        this.amount = amount;
        this.status = status;
        this.participants = participants;
    }

    public UUID getAssetRef() {
        return assetRef;
    }

    public SecureHash getIssuer() {
        return issuer;
    }

    public SecureHash getOwner() {
        return owner;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TokenStatus getStatus() {
        return status;
    }

    @NotNull
    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }

}
