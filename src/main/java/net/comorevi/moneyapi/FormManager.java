package net.comorevi.moneyapi;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import itsu.mcbe.form.base.CustomForm;
import itsu.mcbe.form.base.SimpleForm;
import itsu.mcbe.form.core.NukkitFormAPI;
import itsu.mcbe.form.element.Button;
import itsu.mcbe.form.element.FormElement;
import itsu.mcbe.form.element.Input;
import itsu.mcbe.form.element.Label;

import java.util.ArrayList;
import java.util.List;

public class FormManager implements Listener {
	
	MoneySAPI mainClass;
	
	public FormManager(MoneySAPI plugin) {
		this.mainClass = plugin;
	}
	
	public void sendMoneySAPIHomeWindow(Player player) {
		SimpleForm form = new SimpleForm() {
		    @Override
            public void onEnter(Player player, int index) {
                switch (index) {
                    case 0:
                        sendPayWindow(player);
                        break;
                    case 1:
                        sendCheckMoneyWindow(player);
                        break;
                    case 2:
                        sendGiveMoneyWindow(player);
                        break;
                    case 3:
                        sendTakeMoneyWindow(player);
                        break;
                    case 4:
                        sendTakeMoneyWindow(player);
                        break;
                }
            }
        }
                .setId(3678920)
                .setTitle("しふぉん Wallet - Home");

        List<Button> buttons = new ArrayList<>();
        if (player.isOp()) {
            buttons.add(new Button().setText("送金/支払い"));
            buttons.add(new Button().setText("所持金確認"));
            buttons.add(new Button().setText("お金を付与"));
            buttons.add(new Button().setText("お金を没収"));
            buttons.add(new Button().setText("所持金を再設定"));
        } else {
            buttons.add(new Button().setText("送金/支払い"));
            buttons.add(new Button().setText("所持金確認"));
        }

        form.addButtons(buttons);

        NukkitFormAPI.getSingleton().sendFormToPlayer(player, form);
	}
	
	public void sendPayWindow(Player player) {
		FormElement[] elements = {
				new Label("送金するプレイヤー名と金額を入力して「送信」ボタンを押してください\n送金手数料は送金する金額の１０％です"),
				new Input("", "送金先のプレイヤー名を入力..."),
				new Input("", "送金する金額を入力...")
		};
		
		CustomForm form = new CustomForm() {
			@Override
			public void onEnter(Player p, List<Object> response) {
				String targetName;
				int payAmount;
				int payAmountIncludeCommission;
				
				try {
					targetName = (String) response.get(1);
					payAmount = Integer.parseInt(response.get(2).toString());
					payAmountIncludeCommission = (int) (payAmount + (payAmount * 0.1));
				} catch(NumberFormatException e) {
					p.sendMessage("システム>> 不適切な内容が入力されました");
					return;
				}
				
				if(mainClass.getSQL().existsAccount(targetName)) {
					if(payAmount >= 0) {
						if(mainClass.getSQL().getMoney(p.getName()) > payAmountIncludeCommission) {
							mainClass.getSQL().payMoney(p.getName(), targetName, payAmountIncludeCommission);
							if(mainClass.getServer().getPlayer(targetName) != null) {
								mainClass.getServer().getPlayer(targetName).sendMessage(TextValues.INFO + mainClass.translateString("player-pay2", p.getName(), String.valueOf(payAmount), mainClass.getMoneyUnit()));
							}
							p.sendMessage(TextValues.INFO + mainClass.translateString("player-pay1", targetName, String.valueOf(payAmountIncludeCommission), mainClass.getMoneyUnit()));
						} else {
							p.sendMessage(TextValues.ALERT + mainClass.translateString("error-player-lack"));
						}
					} else {
						p.sendMessage(TextValues.ALERT + mainClass.translateString("error-command-message4"));
					}
				} else {
					p.sendMessage(TextValues.INFO + mainClass.translateString("player-account-not-found", targetName));
				}
			}
		};
		form.setTitle("しふぉん Wallet - 支払い");
		form.addFormElements(elements);
		
		NukkitFormAPI.getSingleton().sendFormToPlayer(player, form);
	}
	
	public void sendCheckMoneyWindow(Player player) {
		FormElement[] elements = {
				new Label("Your money: " + mainClass.getSQL().getMoney(player.getName())),
				new Label("ユーザー名を入力して送信ボタンを押してください"),
				new Input("", "確認したいユーザー名を入力...")
		};
		
		CustomForm form = new CustomForm() {
			@Override
			public void onEnter(Player p, List<Object> response) {
				String targetName = (String) response.get(2);
				if(mainClass.getSQL().existsAccount(targetName)) {
					p.sendMessage(TextValues.INFO + mainClass.translateString("player-money", targetName, String.valueOf(mainClass.getSQL().getMoney(targetName)), mainClass.getMoneyUnit()));
				} else {
					p.sendMessage(TextValues.INFO + mainClass.translateString("player-account-not-found", targetName));
				}
			}
		};
		form.setTitle("しふぉん Wallet - 所持金確認");
		form.addFormElements(elements);
		
		NukkitFormAPI.getSingleton().sendFormToPlayer(player, form);
	}
	
