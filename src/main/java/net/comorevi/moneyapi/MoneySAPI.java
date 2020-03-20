package net.comorevi.moneyapi;

import cn.nukkit.Player;
import net.comorevi.moneyapi.util.SQLite3DataProvider;

public class MoneySAPI {
    private static MoneySAPI instance = new MoneySAPI();
    private SQLite3DataProvider provider = new SQLite3DataProvider();

    private MoneySAPI() {
        instance = this;
    }

    public static MoneySAPI getInstance() {
        return instance;
    }

    public static void registerAccount(Player player) {
        //
    }

    public static void registerAccount(Player player, int def) {
        //
    }

    public static void removeAccount(Player player) {
        //
    }

    public static boolean existsAccount(Player player) {
        return false;
    }

    public static void addMoney(Player player, int amount) {
        //
    }

    public static void setMoney(Player player, int amount) {
        //
    }

    public static void payMoney(Player payer, Player target) {
        //
    }

    public static void payMoney(String payerName, String targetName) {
        //
    }

    public static void payMoney(Player payer, Player target, double commissionRatio) {
        //
    }

    public static void payMoney(String payerName, String targetName, double commissionRatio) {
        //
    }

    public static boolean isPayable(Player player, int amount) {
        return false;
    }

    public static boolean isPayable(String playerName, int amount) {
        return false;
    }

    protected static void disconnectSQL() {
        //
    }
}
