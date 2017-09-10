package net.comorevi.moneyapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;

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
 *    1.1.3
 *     - SQLite関連書き直し
 *    2.0.0
 *     - コマンド関連書き直し
 *     - メッセージファイルを作成
 *     - ヘルプメッセージファイルを作成
 *     - コンフィグを作成
 *     - アカウント作成の手順を変更
 *     - 支払いでお金が無限増殖する問題を修正
 */

public class MoneySAPI extends PluginBase {

    private static MoneySAPI instance;
    private SQLite3DataProvider sql;
    public static String unit;
    public static int defaultmoney;
    
    private Config translateFile;
    private Map<String, Object> configData = new HashMap<String, Object>();
    private Map<String, Object> pluginData = new HashMap<String, Object>();
    private Config conf;

    /**************/
    /** プラグイン関連  */
    /**************/

    public static MoneySAPI getInstance() {
        return instance;
    }

    public SQLite3DataProvider getSQL() {
        return this.sql;
    }

    /**************/
    /** MoneySAPI */
    /**************/

    public void createAccount(Player player, int defaultmoney) {
        sql.createAccount(player.getName(), defaultmoney);
    }

    public int getMoney(Player player) {
        return sql.getMoney(player.getName());
    }

    public void setMoney(Player player, int value) {
        try {
            sql.setMoney(player.getName(), value);
        }
         catch(NumberFormatException e) {
             getServer().getLogger().critical("プレイヤーによる数値の入力でエラーが発生しました。");
        }
    }

    public void addMoney(Player player, int value) {
        sql.addMoney(player.getName(), value);
    }

    public void grantMoney(Player player, int value) {
        sql.addMoney(player.getName(), value);
    }
    
    public void reduceMoney(String username, int value) {
    	sql.reduceMoney(username, value);
    }

    public void payMoney(String username, String targetname, int value) {
        sql.payMoney(username, targetname, value);
    }

    public String getMoneyUnit() {
        return unit;
    }

    /*****************/
    /** NotMoneySAPI */
    /*****************/

    @Override
    public void onEnable(){
        getDataFolder().mkdirs();
        this.initMessageConfig();
        this.initMoneySAPIConfig();
        this.initHelpFile();
        this.sql = new SQLite3DataProvider(this);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable(){
        
    }

    /*************/
    /** Commands */
    /*************/

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("money")) {

            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(TextValues.WARNING + this.translateString("error-command-console"));
                return true;
            }

            try{if(args[0] != null){}}
            catch(ArrayIndexOutOfBoundsException e){
                this.helpMessage(sender);
                return true;
            }

            String name = sender.getName().toLowerCase();

            Player p = (Player)sender;

