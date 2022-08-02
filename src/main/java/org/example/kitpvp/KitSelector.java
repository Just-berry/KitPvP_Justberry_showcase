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

        int slotNumber = 0;
        //for al armours found, create a button selector using the name from the armour as lore for the selector
        for (String key : keys) {
            String armour = String.format(key.toUpperCase() + "_CHESTPLATE");
            inv.setItem(slotNumber, createItem(armour, key));
            slotNumber++;
        }
    }

    //Create sword as button for the kit selector with correct lore
    private ItemStack createItem(String name, String key){
        ItemStack item = new ItemStack(Material.getMaterial(name), 1);
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(key);
        iMeta.setLore(Arrays.asList("Select " + key.toLowerCase() + " armour"));
        item.setItemMeta(iMeta);
        return item;
    }

    //Open the created inventory
    public void show(Player player){
        player.openInventory(inv);
    }

    //Clears player inv - Get armour with the selected kitname - Place in armout slots
    //Check if armour piece is a sword place it in slot 1 of the player (hotbar)
    public void giveSelectedKit(Player player, String kitName){
        player.getInventory().clear();
        List<String> armor = KitPvP.getInstance().getConfig().getStringList(kitName);
        ItemStack[] armour = new ItemStack[4];
        int armourSlot = 0;
        for (String value : armor){
            if (Material.getMaterial(value) != null) {
                ItemStack item = new ItemStack(Material.getMaterial(value), 1);
                if(! value.toLowerCase().contains("sword")) {
                    armour[armourSlot] = item;
                    armourSlot++;
                }
                else {
                    player.getInventory().setItem(0, item);
                }
            } else {
                player.sendMessage("Config screwed up: " + value);
            }
        }
        player.getInventory().setArmorContents(armour);
        player.closeInventory();
    }

    //Clear player inv - Give the kit selector item in players hotbar
    public void giveKitSelector(Player player){
        PlayerInventory inv = player.getInventory();
        inv.clear();
        //Create item & set lore correct
        ItemStack selector = new ItemStack(Material.SPECTRAL_ARROW, 1);
        ItemMeta iMeta = selector.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("§fRight Click to open the kit selector!");
        iMeta.setDisplayName("§a§lKit selector §f(Right Click)");
        iMeta.setLore(lore);
        selector.setItemMeta(iMeta);
        //Give item and select it for the player
        inv.setItem(0, selector);
        player.getInventory().setHeldItemSlot(0);
    }
}
