package net.comorevi.moneyapi;

import cn.nukkit.plugin.PluginBase;
import net.comorevi.moneyapi.command.*;

/*
 * MoneySAPI: Money system API for CosmoSunriseServer.
 */

public class MoneySystemPlugin extends PluginBase {

    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register("givemoney", new GiveMoneyCommand("givemoney"));
        this.getServer().getCommandMap().register("moneyhelp", new HelpMoneyCommand("moneyhelp"));
        this.getServer().getCommandMap().register("paymoney", new PayMoneyCommand("paymoney"));
        this.getServer().getCommandMap().register("rankmoney", new RankMoneyCommand("rankmoney"));
        this.getServer().getCommandMap().register("seemoney", new SeeMoneyCommand("seemoney"));
        this.getServer().getCommandMap().register("setmoney", new SetMoneyCommand("setmoney"));
        this.getServer().getCommandMap().register("takemoney", new TakeMoneyCommand("takemoney"));
    }

    @Override
    public void onDisable() {
        MoneySAPI.getInstance().disconnectSQL();
    }
}
