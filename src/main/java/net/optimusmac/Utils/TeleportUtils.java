package net.optimusmac.Utils;

import net.citizensnpcs.api.npc.NPC;
import net.optimusmac.Merchant;
import net.optimusmac.Utils.Color.IridiumColorAPI;
import net.optimusmac.Utils.messages.Messages;
import net.optimusmac.npc.CustomMerchant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;

public class TeleportUtils {

    private NPC npc;

    public TeleportUtils() {
        this.npc = Merchant.getInstance().getMerchant().getNPC();
        this.npc.setName(Merchant.getInstance().getNameNPC());
    }


    public void safeTeleport(String worldName) {
        npc.spawn(randomLocation(worldName));
    }

    public Location randomLocation(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            System.out.println("Invalid world name: " + worldName);
            return null;
        }
        int worldBorderSize = (int) world.getWorldBorder().getSize() / 2;
        int x = (int) (Math.random() * (worldBorderSize - 10) * 2) - worldBorderSize;
        int z = (int) (Math.random() * (worldBorderSize - 10) * 2) - worldBorderSize;
        int y = world.getHighestBlockYAt(x, z);
        Location location = new Location(world, x, y, z);
        Block block = location.getBlock();
        if (block.getType() != Material.AIR) {
            int minY = Math.max(y - 10, 0);
            int maxY = Math.min(y + 10, 255);
            for (int i = minY; i < maxY; i++) {
                block = world.getBlockAt(x, i, z);
                if (block.getType() == Material.AIR) {
                    location.setY(i);
                    break;
                }
            }
        }


        Merchant.getInstance().getMerchant().setActive(true);
        Messages.writeAll(String.format(Merchant.getInstance().getMessageAll(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), Merchant.getInstance().getNameNPC()));
        if (!location.clone().add(0,-2,0).getBlock().isSolid() && location.getBlock().isLiquid()) {
            return replaceWaterWithDirt(location, npc);
        }
        return location;
    }

    public Location replaceWaterWithDirt(Location loc, NPC npc) {
        int radius = 20;

        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                for (int k = z - radius; k <= z + radius; k++) {
                    Block block = world.getBlockAt(i, j, k);
                    if (block.getType() == Material.WATER || !block.isSolid()) {
                        block.setType(Material.DIRT);
                    }
                }
            }
        }
        Location location = loc.clone();
        location.setY(loc.getWorld().getHighestBlockYAt(x,z));
        npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        return location;
    }
}
