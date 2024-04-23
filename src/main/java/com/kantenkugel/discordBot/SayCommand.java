package com.kantenkugel.discordBot;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SayCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("echo")) {
            event.reply(event.getOption("content").getAsString()).queue(); // reply immediately
        }
    }
}
