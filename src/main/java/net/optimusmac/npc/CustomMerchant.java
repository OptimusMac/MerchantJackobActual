package net.optimusmac.npc;

import de.tr7zw.nbtapi.NBTItem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.optimusmac.Merchant;
import net.optimusmac.Utils.ItemUtils;
import net.optimusmac.Utils.messages.Messages;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomMerchant {

    private NPC npc;
    private boolean active;
    private long canGiveItem;
    private final List<Player> playerTakeItems = new ArrayList<>();

    public CustomMerchant() {
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, "npc");
        this.canGiveItem = System.currentTimeMillis() + 1000L;
        this. active = false;
    }


    public List<Player> getPlayerTakeItems() {
        return playerTakeItems;
    }

    public void addTakePlayer(Player player) {
        getPlayerTakeItems().add(player);
    }

    public NPC getNPC() {
        return npc;
    }

    public long getCanGiveItem() {
        return canGiveItem;
    }

    public void giveItemJack(Player player) {

        boolean give = false;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack == null || stack.getType().equals(Material.AIR)) {
                player.getInventory().addItem(ItemUtils.getItemBounty());
                give = true;
                break;
            }
        }
        if (give) {
            addTakePlayer(player);
            setCanGiveItem(System.currentTimeMillis() + (1000L * Merchant.getInstance().getGivePerSecondBountyItem()));
        }
    }

    public boolean canGiveItem() {
        return getCanGiveItem() < System.currentTimeMillis();
    }


    public void setCanGiveItem(long canGiveItem) {
        this.canGiveItem = canGiveItem;
    }


    public boolean isDead() {
        return getNPC().getEntity().isDead();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void startDead() {
        new BukkitRunnable() {

            @Override
            public void run() {
                Merchant.getInstance().getMerchant().getNPC().despawn();
                Merchant.getInstance().setMerchant(new CustomMerchant());
                System.out.println("NPC is dead");
            }
        }.runTaskLater(Merchant.getInstance(), Merchant.getInstance().minutesDead());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (Merchant.getInstance().getMerchant().isDead() || !Merchant.getInstance().getMerchant().isActive()) {
                executor.shutdownNow();
                return;
            }

            getPlayerTakeItems().forEach(e -> {
                int count = getItemBountyPlayer(e);
                int amount = count * Merchant.getInstance().getPricePerBounty();
                Merchant.econ.depositPlayer(e, amount);
                Messages.sendMessageDepositMoney(e, amount, count);
            });
        }, 0, 10, TimeUnit.SECONDS);

    }

    private int getItemBountyPlayer(Player player) {
        int count = 0;
        Material material = ItemUtils.getItemBounty().getType();
        for (ItemStack stack : player.getInventory()) {
            if (stack == null || stack.getType().equals(Material.AIR) || !material.equals(stack.getType())) continue;
            NBTItem nbtItem = new NBTItem(stack);
            if (nbtItem.hasTag("jack")) {
                count += stack.getAmount();
            }
        }
        return count;
    }
}
