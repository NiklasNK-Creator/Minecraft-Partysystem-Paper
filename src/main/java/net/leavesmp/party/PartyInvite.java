package net.leavesmp.party;

import java.util.Objects;
import java.util.UUID;

public class PartyInvite {

    private final UUID inviter;
    private final UUID target;
    private final long expiryTimestamp;

    public PartyInvite(UUID inviter, UUID target) {
        this.inviter = Objects.requireNonNull(inviter, "Inviter UUID must not be null");
        this.target = Objects.requireNonNull(target, "Target UUID must not be null");
        this.expiryTimestamp = System.currentTimeMillis() + 60_000L;
    }

    public UUID getInviter() {
        return inviter;
    }

    public UUID getTarget() {
        return target;
    }

    public long getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTimestamp;
    }
}
