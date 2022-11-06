package org.gygPlugins.areaSeller;

//region Imports

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
//endregion

public class AddPlayerToArea {
    areaSeller plugin;
    private Statement stmt = null;

    public void AddPlayerToArea(areaSeller plugin, ResultSet resultSet, FileConfiguration config) throws SQLException {
        this.plugin = plugin;

        String task_id = resultSet.getString("taskId");
        String nickname = resultSet.getString("nickname");
        String id = resultSet.getString("id");
        String area_type = resultSet.getString("type");
        String world = resultSet.getString("extraData");

        Connection connection = DriverManager.getConnection(config.getString("messenger_with_plugin_SQL"), config.getString("messenger_admin_name"), config.getString("messenger_password"));
        stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);

        String command = "rg addmember -w \"" + world + "\" " + area_type + id + " " + nickname;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        });
    }
}
