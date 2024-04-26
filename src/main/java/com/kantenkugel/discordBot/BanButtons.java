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
        if (event.getComponentId().equals("ban")) {


                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE BANNED

                // TODO: HERE WE WANT TO EDIT THE MESSAGE SO WE ASK FOR A REASON.

                TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(10)
                        .setMaxLength(1000)
                        .build();

                Modal modal = Modal.create("modal-ban", "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .build();

                event.replyModal(modal).queue();



                /*guild.ban(us, 1, TimeUnit.SECONDS).queue();
                event.getMessage().delete().queue();
                */

                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE BANNED


        } else if (event.getComponentId().equals("kick")) {


                // HERE WE GET THE LAST MESSAGE AND EXTRACT THE ID FROM THE USER WHICH IS SUPPOSED TO BE KICKED

                TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(0)
                        .setMaxLength(1000)
                        .build();

                Modal modal = Modal.create("modal-kick", "USERID: " + event.getMember().getId())
                        .addComponents(ActionRow.of(body))
                        .build();

                event.replyModal(modal).queue();

        } else if (event.getComponentId().equals("mute")){

            UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[3]));

            guild.mute(us, true).queue();


            event.getChannel().delete().queue();

        }else if(event.getComponentId().equals("cancel")){

            event.getChannel().delete().queue();

        } else if (event.getComponentId().equals("banSlash")) {

            UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[2]));

                // TODO: HERE WE WANT TO EDIT THE MESSAGE SO WE ASK FOR A REASON.



            guild.ban(us, 1, TimeUnit.SECONDS).queue();

                // TODO: HERE WE WANT TO CREATE AN EMBED WHICH SHOWS THE BAN.
                // for now i delete the message.

            mess.get(0).delete().queue();


        } else if (event.getComponentId().equals("kickSlash")) {


            UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[2]));
            guild.kick(us).queue();

                // for now i delete the message.
            mess.get(0).delete().queue();



        }else if (event.getComponentId().equals("ban")) {

            try {
                System.out.println(event.getMessage().getContentRaw().split(" ")[2]);
                mess = event.getChannel().getIterableHistory().takeAsync(2)
                        .thenApply(list -> list.stream().collect(Collectors.toList())).get();
                assert member != null;
                UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[2]));

                // TODO: HERE WE WANT TO EDIT THE MESSAGE SO WE ASK FOR A REASON.


                guild.ban(us, 1, TimeUnit.SECONDS).queue();

                // TODO: HERE WE WANT TO CREATE AN EMBED WHICH SHOWS THE BAN.
                // for now i delete the message.
                mess.get(0).delete().queue();

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
                // for now i delete the message.
                mess.get(0).delete().queue();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

