package org.gygPlugins.areaSeller;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Objects;

public class AddMoney {
    areaSeller plugin;

    public void AddMoney(areaSeller plugin, ResultSet resultSet, FileConfiguration config) throws SQLException {
        this.plugin = plugin;

        Bukkit.broadcastMessage("1$");
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("hi");
        }
        String task_id = resultSet.getString("taskId");
        String nickname = resultSet.getString("nickname");
        int money = resultSet.getInt("extraData");
        Bukkit.broadcastMessage("2$");

        Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), "root", "");
        Statement stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);
        Bukkit.broadcastMessage("3$");

        if (!connection.isClosed()) {
            connection.close();
        }
        Bukkit.broadcastMessage("4$");

        Connection connection2 = DriverManager.getConnection(Objects.requireNonNull(config.getString("database")), "root", "");
        stmt = connection2.createStatement();
        Bukkit.broadcastMessage("5$");

        ResultSet resultSet2 = stmt.executeQuery("select * from xconomy");
        Bukkit.broadcastMessage("6$");
        while (resultSet2.next()) {
            Bukkit.broadcastMessage("7$");
            if (resultSet2.getString("player").equals(nickname)) {
                Bukkit.broadcastMessage(String.valueOf(resultSet2.getInt("balance")));
                Bukkit.broadcastMessage(String.valueOf(money));
                query = "UPDATE xconomy SET balance = " + (money + resultSet2.getInt("balance")) +" WHERE player = \"" + nickname + "\"";
                stmt.execute(query);
                Bukkit.broadcastMessage("9$");
                break;
            }
        }
        if (!connection2.isClosed()) {
            connection.close();
        }
        if (!connection.isClosed()) {
            connection.close();
        }
        Bukkit.broadcastMessage("10$");

    }
}
