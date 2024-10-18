package com.kantenkugel.discordBot;

import java.sql.*;
import java.util.ArrayList;


public class DatabaseConnection {
    Connection connection = null;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public DatabaseConnection() {


        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/dcbot",
                    "root", "sqlpassword");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database
            System.out.println(ANSI_GREEN + "Connected to database" + ANSI_RESET);
        }
        catch (Exception exception) {
            System.out.println(ANSI_RED + exception + ANSI_RESET);
        }

    }

    public void close_connection() {
        try {
            this.connection.close();
            System.out.println(ANSI_GREEN + "Connection closed" + ANSI_RESET);
        } catch (SQLException exception) {
            System.out.println(ANSI_RED + exception + ANSI_RESET);
        }
    }

    public String save_for_sql_message(String message){
        StringBuilder result = new StringBuilder(message);
        for (int i = 0; i < message.length(); i++) {

            if(message.charAt(i) == "'".toCharArray()[0]){
                result.setCharAt(i, '´');
            }
        }
        return result.toString();
    }



// ----------------------------------------------------------------------------------------------------------
//                              HERE WE HAVE FUNCTIONS TO GET DATA FROM DB:
// ----------------------------------------------------------------------------------------------------------



    public ArrayList<String[]> get_log_for_user(String user_id, String server_id){

        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT message, message_date FROM user_log WHERE user_id = '" + user_id + "' AND server_id = '" + server_id + "'";
            ResultSet rs = statement.executeQuery(query);
            ArrayList<String[]> result = new ArrayList<>();

            while (rs.next()) {
                result.add(new String[]{rs.getString("message"), rs.getString("message_date")});
            }
            return result;

        } catch (SQLException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return null;
        }
    }


    // get all menu punishments from the db.


    public ArrayList<Integer> get_ban_menu_options(String server_id){

        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT id FROM ban_menu_options WHERE server_id = '" + server_id + "'";
            ResultSet rs = statement.executeQuery(query);
            ArrayList<Integer> result = new ArrayList<>();

            while (rs.next()) {
                result.add(Integer.parseInt(rs.getString("id")));
            }
            return result;

        } catch (SQLException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return null;
        }
    }

    public ArrayList<String[]> get_ban_menu_details(int ban_menu_options_id){
        ArrayList<String[]> result = new ArrayList<>();

        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT p.button_type, p.name " +
                    "FROM punishments p " +
                    "JOIN ban_menu_options bmo ON p.id = bmo.punishment_id " +
                    "WHERE bmo.id = " + ban_menu_options_id + ";";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                result.add(new String[] {rs.getString("name"), String.valueOf(rs.getInt(
                        "button_type"))});
            }
            return result;

        } catch (SQLException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return null;
        }
    }



