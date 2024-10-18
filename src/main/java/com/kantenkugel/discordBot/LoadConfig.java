package com.kantenkugel.discordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LoadConfig {

    public static DatabaseConnection load(Guild guild){
        String guild_id = guild.getId();
        // here we load all configs from our database and implement them.

        DatabaseConnection db = new DatabaseConnection();

        if (!db.server_exists(guild_id)){
            db.insert_new_server(guild_id);
        }else System.out.println("server already exists");
        // db.close_connection();

        // insert the ban_menu_options as well as punishments
        // this will only happen if this server does not have a ban_menu config.



        return db;
    }

    public static void load_for_server(Guild guild, DatabaseConnection db){
        String guild_id = guild.getId();
        if (!db.punishments_exists()){
            System.out.println("creating punishments");
            create_standard_punishments(db);
        }

        if (!db.ban_menu_options_exists(guild_id)){
            // here we create the standard ban menus
            System.out.println("creating ban_menu options");
            create_standard_ban_menu(db, guild_id);
        }
    }

    public static void create_standard_ban_menu(DatabaseConnection db, String guild_id){

        db.add_new_ban_menu_options(guild_id, 1, new int[]{0, 0});
        db.add_new_ban_menu_options(guild_id, 2, new int[]{0, 1});
        db.add_new_ban_menu_options(guild_id, 3, new int[]{1, 0});
        db.add_new_ban_menu_options(guild_id, 4, new int[]{1, 1});
    }


    public static void create_standard_punishments(DatabaseConnection db){

        db.create_new_punishment_option("Ban", 3);
        db.create_new_punishment_option("Kick", 1);
        db.create_new_punishment_option("Timeout", 0);
        db.create_new_punishment_option("Mute", 1);
    }

    public static HashSet<String> loadProfanity(Message message){

        HashSet<String> profanities = new HashSet<>();
        String path = System.getProperty("user.dir") + "\\data\\profanity.txt";
        System.out.println(path);
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while (true) {
                String tempLine = br.readLine();

                if(tempLine != null) {

                    profanities.add(tempLine);

                }else{
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return profanities;
    }
}
