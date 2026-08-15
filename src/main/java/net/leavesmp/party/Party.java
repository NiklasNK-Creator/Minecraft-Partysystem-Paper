package net.leavesmp.party;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Party {

    private final UUID partyId;
    private UUID leader;
    private final Set<UUID> members;

    public Party(UUID leader) {
        this.partyId = UUID.randomUUID();
        this.leader = Objects.requireNonNull(leader, "Leader UUID must not be null");
        this.members = new HashSet<>();
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean addMember(UUID member) {
        if (member == null || member.equals(leader)) {
            return false;
        }
        return members.add(member);
    }

    public boolean removeMember(UUID member) {
        return members.remove(member);
    }

    public boolean isMember(UUID player) {
        return members.contains(player);
    }

    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    public void disband() {
        members.clear();
    }

    public void transferLeadership(UUID newLeader) {
        Objects.requireNonNull(newLeader, "New leader UUID must not be null");
        if (members.contains(newLeader)) {
            members.remove(newLeader);
            members.add(this.leader);
            this.leader = newLeader;
        }
    }

    public Set<UUID> getAllMembers() {
        Set<UUID> all = new HashSet<>(members);
        all.add(leader);
        return Collections.unmodifiableSet(all);
    }
}
