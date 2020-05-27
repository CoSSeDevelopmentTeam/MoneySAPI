package net.comorevi.moneyapi;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.mob.*;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.comorevi.moneyapi.util.ConfigManager;
import net.comorevi.moneyapi.util.MessageManager;
import net.comorevi.moneyapi.util.TextValues;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EventListener implements Listener{
    private final MessageManager message = MessageManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!MoneySAPI.getInstance().existsAccount(event.getPlayer())){
        	MoneySAPI.getInstance().registerAccount(event.getPlayer());
        	event.getPlayer().sendMessage(TextValues.INFO + message.translateString("player-account-add", event.getPlayer().getName()));
        }else{
            event.getPlayer().sendMessage(TextValues.INFO + message.translateString("player-account-load", event.getPlayer().getName(), String.valueOf(MoneySAPI.getInstance().getMoney(event.getPlayer())), MoneySAPI.UNIT));
        }

        if (ConfigManager.getInstance().enabledShowRank()) {
            MoneySystemPlugin.getInstance().showMoneyRank(event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (ConfigManager.getInstance().getIgnoreWorlds().contains(event.getPlayer().getLevel().getName())) return;
        if (EnumBlocks.getIds().contains(event.getBlock().getId())) {
            MoneySAPI.getInstance().addCoin(event.getPlayer(), EnumBlocks.getById(event.getBlock().getId()).getCoin());
        } else {
            MoneySAPI.getInstance().addCoin(event.getPlayer(), 200);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (ConfigManager.getInstance().getIgnoreWorlds().contains(event.getEntity().getLevel().getName())) return;
        if (EnumMobs.getIds().contains(event.getEntity().getNetworkId())) {
            if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
                if (event2.getDamager() instanceof Player) {
                    Player p = (Player) event2.getDamager();
                    MoneySAPI.getInstance().addCoin(p, EnumMobs.getById(event.getEntity().getNetworkId()).getCoin());
                }
            }
        }
    }

    public enum EnumMobs {
        ZOMBIE(EntityZombie.NETWORK_ID, 800),
        HUSK(EntityHusk.NETWORK_ID, 850),
        DROWNED(EntityDrowned.NETWORK_ID, 850),
        SKELETON(EntitySkeleton.NETWORK_ID, 800),
        CREEPER(EntityCreeper.NETWORK_ID, 1000),
        SPIDER(EntitySpider.NETWORK_ID, 800);

        private final int id;
        private final int coin;

        EnumMobs(int id, int coin) {
            this.id = id;
            this.coin = coin;
        }

        public int getId() {
            return id;
        }

        public int getCoin() {
            return coin;
        }

        public static EnumMobs getById(int id) {
            for (EnumMobs type : EnumMobs.values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No such enum object for the id: " + id);
        }

        public static List<Integer> getIds() {
            LinkedList<Integer> list = new LinkedList<>();
            Arrays.asList(EnumMobs.values()).forEach(enumMobs -> list.add(enumMobs.getId()));
            return list;
        }
    }

    public enum EnumBlocks {
        STONE(BlockID.STONE, 200),
        GRASS(BlockID.GRASS, 400),
        DIRT(BlockID.DIRT, 200),
        SAND(BlockID.SAND, 200),
        SANDSTONE(BlockID.SANDSTONE, 300),
        WOOD(BlockID.WOOD, 600),
        WOOD2(BlockID.WOOD2, 600),
        LEAVE(BlockID.LEAVE, 500),
        LEAVE2(BlockID.LEAVE2, 500),
        DIAMOND_ORE(BlockID.DIAMOND_ORE, 1000),
        GOLD_ORE(BlockID.GOLD_ORE, 800),
        IRON_ORE(BlockID.IRON_ORE, 700),
        COAL_ORE(BlockID.COAL_ORE, 700),
        LAPIS_ORE(BlockID.LAPIS_ORE, 600),
        REDSTONE_ORE(BlockID.REDSTONE_ORE, 600),
        EMERALD_ORE(BlockID.EMERALD_ORE, 800);

        private final int id;
        private final int coin;

        EnumBlocks(int id, int coin) {
            this.id = id;
            this.coin = coin;
        }

        public int getId() {
            return id;
        }

        public int getCoin() {
            return coin;
        }

        public static EnumBlocks getById(int id) {
            for (EnumBlocks type : EnumBlocks.values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No such enum object for the id: " + id);
        }

        public static List<Integer> getIds() {
            LinkedList<Integer> list = new LinkedList<>();
            Arrays.asList(EnumBlocks.values()).forEach(enumBlocks -> list.add(enumBlocks.getId()));
            return list;
        }
    }
}