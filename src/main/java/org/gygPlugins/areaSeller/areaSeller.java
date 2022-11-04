package org.gygPlugins.areaSeller;

//region Imports
// BUKKIT

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
        config.addDefault("database", "jdbc:mysql://localhost:3306/horaj");
        config.addDefault("messenger_with_plugin_SQL", "jdbc:mysql://localhost:3306/messenger");
        config.options().copyDefaults(true);
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
            Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), "root", "");
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
                            removemoney.RemoveMoney(this, resultSet, config);
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

