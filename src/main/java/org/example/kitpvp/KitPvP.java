package org.example.kitpvp;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.kitpvp.commands.LobbyCommand;
import org.example.kitpvp.eventslisteners.EventlistenersPlayer;
import org.example.kitpvp.player.PlayerManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


public final class KitPvP extends JavaPlugin{
    static KitPvP instance;
    public static KitPvP getInstance() {
        return instance;
    }
    private FileConfiguration DBConfig;
    //Return the DBconfig file loaded here
    public FileConfiguration getDBConfig() {
        return this.DBConfig;
    }
    public KitPvP (){
        super();
        instance = this;
    }
    private MySQLActions database;
    private PlayerManager playerManager;

    @Override
    public void onEnable()
    {
        //Register BungeeCord as outgoing target for plugin messages
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        //Register Eventlisteners class
        getServer().getPluginManager().registerEvents(new EventlistenersPlayer(this), this);
        //Create config file if not created yet
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        //Check if exist or create DBconfig file
        createDBConfig();
        //Database connection
        database = new MySQLActions();
        try{
            database.connect();
        }catch (SQLException e){
            e.printStackTrace();
        }
        playerManager = new PlayerManager();
        System.out.println(database.isConnected());
        //Track command /lobby and activate Commmands()
        getCommand("lobby").setExecutor(new LobbyCommand());
    }

    @Override
    public void onDisable(){
        database.disconnect();
    }

    public MySQLActions getDatabase(){
        return database;
    }

    public PlayerManager getPlayerManager(){return playerManager;}

    //Check exists else create custom DBconfig file
    //This is used for the connection parameters to the database
    private void createDBConfig() {
        File customConfigFile = new File(getDataFolder(), "DBConfig.yml");
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
