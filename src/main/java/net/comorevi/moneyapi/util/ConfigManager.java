package net.comorevi.moneyapi.util;

import cn.nukkit.utils.Config;
import net.comorevi.moneyapi.MoneySystemPlugin;

import java.io.File;

public class ConfigManager {
    private static final ConfigManager instance = new ConfigManager();
    private Config config;
    public static int DEFAULT_MONEY;
    public static boolean DEFAULT_PUBLISH_STATUS;
    public static String MONEY_UNIT;
    public static boolean PAY_COMMISSION;

    private ConfigManager() {
        new File("./plugins/MoneySAPI").mkdirs();
        MoneySystemPlugin.getInstance().saveDefaultConfig();
        config = MoneySystemPlugin.getInstance().getConfig();
        DEFAULT_MONEY = config.getInt("DefaultMoney", 500);
        DEFAULT_PUBLISH_STATUS = config.getBoolean("Publish", false);
        MONEY_UNIT = config.getString("Unit", "MS");
        PAY_COMMISSION = config.getBoolean("PayCommission", false);
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public int getExchangeRate() {
        return config.getInt("ExchangeRate");
    }

    public int getRatePattern() {
        return config.getInt("RatePattern");
    }

    public int getPatternPhase() {
        return config.getInt("PatternPhase");
    }

    public void setExchangeRate(int rate) {
        config.set("ExchangeRate", rate);
        config.save();
    }

    public void setPatternPhase(int phase) {
        config.set("PatternPhase", phase);
        config.save();
    }

    public void setRatePattern(int pattern) {
        config.set("RatePattern", pattern);
        config.save();
    }

    public boolean isReduced() {
        return config.getBoolean("StoredData");
    }

    public void setReduced(boolean value) {
        config.set("StoredData", value);
        config.save();
    }

    public boolean enabledShowRank() {
        return config.getBoolean("ShowRankBoard");
    }

    public String getShowingLevelName() {
        return config.getString("RankBoardLevel");
    }
}
