package net.comorevi.moneyapi.util;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private static MessageManager instance = new MessageManager();
    private Config translateFile;
    private Map<String, Object> configData = new HashMap<String, Object>();

    private MessageManager() {
        instance = this;
        new File("./plugins/MoneySAPI").mkdirs();
    }

    public MessageManager getInstance() {
        return instance;
    }

    public void helpMessage(CommandSender sender){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./plugins/MoneySAPI/Help.txt")), "UTF-8"));
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

    private void initMessageConfig(){
        if(!new File("./plugins/MoneySAPI/Message.yml").exists()){
            try {
                FileWriter fw = new FileWriter(new File("./plugins/MoneySAPI/Message.yml"), true);//trueで追加書き込み,falseで上書き
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();
                Utils.writeFile(new File("./plugins/MoneySAPI/Message.yml"), this.getClass().getClassLoader().getResourceAsStream("Message.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.translateFile = new Config(new File("./plugins/MoneySAPI/Message.yml"), Config.YAML);
        this.translateFile.load("./plugins/MoneySAPI/Message.yml");
        this.configData = this.translateFile.getAll();
    }

    public void initHelpFile(){
        if(!new File("./plugins/MoneySAPI/" + "Help.txt").exists()){
            try {
                FileWriter fw = new FileWriter(new File("./plugins/MoneySAPI/Help.txt"), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("");
                pw.close();

                Utils.writeFile(new File("./plugins/MoneySAPI/Help.txt"), this.getClass().getClassLoader().getResourceAsStream("Help.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
