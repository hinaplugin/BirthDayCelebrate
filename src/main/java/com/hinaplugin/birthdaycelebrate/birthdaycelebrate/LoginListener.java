package com.hinaplugin.birthdaycelebrate.birthdaycelebrate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class LoginListener extends Insights implements Listener {
    public LoginListener(BirthDayCelebrate plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Database database = new Database(plugin);
        database.CreatePlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString());

        if (database.BirthDayEnableGet(event.getPlayer().getUniqueId().toString()) == 1){
            int month;
            int day;
            int serverMonth;
            int serverDay;
            int serverYear;

            month = database.BirthDayMonthGet(event.getPlayer().getUniqueId().toString());
            day = database.BirthDayDayGet(event.getPlayer().getUniqueId().toString());
            Calendar calendar = Calendar.getInstance();
            serverYear = calendar.get(Calendar.YEAR);
            serverMonth = calendar.get(Calendar.MONTH) + 1;
            serverDay = calendar.get(Calendar.DATE);

            Integer[] array = {0, 1, 2};
            ArrayList<Integer> arrayList = new ArrayList<>(Arrays.asList(array));
            int count;
            count = database.AnnounceCount(event.getPlayer().getUniqueId().toString());

            if (arrayList.contains(count)) {

                if (month == -1 || day == -1) {
                    return;
                }

                if (month == serverMonth && day == serverDay) {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.sendMessage(ChatColor.GOLD + "本日は" + event.getPlayer().getName() + "さんのお誕生日です！");
                    }

                    database.AnnounceCountUpdate(event.getPlayer().getUniqueId().toString());

                    if (count == 0){
                        List<String> lore = new ArrayList<>();
                        lore.add(serverYear + "年" + serverMonth + "月" + serverDay + "日" + event.getPlayer().getName() + "さん");
                        lore.add("お誕生日おめでとうございます!");
                        Inventory inventory = event.getPlayer().getInventory();
                        ItemStack itemStack = new ItemStack(Material.CAKE, 1);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + event.getPlayer().getName() + "の誕生日ケーキ");
                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);
                        inventory.addItem(itemStack);
                    }
                }
            }else {
                if (month != serverMonth || day != serverDay){
                    database.AnnounceCountReset(event.getPlayer().getUniqueId().toString());
                }
            }
        }
    }
}
