package net.comorevi.moneyapi;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{

    private MoneySAPI plugin;
    private int defaultmoney = 500;

    public EventListener(MoneySAPI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
    	plugin.getSQL().createAccount(event.getPlayer().getName(), this.defaultmoney);
        if(!plugin.getSQL().isRegister(event.getPlayer().getName())){
            plugin.getServer().broadcastMessage("[MoneySAPI]次のプレイヤーのアカウントを作成しました。 \n"+" - "+event.getPlayer().getName());
        }else{
        	event.getPlayer().sendMessage("[MoneySAPI]アカウントをロードしました。(現在の所持金: " + plugin.getMoney(event.getPlayer()) + MoneySAPI.unit + ")");
        }
    }

}
