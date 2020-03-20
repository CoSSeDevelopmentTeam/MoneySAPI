package net.comorevi.moneyapi;

import cn.nukkit.plugin.PluginBase;
import net.comorevi.moneyapi.command.*;

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

    @Override
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register("givemoney", new GiveMoneyCommand("givemoney"));
        this.getServer().getCommandMap().register("moneyhelp", new HelpMoneyCommand("moneyhelp"));
        this.getServer().getCommandMap().register("paymoney", new PayMoneyCommand("paymoney"));
        this.getServer().getCommandMap().register("rankmoney", new RankMoneyCommand("rankmoney"));
        this.getServer().getCommandMap().register("seemoney", new SeeMoneyCommand("seemoney"));
        this.getServer().getCommandMap().register("setmoney", new SetMoneyCommand("setmoney"));
        this.getServer().getCommandMap().register("takemoney", new TakeMoneyCommand("takemoney"));
    }

    @Override
    public void onDisable() {
        MoneySAPI.getInstance().disconnectSQL();
    }
}
