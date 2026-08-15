package net.leavesmp.party;

import net.leavesmp.party.commands.PartyCommand;
import net.leavesmp.party.listener.PartyListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class PartySystem extends JavaPlugin {

    private PartyManager partyManager;

    @Override
    public void onEnable() {
        this.partyManager = new PartyManager();

        PartyCommand partyCommand = new PartyCommand(partyManager);
        PluginCommand command = getCommand("party");
        if (command != null) {
            command.setExecutor(partyCommand);
            command.setTabCompleter(partyCommand);
        }

        getServer().getPluginManager().registerEvents(new PartyListener(partyManager), this);
    }

    @Override
    public void onDisable() {
        this.partyManager = null;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }
}
