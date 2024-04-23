package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class BanButtons extends ListenerAdapter {

    Member member;

    public void BanButtons(MessageReceivedEvent event) {
        event.getMessage().reply("ClickButtons:")
                    .addActionRow(
                Button.primary("ban", "BAN"), // Button with only a label
                Button.success("kick", "KICK")) // Button with only an emoji
                .queue();
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild=event.getGuild();
        assert guild != null;

        List<Message> mess = null;

        if (event.getComponentId().equals("ban")) {

            try {
                mess = event.getChannel().getIterableHistory().takeAsync(2)
                        .thenApply(list -> list.stream().collect(Collectors.toList())).get();
                assert member != null;
                UserSnowflake us = User.fromId(mess.get(1).getMentions().getUsers().get(0).getIdLong());

                // TODO: HERE WE WANT TO EDIT THE MESSAGE SO WE ASK FOR A REASON.


                guild.ban(us, 1, TimeUnit.SECONDS).queue();

                // TODO: HERE WE WANT TO CREATE AN EMBED WHICH SHOWS THE BAN.


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

        } else if (event.getComponentId().equals("kick")) {
            try {
                mess = event.getChannel().getIterableHistory().takeAsync(2)
                        .thenApply(list -> list.stream().collect(Collectors.toList())).get();
                assert member != null;
                UserSnowflake us = User.fromId(mess.get(1).getMentions().getUsers().get(0).getIdLong());
                guild.kick(us).queue();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }        }
    }

}

