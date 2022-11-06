package org.gygPlugins.areaSeller;

//region Imports
// BUKKIT

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
// endregion

public final class areaSeller extends JavaPlugin {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        //region Config defaults
        // TODO create database's
        config.addDefault("database", "jdbc:mysql://localhost:3306/areaseller");
        config.addDefault("admin_name", "root");
        config.addDefault("password", "");
        config.addDefault("messenger_with_plugin_SQL", "jdbc:mysql://localhost:3306/messenger");
        config.addDefault("messenger_admin_name", "root");
        config.addDefault("messenger_password", "");
        config.addDefault("path_to_worldguard_folder", "D:\\minecraft\\Paper\\plugins\\WorldGuard");
        config.options().copyDefaults(true);

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), config.getString("messenger_admin_name"), config.getString("messenger_password"));
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS queue (isDone BOOLEAN default false, taskId int(10) NOT NULL PRIMARY KEY auto_increment, actiontodo varchar(45) NOT NULL, nickname varchar(45), id int(11), type varchar(45), extraData varchar(45));";
            stmt.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        saveConfig();
        // endregion

        // region commands
        getCommand("selectall").setExecutor(new SelectCommand(this, config));
        getCommand("viewbalance").setExecutor(new BalanceCheck(this, config));
        //endregion

        // region SQL messenger timer
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::checkMessengerSQL, 0, 3, TimeUnit.SECONDS);
    }

    public void checkMessengerSQL() {
        try {
            // region connection to MySQL
            Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), config.getString("messenger_admin_name"), config.getString("messenger_password")); // TODO change connection to one-time.
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from queue");
            //endregion
            // region actions
            while (resultSet.next()) {
                if (!resultSet.getBoolean("isDone")) {
                    String action = resultSet.getString("actiontodo");
                    switch (action) {
                        case "REVOKE":
                            RemovePlayerFromArea remove = new RemovePlayerFromArea();
                            remove.RemovePlayerFromArea(this, resultSet, config);
                            break;
                        case "ADD": {
                            AddPlayerToArea add = new AddPlayerToArea();
                            add.AddPlayerToArea(this, resultSet, config);
                            break;
                        }
                        case "ADDMONEY": {
                            AddMoney addmoney = new AddMoney();
                            addmoney.AddMoney(this, resultSet, config);
                            break;
                        }
                        case "REMOVEMONEY": {
                            RemoveMoney removemoney = new RemoveMoney();
                            removemoney.removeMoney(this, resultSet, config);
                            break;
                        }
                        case "CLEAR": {
                            Clear clearegion = new Clear();
                            clearegion.clear(this, resultSet, config);
                            break;
                        }
                        case "TRANSFERMONEY": {
                            TransferMoney transfer = new TransferMoney();
                            transfer.transferMoney(resultSet, config);
                            break;
                        }
                    }
                }
            }
            if (!connection.isClosed()) {
                connection.close();
            }
            //endregion
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}

/*
 TODO   Commands
        REVOKE - delete member ✅
        ADD    - add member    ✅
        CLEAR  - delete everything in region
        MESSEGE- messege to the everyone in region
        REMOVEMONEY - delete money from player ✅
        ADDMONEY - add money to player ✅
        TRANSFERMONEY - delete money from one player and add them to another.
        Chest interface
*/
