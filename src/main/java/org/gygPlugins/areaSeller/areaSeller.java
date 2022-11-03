package org.gygPlugins.areaSeller;

//region Imports
// BUKKIT
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

// SQL
import java.sql.*;

// TIME
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
// endregion

public final class areaSeller extends JavaPlugin {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        Bukkit.broadcastMessage("-100$");
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
    }

    public void checkMessengerSQL() {
        try {
            getServer().broadcastMessage("Yes!");

            // region connection to MySQL
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("hi");
            }
            Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from queue");
            Bukkit.broadcastMessage("-10$");
            //endregion
            // region actions
            while (resultSet.next()) {
                if (!resultSet.getBoolean("isDone")) {
                    String action = resultSet.getString("actiontodo");
                    Bukkit.broadcastMessage("-1$");
                    switch (action) {
                        case "REVOKE":
                            Bukkit.broadcastMessage("0!");
                            RemovePlayerFromArea remove = new RemovePlayerFromArea();
                            remove.RemovePlayerFromArea(this, resultSet, config);
                            break;
                        case "ADD": {
                            Bukkit.broadcastMessage("0@");
                            AddPlayerToArea add = new AddPlayerToArea();
                            add.AddPlayerToArea(this, resultSet, config);
                            break;
                        }
                        case "ADDMONEY": {
                            Bukkit.broadcastMessage("0$");
                            AddMoney addmoney = new AddMoney();
                            addmoney.AddMoney(this, resultSet, config);
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

