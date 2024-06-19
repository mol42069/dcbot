package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.entities.Message;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class LoadConfig {

    public static void load(Bot bot){

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
