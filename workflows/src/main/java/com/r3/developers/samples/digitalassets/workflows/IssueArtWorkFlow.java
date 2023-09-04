package com.r3.developers.samples.digitalassets.workflows;

import com.r3.developers.samples.digitalassets.contracts.AssetContract;
import com.r3.developers.samples.digitalassets.states.ArtWork;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

public class IssueArtWorkFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(IssueArtWorkFlow.class);

    @CordaInject
    private JsonMarshallingService jsonMarshallingService;
    @CordaInject
    private MemberLookup memberLookup;
    @CordaInject
    private NotaryLookup notaryLookup;
    @CordaInject
    private UtxoLedgerService ledgerService;

    @NotNull
    @Override
    @Suspendable
    public String call(@NotNull ClientRequestBody requestBody) {
        try{

            IssueArtWorkFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, IssueArtWorkFlowArgs.class);

            MemberInfo myInfo = memberLookup.myInfo();
            NotaryInfo notary = notaryLookup.getNotaryServices().iterator().next();

            ArtWork artWork = new ArtWork(
                    UUID.randomUUID(),
                    flowArgs.getTitle(),
                    flowArgs.getDescription(),
                    flowArgs.getArtist(),
                    myInfo.getName(),
                    Collections.singletonList(myInfo.getLedgerKeys().get(0))
            );

            UtxoTransactionBuilder transactionBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(notary.getName())
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addOutputStates(artWork)
                    .addCommand(new AssetContract.Create())
                    .addSignatories(myInfo.getLedgerKeys().get(0));

            UtxoSignedTransaction signedTransaction = transactionBuilder.toSignedTransaction();
            UtxoSignedTransaction finalizedSignedTransaction =
                    ledgerService.finalize(signedTransaction, Collections.emptyList()).getTransaction();

            String result = finalizedSignedTransaction.getId().toString();
            log.info("Success! Response: " + result);
            return result;

        }catch(Exception e){
            log.warn("Failed to process flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}

/*
RequestBody for triggering the flow via REST:
{
    "clientRequestId": "art-2",
    "flowClassName": "com.r3.developers.samples.digitalassets.workflows.IssueArtWorkFlow",
    "requestBody": {
        "title": "Test",
        "description": "Test",
        "artist": "Test"
    }
}
 */