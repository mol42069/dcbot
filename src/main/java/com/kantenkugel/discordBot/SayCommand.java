package com.kantenkugel.discordBot;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class SayCommand extends ListenerAdapter {
    //THIS WILL ONLY BE FOR NONCRITICAL COMMANDS. EVERYBODY CAN USE.
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("modmail")) {
            TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Your concerns go here")
                    .setMinLength(10)
                    .setMaxLength(1000)
                    .build();

            Modal modal = Modal.create("modmail", "BanReason")
                    .addComponents(ActionRow.of(body))
                    .build();

            event.replyModal(modal).queue();
        }

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
