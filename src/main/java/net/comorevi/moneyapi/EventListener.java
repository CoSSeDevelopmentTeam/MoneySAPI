package net.comorevi.moneyapi;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{

    private MoneySAPI plugin;

    public EventListener(MoneySAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!plugin.getSQL().existsAccount(event.getPlayer().getName())){
        	plugin.createAccount(event.getPlayer(), MoneySAPI.defaultmoney);
            plugin.getServer().broadcastMessage(TextValues.INFO + plugin.translateString("player-account-add", event.getPlayer().getName()));
        }else{
            event.getPlayer().sendMessage(TextValues.INFO + plugin.translateString("player-account-load", event.getPlayer().getName(), String.valueOf(plugin.getMoney(event.getPlayer())), MoneySAPI.unit));
        }
    }

}