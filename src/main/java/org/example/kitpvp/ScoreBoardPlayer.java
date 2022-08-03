package org.example.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreBoardPlayer {
    private KitPvP main;
    //Construct scoreboard player on joining the server
    //Retrieves all the needed information from the database
    public ScoreBoardPlayer(KitPvP main){
        this.main = main;
    }

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
                //MySQLActions sqlAction = new MySQLActions();
                int killsI = main.getPlayerManager().getCustomPlayer(player.getUniqueId()).getKills();
                int deathsI = main.getPlayerManager().getCustomPlayer(player.getUniqueId()).getDeaths();
                Objective obj = board.registerNewObjective("BerryNetwork", "dummy", "KitPvP");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                Score KD = obj.getScore(ChatColor.GRAY + "» K/D");
                KD.setScore(15);

                Score KillName = obj.getScore(ChatColor.GRAY + "Kills:");
                KillName.setScore(14);

                Team kills = board.registerNewTeam("Kills");
                kills.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);
                kills.setPrefix(String.valueOf(killsI));
                obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE).setScore(13);

                Score DeathName = obj.getScore(ChatColor.GRAY + "Deaths:");
                DeathName.setScore(12);

                Team deaths = board.registerNewTeam("Deaths");
                deaths.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
                //String death = sqlAction.getDeaths(player);
                deaths.setPrefix(String.valueOf(deathsI));

                obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(11);

                Score KillsName = obj.getScore(ChatColor.GRAY + "Kill death ratio:");
                KillsName.setScore(10);

                Team deathsT = board.registerNewTeam("KD");
                deathsT.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE);

                //Calculation for the K/D ratio
                //String sDeaths = sqlAction.getDeaths(player);
                //KDDeaths[0] = Double.parseDouble(sDeaths);
                //double KDDeaths = Double.parseDouble(sqlAction.getDeaths(player));
                double KDRatio = 0.0;
                if (deathsI != 0) {
                    KDRatio = killsI / deathsI;
                    double scale = Math.pow(10, 1);
                    KDRatio = Math.round(KDRatio * scale) / scale;
                }
                //if deaths is 0 set KD to amount of kills
                else {
                    KDRatio = killsI;
                }
                String KDRatios = String.format("%s", KDRatio);
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
                //MySQLActions sqlAction = new MySQLActions();
                Scoreboard board = player.getScoreboard();
                int kills = main.getPlayerManager().getCustomPlayer(player.getUniqueId()).getKills();
                int deaths = main.getPlayerManager().getCustomPlayer(player.getUniqueId()).getDeaths();
                //String KDKillsS = sqlAction.getKills(player);
                //String[] KDDeathsS = new String[1];
                //String deaths = sqlAction.getDeaths(player);
                //KDDeathsS[0] = deaths;

                //String KDDeathsS = sqlAction.getDeaths(player);
                board.getTeam("Kills").setPrefix(String.valueOf(kills));
                board.getTeam("Deaths").setPrefix(String.valueOf(deaths));

                //double[] KDDeaths = new double[1];
                //String death = sqlAction.getDeaths(player);
                //KDDeaths[0] = Double.parseDouble(death);
                //double KDDeaths = Double.parseDouble(sqlAction.getDeaths(player));
                double KDRatio = 0.0;
                if (deaths != 0) {
                    KDRatio = kills / deaths;
                    double scale = Math.pow(10, 1);
                    KDRatio = Math.round(KDRatio * scale) / scale;
                } else {
                    KDRatio = kills;
                }
                String KDRatios = String.format("%s", KDRatio);
                board.getTeam("KD").setPrefix(KDRatios);
            }
        });
    }
}