// ----------------------------------------------------------------------------------------------------------
//                                     HERE WE HAVE THE INSERTS:
// ----------------------------------------------------------------------------------------------------------


    public boolean insert_new_server(String guild_id){
        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "INSERT INTO servers(id) VALUES" +
                            "("+ guild_id + ")";

            int x = statement.executeUpdate(query);
            if (x == 0) return false;

            // then if we added a server we add all members that aren`t already in the db because they were on other servers with this bot already.

            return true;


        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    // only call this when this member isn't int the guild
    public void insert_new_member(String member_id, String guild_id){

        try{
            Statement statement = this.connection.createStatement();

            String query = "INSERT INTO users(id) VALUES" +
                    "("+ member_id + ")";

            statement.executeUpdate(query);

            query = "INSERT INTO user_server(server_id, user_id) VALUES" +
                    "("+ guild_id +", " + member_id + ")";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void insert_member_in_server(String member_id, String guild_id){

        try{
            Statement statement = this.connection.createStatement();

            String query = "INSERT INTO user_server(server_id, user_id) VALUES" +
                            "("+ guild_id +", " + member_id + ")";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void insert_into_log(String guild_id, String member_id, String channel_id, String message_id, String message){
        // not needed because timestamp is behind the message -> it will just result in an error,
        // but we wouldn't log the message so we want to run it anyway.
        message_id = save_for_sql_message(message_id);



        try {
            Statement statement = this.connection.createStatement();
            java.util.Date date = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

            String query = "INSERT INTO user_log(user_id, server_id, channel_id, message, message_id, message_date) VALUES" +
                    "( + '" + member_id + "', '" + guild_id + "', '" + channel_id + "', '" + message + "', '" +
                     message_id + "', '" + timestamp + "')";

            statement.executeUpdate(query);


        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }
    }



    // TODO: we need to add this function or the one below wherever a punishment is given so we can log it
    public void give_user_punishment(String server_id, int duration, String user_id, String channel_id, String reason, String message_id){
        // mainly for profanity filter
        try {
            Statement statement = this.connection.createStatement();
            message_id = save_for_sql_message(message_id);
            // first we add the server to the db
            String query = "INSERT INTO user_punishment (user_id, server_id, reason, message_id, duration, punishment_id) " +
                    "SELECT " +
                    "   ul.user_id, " +
                    "   ul.server_id, " +
                    "   '" + reason + "', " +
                    "   ul.id, " +
                    "   " + duration + ", " +
                    " 3 " +
                    "FROM " +
                    "   user_log ul " +
                    "WHERE " +
                    "   ul.server_id = '" + server_id + "' " +
                    "   AND ul.user_id = '" + user_id + "' " +
                    "   AND ul.message_id = '" + message_id + "' " +
                    "   AND ul.channel_id = '" + channel_id + "';";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }

    }

    public void give_user_punishment(String server_id, int duration, String user_id, String reason, int punishment_id){

        try {
            Statement statement = this.connection.createStatement();
            reason = save_for_sql_message(reason);
            // first we add the server to the db
            String query = "INSERT INTO user_punishment (user_id, server_id, reason, duration, punishment_id) VALUES( " +
                            "'" + user_id + "'," +
                    "        '" + server_id + "', " +
                    "        '" + reason + "', " +
                    "         " + duration + ", " +
                                 punishment_id +
                    " )";


                    ;
            statement.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }

    }


    public void give_user_punishment(String server_id, String user_id, String reason){

        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "INSERT INTO user_punishment (user_id, server_id, reason, message_id, duration) " +
                    "SELECT " +
                    "   ul.user_id, " +
                    "   ul.server_id, " +
                    "   '" + reason + "', " +
                    "   ul.id " +
                    "FROM " +
                    "   user_log ul " +
                    "WHERE " +
                    "   ul.server_id = '" + server_id + "' " +
                    "   AND ul.user_id = '" + user_id + "' ";
            statement.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }

    }


    public void create_new_punishment_option(String name, int button_type){
        // button_type: 0 = success; 1 = primary; 2 = secondary; 3 = danger;
        try{
            Statement statement = this.connection.createStatement();

            String query = "INSERT INTO punishments(name, button_type) VALUES" +
                    "('"+ name +"', " + button_type + ")";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add_new_ban_menu_options(String server_id, int punishment_id, int[] position){
        // position[0] is row
        // position[1] is column

        try{
            Statement statement = this.connection.createStatement();

            String query = "INSERT INTO ban_menu_options(position_x, position_y, punishment_id, server_id) VALUES" +
                    "("+ position[1] +", " + position[0] + ", " + punishment_id + ", '" + server_id + "')";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




// ----------------------------------------------------------------------------------------------------------
//                                     HERE WE HAVE THE CHECKS:
// ----------------------------------------------------------------------------------------------------------


    // get if the server already exists in our database:

    public boolean server_exists(String server_id){
        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT id FROM servers WHERE id = '" + server_id + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
            else return false;

        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return false;
        }
    }

    public boolean user_exists(String user_id){
        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT id FROM users WHERE id = '" + user_id + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
            else return false;

        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return false;
        }
    }

    public boolean user_server_exists(String user_id, String server_id){
        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT server_id, user_id FROM user_server WHERE user_id = '" + user_id +
                    "' AND server_id = '" + server_id + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
            else return false;

        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return false;
        }
    }

    public boolean ban_menu_options_exists(String server_id){

        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT id FROM ban_menu_options WHERE server_id = '" + server_id +
                    "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
            else return false;

        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return false;
        }
    }


    public boolean punishments_exists(){

        try {
            Statement statement = this.connection.createStatement();

            // first we add the server to the db
            String query = "SELECT id FROM punishments";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
            else return false;

        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return false;
        }
    }
}
