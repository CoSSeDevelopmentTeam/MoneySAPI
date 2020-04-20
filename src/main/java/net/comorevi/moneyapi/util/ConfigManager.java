package net.comorevi.moneyapi.util;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;
import java.util.List;

public class ConfigManager {
    private static ConfigManager instance = new ConfigManager();
    private Config config;
    public static int DEFAULT_MONEY;
    public static boolean DEFAULT_PUBLISH_STATUS;
    public static String MONEY_UNIT;
    public static boolean PAY_COMMISSION;

    private ConfigManager() {
        instance = this;
        new File("./plugins/MoneySAPI").mkdirs();
        if (!new File("./plugins/MoneySAPI/", "config.yml").exists()) {
            createConfig();
        } else {
            config = new Config(new File("./plugins/MoneySAPI/", "config.yml"), Config.YAML);
        }
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

    public List<String> getIgnoreWorlds() {
        return config.getStringList("IgnoreWorlds");
    }

    private void createConfig() {
        ConfigSection cs = new ConfigSection(){
            {
                put("DefaultMoney", 500);
                put("Publish", false);
                put("Unit", "MS");
                put("PayCommission", false);
                put("RatePattern", 1);
                put("PatternPhase", 1);
                put("ExchangeRate", 150);
                put("IgnoreWorlds", List.of("central", "life2020-01"));
            }
        };
        config = new Config(new File("./plugins/MoneySAPI/", "config.yml"), Config.YAML, cs);
        config.save();
    }
}
