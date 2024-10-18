package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Buttons extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        // HERE WE HAVE THE BAN MENU BUTTON INTERACTIONS.

        String buttonTypeID = event.getComponentId().split("-")[0];
        String buttonID = event.getComponentId().split("-")[1];

        switch (buttonTypeID){

            case "userMenu":
                BanMenu.ButtonInteraction(event, buttonID);
                break;

            case "profanityFilter":
                ProfanityFilter.ButtonInteraction(event, buttonID);
                break;
        }
    }
}
