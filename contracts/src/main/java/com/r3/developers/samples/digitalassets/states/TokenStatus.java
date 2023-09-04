package com.r3.developers.samples.digitalassets.states;

import net.corda.v5.base.annotations.CordaSerializable;

@CordaSerializable
public enum TokenStatus {
    DRAFT,
    APPROVED,
    REJECTED,
    WITHDRAWN
}
