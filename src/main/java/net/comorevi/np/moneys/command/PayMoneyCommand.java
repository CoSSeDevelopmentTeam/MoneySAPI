package net.comorevi.np.moneys.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.comorevi.np.moneys.MoneySAPI;
import net.comorevi.np.moneys.util.ConfigManager;
import net.comorevi.np.moneys.util.MessageManager;
import net.comorevi.np.moneys.util.TaxType;
import net.comorevi.np.moneys.util.TextValues;

public class PayMoneyCommand extends Command {
    public PayMoneyCommand(String name) {
        super(name, "指定した金額をプレイヤーに支払います。", "/paymoney <target: 金額> <amount: 金額>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isPlayer()) {
            commandSender.sendMessage(TextValues.ALERT+ MessageManager.getInstance().translateString("error-command-console"));
            return true;
        }

        if (!commandSender.hasPermission("moneys.api.paymoney")) {
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

        if (ConfigManager.PAY_COMMISSION) {
            if (MoneySAPI.getInstance().isPayable(commandSender.getName(), Integer.parseInt(strings[1]))) {
                MoneySAPI.getInstance().payMoney(strings[0], commandSender.getName(), Integer.parseInt(strings[1]), TaxType.PAYMENT);
                commandSender.sendMessage(TextValues.INFO+MessageManager.getInstance().translateString("player-pay-money", strings[0], strings[1], MoneySAPI.UNIT));
            } else {
                commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("error-player-account-lack-of-money"));
            }
        } else {
            if (MoneySAPI.getInstance().isPayable(commandSender.getName(), Integer.parseInt(strings[1]))) {
                MoneySAPI.getInstance().payMoney(strings[0], commandSender.getName(), Integer.parseInt(strings[1]));
                commandSender.sendMessage(TextValues.INFO+MessageManager.getInstance().translateString("player-pay-money", strings[0], strings[1], MoneySAPI.UNIT));
            } else {
                commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("error-player-account-lack-of-money"));
            }
        }
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
