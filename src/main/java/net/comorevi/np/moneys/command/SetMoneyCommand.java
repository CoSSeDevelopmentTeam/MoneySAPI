package net.comorevi.np.moneys.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.comorevi.np.moneys.MoneySAPI;
import net.comorevi.np.moneys.util.MessageManager;
import net.comorevi.np.moneys.util.TextValues;

public class SetMoneyCommand extends Command {
    public SetMoneyCommand(String name) {
        super(name, "プレイヤーの所持金を設定します。", "/setmoney <target: 名前> <amount: 金額>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.hasPermission("moneys.api.setmoney")) {
            commandSender.sendMessage(TextValues.ALERT+ MessageManager.getInstance().translateString("error-no-permission"));
            return true;
        }

        if (strings.length < 2) {
            commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("error-command-args"));
            return false;
        }

        if (!MoneySAPI.getInstance().existsAccount(strings[0])) {
            commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("player-account-not-found", strings[0]));
            return false;
        }

        if (!isDigit(strings[1]) || Integer.parseInt(strings[1]) < 0) {
            commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("error-command-args-num"));
            return false;
        }

        MoneySAPI.getInstance().setMoney(strings[0], Integer.parseInt(strings[1]));
        commandSender.sendMessage(TextValues.INFO+MessageManager.getInstance().translateString("player-account-money-set", strings[0], strings[1], MoneySAPI.UNIT));
        return true;
    }

    private boolean isDigit(String str) {
        boolean isDigit = true;
        for (int i = 0; i < str.length(); i++) {
            isDigit = Character.isDigit(str.charAt(i));
            if (!isDigit) {
                break;
            }
        }
        return isDigit;
    }
}
