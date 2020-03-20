package net.comorevi.moneyapi;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.comorevi.moneyapi.util.TextValues;

public class EventListener implements Listener{

    private Main plugin;

    public EventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!plugin.getSQL().existsAccount(event.getPlayer().getName())){
        	plugin.create(event.getPlayer().getName(), Main.defaultmoney);
            plugin.getServer().broadcastMessage(TextValues.INFO + plugin.translateString("player-account-add", event.getPlayer().getName()));
        }else{
            event.getPlayer().sendMessage(TextValues.INFO + plugin.translateString("player-account-load", event.getPlayer().getName(), String.valueOf(plugin.getMoney(event.getPlayer().getName())), Main.unit));
        }
    }

}