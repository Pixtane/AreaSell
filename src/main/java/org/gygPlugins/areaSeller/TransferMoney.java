package org.gygPlugins.areaSeller;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransferMoney {

    public void transferMoney(ResultSet resultSet, FileConfiguration config) throws FileNotFoundException, SQLException {
        // Gets variables
        Logger log = Bukkit.getLogger();
        String task_id = resultSet.getString("taskId");
        String from = resultSet.getString("nickname");
        int money = resultSet.getInt("id");
        String to = resultSet.getString("extraData");

        // Setting that the task is done
        Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), config.getString("messenger_admin_name"), config.getString("messenger_password"));
        Statement stmt2 = connection.createStatement();
        String query2 = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt2.execute(query2);

        if (!connection.isClosed()) {
            connection.close();
        }

        // Finds player in xconomy list, if it is the sender, it remove money from him, if it is receiver, it adds him money

        Connection connection2 = DriverManager.getConnection(Objects.requireNonNull(config.getString("database")), config.getString("admin_name"), config.getString("password"));
        Statement stmt = connection2.createStatement();

        ResultSet resultSet2 = stmt.executeQuery("select * from xconomy");

        while (resultSet2.next()) {
            if (resultSet2.getString("player").equals(from)) {
                if (resultSet2.getInt("balance") < money)
                {
                    log.log(Level.SEVERE, "[AreaSeller] Not enough money!");
                    return;
                }
                String query = "UPDATE xconomy SET balance = " + (resultSet2.getInt("balance") - money) + " WHERE player = \"" + from + "\"";
                stmt.execute(query);
                break;
            }

        }
        connection2 = DriverManager.getConnection(Objects.requireNonNull(config.getString("database")), config.getString("admin_name"), config.getString("password"));
        stmt = connection2.createStatement();

        resultSet2 = stmt.executeQuery("select * from xconomy");
        while (resultSet2.next()) {
            if (resultSet2.getString("player").equals(to)) {
                String query = "UPDATE xconomy SET balance = " + (resultSet2.getInt("balance") + money) + " WHERE player = \"" + to + "\"";
                stmt.execute(query);
                break;
            }
        }
        if (!connection2.isClosed()) {
            connection2.close();
        }

        // Logging that the task is done
        log.info("[AreaSeller] " + money + "$ were transfered from \"" + from + "\" to \"" + to + "\"");


    }
}
