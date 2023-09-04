package com.r3.developers.samples.digitalassets.workflows;

import com.r3.developers.samples.digitalassets.states.DigitalAssetToken;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ListTokenFlow implements ClientStartableFlow {

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public UtxoLedgerService ledgerService;

    @NotNull
    @Override
    @Suspendable
    public String call(@NotNull ClientRequestBody requestBody) {
        List<StateAndRef<DigitalAssetToken>> assetStateAndRef =
                ledgerService.findUnconsumedStatesByType(DigitalAssetToken.class);
        List<TokenDetail> tokentDetailsList =
                assetStateAndRef.stream().map(it -> {
                    DigitalAssetToken assetState = it.getState().getContractState();
                    return new TokenDetail(
                            assetState.getAssetRef().toString(),
                            assetState.getIssuer().toString(),
                            assetState.getOwner().toString(),
                            assetState.getSymbol(),
                            assetState.getAmount().toString(),
                            assetState.getStatus().toString()
                    );
                }).collect(Collectors.toList());

        return jsonMarshallingService.format(tokentDetailsList);
    }
}
