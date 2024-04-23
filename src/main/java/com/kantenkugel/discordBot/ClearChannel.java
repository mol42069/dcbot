package com.kantenkugel.discordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ClearChannel extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("clear")) {
            List<Message> messageList = new ArrayList<>();
            try {
                messageList = event.getChannel().getIterableHistory().takeAsync(100000)//
                        .thenApply(list -> list.stream().collect(Collectors.toList())).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            for (Message message : messageList) {
                message.delete().queue();
            }

        }
    }
}