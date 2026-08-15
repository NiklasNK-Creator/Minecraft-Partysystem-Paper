package net.leavesmp.party;

import net.leavesmp.party.commands.PartyCommand;
import net.leavesmp.party.listener.PartyListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PartySystemTest {

    private PartyManager partyManager;

    @BeforeEach
    void setUp() {
        partyManager = new PartyManager();
    }

    @Test
    void testPartyCreationAndMembers() {
        UUID leader = UUID.randomUUID();
        UUID member1 = UUID.randomUUID();
        UUID member2 = UUID.randomUUID();

        Party party = partyManager.createParty(leader);

        assertNotNull(party);
        assertEquals(leader, party.getLeader());
        assertTrue(party.isLeader(leader));

        partyManager.addMemberToParty(party, member1);
        assertTrue(party.isMember(member1));
        assertEquals(party, partyManager.getParty(member1));

        partyManager.addMemberToParty(party, member2);
        assertEquals(2, party.getMembers().size());
        assertEquals(3, party.getAllMembers().size());
    }

    @Test
    void testInviteExpiration() throws InterruptedException {
        UUID inviter = UUID.randomUUID();
        UUID target = UUID.randomUUID();

        PartyInvite invite = new PartyInvite(inviter, target);
        partyManager.addInvite(target, invite);

        assertNotNull(partyManager.getInvite(target));
        assertFalse(invite.isExpired());

        // Dynamic time check without waiting 60s
        PartyInvite customInvite = new PartyInvite(inviter, target) {
            @Override
            public boolean isExpired() {
                return true;
            }
        };

        partyManager.addInvite(target, customInvite);
        assertNull(partyManager.getInvite(target));
    }

    @Test
    void testPartyDisbandAndQuitCleanup() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();

        Party party = partyManager.createParty(leader);
        partyManager.addMemberToParty(party, member);

        partyManager.removeParty(party);

        assertNull(partyManager.getParty(leader));
        assertNull(partyManager.getParty(member));
        assertTrue(party.getMembers().isEmpty());
    }

    @Test
    void testPlayerLeaveMember() {
        UUID leader = UUID.randomUUID();
        UUID member = UUID.randomUUID();

        Party party = partyManager.createParty(leader);
        partyManager.addMemberToParty(party, member);

        partyManager.removePlayerFromParty(member);

        assertNull(partyManager.getParty(member));
        assertNotNull(partyManager.getParty(leader));
        assertFalse(party.isMember(member));
    }
}
