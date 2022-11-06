package org.gygPlugins.areaSeller;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.Objects;

public class AddMoney {
    areaSeller plugin;

    public void AddMoney(areaSeller plugin, ResultSet resultSet, FileConfiguration config) throws SQLException {
        this.plugin = plugin;

        String task_id = resultSet.getString("taskId");
        String nickname = resultSet.getString("nickname");
        int money = resultSet.getInt("extraData");

        Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), config.getString("messenger_admin_name"), config.getString("messenger_password"));
        Statement stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);

        if (!connection.isClosed()) {
            connection.close();
        }

        Connection connection2 = DriverManager.getConnection(Objects.requireNonNull(config.getString("database")), config.getString("admin_name"), config.getString("password"));
        stmt = connection2.createStatement();

        ResultSet resultSet2 = stmt.executeQuery("select * from xconomy");
        while (resultSet2.next()) {
            if (resultSet2.getString("player").equals(nickname)) {
                query = "UPDATE xconomy SET balance = " + (money + resultSet2.getInt("balance")) + " WHERE player = \"" + nickname + "\"";
                stmt.execute(query);
                break;
            }
        }
        if (!connection2.isClosed()) {
            connection.close();
        }
    }
}
