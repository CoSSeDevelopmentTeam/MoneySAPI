package net.comorevi.moneyapi;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;

/**
 * Money System API
 * @author popkechupki
 *
 * change logs
 *  1.0.0
 *   - 基本的な機能の実装
 *   1.1.0
 *    - コマンド仮実装
 *    1.1.1
 *     - すでにアカウントがあるのに新規にアカウントが作成される問題を修正
 *    1.1.2
 *     - すでにアカウントがあるのに新規にアカウントが作成される問題を修正
 */

public class MoneySAPI extends PluginBase{

	private static MoneySAPI instance;
	private SQLiteManager sql;
	public static final String unit = "MS";

	public static MoneySAPI getInstance(){
		return instance;
	}

	public void createAccount(Player player, int defaultMoney){
		sql.createAccount(player.getName(), defaultMoney);
	}

	public int getMoney(Player player){
		return sql.getMoney(player.getName());
	}

	public void setMoney(Player player, int value){
		try {
			sql.setMoney(player.getName(), value);
		}
		 catch(NumberFormatException e) {
			 getServer().getLogger().critical("プレイヤーによる数値の入力でエラーが発生しました。");
		}
	}

	public void addMoney(Player player, int value){
		sql.addMoney(player.getName(), value);
	}

	public void grantMoney(Player player, int value){
		sql.addMoney(player.getName(), value);
	}

	@Override
	public void onEnable(){
		getDataFolder().mkdirs();
		this.sql = new SQLiteManager(this);
		//new EventListener(this);
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
	}

	@Override
	public void onDisable(){
		sql.unLoadSqlite("userdata", "data");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		switch(command.getName()){
			case "mcheck":
				sender.sendMessage("[MoneysAPI]あなたの所持金は" + sql.getMoney(sender.getName()) + unit + "です。");
				break;
			case "msee":
				sender.sendMessage("[MoneySAPI]" + getServer().getPlayer(args[0]) + "さんの所持金は" + sql.getMoney(getServer().getPlayer(args[0]) + unit + "です。"));
				break;
			case "mpay":
				sql.payMoney(sender.getName(), args[0], Integer.parseInt(args[1]));
				sender.sendMessage("[MoneySAPI]" + args[0] + "さんに" + args[1] + unit + "支払いました。");
				break;
			case "mgive":
				sql.addMoney(args[0], Integer.parseInt(args[1]));
				//TODO: message
				break;
			case "mtake":
				sql.reduceMoney(args[0], Integer.parseInt(args[1]));
				//TODO: message
				break;
			case "mtop":
				sender.sendMessage("[MoneySAPI]このコマンドはまだ使用できません。");
				break;
			case "mset":
				sql.setMoney(args[0], Integer.parseInt(args[1]));
				//TODO: message
				break;
			case "mdebt":
				sender.sendMessage("[MoneySAPI]このコマンドはまだ使用できません。");
				break;
			case "stock":
				sender.sendMessage("[MoneySAPI]このコマンドはまだ使用できません。");
				break;
		}
		return true;
	}

	public SQLiteManager getSQL(){
		return this.sql;
	}

}
