package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;


public class CommandsL {

    public static void purge(MessageReceivedEvent event){
        Guild guild = event.getGuild();
        String channelName = event.getChannel().getName();
        TextChannel existingChannel = guild.getTextChannelsByName(channelName, true).stream().findFirst().orElse(null);

        int channelPosition = existingChannel.getPositionRaw();


        String categoryName = existingChannel.getParentCategory() != null ? existingChannel.getParentCategory().getName() : null;

        // Delete the existing channel
        existingChannel.delete().queue(deleted -> {
            // Create a new channel with identical properties
            ChannelAction<TextChannel> channelAction = guild.createTextChannel(channelName);

            channelAction.setPosition(channelPosition);

            // Set the category for the new channel (if the existing channel was in a category)
            if (categoryName != null) {
                channelAction.setParent(guild.getCategoriesByName(categoryName, true).get(0));
            }

            // Copy permissions from the old channel (optional)
            existingChannel.getRolePermissionOverrides().forEach(override -> {
                channelAction.addPermissionOverride(override.getRole(), override.getAllowedRaw(), override.getDeniedRaw());
            });

            // Complete the channel creation and update the guild
            channelAction.queue();
        });



    }



}
