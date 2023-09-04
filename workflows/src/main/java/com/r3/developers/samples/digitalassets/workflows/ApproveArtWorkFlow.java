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
import java.util.stream.Collectors;

@InitiatingFlow(protocol = "approve-art")
public class ApproveArtWorkFlow implements ClientStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(ApproveArtWorkFlow.class);

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
        try {

            ApproveArtWorkFlowArgs flowArgs =
                    requestBody.getRequestBodyAs(jsonMarshallingService, ApproveArtWorkFlowArgs.class);
            MemberInfo myInfo = memberLookup.myInfo();

            List<StateAndRef<DigitalAssetToken>> tokenStateAndRefs =
                    ledgerService.findUnconsumedStatesByType(DigitalAssetToken.class)
                    .stream().filter(assetSR ->
                            assetSR.getState().getContractState().getAssetRef().toString()
                                    .equals(flowArgs.getAssetRef()))
                    .collect(Collectors.toList());
            if(tokenStateAndRefs.size() != 1){
                throw new CordaRuntimeException("Multiple or zero Token states with Asset Ref " + flowArgs.getAssetRef() + " found");
            }
            StateAndRef<DigitalAssetToken> tokenStateAndRef = tokenStateAndRefs.get(0);

            TokenStatus status = null;
            if(flowArgs.getAction().equals("APPROVE")){
                status = TokenStatus.APPROVED;
            }else if(flowArgs.getAction().equals("REJECT")){
                status = TokenStatus.REJECTED;
            }else{
                throw new CordaRuntimeException("Token should either be Approved or Rejected");
            }

            DigitalAssetToken token = new DigitalAssetToken(
                    tokenStateAndRef.getState().getContractState().getAssetRef(),
                    tokenStateAndRef.getState().getContractState().getIssuer(),
                    tokenStateAndRef.getState().getContractState().getOwner(),
                    tokenStateAndRef.getState().getContractState().getSymbol(),
                    tokenStateAndRef.getState().getContractState().getAmount(),
                    status,
                    tokenStateAndRef.getState().getContractState().participants
            );

            UtxoTransactionBuilder txBuilder = ledgerService.createTransactionBuilder()
                    .setNotary(tokenStateAndRef.getState().getNotaryName())
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addInputState(tokenStateAndRef.getRef())
                    .addOutputStates(token)
                    .addCommand(new DigitalAssetTokenContract.Approval())
                    .addSignatories(myInfo.getLedgerKeys().get(0));

            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction();

            UtxoSignedTransaction finalizedSignedTransaction =
                    ledgerService.finalize(signedTransaction, Collections.emptyList()).getTransaction();

            return finalizedSignedTransaction.getId().toString();


        }catch (Exception e){
            log.warn("Failed to process flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}
