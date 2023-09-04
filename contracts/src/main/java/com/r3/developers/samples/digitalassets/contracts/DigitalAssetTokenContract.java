package com.r3.developers.samples.digitalassets.contracts;

import com.r3.developers.samples.digitalassets.states.DigitalAssetToken;
import com.r3.developers.samples.digitalassets.states.TokenStatus;
import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.Contract;
import net.corda.v5.ledger.utxo.ContractState;
import net.corda.v5.ledger.utxo.VisibilityChecker;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class DigitalAssetTokenContract implements Contract {

    public static class Create implements Command { }
    public static class Approval implements Command { }

    @Override
    public void verify(@NotNull UtxoLedgerTransaction transaction) {

    }

    @Override
    public boolean isVisible(@NotNull ContractState state, @NotNull VisibilityChecker checker) {
        if(state instanceof DigitalAssetToken){
            DigitalAssetToken token = (DigitalAssetToken) state;
            if(token.getStatus().equals(TokenStatus.APPROVED)){
                return true;
            }
        }
        return checker.containsMySigningKeys(state.getParticipants());
    }
}
