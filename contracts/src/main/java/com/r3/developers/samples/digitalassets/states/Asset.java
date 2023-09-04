package com.r3.developers.samples.digitalassets.states;

import com.r3.developers.samples.digitalassets.contracts.AssetContract;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@BelongsToContract(AssetContract.class)
public abstract class Asset implements ContractState {

    protected UUID assetId;
    protected MemberX500Name owner;
    protected List<PublicKey> participants;

    public Asset(UUID assetId, MemberX500Name owner, List<PublicKey> participants) {
        this.assetId = assetId;
        this.owner = owner;
        this.participants = participants;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public MemberX500Name getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }
}
