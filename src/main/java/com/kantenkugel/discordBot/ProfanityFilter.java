package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class ProfanityFilter {

    public static void filter(HashSet<String> profanities, Message message, TextChannel mod_text_channel,  DatabaseConnection db){

        String[] tempMessage = message.getContentRaw().split(" ");

        for (String s : tempMessage) {

            if (profanities.contains(s)) {
                message.delete().queue();
                timeout_user(message, db);
                //createDeletedMsgEmbed(message, mod_text_channel, s);
                System.out.println(message);

                return;
            }

        }

    }


    public static void timeout_user(Message message, DatabaseConnection db){
        int duration = 30;
        db.give_user_punishment(message.getGuildId().toString(), duration, message.getAuthor().getId(),
                message.getChannelId(), "ProfanityFilter_timeout", message.getContentRaw());

        Guild guild = message.getGuild();

        guild.timeoutFor(message.getAuthor(), duration, TimeUnit.SECONDS)
                .reason("ProfanityFilter_timeout")
                .queue();


        System.out.println("timedout user: " + message.getAuthor().getId() + " | reason: " + "ProfanityFilter_timeout"
                + " | duration:  " + duration + " seconds");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        // Set the title of the embed
        embedBuilder.setTitle("TimedOut User: " + message.getAuthor().getEffectiveName());

        // Set the description of the embed

        // Set other properties as needed
        embedBuilder.setColor(0xFFD700); // Set the color to gold
        embedBuilder.addField("USER:", message.getAuthor().getEffectiveName(), true); // Add a field with inline formatting
        embedBuilder.addField("USERID: ", message.getAuthor().getId(), true);
        embedBuilder.addField("REASON: ", "ProfanityFilter_timeout", false);
        embedBuilder.addField("DURATION: ", duration + "s", false);

        // Build the embed object

        message.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void createDeletedMsgEmbed(Message message, TextChannel mod_text_channel, String reason){

        EmbedBuilder embedBuilder = new EmbedBuilder();

        // Set the title of the embed
        embedBuilder.setTitle("Auto Deleted Message");


        UserSnowflake user = message.getAuthor();

        embedBuilder.setColor(0xFFD700); // Set the color to gold
        embedBuilder.addField("USER:", message.getAuthor().getEffectiveName(), true); // Add a field with inline formatting
        embedBuilder.addField("USERID: ", user.getId(), true);
        embedBuilder.addField("MESSAGE: ", message.getContentRaw(), false);
        embedBuilder.addField("REASON: ", reason, false);


        mod_text_channel.sendMessageEmbeds(embedBuilder.build())

                .addActionRow(
                        Button.danger("profanityFilter-ban", "BAN"), // Button with only a label
                        Button.primary("profanityFilter-kick", "KICK")) // Button with only an emoji

                .addActionRow(
                       Button.success("profanityFilter-timeout", "TIMEOUT")) // Button with only an emoji

                .queue();
    }

    public static void ButtonInteraction(ButtonInteractionEvent event, String banButtonID){
        Guild guild=event.getGuild();
        assert guild != null;
        String userID = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();
        String channelID = event.getChannelId();
        TextInput body;
        TextInput timeSpan;
        Modal modal;

        switch (banButtonID){

            case "ban":                 // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE BANNED

                // TODO: HERE WE WANT TO EDIT THE MESSAGE SO WE ASK FOR A REASON.

                timeSpan = TextInput.create("time", "TIME:", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("how long should the messages be removed? (in s)")
                        .setMinLength(0)
                        .setMaxLength(1000)
                        .build();


                body = TextInput.create("reason", "REASON:", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(10)
                        .setMaxLength(1000)
                        .build();

                modal = Modal.create("profanityFilter-ban-" + userID + "-" + channelID, "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .addComponents(ActionRow.of(timeSpan))
                        .build();

                event.replyModal(modal).queue();

                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE BANNED
                break;

            case "kick":
                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE KICKED

                body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(0)
                        .setMaxLength(1000)
                        .build();

                modal = Modal.create("profanityFilter-kick-" + userID + "-" + channelID, "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .build();

                event.replyModal(modal).queue();

                break;

            case "timeout":

                timeSpan = TextInput.create("time", "TIME:", TextInputStyle.SHORT)
                        .setPlaceholder("how long should the User be timed out?")
                        .setMinLength(0)
                        .setMaxLength(1000)
                        .build();


                body = TextInput.create("reason", "REASON:", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(0)
                        .setMaxLength(1000)
                        .build();

                modal = Modal.create("profanityFilter-timeout-" + userID + "-" + channelID, "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .addComponents(ActionRow.of(timeSpan))
                        .build();

                event.replyModal(modal).queue();
                break;



            default:
                break;

        }
    }


    public static void ModalInteraction(ModalInteractionEvent event, String modalID, String userID, String channelID){


        event.deferReply(true).queue();
        Guild guild = event.getGuild();
        UserSnowflake us = User.fromId(userID);
        String username = User.fromId(userID).toString();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String body, timeS;
        int timespan;

        switch (modalID) {
            case "ban":


                body = event.getValue("reason").getAsString();
                timespan = Integer.parseInt(event.getValue("time").getAsString());


                guild.ban(us, timespan, TimeUnit.SECONDS)
                        .reason(body)
                        .queue();

                System.out.println("banned user: " + userID + " | reason: " + body);

                // Set the title of the embed
                embedBuilder.setTitle("Banned User: " + username);

                // Set the description of the embed

                // Set other properties as needed
                embedBuilder.setColor(0xFF0028); // Set the color to gold
                embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
                embedBuilder.addField("USERID: ", us.getId(), true);
                embedBuilder.addField("REASON: ", body, false);

                // Build the embed object

                guild.getTextChannelById(channelID)
                        .sendMessageEmbeds(embedBuilder.build()).queue();

                break;

            case "kick":
                guild.kick(us).queue();

                body = event.getValue("reason").getAsString();

                System.out.println("kicked user: " + userID + " | reason: " + body);

                // Set the title of the embed
                embedBuilder.setTitle("Kicked User: " + username);

                // Set the description of the embed

                // Set other properties as needed
                embedBuilder.setColor(0xFFD700); // Set the color to gold
                embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
                embedBuilder.addField("USERID: ", us.getId(), false);
                embedBuilder.addField("REASON: ", body, false);

                // Build the embed object

                guild.getTextChannelById(channelID)
                        .sendMessageEmbeds(embedBuilder.build()).queue();

                break;

            case "timeout":

                body = event.getValue("reason").getAsString();
                timespan = Integer.parseInt(event.getValue("time").getAsString());

                assert guild != null;
                guild.timeoutFor(us, timespan, TimeUnit.SECONDS)
                        .reason(body)
                        .queue();

                timeS = event.getValue("time").getAsString();

                System.out.println("timedout user: " + userID + " | reason: " + body + " | duration:  " + timeS);

                // Set the title of the embed
                embedBuilder.setTitle("TimedOut User: " + username);

                // Set the description of the embed

                // Set other properties as needed
                embedBuilder.setColor(0xFFD700); // Set the color to gold
                embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
                embedBuilder.addField("USERID: ", us.getId(), true);
                embedBuilder.addField("REASON: ", body, false);
                embedBuilder.addField("DURATION: ", timeS + "s", false);

                // Build the embed object

                guild.getTextChannelById(channelID)
                        .sendMessageEmbeds(embedBuilder.build()).queue();

                break;
        }
        event.getMessage().delete().queue();
    }
}
