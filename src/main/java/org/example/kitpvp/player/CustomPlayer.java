package org.example.kitpvp.player;

import org.bukkit.entity.Player;
import org.example.kitpvp.KitPvP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CustomPlayer {
    //private final String table = KitPvP.getInstance().getDBConfig().getString("table");
    private UUID uuid;
    private KitPvP main;
    private int kills;
    private int deaths;

    public CustomPlayer(KitPvP main, UUID uuid) throws SQLException {
        this.uuid = uuid;
        this.main = main;
        System.out.println("uuid length: "+uuid.toString().length());
        try(Connection connection = main.getDatabase().getHikari().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT kills, deaths FROM berrynetworkdb.kill_deaths WHERE player_uuid=?")){
            //statement.setString(1,table);
            statement.setString(1,uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                kills = rs.getInt("kills");
                deaths = rs.getInt("deaths");
            } else{
                kills = 0;
                deaths = 0;
                try(Connection connection1 = main.getDatabase().getHikari().getConnection();
                    PreparedStatement statement1 = connection1.prepareStatement("INSERT INTO berrynetworkdb.kill_deaths (player_uuid, kills, deaths) VALUES (?,?,?)")) {
                    //statement1.setString(1, table);
                    statement1.setString(1, uuid.toString());
                    statement1.setInt(2, kills);
                    statement1.setInt(3, deaths);
                    statement1.executeUpdate();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void increaseKills(Player player){
        int oldKills = this.kills;
        int newKills = oldKills + 1;
        this.kills = newKills;
        ScoreBoardPlayer score = new ScoreBoardPlayer(main);
        score.updateScoreBoard(player);
        try(Connection connection = main.getDatabase().getHikari().getConnection();
            PreparedStatement updateKills = connection.prepareStatement("UPDATE berrynetworkdb.kill_deaths SET kills = ? WHERE player_uuid = ?")){
            //updateKills.setString(1,table);
            updateKills.setInt(1,newKills);
            updateKills.setString(2,uuid.toString());
            updateKills.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void increaseDeaths(Player player){
        int oldDeaths = this.deaths;
        int newDeaths = oldDeaths + 1;
        this.deaths = newDeaths;
        ScoreBoardPlayer score = new ScoreBoardPlayer(main);
        score.updateScoreBoard(player);
        try(Connection connection = main.getDatabase().getHikari().getConnection();
            PreparedStatement updateDeaths = connection.prepareStatement("UPDATE berrynetworkdb.kill_deaths SET deaths = ? WHERE player_uuid = ?")){
            //updateDeaths.setString(1,table);
            updateDeaths.setInt(1,newDeaths);
            updateDeaths.setString(2,uuid.toString());
            updateDeaths.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }
}
