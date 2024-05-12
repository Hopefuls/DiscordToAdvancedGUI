package de.hopedev;

import java.util.EnumSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.hopedev.loader.LayoutHandler;
import me.leoko.advancedgui.manager.LayoutManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main extends JavaPlugin {
    public static String LAYOUT_NAME = "LeokoDevChat";
    public static JDA discordbot = null;
    public static String channelID = null;
    public static Plugin instance = null;
    public static FileConfiguration config = null;
    
    @Override
    public void onEnable() {
        instance = this;
        System.out.println("Plugin enabled");
        LayoutManager.getInstance().registerLayoutExtension(new LayoutHandler(), this);
        System.out.println("Loading config");

        saveDefaultConfig();

        config = getConfig();

        channelID = config.getString("channelID");


        EnumSet<GatewayIntent> intents = EnumSet.allOf(GatewayIntent.class);
        discordbot = JDABuilder.createDefault(config.getString("token")).enableIntents(intents).build();
        try {
            discordbot.awaitReady();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Successfully logged in as:" + discordbot.getSelfUser().getName());

    }

}