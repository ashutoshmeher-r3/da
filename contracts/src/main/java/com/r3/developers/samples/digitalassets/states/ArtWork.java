package com.r3.developers.samples.digitalassets.states;

import com.r3.developers.samples.digitalassets.contracts.AssetContract;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;

import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

@BelongsToContract(AssetContract.class)
public class ArtWork extends Asset implements ContractState {
    private final String title;
    private final String description;
    private final String artist;

    public ArtWork(UUID assetId, String title, String description, String artist, MemberX500Name owner, List<PublicKey> participants) {
        super(assetId, owner, participants);
        this.title = title;
        this.description = description;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getArtist() {
        return artist;
    }
}
