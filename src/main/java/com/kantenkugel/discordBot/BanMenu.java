package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class BanMenu {

    public static void create_layout(MessageReceivedEvent event) {

        Guild guild = event.getGuild();
        String channelName = event.getChannel().getName();

        TextChannel existingChannel = guild.getTextChannelsByName(channelName, true).stream().findFirst().orElse(null);
        int channelPosition = existingChannel.getPositionRaw();
        channelName += "-mod";

        String categoryName = existingChannel.getParentCategory() != null ? existingChannel.getParentCategory().getName() : null;


        ChannelAction<TextChannel> channelAction = guild.createTextChannel(channelName);


        // Set the category for the new channel (if the existing channel was in a category)
        channelAction.setPosition(channelPosition);

        if (categoryName != null) {
            channelAction.setParent(guild.getCategoriesByName(categoryName, true).get(0));
        }

        EnumSet<Permission> permissions = EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND);
        channelAction
                .addPermissionOverride(event.getMember(), permissions, null) // grant access to the user
                .addPermissionOverride(guild.getPublicRole(), null, permissions) // deny access to @everyone.addPermissionOverride(event.getMember())
                .addPermissionOverride(guild.getRolesByName("Besserer-Mensch", false).get(0), permissions, null);


        // Complete the channel creation and update the guild
        channelAction.complete();

        System.out.println(guild.getTextChannelsByName(channelName, true).get(0));

        guild.getTextChannelsByName(channelName, false).get(0)
                .sendMessage("User: " + event.getMessage().getMentions().getUsers().get(0).getEffectiveName() + " \n" +
                        "UserId: " + event.getMessage().getMentions().getUsers().get(0).getId() + " \n" +
                        "ChannelID: " + event.getChannel().getId()
                )
                .addActionRow(
                        Button.danger("userMenu-ban", "BAN"), // Button with only a label
                        Button.primary("userMenu-kick", "KICK")) // Button with only an emoji

                .addActionRow(
                        Button.primary("userMenu-mute", "MUTE"), // Button with only a label
                        Button.success("userMenu-timeout", "TIMEOUT")) // Button with only an emoji

                .addActionRow(
                        Button.secondary("userMenu-cancel", "CANCEL"))// Button with only a label


                .queue();

        event.getMessage().delete().queue();


    }

    public static void ButtonInteraction(ButtonInteractionEvent event, String banButtonID){


        Guild guild=event.getGuild();
        assert guild != null;

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

                modal = Modal.create("userMenu-ban", "USERID: " + event.getMember().getId())
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

                modal = Modal.create("userMenu-kick", "USERID: " + event.getMember().getId())
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

                modal = Modal.create("userMenu-timeout", "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .addComponents(ActionRow.of(timeSpan))
                        .build();

                event.replyModal(modal).queue();
                break;

            case "mute":

                UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[3]));

                guild.mute(us, true).queue();

                event.getChannel().delete().queue();
                break;

            case "cancel":
                event.getChannel().delete().queue();

                break;

            default:
                break;

        }


    }

    public static void ModalInteraction(ModalInteractionEvent event, String modalID){


        event.deferReply(true).queue();
        System.out.println(event.getMessage().getContentRaw());
        Guild guild = event.getGuild();
        UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[3]));
        String username = event.getMessage().getContentRaw().split(" ")[1];

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

                timeS = event.getValue("time").getAsString();

                // Set the title of the embed
                embedBuilder.setTitle("Banned User: " + username);

                // Set the description of the embed

                // Set other properties as needed
                embedBuilder.setColor(0xFF0028); // Set the color to gold
                embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
                embedBuilder.addField("USERID: ", us.getId(), true);
                embedBuilder.addField("REASON: ", body, false);

                // Build the embed object


                guild.getTextChannelById(Long.parseLong(event.getMessage().getContentRaw().split(" ")[5]))
                        .sendMessageEmbeds(embedBuilder.build()).queue();


                event.getChannel().delete().queue();
                break;

            case "kick":

                guild.kick(us).queue();

                body = event.getValue("reason").getAsString();

                // Set the title of the embed
                embedBuilder.setTitle("Kicked User: " + username);

                // Set the description of the embed

                // Set other properties as needed
                embedBuilder.setColor(0xFFD700); // Set the color to gold
                embedBuilder.addField("USER:", username, true); // Add a field with inline formatting
                embedBuilder.addField("USERID: ", us.getId(), false);
                embedBuilder.addField("REASON: ", body, false);

                // Build the embed object

                guild.getTextChannelById(Long.parseLong(event.getMessage().getContentRaw().split(" ")[5]))
                        .sendMessageEmbeds(embedBuilder.build()).queue();

                event.getChannel().delete().queue();
                break;

            case "timeout":

                body = event.getValue("reason").getAsString();
                timespan = Integer.parseInt(event.getValue("time").getAsString());

                assert guild != null;
                guild.timeoutFor(us, timespan, TimeUnit.SECONDS)
                        .reason(body)
                        .queue();

                timeS = event.getValue("time").getAsString();

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

                guild.getTextChannelById(Long.parseLong(event.getMessage().getContentRaw().split(" ")[5]))
                        .sendMessageEmbeds(embedBuilder.build()).queue();

                break;

        }
        event.getChannel().delete().queue();
    }



}
