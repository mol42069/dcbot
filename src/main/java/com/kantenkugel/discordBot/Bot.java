package com.kantenkugel.discordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.util.*;

public class Bot extends ListenerAdapter
{
    public DatabaseConnection db = null;
    public String mod_channel_name = "mod";
    public TextChannel mod_text_channel;
    public boolean on = false;
    public char prefix = '!';
    private Buttons banMenu;
    private HashSet<String> profanities;

    private List<Role> commandPermissions = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        // HERE ADD THE TOKEN LOCATION
        File file = new File(
                "C:\\Users\\morat\\IdeaProjects\\dcBottesta\\dctoken.txt");

        // Note:  Double backquote is to avoid compiler
        // interpret words
        // like \test as \t (ie. as a escape sequence)

        // Creating an object of BufferedReader class
        BufferedReader br
                = new BufferedReader(new FileReader(file));

        String token = br.readLine();

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT) // enables explicit access to message.getContentDisplay()
                .enableIntents(GatewayIntent.AUTO_MODERATION_CONFIGURATION)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
        // You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent
        jda.addEventListener(new Bot());
        jda.addEventListener(new Buttons());
        jda.addEventListener(new ModalInteraction());
    }


    private int convert_punishment(String punishment){
        switch (punishment.toLowerCase()){
            case "timedout":
                return 3;
            case "banned":
                return 1;
            case "kicked":
                return 2;
            case "muted":
                return 4;
        }

        return 0;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {


        Message message = event.getMessage();


        if(message.getAuthor().isBot()) {
            if (message.getEmbeds().isEmpty()) {
                return;
            }
            System.out.println(message.getContentRaw() + "\n\n");
            System.out.println(message.getEmbeds());

            if (this.db == null){
                System.out.println(message.getContentRaw());
            } else if (Objects.equals(message.getEmbeds().get(0).getFields().get(0).getName(), "USER:") &&
                        !Objects.equals(message.getEmbeds().get(0).getFields().get(2).getValue(), "ProfanityFilter_timeout"))
            {

                String dur = "0s";
                try {
                    dur = message.getEmbeds().get(0).getFields().get(3).getValue();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(" ");
                }


                assert dur != null;
                dur = dur.substring(0, dur.length() - 1);
                int punishment_id = convert_punishment(message.getEmbeds().get(0).getTitle().split(" ")[0]);

                this.db.give_user_punishment(
                        message.getGuildId(),
                        Integer.parseInt(dur),
                        message.getEmbeds().get(0).getFields().get(1).getValue(),
                        message.getEmbeds().get(0).getFields().get(2).getValue(),
                        punishment_id
                        );
            }
            return;
        };

        String content = message.getContentRaw();
        Guild tguild = event.getGuild();

        System.out.println("message received event");
        if (this.db != null){
            if(!db.server_exists(tguild.getId())){
                db.insert_new_server(tguild.getId());
            }
            // if needed to create a new user
            if (!db.user_exists(Objects.requireNonNull(message.getMember()).getId())){
                db.insert_new_member(Objects.requireNonNull(message.getMember()).getId(), tguild.getId());
            } // if needed to create a new user-server relation
            else if (!db.user_server_exists(Objects.requireNonNull(message.getMember()).getId(), tguild.getId())){
                db.insert_member_in_server(Objects.requireNonNull(message.getMember()).getId(), tguild.getId());
            }
            else System.out.println("member_server already exists");

            // write all messages into the log table with all the needed info.
            this.db.insert_into_log(tguild.getId(), Objects.requireNonNull(message.getMember()).getId(),
                    Objects.requireNonNull(message.getChannelId()), message.getId(), message.getContentRaw());

        }

        // TODO: following has to replaced so we can use multiple-letter prefixes.

        if(content.charAt(0) != this.prefix){

            // if we don't have a prefix we look that there is no profanity in the message

            if(this.on) ProfanityFilter.filter(profanities, message, this.mod_text_channel, this.db);

            return;
        }



        if (content.equals("!start") && !this.on){

            // here we initialize the profanity hashmap with the profanities which are in a specific file for that server
            this.mod_text_channel = event.getGuild().getTextChannelsByName(this.mod_channel_name, false).get(0);

            this.profanities = LoadConfig.loadProfanity(message);
            this.db = LoadConfig.load(tguild);
            this.on = true;
            this.prefix = '!';
            this.banMenu = new Buttons();
            // TODO: WE NEED TO GET THESE PERMISSIONS FROM THE WEBSITE.

            this.commandPermissions.add(message.getGuild().getRolesByName("Besserer-Mensch", true).get(0));

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

        }else if(!this.on) {
            message.delete().queue();
            return;

        }else if (content.equals("!start") && this.db != null){
            this.commandPermissions.add(message.getGuild()
                    .getRolesByName("Besserer-Mensch", true).get(0));

            LoadConfig.load_for_server(tguild, this.db);

        }else if(content.equals("!get_messages")){

            System.out.println("get_messages");
            ArrayList<String[]> user_log = this.db.get_log_for_user(Objects.requireNonNull(message.getMember()).getId(), tguild.getId());
            for (int i = 0; i < user_log.size(); i++){
                System.out.println(Arrays.toString(user_log.get(i)));
            }
        }
        System.out.println("1");
        boolean allowed = false;
        for(Role role : Objects.requireNonNull(message.getMember()).getRoles()){
            if(this.commandPermissions.contains(role)){
                allowed = true;
                break;
            }
        }
        System.out.println("2");
        if(!allowed) return;
        System.out.println("3");



        String command = content.split(" ")[0];
        System.out.println(command);
        switch(command){
            case "!purge":
                CommandsL.purge(event);
                break;

            case "!ban":
                if(!this.db.user_exists(event.getMessage().getMentions().getMembers().get(0).getId())) {
                    this.db.insert_new_member(event.getMessage().getMentions().getMembers().get(0).getId(),
                            event.getGuild().getId());
                }
                BanMenu.create_layout(event, this.db);
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
                if (message.getContentRaw().equals("!start")) break;
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


