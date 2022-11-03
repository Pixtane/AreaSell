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
        String task_id = resultSet.getString("taskId");

        Connection connection = DriverManager.getConnection(config.getString("messenger_with_plugin_SQL"), "root", "");
        Statement statement = connection.createStatement();
        stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);

        String nickname = resultSet.getString("nickname");//resultSet.getString("nickname"); "17661d83-4680-3e6a-bff3-f7820783f3f0";
        String id = resultSet.getString("id");
        String area_type = resultSet.getString("type");
        String world = resultSet.getString("extraData");

        String command = "rg addmember -w \"" + world + "\" " + area_type + id + " " + nickname; // + world + " " /rg removemember -w "world" store1
        Bukkit.broadcastMessage(command);
        this.plugin = plugin;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        });
    }
}
