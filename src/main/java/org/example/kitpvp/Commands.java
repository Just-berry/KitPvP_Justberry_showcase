package org.example.kitpvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    //On use of /lobby send player to the lobby server
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player) {
            if (command.getName().equalsIgnoreCase("lobby")) {
                if (args.length == 0) {
                    Player player = (Player) sender;
                    BungeeCommands.sendToServer(player, "lobby");
                } else {
                    sender.sendMessage("/lobby does not need any arguments");
                }
            }
        }
        return true;
    }
}
