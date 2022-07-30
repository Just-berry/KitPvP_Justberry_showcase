package org.example.kitpvp;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KitSelector {

    public Inventory inv;

    public KitSelector(){
        //Retrieve all armours from config file
        Set<String> keys = KitPvP.getInstance().getConfig().getKeys(false);

        //Create special Kits inv, size based on amount of kits
        int rows = keys.size()/9;
        int slots = rows*9+9;
        inv = Bukkit.getServer().createInventory(null, slots, "Kits");

        int i = 0;
        //for al armours found, create a button selector using the name from the armour as lore for the selector
        for (String key : keys) {
            String armour = String.format(key.toUpperCase() + "_CHESTPLATE");
            inv.setItem(i, createItem(armour, key));
            i++;
        }
    }

    //Create sword as button for the kit selector with correct lore
    private ItemStack createItem(String name, String key){
        ItemStack i = new ItemStack(Material.getMaterial(name), 1);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(key);
        im.setLore(Arrays.asList("Select " + key.toLowerCase() + " armour"));
        i.setItemMeta(im);
        return i;
    }

    //Open the created inventory
    public void show(Player p){
        p.openInventory(inv);
    }

    //Clears player inv - Get armours with the selected kitname & check if not null - Check if player has enough inventory
    //Check if armour piece is a sword, if not place it in the armour slot of the player. else place it in slot 1 of the player (hotbar)
    public void giveSelectedKit(Player player, String kitName){
        player.getInventory().clear();
        List<String> armor = KitPvP.getInstance().getConfig().getStringList(kitName);
        ItemStack[] armour = new ItemStack[4];
        int i = 0;
        for (String value : armor){
            if (Material.getMaterial(value) != null) {
                ItemStack piece = new ItemStack(Material.getMaterial(value), 1);
                if(! value.toLowerCase().contains("sword")) {
                    armour[i] = new ItemStack(Material.getMaterial(value), 1);
                    i++;
                }
                else {
                    player.getInventory().setItem(0, piece);
                }
            } else {
                player.sendMessage("Config screwed up: " + value);
            }
        }
        player.getInventory().setArmorContents(armour);
        player.closeInventory();
    }

    //Clear player inv - create kit selector item with correct display name and lore
    //Place the kit selector in the first slot of the players hotbar
    public void giveKitSelector(Player player){
        PlayerInventory inv = player.getInventory();
        player.getInventory().clear();
        ItemStack selector = new ItemStack(Material.SPECTRAL_ARROW, 1);
        ItemMeta im = selector.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§fRight Click to open the kit selector!");
        im.setDisplayName("§a§lKit selector §f(Right Click)");
        im.setLore(lore);
        selector.setItemMeta(im);
        inv.setItem(0, selector); //Slot 1 = 0
        player.getInventory().setHeldItemSlot(0);
    }
}
