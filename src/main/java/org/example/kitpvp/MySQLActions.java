package org.example.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import java.sql.*;
import java.util.ArrayList;

public class MySQLActions {
    //JavaPlugin plugin;
    private java.sql.Connection con = null;
    private final String url = KitPvP.getInstance().getDBConfig().getString("url");
    private final String user = KitPvP.getInstance().getDBConfig().getString("user");
    private final String password = KitPvP.getInstance().getDBConfig().getString("password");
    private final String table = KitPvP.getInstance().getDBConfig().getString("table");
    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public boolean isConnected(){
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect(){
        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void prepStatements() throws SQLException {
        PreparedStatement psCreate = getConnection().prepareStatement("INSERT INTO ? (player_uuid, kills, deaths) VALUES (?, '0', '0')");
        psCreate.setString(1,table);
        psCreate.setString(2,"asda");
        psCreate.executeUpdate();

        PreparedStatement psUpdate = getConnection().prepareStatement("UPDATE ? SET kills = ? WHERE player_uuid = ?");
        psUpdate.setString(1,table);
        psUpdate.setInt(2, 123);
        psUpdate.setString(3,"asda");
        psUpdate.executeUpdate();

        PreparedStatement psDelete = getConnection().prepareStatement("DELETE FROM ? WHERE player_uuid = ?");
        psDelete.setString(1,table);
        psDelete.setString(2,"asda");
        psDelete.executeUpdate();

        PreparedStatement psGetAll = getConnection().prepareStatement("SELECT * FROM ? WHERE player_uuid=?");
        psGetAll.setString(1,table);
        psGetAll.setString(2,"asda");
        ResultSet rs = psGetAll.executeQuery();
        //loop while info

        while(rs.next()){

        }
    }

    //Checks if player exists in the DB else creates a new player item
    public void checkPlayerData(Player player) {
        String sqlFindPlayer = String.format("SELECT * FROM %s WHERE player_uuid='%s'", table,player.getUniqueId().toString());
        checkPlayerAsync(sqlFindPlayer, new QueryCallback() {
            @Override
            public void onQueryDone(ArrayList findPlayer) {
                player.sendMessage("Searching player database item");
                if(findPlayer.size() == 0){
                    String sqlNewPlayer = String.format("INSERT INTO %s (player_uuid, kills, deaths) VALUES ('%s', '0', '0')",table,player.getUniqueId().toString());
                    updateSQL(sqlNewPlayer);
                    player.sendMessage("Welcome, we created a new profile for you");
                }
                else{
                    player.sendMessage("Welcome back!, we loaded your profile");
                }
            }
        });
    }

    public void checkPlayerAsync(final String query, final QueryCallback callback){
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ArrayList Results = runSQL(query);
                Bukkit.getScheduler().runTask(KitPvP.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        // call the callback with the result
                        callback.onQueryDone(Results);
                    }
                });
            }
        });
    }

    public void runSQLASync(final String query, final QueryCallback callback){
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ArrayList Results = runSQL(query);
                Bukkit.getScheduler().runTask(KitPvP.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        // call the callback with the result
                        callback.onQueryDone(Results);
                    }
                });
            }
        });
    }

    public void updateSQLASync(final String query, final QueryCallback callback){
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                updateSQL(query);
                final ArrayList Results = new ArrayList();
                Bukkit.getScheduler().runTask(KitPvP.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        // call the callback with the result
                        callback.onQueryDone(Results);
                    }
                });
            }
        });
    }

    //Retrieve current kills using player uuid
    //Increment the kills
    //Update the table with the new data
    //Update scoreboard
    public void updatePlayerKills(Player player) {
        String sqlGetKills = String.format("SELECT kills FROM %s WHERE player_uuid = '%s'",table,player.getUniqueId().toString());
        runSQLASync(sqlGetKills, new QueryCallback() {
            @Override
            public void onQueryDone(ArrayList getKills) {
                String myString = getKills.get(0).toString();
                int kills = Integer.parseInt(myString);
                kills++;
                String sqlSetKills = String.format("UPDATE %s SET kills='%d' WHERE player_uuid = '%s'",table,kills,player.getUniqueId().toString());
                updateSQLASync(sqlSetKills, new QueryCallback() {
                    @Override
                    public void onQueryDone(ArrayList filler) {
                        ScoreBoardPlayer score = new ScoreBoardPlayer();
                        score.updateScoreBoard(player);
                    }
                });
            }
        });
    }

    //Retrieves current deaths using player uuid
    //Increments the deaths
    //Updates the table with the new data
    public void updatePlayerDeaths(Player player){
        String sqlGetDeaths = String.format("SELECT deaths FROM %s WHERE player_uuid = '%s'",table,player.getUniqueId().toString());
        runSQLASync(sqlGetDeaths, new QueryCallback() {
            @Override
            public void onQueryDone(ArrayList getDeaths) {
                String myString = getDeaths.get(0).toString();
                int deaths = Integer.parseInt(myString);
                deaths++;
                String sqlSetDeaths = String.format("UPDATE %s SET deaths='%d' WHERE player_uuid = '%s'",table,deaths,player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), () -> updateSQL(sqlSetDeaths));
            }
        });
    }

    //Retrieves data from the table
    public String getDeaths(Player player){
        String sqlGetData = String.format("SELECT * FROM %s WHERE player_uuid = '%s'", table, player.getUniqueId().toString());
        ArrayList playerData = runSQL(sqlGetData);
        String deaths = playerData.get(2).toString();
        return deaths;
    }

    //Return kills after running the needed sql query
    public String getKills(Player player){
        //gebruik prepared statements, voorkomt injecties
        String sqlGetData = String.format("SELECT * FROM %s WHERE player_uuid = '%s'",table,player.getUniqueId().toString());
        ArrayList playerData = runSQL(sqlGetData);
        String kills = playerData.get(1).toString();
        return kills;
    }

    public interface QueryCallback{
            public void onQueryDone(ArrayList results);
    }


    //Makes connections to the database and runs the provided SQL query
    //This is for SELECT queries
    public ArrayList runSQL(String sqlQuery){
        try {
            //setup connection to the DB
            con = DriverManager.getConnection(url, user, password);
            Statement st = (Statement) con.createStatement();

            //Execute the sql query on the connected DB
            ResultSet rs = st.executeQuery(sqlQuery);
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            //create result list to store all the data retrieved from the query
            //Array hoeft niet ervoor
            final ArrayList<String> Results = new ArrayList<>(count);
            while (rs.next()) {
                int i = 1;
                while (i <= count) {
                    //cycle through all results and place them in the string list
                    Results.add(rs.getString(i++));
                }
            }

            con.close();
            return Results;
        }
        catch (SQLException ex){
            Bukkit.broadcastMessage(ex.toString());
            return null;
        }

    }

    //Runs the given sql query from the previous commands
    //This is for UPDATE queries
    public boolean updateSQL(String sqlQuery){
        try {
            //setup connection to the DB
            con = DriverManager.getConnection(url, user, password);
            Statement st = (Statement) con.createStatement();

            //Execute the sql query on the connected DB
            st.executeUpdate(sqlQuery);
            st.close();
            con.close();
            //return true on success
            return true;
        }

        catch (SQLException ex) {
            Bukkit.broadcastMessage(ex.toString());
            //return false on failure
            return false;
        }
    }
}



