package com.r3.developers.samples.digitalassets.contracts;

import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.Contract;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class AssetContract implements Contract {

    public static class Create implements Command { }
    public static class Place implements Command { }

    @Override
    public void verify(@NotNull UtxoLedgerTransaction transaction) {

    }
}
