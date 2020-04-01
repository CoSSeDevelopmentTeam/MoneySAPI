package net.comorevi.moneyapi.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.comorevi.moneyapi.MoneySAPI;
import net.comorevi.moneyapi.util.MessageManager;
import net.comorevi.moneyapi.util.TextValues;

public class SeeMoneyCommand extends Command {
    public SeeMoneyCommand(String name) {
        super(name, "プレイヤーの所持金を確認します。", "/seemoney <target: 名前>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.hasPermission("moneysapi.command.seemoney")) {
            commandSender.sendMessage(TextValues.ALERT+ MessageManager.getInstance().translateString("error-no-permission"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("error-command-args"));
            return false;
        }

        if (!MoneySAPI.getInstance().existsAccount(strings[0])) {
            commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("player-account-not-found", strings[0]));
            return false;
        }

        if (commandSender.isOp()) {
            commandSender.sendMessage(TextValues.INFO+MessageManager.getInstance().translateString("player-account-money-see", strings[0], String.valueOf(MoneySAPI.getInstance().getMoney(strings[0])), MoneySAPI.UNIT));
        } else {
            if (MoneySAPI.getInstance().isPublished(strings[0])) {
                commandSender.sendMessage(TextValues.INFO+MessageManager.getInstance().translateString("player-account-money-see", strings[0], String.valueOf(MoneySAPI.getInstance().getMoney(strings[0])), MoneySAPI.UNIT));
            } else {
                commandSender.sendMessage(TextValues.INFO+MessageManager.getInstance().translateString("error-player-account-not-publish"));
            }
        }
        return true;
    }
}
