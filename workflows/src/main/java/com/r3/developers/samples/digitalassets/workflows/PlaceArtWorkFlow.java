package com.r3.developers.samples.digitalassets.workflows;

import com.r3.developers.samples.digitalassets.contracts.AssetContract;
import com.r3.developers.samples.digitalassets.contracts.DigitalAssetTokenContract;
import com.r3.developers.samples.digitalassets.states.ArtWork;
import com.r3.developers.samples.digitalassets.states.DigitalAssetToken;
import com.r3.developers.samples.digitalassets.states.TokenStatus;
import net.corda.v5.application.crypto.DigestService;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.InitiatingFlow;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.application.messaging.FlowMessaging;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.crypto.SecureHash;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.corda.v5.crypto.DigestAlgorithmName.SHA2_256;

@InitiatingFlow(protocol = "place-art")
public class PlaceArtWorkFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(PlaceArtWorkFlow.class);

    @CordaInject
    private JsonMarshallingService jsonMarshallingService;
    @CordaInject
    private MemberLookup memberLookup;
    @CordaInject
    private NotaryLookup notaryLookup;
    @CordaInject
    private UtxoLedgerService ledgerService;
    @CordaInject
    public DigestService digestService;
    @CordaInject
    private FlowMessaging flowMessaging;

    @NotNull
    @Override
    @Suspendable
    public String call(@NotNull ClientRequestBody requestBody) {
        try{
            PlaceArtFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, PlaceArtFlowArgs.class);
            MemberInfo myInfo = memberLookup.myInfo();
            MemberInfo issuer = Objects.requireNonNull(
                    memberLookup.lookup(flowArgs.getIssuer()),
                    "MemberLookup can't find issuer specified in flow arguments."
            );

            List<StateAndRef<ArtWork>> artStateAndRefs = ledgerService.findUnconsumedStatesByType(ArtWork.class)
                    .stream().filter(assetSR ->
                            assetSR.getState().getContractState().getAssetId().toString()
                                    .equals(flowArgs.getAssetId()))
                    .collect(Collectors.toList());
            if(artStateAndRefs.size() != 1){
                throw new CordaRuntimeException("Multiple or zero Art states with id " + flowArgs.getAssetId() + " found");
            }
            StateAndRef<ArtWork> artStateAndRef = artStateAndRefs.get(0);

            DigitalAssetToken token = new DigitalAssetToken(
                    artStateAndRef.getState().getContractState().getAssetId(),
                    getSecureHash(flowArgs.getIssuer().getCommonName()),
                    getSecureHash(myInfo.getName().getCommonName()),
                    flowArgs.getSymbol(),
                    new BigDecimal(flowArgs.getAmount()),
                    TokenStatus.DRAFT,
                    Arrays.asList(myInfo.getLedgerKeys().get(0), issuer.getLedgerKeys().get(0))
            );

            ArtWork artWork = new ArtWork(
                    artStateAndRef.getState().getContractState().getAssetId(),
                    artStateAndRef.getState().getContractState().getTitle(),
                    artStateAndRef.getState().getContractState().getDescription(),
                    artStateAndRef.getState().getContractState().getArtist(),
                    artStateAndRef.getState().getContractState().getOwner(),
                    Arrays.asList(myInfo.getLedgerKeys().get(0), issuer.getLedgerKeys().get(0))
            );

            UtxoTransactionBuilder txBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(artStateAndRef.getState().getNotaryName())
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addInputState(artStateAndRef.getRef())
                    .addOutputStates(artWork, token)
                    .addCommand(new AssetContract.Place())
                    .addCommand(new DigitalAssetTokenContract.Create())
                    .addSignatories(myInfo.getLedgerKeys().get(0));

            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction();

            UtxoSignedTransaction finalizedSignedTransaction =
                    ledgerService.finalize(signedTransaction,
                            Collections.singletonList(flowMessaging.initiateFlow(issuer.getName()))).getTransaction();

            return finalizedSignedTransaction.getId().toString();

        }catch (Exception e){
            log.warn("Failed to process flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }

    @Suspendable
    private SecureHash getSecureHash(String commonName) {
        return digestService.hash(commonName.getBytes(), SHA2_256);
    }
}

/*
RequestBody for triggering the flow via REST:
{
    "clientRequestId": "place-art-2",
    "flowClassName": "com.r3.developers.samples.digitalassets.workflows.PlaceArtWorkFlow",
    "requestBody": {
        "assetId": "7c042f8e-3da8-4f11-b473-d0b2c3cfab24",
        "symbol": "ART-1",
        "amount": 500,
        "issuer": "CN=Bank, OU=Test Dept, O=R3, L=London, C=GB"
    }
}
 */