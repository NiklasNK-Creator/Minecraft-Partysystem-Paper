package net.leavesmp.party.listener;

import net.leavesmp.party.Messages;
import net.leavesmp.party.Party;
import net.leavesmp.party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyListener implements Listener {

    private final PartyManager partyManager;

    public PartyListener(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        Party party = partyManager.getParty(playerId);
        if (party != null) {
            if (party.isLeader(playerId)) {
                List<UUID> members = new ArrayList<>(party.getAllMembers());
                partyManager.removeParty(party);

                for (UUID memberId : members) {
                    if (!memberId.equals(playerId)) {
                        Player memberPlayer = Bukkit.getPlayer(memberId);
                        if (memberPlayer != null && memberPlayer.isOnline()) {
                            memberPlayer.sendMessage(Messages.parse(Messages.partyDisbanded()));
                        }
                    }
                }
            } else {
                partyManager.removePlayerFromParty(playerId);

                for (UUID memberId : party.getAllMembers()) {
                    Player memberPlayer = Bukkit.getPlayer(memberId);
                    if (memberPlayer != null && memberPlayer.isOnline()) {
                        memberPlayer.sendMessage(Messages.parse(Messages.partyLeft(player.getName())));
                    }
                }
            }
        }

        partyManager.removeInvitesAssociatedWith(playerId);
    }
}
