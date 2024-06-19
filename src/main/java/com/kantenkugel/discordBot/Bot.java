package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.*;


public class Bot extends ListenerAdapter
{
    public String mod_channel_name = "mod";
    public TextChannel mod_text_channel;
    public boolean on = false;
    public char prefix = '!';
    private Buttons banMenu;
    private HashSet<String> profanities;

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
        jda.addEventListener(new Buttons());
        jda.addEventListener(new ModalInteraction());
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {


        Message message = event.getMessage();

        if(message.getAuthor().isBot()) return;

        String content = message.getContentRaw();
        Guild tguild = event.getGuild();

        // TODO: following has to replaced so we can use multiple-letter prefixes.

        if(content.charAt(0) != this.prefix){

            // if we don't have a prefix we look that there is no profanity in the message

            if(this.on) ProfanityFilter.filter(profanities, message, this.mod_text_channel);

            return;
        }



        if (content.equals("!start") && !this.on){

            // here we initialize the profanity hashmap with the profanities which are in a specific file for that server
            this.mod_text_channel = event.getGuild().getTextChannelsByName(this.mod_channel_name, false).get(0);

            this.profanities = LoadConfig.loadProfanity(message);
            LoadConfig.load(this);
            this.on = true;
            this.prefix = '!';
            this.banMenu = new Buttons();

            // TODO: WE NEED TO GET THESE PERMISSIONS FROM THE WEBSITE.

            this.commandPermissions.add(message.getGuild().getRolesByName("Besserer-Mensch", true).getFirst());

            message.delete().queue();
            event.getJDA().getGuilds().forEach(guild-> guild.updateCommands().addCommands(

                    Commands.slash("echo", "Repeats messages back to you.")
                            .addOption(OptionType.STRING, "content", "The message to repeat."),

                    Commands.slash("clear", "Clears all messages in this channel"),

                    Commands.slash("ban", "Clears all messages in this channel")
                            .addOption(OptionType.USER, "user", "which user"),

                    Commands.slash("modmail", "Repeats messages back to you.")

                    ).queue());
            message.reply("Bot-Started").queue();
            return;

        }else if(!this.on){
            message.delete().queue();
            return;
        }



        boolean allowed = false;
        for(Role role : Objects.requireNonNull(message.getMember()).getRoles()){
            if(this.commandPermissions.contains(role)){
                allowed = true;
                break;
            }
        }
        if(!allowed) return;



        String command = content.split(" ")[0];
        System.out.println(command);

        switch(command){
            case "!purge":
                CommandsL.purge(event);
                break;

            case "!ban":
                BanMenu.create_layout(event);
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
                message.reply("Command does not exits: " + message.getContentRaw().split(" ")[0]).queue();


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
                    event.getChannel().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(),
                    event.getMessage().getContentDisplay());
        }

    }
}


