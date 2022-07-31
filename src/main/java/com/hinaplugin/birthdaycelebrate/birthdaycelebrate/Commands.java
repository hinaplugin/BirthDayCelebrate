package com.hinaplugin.birthdaycelebrate.birthdaycelebrate;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Commands extends Insights implements CommandExecutor {
    public Commands(BirthDayCelebrate plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Database database = new Database(plugin);
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("set")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "誕生日の設定:      /birthday set <month> <day>");
                    sender.sendMessage(ChatColor.RED + "誕生日通知の有効化: /birthday <true|false>");
                    return true;
                }
                if (!StringUtils.isNumeric(args[1]) || !StringUtils.isNumeric(args[2])) {
                    sender.sendMessage(ChatColor.RED + "誕生日の設定: /birthday set <month> <day>");
                    sender.sendMessage(ChatColor.RED + "monthとdayは整数で入力してください．");
                    return true;
                }

                int month = Integer.parseInt(args[1]);
                int day = Integer.parseInt(args[2]);

                Integer[] array = {1, 3, 5, 7, 8, 10, 12};
                ArrayList<Integer> arrayList = new ArrayList<>(Arrays.asList(array));
                Integer[] array1 = {4, 6, 9, 11};
                ArrayList<Integer> arrayList1 = new ArrayList<>(Arrays.asList(array1));


                if (arrayList.contains(month)) {
                    if (day >= 32) {
                        sender.sendMessage(ChatColor.RED + "存在しない日付は設定できません．");
                        return true;
                    }
                    database.BirthDaySetting(month, day, player.getUniqueId().toString(), player);
                    return true;
                }

                if (arrayList1.contains(month)){
                    if (day >= 31){
                        sender.sendMessage(ChatColor.RED + "存在しない日付は設定できません．");
                        return true;
                    }
                    database.BirthDaySetting(month, day, player.getUniqueId().toString(), player);
                    return true;
                }

                if (month == 2){
                    if (day >= 30){
                        sender.sendMessage(ChatColor.RED + "存在しない日付は設定できません．");
                        return true;
                    }
                    database.BirthDaySetting(month, day, player.getUniqueId().toString(), player);
                    return true;
                }
            }else if (args[0].equalsIgnoreCase("true")){
                database.BirthDayEnable(player.getUniqueId().toString(), 1);
                sender.sendMessage(ChatColor.AQUA + "誕生日の通知設定を有効に設定しました．");
            }else if (args[0].equalsIgnoreCase("false")){
                database.BirthDayEnable(player.getUniqueId().toString(), 0);
                sender.sendMessage(ChatColor.AQUA + "誕生日の通知設定を無効に設定しました．");
            }else if (args[0].equalsIgnoreCase("reset")){
                if (!sender.isOp()){
                    sender.sendMessage(ChatColor.RED + "あなたにはこのコマンドを実行する権限がありません．");
                    return true;
                }
                if (args[1].isEmpty()){
                    sender.sendMessage(ChatColor.RED + "リセットしたいユーザーを指定してください．");
                    return true;
                }
                database.BirthDayResetAdmin(args[1], player);
            }
        }
        return false;
    }
}
