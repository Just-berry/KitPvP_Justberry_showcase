package org.example.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoardPlayer {

    //Construct scoreboard player on joining the server
    //Retrieves all the needed information from the database
    public void setScoreBoard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        setScoreboardASync(player, board, new Callback() {
            @Override
            public void onAsyncDone(Scoreboard board) {
                player.setScoreboard(board);
            }
        });
    }

    public void setScoreboardASync(Player player, Scoreboard board, final Callback callback){
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                MySQLActions sqlAction = new MySQLActions();
                Objective obj = board.registerNewObjective("BerryNetwork", "dummy", "KitPvP");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                Score KD = obj.getScore(ChatColor.GRAY + "» K/D");
                KD.setScore(15);

                Score KillName = obj.getScore(ChatColor.GRAY + "Kills:");
                KillName.setScore(14);

                Team kills = board.registerNewTeam("Kills");
                kills.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);
                kills.setPrefix(sqlAction.getKills(player));
                obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE).setScore(13);

                Score DeathName = obj.getScore(ChatColor.GRAY + "Deaths:");
                DeathName.setScore(12);

                Team deaths = board.registerNewTeam("Deaths");
                deaths.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
                String death = sqlAction.getDeaths(player);
                deaths.setPrefix(death);

                obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(11);

                Score KillsName = obj.getScore(ChatColor.GRAY + "Kill death ratio:");
                KillsName.setScore(10);

                Team deathsT = board.registerNewTeam("KD");
                deathsT.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE);

                //Calculation for the K/D ratio
                double KDKills = Double.parseDouble(sqlAction.getKills(player));
                double[] KDDeaths = {0};
                String sDeaths = sqlAction.getDeaths(player);
                KDDeaths[0] = Double.parseDouble(sDeaths);

                //double KDDeaths = Double.parseDouble(sqlAction.getDeaths(player));
                double KDRatiof = 0.0;

                if (KDDeaths[0] != 0) {
                    KDRatiof = KDKills / KDDeaths[0];
                    double scale = Math.pow(10, 1);
                    KDRatiof = Math.round(KDRatiof * scale) / scale;
                }
                //if deaths is 0 set KD to amount of kills
                else {
                    KDRatiof = KDKills;
                }
                String KDRatios = String.format("%s", KDRatiof);
                deathsT.setPrefix(KDRatios);
                obj.getScore(ChatColor.BLUE + "" + ChatColor.WHITE).setScore(9);
                Bukkit.getScheduler().runTask(KitPvP.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        callback.onAsyncDone(board);
                    }
                });
            }
        });
    }

    public interface Callback {
        public void onAsyncDone(Scoreboard board);
    }

    //Updates scoreboard with information from the database
    public void updateScoreBoard(Player player){
        updateScoreBoardASync(player);
    }

    public void updateScoreBoardASync(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(KitPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                MySQLActions sqlAction = new MySQLActions();
                Scoreboard board = player.getScoreboard();
                String KDKillsS = sqlAction.getKills(player);
                String[] KDDeathsS = new String[1];
                String deaths = sqlAction.getDeaths(player);
                KDDeathsS[0] = deaths;

                //String KDDeathsS = sqlAction.getDeaths(player);
                board.getTeam("Kills").setPrefix(KDKillsS);
                board.getTeam("Deaths").setPrefix(KDDeathsS[0]);
                double KDKills = Double.parseDouble(sqlAction.getKills(player));
                double[] KDDeaths = new double[1];
                String death = sqlAction.getDeaths(player);
                KDDeaths[0] = Double.parseDouble(death);

                //double KDDeaths = Double.parseDouble(sqlAction.getDeaths(player));
                double KDRatiof = 0.0;
                if (KDDeaths[0] != 0) {
                    KDRatiof = KDKills / KDDeaths[0];
                    double scale = Math.pow(10, 1);
                    KDRatiof = Math.round(KDRatiof * scale) / scale;
                } else {
                    KDRatiof = KDKills;
                }
                String KDRatios = String.format("%s", KDRatiof);
                board.getTeam("KD").setPrefix(KDRatios);
            }
        });
    }
}
