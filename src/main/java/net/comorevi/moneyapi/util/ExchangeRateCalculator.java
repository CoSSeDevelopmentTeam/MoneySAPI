package net.comorevi.moneyapi.util;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class ExchangeRateCalculator {
    public int exchangeRate;

    private Calendar calendar;
    private Random random = new Random();

    private int baseExchangeRate = 150;
    private int maxExchangeRate = 320;
    private int minExchangeRate = 20;

    public ExchangeRateCalculator() {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("Tokyo/Asia"));
        exchangeRate = calculate();
        //System.out.println("[DEBUG] Rate: " + exchangeRate + ", Pattern: " + Main.getRatePattern() + ", PatternPhase: " + Main.getPatternPhase());
    }

    private int calculate() {
        //日曜日にその週の開始レートを生成、週の変動パターンを生成
        //if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { //TODO: experiment
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            //開始レート設定
            ConfigManager.getInstance().setExchangeRate(baseExchangeRate);
            //パターン設定
            switch (ConfigManager.getInstance().getRatePattern()) {
                case 1: //通常
                    if (random.nextBoolean()) {
                        ConfigManager.getInstance().setRatePattern(2);
                    } else {
                        ConfigManager.getInstance().setRatePattern(3);
                    }
                    break;
                case 2: //フィーバー
                    if (random.nextBoolean()) {
                        ConfigManager.getInstance().setRatePattern(1);
                    } else {
                        ConfigManager.getInstance().setRatePattern(3);
                    }
                    break;
                case 3: //ジリ貧
                    if (random.nextBoolean()) {
                        ConfigManager.getInstance().setRatePattern(1);
                    } else {
                        ConfigManager.getInstance().setRatePattern(2);
                    }
                    break;
            }
            ConfigManager.getInstance().setPatternPhase(1);
        }

        //TODO: if (calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.HOUR_OF_DAY) != 12) return Main.getExchangeRate();
        int value;
        switch (ConfigManager.getInstance().getRatePattern()) {
            case 1:
                value = ConfigManager.getInstance().getExchangeRate() + random.nextInt(30) - 20;
                if (value < baseExchangeRate - 50 || value > baseExchangeRate + 50) {
                    ConfigManager.getInstance().setExchangeRate(baseExchangeRate + random.nextInt(20) - 10);
                } else {
                    ConfigManager.getInstance().setExchangeRate(value);
                }
                break;
            case 2:
                switch (ConfigManager.getInstance().getPatternPhase()) {
                    case 1:
                        value = ConfigManager.getInstance().getExchangeRate() + random.nextInt(40);
                        if (value <= maxExchangeRate - 30) {
                            ConfigManager.getInstance().setExchangeRate(value);
                        } else {
                            ConfigManager.getInstance().setExchangeRate(ConfigManager.getInstance().getExchangeRate() - random.nextInt(100));
                            ConfigManager.getInstance().setPatternPhase(2);
                        }
                        break;
                    case 2:
                        value = ConfigManager.getInstance().getExchangeRate() - random.nextInt(100);
                        if (value >= minExchangeRate + 10) {
                            ConfigManager.getInstance().setExchangeRate(value);
                        } else {
                            ConfigManager.getInstance().setExchangeRate(ConfigManager.getInstance().getExchangeRate() - random.nextInt(20));
                            ConfigManager.getInstance().setPatternPhase(3);
                        }
                    case 3:
                        value = ConfigManager.getInstance().getExchangeRate() - random.nextInt(50);
                        if (value >= minExchangeRate) {
                            ConfigManager.getInstance().setExchangeRate(value);
                        } else {
                            ConfigManager.getInstance().setExchangeRate(baseExchangeRate + random.nextInt(50));
                            ConfigManager.getInstance().setPatternPhase(1);
                        }
                        break;
                }
                break;
            case 3:
                value = ConfigManager.getInstance().getExchangeRate() + random.nextInt(20) - 5;
                ConfigManager.getInstance().setExchangeRate(value);

                switch (ConfigManager.getInstance().getPatternPhase()) {
                    case 1:
                        value = ConfigManager.getInstance().getExchangeRate() + random.nextInt(30);
                        if (value <= maxExchangeRate - 40) {
                            ConfigManager.getInstance().setExchangeRate(value);
                        } else {
                            ConfigManager.getInstance().setExchangeRate(ConfigManager.getInstance().getExchangeRate() - random.nextInt(50));
                            ConfigManager.getInstance().setPatternPhase(2);
                        }
                        break;
                    case 2:
                        value = ConfigManager.getInstance().getExchangeRate() - random.nextInt(40);
                        ConfigManager.getInstance().setExchangeRate(value);
                        ConfigManager.getInstance().setPatternPhase(3);
                        break;
                    case 3:
                        value = ConfigManager.getInstance().getExchangeRate() + random.nextInt(20) - 10;
                        if (value <= maxExchangeRate - 40) {
                            ConfigManager.getInstance().setExchangeRate(value);
                        } else {
                            ConfigManager.getInstance().setExchangeRate(maxExchangeRate - 60 + random.nextInt(20) - 10);
                        }
                }
                break;
        }
        return ConfigManager.getInstance().getExchangeRate();
    }
}
