package net.optimusmac;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.milkbowl.vault.economy.Economy;
import net.optimusmac.Utils.Color.IridiumColorAPI;
import net.optimusmac.Utils.TeleportUtils;
import net.optimusmac.handlers.ListenersNPC;
import net.optimusmac.npc.CustomMerchant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class Merchant extends JavaPlugin {

    private static Merchant instance;
    private double chanceSpawn;
    private CustomMerchant merchant;
    private String worldName;
    private String messageAll;
    private int minutesFromDead;
    private String materialBounty;
    private int givePerSecondBountyItem;
    private int pricePerBounty;
    private String messageDepositMoney;
    private int worldBorder;

    public static Economy econ;

    @Override
    public void onEnable() {
        instance = this;
        merchant = new CustomMerchant();
        saveDefaultConfig();
        setupEconomy();
        Bukkit.getPluginManager().registerEvents(new ListenersNPC(), this);
        if (getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
        }
        load();
        start();
        Objects.requireNonNull(Bukkit.getWorld(worldName)).getWorldBorder().setSize(worldBorder);

    }


    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }
        return econ != null;
    }

    public String getNameNPC() {
        return IridiumColorAPI.process("<GRADIENT:ff0000>Зажиточный Джейкоб</GRADIENT:ffff00>");
    }


    public int getPricePerBounty() {
        return pricePerBounty;
    }

    private void load() {
        chanceSpawn = getInstance().getConfig().getDouble("SettingsNPC.chanceSpawnPerSecond", 0.2f);
        minutesFromDead = getInstance().getConfig().getInt("SettingsNPC.minutesFromDead", 20);
        pricePerBounty = getInstance().getConfig().getInt("SettingsNPC.pricePerBounty", 1000);
        worldBorder = getInstance().getConfig().getInt("SettingsNPC.worldBorder", 5000);
        givePerSecondBountyItem = getInstance().getConfig().getInt("SettingsNPC.givePerSecondBountyItem", 30);
        worldName = getInstance().getConfig().getString("SettingsNPC.onlyWorld", "world");
        messageDepositMoney = getInstance().getConfig().getString("SettingsNPC.messageDepositMoney");
        messageAll = getInstance().getConfig().getString("SettingsNPC.allMessage");
        materialBounty = getInstance().getConfig().getString("SettingsNPC.itemBounty.material", "gold_ingot").toUpperCase();

    }

    public String getMessageDepositMoney() {
        return messageDepositMoney;
    }

    public int getGivePerSecondBountyItem() {
        return givePerSecondBountyItem;
    }

    public String getMaterialBounty() {
        return materialBounty;
    }

    public Material MaterialBounty() {
        return Material.getMaterial(getMaterialBounty()) == null ? Material.GOLD_INGOT : Material.getMaterial(getMaterialBounty());
    }

    public String getMessageAll() {
        return messageAll;
    }

    private int getMinutes() {
        return minutesFromDead;
    }

    public CustomMerchant getMerchant() {
        return merchant;
    }

    public int minutesDead() {
        return (20 * 60) * getMinutes();
    }

    @Override
    public void onDisable() {
        if (merchant.getNPC().isSpawned())
            merchant.getNPC().destroy();
    }


    public static Merchant getInstance() {
        return instance;
    }


    private void start() {
        new BukkitRunnable() {


            @Override
            public void run() {
                double randomValue = Math.random() * 100.0;
                if (merchant.isActive() || Bukkit.getOnlinePlayers().size() == 0) return;
                if (randomValue <= chanceSpawn) {
                    Merchant.getInstance().getMerchant().setActive(true);
                    TeleportUtils teleportUtils = new TeleportUtils();
                    teleportUtils.safeTeleport(worldName);
                    merchant.startDead();
                }
            }
        }.runTaskTimer(this, 0L, 20L);

    }


    public void setMerchant(CustomMerchant merchant) {
        this.merchant = merchant;
    }
}

