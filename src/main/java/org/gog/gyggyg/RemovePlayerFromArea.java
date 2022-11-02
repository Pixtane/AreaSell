package org.gog.gyggyg;
//region Imports
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
//endregion

public class RemovePlayerFromArea {
    private Statement stmt = null;
    GyGgYg plugin;

    public void RemovePlayerFromArea(GyGgYg plugin, ResultSet resultSet, FileConfiguration config) throws SQLException {
        String task_id = resultSet.getString("taskId");

        Connection connection = DriverManager.getConnection(config.getString("messenger_with_plugin_SQL"), "root", "");
        Statement statement = connection.createStatement();
        stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);

        String nickname = resultSet.getString("nickname");
        String id = resultSet.getString("id");
        String area_type = resultSet.getString("type");
        String world = resultSet.getString("extraData");

        String command = "rg removemember -w \"" + world + "\" " + area_type + id + " " + nickname;
        Bukkit.broadcastMessage(command);
        this.plugin = plugin;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        });
    }
}
