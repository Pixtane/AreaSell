package org.gygPlugins.areaSeller;
//region Imports
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
//endregion

public class RemovePlayerFromArea {
    private Statement stmt = null;
    areaSeller plugin;

    public void RemovePlayerFromArea(areaSeller plugin, ResultSet resultSet, FileConfiguration config) throws SQLException {
        String task_id = resultSet.getString("taskId");

        Connection connection = DriverManager.getConnection(config.getString("messenger_with_plugin_SQL"), config.getString("messenger_admin_name"), config.getString("messenger_password"));
        stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);

        String nickname = resultSet.getString("nickname");
        String id = resultSet.getString("id");
        String area_type = resultSet.getString("type");
        String world = resultSet.getString("extraData");

        String command = "rg removemember -w \"" + world + "\" " + area_type + id + " " + nickname;
        this.plugin = plugin;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        });
    }
}