            switch(args[0]) {
                case "see":
                	if(args.length <= 1) {
                		sender.sendMessage(TextValues.INFO + this.translateString("player-money", sender.getName(), String.valueOf(sql.getMoney(sender.getName())), this.unit));
                	} else {
                		if(!sql.existsAccount(args[1])) {
                			sender.sendMessage(TextValues.INFO + this.translateString("player-account-not-found", args[1]));
                		} else {
                			sender.sendMessage(TextValues.INFO + this.translateString("player-money", args[1], String.valueOf(sql.getMoney(args[1])), this.unit));
                		}
                	}
                    return true;

                case "pay":
                	if(args.length <= 1) {
                		sender.sendMessage(TextValues.HELP + this.translateString("help-command-pay"));
                	} else {
                		if(!sql.existsAccount(args[1])) {
                			sender.sendMessage(TextValues.INFO + this.translateString("player-account-not-found", args[1]));
                		} else {
                			if(this.getMoney(p) > Integer.parseInt(args[2])) {
                				this.payMoney(sender.getName(), args[1], Integer.parseInt(args[2]));
                				getServer().getPlayer(args[1]).sendMessage(TextValues.INFO + this.translateString("player-pay2", sender.getName(), args[2], this.unit));
                				sender.sendMessage(TextValues.INFO + this.translateString("player-pay1", args[1], args[2], this.unit));
                			} else {
                				sender.sendMessage(TextValues.ALERT + this.translateString("error-player-lack"));
                			}
                		}
                	}
                    return true;
                    
                case "give":
                	if(sender.isOp()) {
                		if(!sql.existsAccount(args[1])) {
                			sender.sendMessage(TextValues.INFO + this.translateString("player-account-not-found", args[1]));
                		} else {
                			this.addMoney(getServer().getPlayer(args[1]), Integer.parseInt(args[2]));
                			getServer().getPlayer(args[1]).sendMessage(TextValues.INFO + this.translateString("player-give2", args[2], this.unit));
                			sender.sendMessage(TextValues.INFO + this.translateString("player-give1", args[1], args[2], this.unit));
                		}
                	}
                    return true;
                    
                case "take":
                	if(sender.isOp()) {
                		if(!sql.existsAccount(args[1])) {
                			sender.sendMessage(TextValues.INFO + this.translateString("player-account-not-found", args[1]));
                		} else {
                			this.reduceMoney(getServer().getPlayer(args[1]).getName(), Integer.parseInt(args[2]));
                			getServer().getPlayer(args[1]).sendMessage(TextValues.ALERT + this.translateString("player-take2", args[2], this.unit));
                			sender.sendMessage(TextValues.ALERT + this.translateString("player-take1", args[1], args[2], this.unit));
                		}
                	}
                    return true;
                    
                case "set":
                	if(sender.isOp()) {
                		if(!sql.existsAccount(args[1])) {
                			sender.sendMessage(TextValues.INFO + this.translateString("player-account-not-found", args[1]));
                		} else {
                			this.setMoney(getServer().getPlayer(args[1]), Integer.parseInt(args[2]));
                			getServer().getPlayer(args[1]).sendMessage(TextValues.WARNING + this.translateString("player-set2", args[2], this.unit));
                			sender.sendMessage(TextValues.WARNING + this.translateString("player-set1", args[1], args[2], this.unit));
                		}
                	}
                    return true;
                    
                case "top":
                	sender.sendMessage(TextValues.INFO + "このコマンドは未実装です。");
                    return true;
                case "debt":
                	sender.sendMessage(TextValues.INFO + "このコマンドは未実装です。");
                    return true;
                case "stock":
                	sender.sendMessage(TextValues.INFO + "このコマンドは未実装です。");
                    return true;
                
            }
        }
        return false;
    }
    
    public void helpMessage(CommandSender sender){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getDataFolder().toString() + "/Help.txt")), "UTF-8"));
                    String txt;
                    boolean op = (boolean) sender.isOp();
                    boolean send = true;
                    while(true){
                        txt = br.readLine();
                        if(txt == null)break;
                        if(txt.startsWith("##"))continue;
                        if(txt.equals("::op")){
                            send = false;
                            continue;
                        }
                        if(op)send = true;
                        if(txt.equals("::all")){
                            send = true;
                            continue;
                        }
                        if(send) sender.sendMessage(txt);
                    }
                    br.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return;
            }
        });
        th.start();
    }

    public String translateString(String key, String... args){
        if(configData != null || !configData.isEmpty()){
            String src = (String) configData.get(key);
            if(src == null || src.equals("")) return TextValues.ALERT + (String) configData.get("error-notFoundKey");
            for(int i=0;i < args.length;i++){
                src = src.replace("{%" + i + "}", args[i]);
            }
            return src;
        }
        return null;
    }

    public String parseMessage(String message) {
        return "";
    }

    /**************/
    /**   その他      **/
    /**************/

    private void initMessageConfig(){
        if(!new File(getDataFolder().toString() + "/Message.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Message.yml"), true);//trueで追加書き込み,falseで上書き
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                Utils.writeFile(new File(getDataFolder().toString() + "/Message.yml"), this.getClass().getClassLoader().getResourceAsStream("Message.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.translateFile = new Config(new File(getDataFolder().toString() + "/Message.yml"), Config.YAML);
        this.translateFile.load(getDataFolder().toString() + "/Message.yml");
        this.configData = this.translateFile.getAll();
        return;
    }

    private void initMoneySAPIConfig(){
        if(!new File(getDataFolder().toString() + "/Config.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Config.yml"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.conf = new Config(new File(getDataFolder().toString() + "/Config.yml"), Config.YAML);
            this.conf.load(getDataFolder().toString() + "/Config.yml");
            this.conf.set("Unit", "MS");
            this.conf.set("DefaultMoney", 500);
            this.conf.save();
        }

        this.conf = new Config(new File(getDataFolder().toString() + "/Config.yml"), Config.YAML);
        this.conf.load(getDataFolder().toString() + "/Config.yml");
        this.pluginData = this.conf.getAll();

        /*コンフィグからデータを取得*/
        unit = (String) pluginData.get("Unit");
        defaultmoney = (Integer) pluginData.get("DefaultMoney");
        
        return;
    }
    
    public void initHelpFile(){
    	if(!new File(getDataFolder().toString() + "/Help.txt").exists()){
            try {
                FileWriter fw = new FileWriter(new File(getDataFolder().toString() + "/Help.txt"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                
                Utils.writeFile(new File(getDataFolder().toString() + "/Help.txt"), this.getClass().getClassLoader().getResourceAsStream("Help.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}
    	return;
    }
}
