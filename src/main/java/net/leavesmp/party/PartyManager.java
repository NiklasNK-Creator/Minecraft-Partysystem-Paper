package net.leavesmp.party;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

    private final Map<UUID, Party> playerPartyMap;
    private final Map<UUID, PartyInvite> pendingInvites;

    public PartyManager() {
        this.playerPartyMap = new HashMap<>();
        this.pendingInvites = new HashMap<>();
    }

    public Party createParty(UUID leader) {
        if (playerPartyMap.containsKey(leader)) {
            return playerPartyMap.get(leader);
        }
        Party party = new Party(leader);
        playerPartyMap.put(leader, party);
        return party;
    }

    public Party getParty(UUID player) {
        return playerPartyMap.get(player);
    }

    public void removeParty(Party party) {
        if (party == null) {
            return;
        }
        playerPartyMap.remove(party.getLeader());
        for (UUID member : party.getMembers()) {
            playerPartyMap.remove(member);
        }
        party.disband();
    }

    public void addInvite(UUID target, PartyInvite invite) {
        pendingInvites.put(target, invite);
    }

    public PartyInvite getInvite(UUID target) {
        PartyInvite invite = pendingInvites.get(target);
        if (invite != null && invite.isExpired()) {
            pendingInvites.remove(target);
            return null;
        }
        return invite;
    }

    public void removeInvite(UUID target) {
        pendingInvites.remove(target);
    }

    public void removePlayerFromParty(UUID player) {
        Party party = playerPartyMap.get(player);
        if (party == null) {
            return;
        }

        if (party.isLeader(player)) {
            removeParty(party);
        } else {
            party.removeMember(player);
            playerPartyMap.remove(player);
        }
    }

    public void removeInvitesAssociatedWith(UUID player) {
        pendingInvites.remove(player);
        pendingInvites.values().removeIf(invite -> invite.getInviter().equals(player));
    }

    public void addMemberToParty(Party party, UUID member) {
        party.addMember(member);
        playerPartyMap.put(member, party);
    }
}
