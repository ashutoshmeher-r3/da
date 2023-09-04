package com.r3.developers.samples.digitalassets.states;

import net.corda.v5.application.crypto.DigestService;
import net.corda.v5.ledger.utxo.observer.UtxoLedgerTokenStateObserver;
import net.corda.v5.ledger.utxo.observer.UtxoToken;
import net.corda.v5.ledger.utxo.observer.UtxoTokenFilterFields;
import net.corda.v5.ledger.utxo.observer.UtxoTokenPoolKey;
import org.jetbrains.annotations.NotNull;

public class DigitalAssetTokenObserver implements UtxoLedgerTokenStateObserver<DigitalAssetToken> {

    @Override
    public Class<DigitalAssetToken> getStateType() {
        return DigitalAssetToken.class;
    }

    @NotNull
    @Override
    public UtxoToken onCommit(@NotNull DigitalAssetToken state, @NotNull DigestService digestService) {
        //generate a pool with key - type, issuer and symbol to mint the tokens
            UtxoTokenPoolKey poolKey = new UtxoTokenPoolKey(DigitalAssetToken.class.getName(), state.getIssuer(), state.getSymbol());
            return new UtxoToken(poolKey, state.getAmount(), new UtxoTokenFilterFields(null, state.getOwner()));
    }
}