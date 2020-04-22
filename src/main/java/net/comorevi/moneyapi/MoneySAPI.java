package net.comorevi.moneyapi;

import cn.nukkit.Player;
import net.comorevi.moneyapi.util.ConfigManager;
import net.comorevi.moneyapi.util.DataProvider;
import net.comorevi.moneyapi.util.ExchangeRateCalculator;
import net.comorevi.moneyapi.util.TAXType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoneySAPI {
    private static MoneySAPI instance = new MoneySAPI();
    private DataProvider dataProvider = new DataProvider();
    private int exchangeRate = new ExchangeRateCalculator().exchangeRate;

    public static final String UNIT = ConfigManager.MONEY_UNIT;

    private MoneySAPI() {
        instance = this;
    }

    /* API version 4.0- */
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
        registerAccount(player.getName(), def, publish);
    }

    public void registerAccount(String playerName, int def, boolean publish) {
        try {
            dataProvider.createUserData(DataProvider.TABLE_MONEY, playerName, def, publish);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(Player player) {
        deleteAccount(player.getName());
    }

    public void deleteAccount(String playerName) {
        try {
            dataProvider.deleteUserData(DataProvider.TABLE_MONEY, playerName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existsAccount(Player player) {
        return existsAccount(player.getName());
    }

    public boolean existsAccount(String playerName) {
        try {
            return dataProvider.existsUserData(DataProvider.TABLE_MONEY, playerName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addMoney(Player player, int amount) {
        addMoney(player.getName(), amount);
    }

    public void addMoney(String playerName, int amount) {
        try {
            dataProvider.addValue(DataProvider.TABLE_MONEY, playerName, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMoney(Player player, int amount) {
        setMoney(player.getName(), amount);
    }

    public void setMoney(String playerName, int amount) {
        try {
            dataProvider.setUserData(DataProvider.TABLE_MONEY, playerName, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reduceMoney(Player player, int amount) {
        reduceMoney(player.getName(), amount);
    }

    public void reduceMoney(String playerName, int amount) {
        try {
            dataProvider.reduceValue(DataProvider.TABLE_MONEY, playerName, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getMoney(Player player) {
        return getMoney(player.getName());
    }

    public int getMoney(String playerName) {
        try {
            return dataProvider.getValue(DataProvider.TABLE_MONEY, playerName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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

    /* API version 4.1- */
    public boolean existsCoinData(Player player) {
        try {
            return dataProvider.existsUserData(DataProvider.TABLE_COIN, player.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createCoinData(Player player) {
        createCoinData(player, 0);
    }

    public void createCoinData(Player player, int defValue) {
        try {
            dataProvider.createUserData(DataProvider.TABLE_COIN, player.getName(), defValue, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCoinData() {
        try {
            dataProvider.deleteTableData(DataProvider.TABLE_COIN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCoinData(Player player) {
        try {
            dataProvider.deleteUserData(DataProvider.TABLE_COIN, player.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCoin(Player player) {
        try {
            return dataProvider.getValue(DataProvider.TABLE_COIN, player.getName());
        } catch (Exception e) {
            createCoinData(player);
        }
        return 0;
    }

    public void addCoin(Player player, int value) {
        if (!existsCoinData(player)) createCoinData(player);
        try {
            dataProvider.addValue(DataProvider.TABLE_COIN, player.getName(), value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reduceCoin(Player player, int value) {
        try {
            dataProvider.reduceValue(DataProvider.TABLE_COIN, player.getName(), value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCoin(Player player, int value) {
        if (!existsCoinData(player)) createCoinData(player);
        try {
            dataProvider.setUserData(DataProvider.TABLE_COIN, player.getName(), value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exchangeCoinToMoney(Player player) throws Exception {
        if (!existsCoinData(player)) throw new Exception("コインデータが見つかりません。");
        int coin = getCoin(player) / exchangeRate;
        MoneySAPI.getInstance().addMoney(player, coin);
        setCoin(player, getCoin(player) % exchangeRate);
    }

    public void exchangeCoinToMoney(Player player, int coin) throws Exception {
        if (!existsCoinData(player)) throw new Exception("コインデータが見つかりません。");
        int money = coin / exchangeRate;
        MoneySAPI.getInstance().addMoney(player, money);
        setCoin(player, getCoin(player) - coin + (coin % exchangeRate));
    }

    public void exchangeMoneyToCoin(Player player) {
        setCoin(player, getCoin(player) + MoneySAPI.getInstance().getMoney(player) * exchangeRate);
        MoneySAPI.getInstance().reduceMoney(player, MoneySAPI.getInstance().getMoney(player));
    }

    public void exchangeMoneyToCoin(Player player, int value) {
        if (MoneySAPI.getInstance().isPayable(player, value)) {
            setCoin(player, getCoin(player) + value * exchangeRate);
            MoneySAPI.getInstance().reduceMoney(player, value);
        }
    }

    public int getExchangeRate() {
        return exchangeRate;
    }

    /* For MoneySAPI */
    protected void reduceMoney() {
        int[] ranks = {100000, 3000000, 5000000};
        double[] rates = {TAXType.INCOME_LOWEST, TAXType.INCOME_LOW, TAXType.INCOME_MEDIUM};

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < ranks.length; i++) {
            try {
                arrayList = (ArrayList<String>) dataProvider.getUserListHoldOverCertainAmount(DataProvider.TABLE_MONEY, ranks[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            executeReduceMoney(arrayList, rates[i]);
            arrayList.clear();
        }
    }

    private void executeReduceMoney(List<String> players, double rate) {
        players.forEach(name -> {
            int result = (int) (getMoney(name) * rate - getMoney(name));
            if (getMoney(name) - result >= 1000) {
                reduceMoney(name, result);
            }
        });
    }

    protected void disconnectSQL() {
        dataProvider.disConnectSQL();
    }
}
