package com.r3.developers.samples.digitalassets.workflows;

import net.corda.v5.application.flows.*;
import net.corda.v5.application.messaging.FlowSession;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(protocol = "approve-art")
public class ApproveArtWorkFlowResponder implements ResponderFlow {

    @CordaInject
    private UtxoLedgerService utxoLedgerService;

    @Override
    @Suspendable
    public void call(@NotNull FlowSession session) {
        utxoLedgerService.receiveFinality(session, transaction -> {});
    }
}
