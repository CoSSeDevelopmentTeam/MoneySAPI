package net.comorevi.moneyapi.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class SeeMoneyCommand extends Command {
    public SeeMoneyCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
