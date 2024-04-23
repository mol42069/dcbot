package com.kantenkugel.discordBot;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SayCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("echo")) {
            event.reply(event.getOption("content").getAsString()).queue(); // reply immediately
        }
        else if(event.getName().equals("ban")){

            event.deferReply(true)
                    .addContent("USER: " + event.getOption("user").getAsUser().getEffectiveName()+ "\n"
                            + "USERID: "+event.getOption("user").getAsMember().getUser().getIdLong())
                    .addActionRow(
                            Button.primary("banSlash", "BAN"),
                            Button.success("kickSlash", "KICK"))
                    .queue();

        }
    }
}
