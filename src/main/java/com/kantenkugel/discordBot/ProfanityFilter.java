package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;

public class ProfanityFilter {

    public static void filter(HashMap<String, Integer> profanities, Message message){

        String[] tempMessage = message.getContentRaw().split(" ");

        for(int i = 0; i < tempMessage.length; i++){

            if(profanities.containsKey(tempMessage[i])){
                message.delete().queue();



                return;
            }

        }

    }

}
