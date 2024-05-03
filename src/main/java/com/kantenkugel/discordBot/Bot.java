package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;


public class Bot extends ListenerAdapter
{

    public boolean on = false;
    public char prefix = '!';
    private BanButtons banMenu;
    private com.kantenkugel.discordBot.Commands commands;

    private List<Role> commandPermissions = new ArrayList<>();

    public static void main(String[] args)
    {



        JDA jda = JDABuilder.createDefault("ODg5NTY5NTU0NDgzNzk4MTM3.GUymbU.oY5MbUev1dCgUGGeXw8ZUVn28Ob-JE8VzoMvXM")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT) // enables explicit access to message.getContentDisplay()
                .enableIntents(GatewayIntent.AUTO_MODERATION_CONFIGURATION)
                .build();
        // You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent
        jda.addEventListener(new Bot());
        jda.addEventListener(new SayCommand());
        jda.addEventListener(new ClearChannel());
        jda.addEventListener(new BanButtons());
        jda.addEventListener(new ModalInteraction());
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        Guild tguild = event.getGuild();

        if(content.equals("")) return;

        // TODO: following has to replaced so we can use multiple-letter prefixes.

        if(content.charAt(0) != this.prefix){return;}



        if (content.equals("!start") && !this.on){
            this.on = true;
            this.prefix = '!';
            this.banMenu = new BanButtons();
            this.commands = new com.kantenkugel.discordBot.Commands();

            // TODO: WE NEED TO GET THESE PERMISSIONS FROM THE WEBSITE.

            this.commandPermissions.add(message.getGuild().getRolesByName("Besserer-Mensch", true).getFirst());

            message.delete().queue();
            event.getJDA().getGuilds().forEach(guild->{
                guild.updateCommands().addCommands(

                        Commands.slash("echo", "Repeats messages back to you.")
                                .addOption(OptionType.STRING, "content", "The message to repeat."),

                        Commands.slash("clear", "Clears all messages in this channel"),

                        Commands.slash("ban", "Clears all messages in this channel")
                                .addOption(OptionType.USER, "user", "which user"),

                        Commands.slash("modmail", "Repeats messages back to you.")

                        ).queue();

            });
            message.reply("Bot-Started").queue();
            return;

        }else if(!this.on){
            message.delete().queue();
            return;
        }

        boolean allowed = false;
        for(Role role : message.getMember().getRoles()){
            if(this.commandPermissions.contains(role)){
                allowed = true;
            }
        }
        if(!allowed) return;




        String command = content.split(" ")[0];
        System.out.println(command);

        switch(command){
            case "!purge":
                this.commands.purge(event);
                break;

            case "!ban":
                banMenu.BanButtons(event, this.commands);
                break;

            case "!stop":
                this.on = false;
                message.reply("Bot-ShutDown").queue();
                message.delete().queue();
                break;

            case "!unban":
                System.out.println(Long.parseLong(event.getMessage().getContentRaw().split(" ")[1]));
                UserSnowflake us = User.fromId(Long.parseLong(event.getMessage().getContentRaw().split(" ")[1]));

                tguild.unban(us).queue();

                event.getMessage().delete().queue();

                event.getMessage().getChannel().sendMessage("unbanned user: " + us.getId()).queue();

                break;


            default:
                message.delete().queue();
                message.reply("Command doesen`t exits: " + message.getContentRaw().split(" ")[0]).queue();


                break;
        }



        if(!this.on){return;}

        if (event.isFromType(ChannelType.PRIVATE))
        {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        }
        else
        {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }


        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself

        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.equals("!ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }
    }
}


