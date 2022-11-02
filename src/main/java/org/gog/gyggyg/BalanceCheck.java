package org.gog.gyggyg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class BalanceCheck implements CommandExecutor {
    GyGgYg plugin;
    FileConfiguration config;

    public BalanceCheck(GyGgYg plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("viewbalance")) {
            return false;
        }

        // sender.sendMessage("Sukky bakka");

        try {
            Connection connection = DriverManager.getConnection(config.getString("database"), "root", "");
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from xconomy");
            String result = "";
            while (resultSet.next()) {
                String nameOfArea = resultSet.getString("player");
                result = result + nameOfArea + " ";
                nameOfArea = resultSet.getString("balance");
                result = result + nameOfArea + '\n';
            }
            sender.sendMessage(result);

            if (!connection.isClosed()){ // checking if connection isn't null to
                // avoid receiving a nullpointer
                connection.close(); // closing the connection field variable.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}