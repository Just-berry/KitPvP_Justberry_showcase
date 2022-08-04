package org.example.kitpvp.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.kitpvp.KitPvP;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class BungeeCommands {
    public static void sendToServer(Player player, String targetServer)
    {
        //Setup the bytearray
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);
        try{
            //Command: Connect "targetServer"
            out.writeUTF("Connect");
            out.writeUTF(targetServer);
        } catch (Exception e){
            e.printStackTrace();
        }
        //Send command message to BungeeCord
        player.sendPluginMessage(KitPvP.getInstance(),"BungeeCord",byteArray.toByteArray());

    }
}
