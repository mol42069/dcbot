package com.kantenkugel.discordBot;

import java.sql.*;


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

    public void insert_into_log(String guild_id, String member_id, String channel_id, String message){

        try {
            Statement statement = this.connection.createStatement();
            java.util.Date date = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());

            String query = "INSERT INTO user_log(user_id, server_id, channel_id, message, message_date) VALUES" +
                    "( + '" + member_id + "', '" + guild_id + "', '" + channel_id + "', '" + message + "', '" +
                    timestamp + "')";

            statement.executeUpdate(query);


        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
        }
    }


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
            String query = "SELECT server_id, user_id FROM user_server WHERE user_id = '" + user_id + "' AND server_id = '" + server_id + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
            else return false;

        } catch (SQLException e){
            System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
            return false;
        }
    }


}
