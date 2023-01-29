package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract;
import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.*;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.common.Party;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.*;

public class CreateNewChatFlow implements RPCStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(CreateNewChatFlow.class);

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public MemberLookup memberLookup;

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public NotaryLookup notaryLookup;

    @CordaInject
    public FlowEngine flowEngine;


    @Suspendable
    @Override
    public String call( RPCRequestData requestBody) {

        log.info("CreateNewChatFlow.call() called");

        try {
            CreateNewChatFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, CreateNewChatFlowArgs.class);

            MemberInfo myInfo = memberLookup.myInfo();
            MemberInfo otherMember = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getOtherMember())),
                    "MemberLookup can't find otherMember specified in flow arguments."
            );

            ChatState chatState = new ChatState(
                    UUID.randomUUID(),
                    flowArgs.getChatName(),
                    myInfo.getName(),
                    flowArgs.getMessage(),
                    Arrays.asList(myInfo.getLedgerKeys().get(0), otherMember.getLedgerKeys().get(0))
            );

            NotaryInfo notary = notaryLookup.getNotaryServices().iterator().next();

            PublicKey notaryKey = null;
            for(MemberInfo memberInfo: memberLookup.lookup()){
                if(Objects.equals(
                        memberInfo.getMemberProvidedContext().get("corda.notary.service.name"),
                        notary.getName().toString())) {
                    notaryKey = memberInfo.getLedgerKeys().get(0);
                    break;
                }
            }

            if(notaryKey == null) {
                throw new CordaRuntimeException("No notary PublicKey found");
            }

            UtxoTransactionBuilder txBuilder = ledgerService.getTransactionBuilder()
                    .setNotary(new Party(notary.getName(), notaryKey))
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addOutputState(chatState)
                    .addCommand(new ChatContract.Create())
                    .addSignatories(chatState.getParticipants());

            @SuppressWarnings("DEPRECATION")
            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction(myInfo.getLedgerKeys().get(0));

            return flowEngine.subFlow(new FinalizeChatSubFlow(signedTransaction, otherMember.getName()));
        }
        catch (Exception e) {
            log.warn("Failed to process utxo flow for request body " + requestBody + " because: " + e.getMessage());
            throw new CordaRuntimeException(e.getMessage());
        }
    }
}

/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "create-1",
    "flowClassName": "com.r3.developers.csdetemplate.utxoexample.workflows.CreateNewChatFlow",
    "requestData": {
        "chatName":"Chat with Bob",
        "otherMember":"CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
        "message": "Hello Bob"
        }
}
 */