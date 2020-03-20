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

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import net.comorevi.moneyapi.util.SQLite3DataProvider;
import net.comorevi.moneyapi.util.TextValues;

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
 *     1.1.2
 *      - すでにアカウントがあるのに新規にアカウントが作成される問題を修正
 *     1.1.3
 *      - SQLite関連書き直し
 *  2.0.0
 *   - コマンド関連書き直し
 *   - メッセージファイルを作成
 *   - ヘルプメッセージファイルを作成
 *   - コンフィグを作成
 *   - アカウント作成の手順を変更
 *   - 支払いでお金が無限増殖する問題を修正
 *   2.0.1
 *    - マイナスの支払いができた問題を修正
 *    2.0.2
 *     - SQLite3Providerでstatement使いまわししないように
 *     2.0.3
 *      - インスタンスが空だったものを修正
 *      2.0.4
 *       - 送金などの処理の際送金先のプレイヤーがオフラインだった場合エラーが発生する問題を修正
 *   2.1.0
 *    - 操作系をコマンドからフォームに変更
 *    - SQLite3Dataproviderで発生していたバグの修正
 *   2.2.0
 *    - 送金手数料１０％に設定
 *   2.3.0
 *    - 一定金額以上を持っているプレイヤーを取得できるメソッドを追加
 *    2.3.1
 *    - payAmount()で自動的に手数料を追加していた仕様を変更
 *  3.0.0
 *   - しふぉん実装に伴い、フォーム表示部分を削除しアカウント処理のみを残す
 *   - データベースのセキュリティ向上と、ランキングへの公開可否recordをカラムに追加
 *   - canPayメソッドで支払える金額を所持しているか確認できるように
 *   3.0.1
 *    - SQLiteのConnectionがnullになっていた問題の修正
 *   3.1.0
 *    - 関数名の変更canPayからisPayableに
 *    3.1.1
 *     - isPayableの判定が反対になっていた
 *    3.1.2
 *     - isExistsを追加
 *  4.0.0
 *   - APIをメインクラスから分離、SQLProviderの書き直し等
 *
 */

public class Main extends PluginBase {

    public static String unit;
    public static int defaultmoney;
    
    private Config translateFile;
    private Map<String, Object> configData = new HashMap<String, Object>();

    @Override
    public void onEnable(){
        getDataFolder().mkdirs();
        this.initMessageConfig();
        this.initHelpFile();
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        MoneySAPI.getInstance().disconnectSQL();
    }

    /*************/
    /**  Utils   */
    /*************/

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
