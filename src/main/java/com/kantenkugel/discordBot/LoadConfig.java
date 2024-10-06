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

        return db;
    }

    public static HashSet<String> loadProfanity(Message message){

        HashSet<String> profanities = new HashSet<>();
        String path = System.getProperty("user.dir") + "\\data\\" + message.getGuildId() + "-profanity.txt";
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
