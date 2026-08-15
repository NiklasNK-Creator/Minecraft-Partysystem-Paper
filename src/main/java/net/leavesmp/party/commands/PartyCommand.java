package net.leavesmp.party.commands;

import net.leavesmp.party.Messages;
import net.leavesmp.party.Party;
import net.leavesmp.party.PartyInvite;
import net.leavesmp.party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartyCommand implements CommandExecutor, TabCompleter {

    private final PartyManager partyManager;

    public PartyCommand(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.parse(Messages.ONLY_PLAYERS));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Messages.parse(Messages.USAGE));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "invite" -> handleInvite(player, args);
            case "accept" -> handleAccept(player);
            case "leave" -> handleLeave(player);
            case "info" -> handleInfo(player);
            default -> player.sendMessage(Messages.parse(Messages.USAGE));
        }

        return true;
    }

    private void handleInvite(Player sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.parse("<red>Verwendung: /party invite <Spieler></red>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(Messages.parse(Messages.PLAYER_NOT_FOUND));
            return;
        }

        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(Messages.parse(Messages.CANNOT_INVITE_SELF));
            return;
        }

        Party senderParty = partyManager.getParty(sender.getUniqueId());

        if (senderParty != null && !senderParty.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Messages.parse(Messages.NOT_LEADER));
            return;
        }

        if (senderParty == null) {
            senderParty = partyManager.createParty(sender.getUniqueId());
        }

        if (senderParty.isMember(target.getUniqueId())) {
            sender.sendMessage(Messages.parse(Messages.ALREADY_IN_TARGET_PARTY));
            return;
        }

        Party targetParty = partyManager.getParty(target.getUniqueId());
        if (targetParty != null) {
            sender.sendMessage(Messages.parse(Messages.TARGET_ALREADY_IN_PARTY));
            return;
        }

        PartyInvite existingInvite = partyManager.getInvite(target.getUniqueId());
        if (existingInvite != null) {
            sender.sendMessage(Messages.parse(Messages.HAS_PENDING_INVITE));
            return;
        }

        PartyInvite invite = new PartyInvite(sender.getUniqueId(), target.getUniqueId());
        partyManager.addInvite(target.getUniqueId(), invite);

        sender.sendMessage(Messages.parse(Messages.inviteSent(target.getName())));
        target.sendMessage(Messages.parse(Messages.inviteReceived(sender.getName())));
    }

    private void handleAccept(Player player) {
        PartyInvite invite = partyManager.getInvite(player.getUniqueId());

        if (invite == null) {
            senderNoInviteOrExpired(player);
            return;
        }

        if (invite.isExpired()) {
            partyManager.removeInvite(player.getUniqueId());
            senderNoInviteOrExpired(player);
            return;
        }

        Player inviter = Bukkit.getPlayer(invite.getInviter());
        if (inviter == null || !inviter.isOnline()) {
            partyManager.removeInvite(player.getUniqueId());
            player.sendMessage(Messages.parse(Messages.PLAYER_NOT_FOUND));
            return;
        }

        Party party = partyManager.getParty(inviter.getUniqueId());
        if (party == null) {
            party = partyManager.createParty(inviter.getUniqueId());
        }

        partyManager.addMemberToParty(party, player.getUniqueId());
        partyManager.removeInvite(player.getUniqueId());

        for (UUID memberId : party.getAllMembers()) {
            Player memberPlayer = Bukkit.getPlayer(memberId);
            if (memberPlayer != null && memberPlayer.isOnline()) {
                memberPlayer.sendMessage(Messages.parse(Messages.partyJoined(player.getName())));
            }
        }
    }

    private void senderNoInviteOrExpired(Player player) {
        player.sendMessage(Messages.parse(Messages.NO_PENDING_INVITE));
    }

    private void handleLeave(Player player) {
        Party party = partyManager.getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(Messages.parse(Messages.NOT_IN_PARTY));
            return;
        }

        if (party.isLeader(player.getUniqueId())) {
            List<UUID> members = new ArrayList<>(party.getAllMembers());
            partyManager.removeParty(party);

            for (UUID memberId : members) {
                Player memberPlayer = Bukkit.getPlayer(memberId);
                if (memberPlayer != null && memberPlayer.isOnline()) {
                    memberPlayer.sendMessage(Messages.parse(Messages.partyDisbanded()));
                }
            }
        } else {
            partyManager.removePlayerFromParty(player.getUniqueId());
            player.sendMessage(Messages.parse(Messages.partyLeft(player.getName())));

            for (UUID memberId : party.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(memberId);
                if (memberPlayer != null && memberPlayer.isOnline()) {
                    memberPlayer.sendMessage(Messages.parse(Messages.partyLeft(player.getName())));
                }
            }
        }
    }

    private void handleInfo(Player player) {
        Party party = partyManager.getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(Messages.parse(Messages.NOT_IN_PARTY));
            return;
        }

        Player leader = Bukkit.getPlayer(party.getLeader());
        String leaderName = leader != null ? leader.getName() : party.getLeader().toString();

        List<String> memberNames = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            memberNames.add(member != null ? member.getName() : memberId.toString());
        }

        String membersList = memberNames.isEmpty() ? "Keine" : String.join(", ", memberNames);

        player.sendMessage(Messages.raw("<gray>--- <gradient:#55ffff:#5555ff><bold>Party Info</bold></gradient> ---</gray>"));
        player.sendMessage(Messages.raw("<gray>Leader:</gray> <yellow>" + leaderName + "</yellow>"));
        player.sendMessage(Messages.raw("<gray>Mitglieder:</gray> <yellow>" + membersList + "</yellow>"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }

        if (args.length == 1) {
            List<String> subcommands = List.of("invite", "accept", "leave", "info");
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> !name.equalsIgnoreCase(sender.getName()))
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
