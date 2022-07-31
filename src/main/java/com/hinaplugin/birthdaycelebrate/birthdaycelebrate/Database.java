package com.hinaplugin.birthdaycelebrate.birthdaycelebrate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class Database extends Insights{
    public Database(BirthDayCelebrate plugin) {
        super(plugin);
    }

    private Connection connection;
    private final File database = new File(plugin.getDataFolder(), "Database.db");

    private void getConnection() throws Exception{
        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "Database.db");
        connection.setAutoCommit(false);
    }

    void CreateDatabase(){
            try {
                if (!plugin.getDataFolder().exists()){
                    try {
                        plugin.getDataFolder().mkdirs();
                    }catch (Exception e){
                        plugin.getLogger().log(Level.SEVERE, "ディレクトリの作成に失敗しました． Error: " + e.getMessage());
                    }
                }
                if (!database.exists()) {
                    try {
                        database.createNewFile();
                    } catch (IOException e) {
                        plugin.getLogger().log(Level.SEVERE, "SQLiteDatabaseの作成に失敗しました．　Error: " + e.getMessage());
                    }
                }
                Class.forName("org.sqlite.JDBC");

                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "Database.db");
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                statement.executeUpdate("create table if not exists birthday (" +
                        "id integer primary key autoincrement," +
                        "uuid text not null," +
                        "name text not null," +
                        "month integer not null default 1," +
                        "day integer not null default 1," +
                        "bool integer not null default 0," +
                        "ano integer not null default 0," +
                        "setup integer not null default 0" +
                        ");"
                );

                connection.commit();
                statement.close();
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "データベースまたはテーブルの作成に失敗しました． Error: " + e.getMessage());
            }finally {
                CloseConnection();
            }
    }

    void CreatePlayer(String name, String uuid){
        try {
            getConnection();
            try {
                String dbPlayerName;
                String QueryUpdatePlayer = "";
                boolean existsPlayer = false;
                PreparedStatement preparedStatement = connection.prepareStatement("select name from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    existsPlayer = true;
                    dbPlayerName = resultSet.getString(1);
                    if (!dbPlayerName.equals(name)){
                        QueryUpdatePlayer = "update birthday set name=? where uuid=?";
                    }
                }
                resultSet.close();
                preparedStatement.close();

                if (!existsPlayer){
                    preparedStatement = connection.prepareStatement("insert into birthday (uuid, name, month, day, bool, ano, setup) values (?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, uuid);
                    preparedStatement.setString(2, name);
                    preparedStatement.setInt(3, 1);
                    preparedStatement.setInt(4, 1);
                    preparedStatement.setInt(5, 0);
                    preparedStatement.setInt(6, 0);
                    preparedStatement.setInt(7, 0);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.close();
                }

                if (!QueryUpdatePlayer.isEmpty()){
                    preparedStatement = connection.prepareStatement(QueryUpdatePlayer);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, uuid);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.close();
                }
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "プレイヤー情報の作成またはアップデートに失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
    }

    void BirthDaySetting(int month, int day, String uuid, Player player){
        try {
            getConnection();
            try {
                int check;
                PreparedStatement preparedStatement = connection.prepareStatement("select setup from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    check = resultSet.getInt(1);
                }else {
                    check = -1;
                }
                resultSet.close();
                preparedStatement.close();

                if (check == 0) {
                    preparedStatement = connection.prepareStatement("update birthday set month=? where uuid=?");
                    preparedStatement.setInt(1, month);
                    preparedStatement.setString(2, uuid);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.close();

                    preparedStatement = connection.prepareStatement("update birthday set day=? where uuid=?");
                    preparedStatement.setInt(1, day);
                    preparedStatement.setString(2, uuid);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.close();

                    preparedStatement = connection.prepareStatement("update birthday set setup=1 where uuid=?");
                    preparedStatement.setString(1, uuid);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();
                    connection.commit();
                    preparedStatement.close();

                    player.sendMessage(ChatColor.AQUA + "誕生日を" + month + "月" + day + "日に設定しました！");
                }else if (check == 1){
                    player.sendMessage(ChatColor.RED + "すでに誕生日が設定されているため実行できませんでした．");
                    player.sendMessage(ChatColor.RED + "運営に連絡してユーザーデータの初期化をしてください．");
                }else if (check == -1){
                    player.sendMessage(ChatColor.RED + "ユーザーデータの取得に失敗したため実行できませんでした．");
                }

            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "誕生日の保存に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
    }

    void BirthDayEnable(String uuid, int bool){
        try {
            getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("update birthday set bool=? where uuid=?");
                preparedStatement.setInt(1, bool);
                preparedStatement.setString(2, uuid);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.close();
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "誕生日の有効設定の保存に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
    }

    int BirthDayEnableGet(String uuid){
        try {
            getConnection();
            try {
                int bool = -1;
                PreparedStatement preparedStatement = connection.prepareStatement("select bool from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    bool = resultSet.getInt(1);
                }
                resultSet.close();
                preparedStatement.close();
                return bool;
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "誕生日の有効設定の取得に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
        return -1;
    }

    int BirthDayMonthGet(String uuid){
        try {
            getConnection();
            try {
                int month = -1;
                PreparedStatement preparedStatement = connection.prepareStatement("select month from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    month = resultSet.getInt(1);
                }
                resultSet.close();
                preparedStatement.close();
                return month;
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "誕生日の月の取得に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
        return -1;
    }

    int BirthDayDayGet(String uuid){
        try {
            getConnection();
            try {
                int day = -1;
                PreparedStatement preparedStatement = connection.prepareStatement("select day from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    day = resultSet.getInt(1);
                }
                resultSet.close();
                preparedStatement.close();
                return day;
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "誕生日の日の取得に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
        return -1;
    }

    int AnnounceCount(String uuid){
        try {
            getConnection();
            try {
                int ano = -1;
                PreparedStatement preparedStatement = connection.prepareStatement("select ano from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    ano = resultSet.getInt(1);
                }
                resultSet.close();
                preparedStatement.close();
                return ano;
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "アナウンス回数の取得に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
        return -1;
    }

    void AnnounceCountUpdate(String uuid){
        try {
            getConnection();
            try {
                int ano = 0;
                PreparedStatement preparedStatement = connection.prepareStatement("select ano from birthday where uuid=?");
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    ano = resultSet.getInt(1);
                }
                resultSet.close();
                preparedStatement.close();

                preparedStatement = connection.prepareStatement("update birthday set ano=? where uuid=?");
                int nextAno = ano + 1;
                preparedStatement.setInt(1, nextAno);
                preparedStatement.setString(2, uuid);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.close();
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "アナウンス回数の保存に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
    }

    void AnnounceCountReset(String uuid){
        try {
            getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("update birthday set ano=? where uuid=?");
                preparedStatement.setInt(1, 0);
                preparedStatement.setString(2, uuid);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.close();
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "アナウンス回数のリセットに失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }finally {
            CloseConnection();
        }
    }

    void BirthDayResetAdmin(String name, Player player){
        try {
            getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select id from birthday where name=?");
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()){
                    player.sendMessage(ChatColor.RED + "指定したユーザーのデータが見つかりませんでした．");
                    resultSet.close();
                    preparedStatement.close();
                    return;
                }
                resultSet.close();
                preparedStatement.close();

                preparedStatement = connection.prepareStatement("update birthday set setup=0 where name=?");
                preparedStatement.setString(1, name);
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.close();
                player.sendMessage(ChatColor.AQUA + "ユーザーデータの初期化を実行しました．");
            }catch (Exception e){
                plugin.getLogger().log(Level.SEVERE, "ユーザーデータの初期化に失敗しました． Error: " + e.getMessage());
            }
        }catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Connectionに失敗しました． Error: " + e.getMessage());
        }
    }

    void CloseConnection(){
        try {
            if (connection != null) {
                connection.close();
            }
        }catch (SQLException e){
            plugin.getLogger().log(Level.SEVERE, "データベースの終了処理に失敗しました． Error: " + e.getMessage());
        }
    }
}
