package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class BanButtons extends ListenerAdapter {

    Member member;

    public void BanButtons(MessageReceivedEvent event, com.kantenkugel.discordBot.Commands commands) {


        commands.createModWindow(event);


    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild=event.getGuild();
        assert guild != null;

        List<Message> mess = null;
        // HERE WE HAVE THE BAN MENU BUTTON INTERACTIONS.

        TextInput body;
        TextInput timeSpan;
        Modal modal;

        switch (event.getComponentId()){

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

                modal = Modal.create("modal-ban", "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .addComponents(ActionRow.of(timeSpan))
                        .build();

                event.replyModal(modal).queue();



                /*guild.ban(us, 1, TimeUnit.SECONDS).queue();
                event.getMessage().delete().queue();
                */

                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE BANNED
                break;

            case "kick":
                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE KICKED

                body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(0)
                        .setMaxLength(1000)
                        .build();

                modal = Modal.create("modal-kick", "USERID: " + event.getMember().getId())
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

                modal = Modal.create("modal-timeout", "USERID: " + event.getMember().getId())
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
}

