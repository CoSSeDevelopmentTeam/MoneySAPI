package net.comorevi.moneyapi.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.comorevi.moneyapi.MoneySystemPlugin;
import net.comorevi.moneyapi.util.MessageManager;
import net.comorevi.moneyapi.util.TextValues;

public class RankMoneyCommand extends Command {
    public RankMoneyCommand(String name) {
        super(name, "所持金ランキングを表示します。", "/rankmoney <順位: 何位までか>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.hasPermission("moneysapi.command.moneyrank")) {
            commandSender.sendMessage(TextValues.ALERT+ MessageManager.getInstance().translateString("error-no-permission"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(TextValues.ALERT+MessageManager.getInstance().translateString("error-command-args"));
            return false;
        }

        commandSender.sendMessage("=== "+MessageManager.getInstance().translateString("player-rank-title")+" ===");
        commandSender.sendMessage(MoneySystemPlugin.getInstance().getRankString(Integer.parseInt(strings[0])));
        return true;
    }
}
