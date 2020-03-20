package net.comorevi.moneyapi;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.comorevi.moneyapi.util.MessageManager;
import net.comorevi.moneyapi.util.TextValues;

public class EventListener implements Listener{
    private MessageManager message = MessageManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!MoneySAPI.getInstance().existsAccount(event.getPlayer())){
        	MoneySAPI.getInstance().registerAccount(event.getPlayer());
        	event.getPlayer().sendMessage(TextValues.INFO + message.translateString("player-account-add", event.getPlayer().getName()));
        }else{
            event.getPlayer().sendMessage(TextValues.INFO + message.translateString("player-account-load", event.getPlayer().getName(), String.valueOf(MoneySAPI.getInstance().getMoney(event.getPlayer())), MoneySAPI.UNIT));
        }
    }

}