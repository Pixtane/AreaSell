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
import java.util.logging.Logger;

public class Clear {
    areaSeller plugin;

    public void clear(areaSeller plugin, ResultSet resultSet, FileConfiguration config) throws FileNotFoundException, SQLException {
        int[] pos1 = new int[3];
        int[] pos2 = new int[3];

        String task_id = resultSet.getString("taskId");
        String block = resultSet.getString("nickname");
        String id = resultSet.getString("id");
        String area_type = resultSet.getString("type");
        String world = resultSet.getString("extraData");

        InputStream inputStream = new FileInputStream(config.getString("path_to_worldguard_folder") + "\\worlds\\" + world + "\\regions.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        JSONObject json = new JSONObject(data);
        Map<String, Object> position1 = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) json.get("regions")).get(area_type + id)).get("min");
        Map<String, Object> position2 = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) json.get("regions")).get(area_type + id)).get("max");

        pos1[0] = (int) position1.get("x");
        pos1[1] = (int) position1.get("y");
        pos1[2] = (int) position1.get("z");

        pos2[0] = (int) position2.get("x");
        pos2[1] = (int) position2.get("y");
        pos2[2] = (int) position2.get("z");

        String toPrint = "[AreaSeller] The region \"" + (area_type + id) + "\" was emptied. Location :" +
                "\nPos1 : " + pos1[0] + ", " + pos1[1] + ", " + pos1[2] + "\n"
                + "Pos2 : " + pos2[0] + ", " + pos2[1] + ", " + pos2[2];

        String command;
        if (block != null)
        {
            command = "fill " + pos1[0] + " " + pos1[1] + " " + pos1[2] + " " + pos2[0] + " " + pos2[1] + " " + pos2[2] + " " + block;
        } else {
            command = "fill " + pos1[0] + " " + pos1[1] + " " + pos1[2] + " " + pos2[0] + " " + pos2[1] + " " + pos2[2] + " minecraft:air";
        }

        this.plugin = plugin;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        });
        Logger log = Bukkit.getLogger();
        log.info(toPrint);

        // Setting that the task is done
        Connection connection = DriverManager.getConnection(Objects.requireNonNull(config.getString("messenger_with_plugin_SQL")), config.getString("messenger_admin_name"), config.getString("messenger_password"));
        Statement stmt = connection.createStatement();
        String query = "UPDATE queue SET isDone = 1 WHERE taskId = " + task_id;
        stmt.execute(query);

        if (!connection.isClosed()) {
            connection.close();
        }
    }
}
