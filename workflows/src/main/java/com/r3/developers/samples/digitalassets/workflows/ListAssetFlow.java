package com.r3.developers.samples.digitalassets.workflows;

import com.r3.developers.samples.digitalassets.states.ArtWork;
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

public class ListAssetFlow implements ClientStartableFlow {
    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public UtxoLedgerService ledgerService;

    //TODO : Make Generic for all asset
    @Override
    @Suspendable
    public String call(ClientRequestBody requestBody) {

        List<StateAndRef<ArtWork>> assetStateAndRef = ledgerService.findUnconsumedStatesByType(ArtWork.class);
        List<AssetDetail> assetDetailsList =
                assetStateAndRef.stream().map(it -> {
                    ArtWork assetState = it.getState().getContractState();
                    return new AssetDetail(
                            assetState.getAssetId().toString(),
                            assetState.getOwner().toString(),
                            assetState.getTitle(),
                            assetState.getDescription(),
                            assetState.getArtist()
                    );
                }).collect(Collectors.toList());

        return jsonMarshallingService.format(assetDetailsList);
    }
}