	public void sendGiveMoneyWindow(Player player) {
		FormElement[] elements = {
				new Label("プレイヤーに指定した金額を与える(OP権限)"),
				new Input("", "対象プレイヤー名を入力..."),
				new Input("", "付与する金額を入力...")
		};
		
		CustomForm form = new CustomForm() {
			@Override
			public void onEnter(Player p, List<Object> response) {
				String targetName = (String) response.get(1);
				int giveAmount = 0;
				try {
					giveAmount = Integer.parseInt(response.get(2).toString());
				} catch(NumberFormatException e) {
					p.sendMessage("システム>> 不適切な内容が入力されました");
				}
				
				if(mainClass.getSQL().existsAccount(targetName)) {
					if(giveAmount > 0) {
						mainClass.getSQL().addMoney(targetName, giveAmount);
						if(mainClass.getServer().getPlayer(targetName) != null) {
							mainClass.getServer().getPlayer(targetName).sendMessage(TextValues.INFO + mainClass.translateString("player-give2", String.valueOf(giveAmount), mainClass.getMoneyUnit()));
						}
						p.sendMessage(TextValues.INFO + mainClass.translateString("player-give1", targetName, String.valueOf(giveAmount), mainClass.getMoneyUnit()));
					} else {
						p.sendMessage(TextValues.ALERT + mainClass.translateString("error-command-message4"));
					}
				} else {
					p.sendMessage(TextValues.INFO + mainClass.translateString("player-account-not-found", targetName));
				}
			}
		};
		form.setTitle("しふぉん Wallet - お金を付与");
		form.addFormElements(elements);
		
		NukkitFormAPI.getSingleton().sendFormToPlayer(player, form);
	}
	
	public void sendTakeMoneyWindow(Player player) {
		FormElement[] elements = {
				new Label("プレイヤーから所持金を没収する(OP権限)"),
				new Input("", "対象プレイヤー名を入力..."),
				new Input("", "没収する金額を入力...")
		};
		
		CustomForm form = new CustomForm() {
			@Override
			public void onEnter(Player p, List<Object> response) {
				String targetName = (String) response.get(1);
				int takeAmount = 0;
				try {
					takeAmount = Integer.parseInt(response.get(2).toString());
				} catch(NumberFormatException e) {
					p.sendMessage("システム>> 不適切な内容が入力されました");
					return;
				}
				
				if(mainClass.getSQL().existsAccount(targetName)) {
					if(takeAmount > 0) {
						int targetPlayerMoney = mainClass.getSQL().getMoney(targetName) - takeAmount;
						if(targetPlayerMoney > 0) {
							mainClass.getSQL().reduceMoney(targetName, takeAmount);
							if(mainClass.getServer().getPlayer(targetName) != null) {
								mainClass.getServer().getPlayer(targetName).sendMessage(TextValues.ALERT + mainClass.translateString("player-take2", String.valueOf(takeAmount), mainClass.getMoneyUnit()));
							}
							p.sendMessage(TextValues.ALERT + mainClass.translateString("player-take1", targetName, String.valueOf(takeAmount), mainClass.getMoneyUnit()));
						} else {
							p.sendMessage("システム>> 対象プレイヤーの所持金をマイナスにはできません");
						}
					} else {
						p.sendMessage(TextValues.ALERT + mainClass.translateString("error-command-message4"));
					}
				} else {
					p.sendMessage(TextValues.INFO + mainClass.translateString("player-account-not-found", targetName));
				}
			}
		};
		form.setTitle("しふぉん Wallet - お金を没収");
		form.addFormElements(elements);
		
		NukkitFormAPI.getSingleton().sendFormToPlayer(player, form);
	}
	
	public void sendSetMoneyWindow(Player player) {
		FormElement[] elements = {
				new Label("対象プレイヤーの所持金を設定します(OP権限)"),
				new Input("", "対象プレイヤー名を入力..."),
				new Input("", "設定金額を入力...")
		};
		
		CustomForm form = new CustomForm() {
			@Override
			public void onEnter(Player p, List<Object> response) {
				String targetName = (String) response.get(1);
				int setAmount = 0;
				try {
					setAmount = Integer.parseInt(response.get(2).toString());
				} catch(NumberFormatException e) {
					p.sendMessage("システム>> 不適切な内容が入力されました");
				}
				
				if(mainClass.getSQL().existsAccount(targetName)) {
					if(setAmount > 0) {
						mainClass.getSQL().setMoney(targetName, setAmount);
						if(mainClass.getServer().getPlayer(targetName) != null) {
							mainClass.getServer().getPlayer(targetName).sendMessage(TextValues.WARNING + mainClass.translateString("player-set2", String.valueOf(setAmount), mainClass.getMoneyUnit()));
						}
						p.sendMessage(TextValues.WARNING + mainClass.translateString("player-set1", targetName, String.valueOf(setAmount), mainClass.getMoneyUnit()));
					} else {
						p.sendMessage("システム>> 対象プレイヤーの所持金をマイナスにはできません");
					}
				} else {
					p.sendMessage(TextValues.INFO + mainClass.translateString("player-account-not-found", targetName));
				}
			}
		};
		form.setTitle("しふぉん Wallet - 所持金再設定");
		form.addFormElements(elements);
		
		NukkitFormAPI.getSingleton().sendFormToPlayer(player, form);
	}
}
