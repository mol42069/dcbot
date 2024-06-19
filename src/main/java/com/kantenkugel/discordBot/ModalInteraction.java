package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ModalInteraction extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        String modalTypeID = event.getModalId().split("-")[0];
        String modalID = event.getModalId().split("-")[1];
        switch(modalTypeID){

            case "userMenu":
                BanMenu.ModalInteraction(event, modalID);

            case "profanityFilter":
                String userID = event.getModalId().split("-")[2];
                String channelID = event.getModalId().split("-")[3];
                ProfanityFilter.ModalInteraction(event, modalID, userID, channelID);
            default:

                break;

        }



    }
}
