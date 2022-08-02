package org.example.kitpvp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CustomPlayer {
    private final String table = KitPvP.getInstance().getDBConfig().getString("table");
    private UUID uuid;
    private KitPvP main;
    private int kills;
    private int deaths;

    public CustomPlayer(KitPvP main, UUID uuid) throws SQLException {
        this.uuid = uuid;
        this.main = main;
        PreparedStatement statement = main.getDatabase().getConnection().prepareStatement("SELECT kills, deaths FROM ? WHERE player_uuid=?");
        statement.setString(1,table);
        statement.setString(2,uuid.toString());
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            kills = rs.getInt("kills");
            deaths = rs.getInt("deaths");
        } else{
            kills = 0;
            deaths = 0;
            PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement("INSERT INTO ? (player_uuid, kills, deaths) VALUES (?,?,?)");
            statement1.setString(1,table);
            statement1.setString(2,uuid.toString());
            statement1.setInt(3,kills);
            statement1.setInt(4,deaths);
            statement1.executeUpdate();
        }

    }

    public void increaseKills(){
        this.kills = this.kills++;

        try {
            PreparedStatement updateKills = main.getDatabase().getConnection().prepareStatement("UPDATE ? SET kills = ? WHERE player_uuid = ?");
            updateKills.setString(1,table);
            updateKills.setInt(2,kills);
            updateKills.setString(3,uuid.toString());
            updateKills.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void increaseDeaths(){
        this.deaths = this.deaths++;

        try {
            PreparedStatement updateKills = main.getDatabase().getConnection().prepareStatement("UPDATE ? SET deaths = ? WHERE player_uuid = ?");
            updateKills.setString(1,table);
            updateKills.setInt(2,deaths);
            updateKills.setString(3,uuid.toString());
            updateKills.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKills() {return kills;}
    public int getDeaths() {return deaths;}


}
