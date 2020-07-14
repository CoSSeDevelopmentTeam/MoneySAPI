package net.comorevi.moneyapi;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import net.comorevi.moneyapi.command.*;
import net.comorevi.moneyapi.util.ConfigManager;
import net.comorevi.moneyapi.util.ExchangeRateCalculator;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/*
 * MoneySAPI: Money system API for CosmoSunriseServer.
 */

public class MoneySystemPlugin extends PluginBase {

    private static MoneySystemPlugin instance;
    private FloatingTextParticle particle;

    @Override
    public void onEnable(){
        instance = this;
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register("MoneySAPI", new GiveMoneyCommand("givemoney"));
        this.getServer().getCommandMap().register("MoneySAPI", new HelpMoneyCommand("moneyhelp"));
        this.getServer().getCommandMap().register("MoneySAPI", new PayMoneyCommand("paymoney"));
        this.getServer().getCommandMap().register("MoneySAPI", new RankMoneyCommand("rankmoney"));
        this.getServer().getCommandMap().register("MoneySAPI", new SeeMoneyCommand("seemoney"));
        this.getServer().getCommandMap().register("MoneySAPI", new SetMoneyCommand("setmoney"));
        this.getServer().getCommandMap().register("MoneySAPI", new TakeMoneyCommand("takemoney"));

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

        if (ConfigManager.getInstance().enabledShowRank()) {
            particle = new FloatingTextParticle(Server.getInstance().getLevelByName(ConfigManager.getInstance().getShowingLevelName()).getSafeSpawn().up(4).east(3), TextFormat.GOLD + "☆--- 所持金ランキング ---☆", getRankString(5));
            getServer().getScheduler().scheduleRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    particle.setText(getRankString(5));
                    Server.getInstance().getLevelByName(ConfigManager.getInstance().getShowingLevelName()).addParticle(particle, Server.getInstance().getOnlinePlayers().values());
                }
            }, 1200, true);
        }
    }

    @Override
    public void onDisable() {
        MoneySAPI.getInstance().disconnectSQL();
    }

    public void showMoneyRank(Player player) {
        Server.getInstance().getLevelByName(ConfigManager.getInstance().getShowingLevelName()).addParticle(particle, player);
    }

    public String getRankString(int range) {
        StringBuilder sb = new StringBuilder();
        List<String> rankList = MoneySAPI.getInstance().getMoneyRankList(range);
        for (int i = 0; i < rankList.size(); i++) {
            if (!MoneySAPI.getInstance().existsAccount(rankList.get(i))) break;
            sb.append(i + 1).append(": ").append(rankList.get(i)).append(", ").append(MoneySAPI.getInstance().getMoney(rankList.get(i))).append(MoneySAPI.UNIT).append("\n");
        }
        return sb.toString();
    }

    public static MoneySystemPlugin getInstance() {
        return instance;
    }
}
