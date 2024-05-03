package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.w3c.dom.Text;

import java.util.EnumSet;


public class Commands {

    public void purge(MessageReceivedEvent event){
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

    public void createModWindow(MessageReceivedEvent event){

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
                              "UserId: " + event.getMessage().getMentions().getUsers().get(0).getId()+ " \n" +
                              "ChannelID: " + event.getChannel().getId()
                )
                .addActionRow(
                        Button.danger("ban", "BAN"), // Button with only a label
                        Button.primary("kick", "KICK")) // Button with only an emoji

                .addActionRow(
                        Button.primary("mute", "MUTE"), // Button with only a label
                        Button.success("timeout", "TIMEOUT")) // Button with only an emoji

                .addActionRow(
                        Button.secondary("cancel", "CANCEL"))// Button with only a label


                .queue();

        event.getMessage().delete().queue();
    }

}
