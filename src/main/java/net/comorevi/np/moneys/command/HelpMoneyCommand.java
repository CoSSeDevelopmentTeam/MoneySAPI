package net.comorevi.np.moneys.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.comorevi.np.moneys.util.MessageManager;
import net.comorevi.np.moneys.util.TextValues;

public class HelpMoneyCommand extends Command {
    public HelpMoneyCommand(String name) {
        super(name, "MoneySAPIのコマンドのヘルプを表示します。", "/moneyhelp");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.hasPermission("moneys.api.moneyhelp")) {
            commandSender.sendMessage(TextValues.ALERT+ MessageManager.getInstance().translateString("error-no-permission"));
            return true;
        }

        MessageManager.getInstance().helpMessage(commandSender);
        return true;
    }
}
