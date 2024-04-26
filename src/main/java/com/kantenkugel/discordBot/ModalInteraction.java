package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ModalInteraction extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        event.deferReply(true).queue();
        System.out.println(event.getMessage().getContentRaw());
        if (event.getModalId().equals("modal-ban")) {
            Guild guild = event.getGuild();
            UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[3]));
            String username = event.getMessage().getContentRaw().split(" ")[1];

            System.out.println(event.isAcknowledged());

            guild.ban(us, 2, TimeUnit.SECONDS).queue();



            String body = event.getValue("body").getAsString();

            System.out.println("REASON: " + body);


            EmbedBuilder embedBuilder = new EmbedBuilder();

            // Set the title of the embed
            embedBuilder.setTitle("Banned User: " + username);

            // Set the description of the embed
            embedBuilder.setDescription("This is a simple example of an embed message.");

            // Set other properties as needed
            embedBuilder.setColor(0xFF0028); // Set the color to gold
            embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
            embedBuilder.addField("USERID: ", us.getId(), true);
            embedBuilder.addField("REASON: ", body, false);

            // Build the embed object


            guild.getTextChannelById(Long.parseLong(event.getMessage().getContentRaw().split(" ")[5]))
                    .sendMessageEmbeds(embedBuilder.build()).queue();



            event.getChannel().delete().queue();

        }

        if (event.getModalId().equals("modal-kick")) {
            Guild guild = event.getGuild();
            UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[3]));
            String username = event.getMessage().getContentRaw().split(" ")[1];

            System.out.println(event.isAcknowledged());

            guild.kick(us).queue();



            String body = event.getValue("body").getAsString();

            System.out.println("REASON: " + body);

            EmbedBuilder embedBuilder = new EmbedBuilder();

            // Set the title of the embed
            embedBuilder.setTitle("Kicked User: " + username);

            // Set the description of the embed
            embedBuilder.setDescription("This is a simple example of an embed message.");

            // Set other properties as needed
            embedBuilder.setColor(0xFFD700); // Set the color to gold
            embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
            embedBuilder.addField("USERID: ", us.getId(), false);
            embedBuilder.addField("REASON: ", body, false);

            // Build the embed object


            guild.getTextChannelById(Long.parseLong(event.getMessage().getContentRaw().split(" ")[5]))
                    .sendMessageEmbeds(embedBuilder.build()).queue();



            event.getChannel().delete().queue();

        }

    }
}
