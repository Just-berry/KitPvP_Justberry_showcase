package org.example.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.kitpvp.commands.BungeeCommands;

public class LobbyCommand implements CommandExecutor {

    //On use of /lobby send player to the lobby server
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player) {
            if (args.length == 0) {
                BungeeCommands.sendToServer(((Player) sender), "lobby");
            } else {
                ((Player) sender).sendMessage("/lobby does not need any arguments");
            }
        }
        else {
            System.out.println("The console/command block cannot be send to the lobby, that would be weird");
        }
        return true;
    }
}
