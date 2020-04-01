package net.comorevi.moneyapi.util;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.io.File;

public class ConfigManager {
    private static final ConfigManager instance = new ConfigManager();
    private Config config;
    public static int DEFAULT_MONEY;
    public static boolean DEFAULT_PUBLISH_STATUS;
    public static String MONEY_UNIT;
    public static boolean PAY_COMMISSION;

    public ConfigManager() {
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

    private void createConfig() {
        ConfigSection cs = new ConfigSection(){
            {
                put("DefaultMoney", 500);
                put("Publish", false);
                put("Unit", "MS");
                put("PayCommission", false);
            }
        };
        config = new Config(new File("./plugins/MoneySAPI/", "config.yml"), Config.YAML, cs);
        config.save();
    }
}
