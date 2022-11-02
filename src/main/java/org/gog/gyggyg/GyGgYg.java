package org.gog.gyggyg;

//region Imports
// BUKKIT
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

// SQL
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// TIME
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
// endregion

public final class GyGgYg extends JavaPlugin {
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        // region Config.yml data
        config.addDefault("database", "jdbc:mysql://localhost:3306/horaj");
        config.addDefault("messenger_with_plugin_SQL", "jdbc:mysql://localhost:3306/messenger");
        config.options().copyDefaults(true);
        saveConfig();
        // endregion
        // region commands
        getCommand("selectall").setExecutor(new SelectCommand(this, config));
        getCommand("viewbalance").setExecutor(new BalanceCheck(this, config));
        //endregion
        // region comments
        // Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + commandSender.getName() + " Automatic Console Ban!")
        // Bukkit.broadcastMessage("This sh*t really works!");
        //endregion
        // region SQL messenger timer
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::checkMessengerSQL, 0, 3, TimeUnit.SECONDS);
        //endregion
    }

    public void checkMessengerSQL() {
        try {
            // region connection to MySQL
            Connection connection = DriverManager.getConnection(config.getString("messenger_with_plugin_SQL"), "root", "");
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
                    }
                }
            }
            //endregion
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
