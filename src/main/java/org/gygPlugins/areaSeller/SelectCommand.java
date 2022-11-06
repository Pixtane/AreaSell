package org.gygPlugins.areaSeller;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class SelectCommand implements CommandExecutor {
    areaSeller plugin;
    FileConfiguration config;

    public SelectCommand(areaSeller plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("selectall")) {
            return false;
        }

        // sender.sendMessage("Sukky bakka");

        try {
            Connection connection = DriverManager.getConnection(config.getString("database"), "root", "");
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from store");
            String result = "";
            while (resultSet.next()) {
                String nameOfArea = resultSet.getString("nickname");
                result += nameOfArea;
                nameOfArea = resultSet.getString("price");
                result += nameOfArea;
                result += '\n';
            }
            sender.sendMessage(result);

            if (!connection.isClosed()){
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}