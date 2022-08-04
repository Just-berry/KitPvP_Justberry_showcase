package org.example.kitpvp.eventslisteners;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.example.kitpvp.KitPvP;
import org.example.kitpvp.KitSelector;
import org.example.kitpvp.player.CustomPlayer;
import org.example.kitpvp.player.ScoreBoardPlayer;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EventlistenersPlayer implements Listener{
    private KitPvP main;

    public EventlistenersPlayer(KitPvP main){
        this.main = main;
    }
    //If player gets damaged check if the player health goes below 0
    //if so, increase deaths & check if attacker was a player and then increase that players kill score
    @EventHandler
    public void PlayerDamageReceive(EntityDamageByEntityEvent DamageEvent) throws SQLException {
        if(DamageEvent.getEntity() instanceof Player & DamageEvent.getDamager() instanceof Player) {
            Player player = (Player) DamageEvent.getEntity();
            Player killer = (Player) DamageEvent.getDamager();
            if((player.getHealth()-DamageEvent.getFinalDamage()) <= 0) {
                //MySQLActions sqlAction = new MySQLActions();
                main.getPlayerManager().getCustomPlayer(player.getUniqueId()).increaseDeaths(player);
                main.getPlayerManager().getCustomPlayer(killer.getUniqueId()).increaseKills(killer);
                //sqlAction.updatePlayerDeaths(player);
                //sqlAction.updatePlayerKills((Player) DamageEvent.getDamager());
                killer.sendMessage("You killed: " + player.getDisplayName() + " - Current kills:" + main.getPlayerManager().getCustomPlayer(killer.getUniqueId()).getKills());
                player.sendMessage("You got killed by: " + ((Player) DamageEvent.getDamager()).getDisplayName() + " - Current deaths:" + main.getPlayerManager().getCustomPlayer(player.getUniqueId()).getDeaths());
            }
        }
    }

    //Block place event checks for gold blocks. The placement of an Gold block sets the spawn location on that location
    @EventHandler
    public void BlockPlaceEvent​(BlockPlaceEvent event){
        Block block = event.getBlock();
        if(block.getType() == Material.GOLD_BLOCK){
            event.getPlayer().getWorld().setSpawnLocation(block.getLocation().add(0,1,0));
            event.getPlayer().sendMessage("New spawn location set");
        }
    }

    //On join setup player info
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getPlayer();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                CustomPlayer playerData = new CustomPlayer(main, player.getUniqueId());
                main.getPlayerManager().addCustomPlayer(player.getUniqueId(), playerData);
            } catch (SQLException ex) {
                player.kickPlayer("Database error");
            }
        });
        future.get();
        //Player player = event.getPlayer();
        //Check/Create DB record
        //MySQLActions sqlAction = new MySQLActions();
        //sqlAction.checkPlayerData(player);

        //Create scoreboard
        ScoreBoardPlayer score = new ScoreBoardPlayer(main);
        score.setScoreBoard(player);
        event.getPlayer().teleport(player.getWorld().getSpawnLocation());
        //Clear inv and give player kit selector tool
        KitSelector kitselector = new KitSelector();
        kitselector.giveKitSelector(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        main.getPlayerManager().removeCustomPlayer(e.getPlayer().getUniqueId());
    }

    //On respawn reset player
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        //Clear inv and give player kit selector tool
        KitSelector kitselector = new KitSelector();
        kitselector.giveKitSelector(event.getPlayer());
    }

    //Check if player interacts with the kit selector
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws SQLException {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SPECTRAL_ARROW){
                KitSelector inventory = new KitSelector();
                inventory.show(event.getPlayer());
            }
        }
    }


    //On inventory click, check if the inventory is our special kit selector inventory
    //Check if clicked item to the config file
    //Cancel click event and give player the kit
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        KitSelector inventory = new KitSelector();
        try {
            if (e.getCurrentItem().hasItemMeta()) {
                if (!e.getView().getTitle().equalsIgnoreCase("Kits")) {
                    return;
                }
                String nameArmour = e.getCurrentItem().getItemMeta().getDisplayName();
                if (KitPvP.getInstance().getConfig().isList(e.getCurrentItem().getItemMeta().getDisplayName())) {
                    e.setCancelled(true);
                    inventory.giveSelectedKit((Player) e.getWhoClicked(), nameArmour);
                } else {
                    e.setCancelled(true);
                    e.getWhoClicked().sendMessage(nameArmour + " does not exist" + KitPvP.getInstance().getConfig().getString(nameArmour));
                }
            }
        }
        catch (NullPointerException ex){

        }
    }
}
