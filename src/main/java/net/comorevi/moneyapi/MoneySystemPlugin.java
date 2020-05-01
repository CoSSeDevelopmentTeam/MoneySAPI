package net.comorevi.moneyapi;

import cn.nukkit.plugin.PluginBase;
import net.comorevi.moneyapi.command.*;
import net.comorevi.moneyapi.util.ConfigManager;
import net.comorevi.moneyapi.util.ExchangeRateCalculator;

import java.util.Calendar;
import java.util.TimeZone;

/*
 * MoneySAPI: Money system API for CosmoSunriseServer.
 */

public class MoneySystemPlugin extends PluginBase {

    private static MoneySystemPlugin instance;

    @Override
    public void onEnable(){
        instance = this;
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register("givemoney", new GiveMoneyCommand("givemoney"));
        this.getServer().getCommandMap().register("moneyhelp", new HelpMoneyCommand("moneyhelp"));
        this.getServer().getCommandMap().register("paymoney", new PayMoneyCommand("paymoney"));
        this.getServer().getCommandMap().register("rankmoney", new RankMoneyCommand("rankmoney"));
        this.getServer().getCommandMap().register("seemoney", new SeeMoneyCommand("seemoney"));
        this.getServer().getCommandMap().register("setmoney", new SetMoneyCommand("setmoney"));
        this.getServer().getCommandMap().register("takemoney", new TakeMoneyCommand("takemoney"));

        ExchangeRateCalculator.getInstance().calculate();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
        if (calendar.get(Calendar.HOUR_OF_DAY) != 0) ConfigManager.getInstance().setReduced(false);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            if (ConfigManager.getInstance().isReduced()) return;
            MoneySAPI.getInstance().deleteCoinData();
            ConfigManager.getInstance().setReduced(true);
        } else if (calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            if (ConfigManager.getInstance().isReduced()) return;
            MoneySAPI.getInstance().reduceMoney();
            ConfigManager.getInstance().setReduced(true);
        }
    }

    @Override
    public void onDisable() {
        MoneySAPI.getInstance().disconnectSQL();
    }

    public static MoneySystemPlugin getInstance() {
        return instance;
    }
}
