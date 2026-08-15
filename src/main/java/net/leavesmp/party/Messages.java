package net.leavesmp.party;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Messages {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final String PREFIX = "<gray>[<gradient:#55ffff:#5555ff><bold>Party</bold></gradient>]</gray> ";

    public static Component parse(String input) {
        return MINI_MESSAGE.deserialize(PREFIX + input);
    }

    public static Component raw(String input) {
        return MINI_MESSAGE.deserialize(input);
    }

    public static final String ONLY_PLAYERS = "<red>Dieser Befehl kann nur von Spielern ausgeführt werden.</red>";
    public static final String PLAYER_NOT_FOUND = "<red>Der angegebene Spieler wurde nicht gefunden oder ist offline.</red>";
    public static final String CANNOT_INVITE_SELF = "<red>Du kannst dich nicht selbst einladen.</red>";
    public static final String ALREADY_IN_TARGET_PARTY = "<red>Dieser Spieler ist bereits in deiner Party.</red>";
    public static final String TARGET_ALREADY_IN_PARTY = "<red>Dieser Spieler ist bereits in einer Party.</red>";
    public static final String HAS_PENDING_INVITE = "<red>Dieser Spieler hat bereits eine offene Einladung.</red>";
    public static final String NO_PENDING_INVITE = "<red>Du hast keine offene Einladung.</red>";
    public static final String INVITE_EXPIRED = "<red>Deine Einladung ist abgelaufen.</red>";
    public static final String NOT_IN_PARTY = "<red>Du bist in keiner Party.</red>";
    public static final String NOT_LEADER = "<red>Nur der Partyleader kann Spieler einladen.</red>";
    public static final String USAGE = "<red>Verwendung: /party <invite|accept|leave|info></red>";

    public static String inviteSent(String targetName) {
        return "<green>Einladung an <yellow>" + targetName + "</yellow> gesendet.</green>";
    }

    public static String inviteReceived(String inviterName) {
        return "<green>Du wurdest von <yellow>" + inviterName + "</yellow> in eine Party eingeladen. Nutze <click:run_command:'/party accept'><hover:show_text:'<green>Klicke zum Annehmen</green>'><yellow>/party accept</yellow></click> zum Annehmen.</green>";
    }

    public static String partyJoined(String playerName) {
        return "<green><yellow>" + playerName + "</yellow> ist der Party beigetreten.</green>";
    }

    public static String partyLeft(String playerName) {
        return "<red><yellow>" + playerName + "</yellow> hat die Party verlassen.</red>";
    }

    public static String partyDisbanded() {
        return "<red>Die Party wurde aufgelöst.</red>";
    }

    private Messages() {}
}
