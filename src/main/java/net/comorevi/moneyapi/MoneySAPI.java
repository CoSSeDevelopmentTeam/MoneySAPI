package net.comorevi.moneyapi;

import cn.nukkit.Player;
import net.comorevi.moneyapi.util.ConfigManager;
import net.comorevi.moneyapi.util.SQLite3DataProvider;

public class MoneySAPI {
    private static MoneySAPI instance = new MoneySAPI();
    private SQLite3DataProvider dataProvider = new SQLite3DataProvider();

    private MoneySAPI() {
        instance = this;
    }

    public static MoneySAPI getInstance() {
        return instance;
    }

    public void registerAccount(Player player) {
        registerAccount(player, ConfigManager.DEFAULT_MONEY);
    }

    public void registerAccount(String playerName) {
        registerAccount(playerName, ConfigManager.DEFAULT_MONEY);
    }

    public void registerAccount(Player player, int def) {
        registerAccount(player, def, ConfigManager.DEFAULT_PUBLISH_STATUS);
    }

    public void registerAccount(String playerName, int def) {
        registerAccount(playerName, def, ConfigManager.DEFAULT_PUBLISH_STATUS);
    }

    public void registerAccount(Player player, int def, boolean publish) {
        dataProvider.createAccount(player.getName(), def, publish);
    }

    public void registerAccount(String playerName, int def, boolean publish) {
        dataProvider.createAccount(playerName, def, publish);
    }

    public void deleteAccount(Player player) {
        deleteAccount(player.getName());
    }

    public void deleteAccount(String playerName) {
        dataProvider.deleteAccount(playerName);
    }

    public boolean existsAccount(Player player) {
        return existsAccount(player.getName());
    }

    public boolean existsAccount(String playerName) {
        return dataProvider.existsAccount(playerName);
    }

    public void addMoney(Player player, int amount) {
        addMoney(player.getName(), amount);
    }

    public void addMoney(String playerName, int amount) {
        dataProvider.addMoney(playerName, amount);
    }

    public void setMoney(Player player, int amount) {
        setMoney(player.getName(), amount);
    }

    public void setMoney(String playerName, int amount) {
        dataProvider.setMoney(playerName, amount);
    }

    public void reduceMoney(Player player, int amount) {
        reduceMoney(player.getName(), amount);
    }

    public void reduceMoney(String playerName, int amount) {
        dataProvider.reduceMoney(playerName, amount);
    }

    public int getMoney(Player player) {
        return getMoney(player.getName());
    }

    public int getMoney(String playerName) {
        return dataProvider.getMoney(playerName);
    }

    public void payMoney(Player payer, Player target, int amount) {
        payMoney(payer.getName(), target.getName(), amount);
    }

    public void payMoney(String payerName, String targetName, int amount) {
        if (isPayable(payerName, amount)) {
            reduceMoney(payerName, amount);
            addMoney(targetName, amount);
        }
    }

    public void payMoney(Player payer, Player target, int amount, double commissionRatio) {
        payMoney(payer.getName(), target.getName(), amount, commissionRatio);
    }

    public void payMoney(String payerName, String targetName, int amount, double commissionRatio) {
        if (isPayable(payerName, (int) (amount * commissionRatio))) {
            reduceMoney(payerName, (int) (amount * commissionRatio));
            addMoney(targetName, amount);
        }
    }

    public boolean isPayable(Player player, int amount) {
        return isPayable(player.getName(), amount);
    }

    public boolean isPayable(String playerName, int amount) {
        return getMoney(playerName) >= amount;
    }

    public void setPublishStatus(Player player, boolean status) {
        setPublishStatus(player.getName(), status);
    }

    public void setPublishStatus(String playerName, boolean status) {
        dataProvider.setPublishStatus(playerName, status);
    }

    public boolean isPublished(Player player) {
        return isPublished(player.getName());
    }

    public boolean isPublished(String playerName) {
        return dataProvider.getPublishStatus(playerName);
    }

    /*
    public LinkedList<String> getMoneyRank(int limit) {
        return null;
    }
     */

    public String getMoneyUnit() {
        return ConfigManager.MONEY_UNIT;
    }

    protected void disconnectSQL() {
        dataProvider.disConnectSQL();
    }
}
