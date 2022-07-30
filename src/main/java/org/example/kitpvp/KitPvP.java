package org.example.kitpvp;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public final class KitPvP extends JavaPlugin{
    static KitPvP instance;
    public static KitPvP getInstance() {
        return instance;
    }

    private File customConfigFile;
    private FileConfiguration DBConfig;

    //Return the DBconfig file loaded here
    public FileConfiguration getDBConfig() {
        return this.DBConfig;
    }

    public KitPvP (){
        super();
        instance = this;
    }

    @Override
    public void onEnable()
    {
        //Register BungeeCord as outgoing target for plugin messages
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        //Register Eventlisteners class
        getServer().getPluginManager().registerEvents(new Eventlisteners(), this);
        //Create config file if not created yet
        this.saveDefaultConfig();

        //Check if exist or create DBconfig file
        createDBConfig();

        //Track command /lobby and activate Commmands()
        this.getCommand("lobby").setExecutor(new Commands());
    }

    //Check exists else create custom DBconfig file
    //This is used for the connection parameters to the database
    private void createDBConfig() {
        customConfigFile = new File(getDataFolder(), "DBConfig.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("DBConfig.yml", false);
        }

        DBConfig= new YamlConfiguration();
        try {
            DBConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


}
