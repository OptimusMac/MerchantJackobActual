package net.optimusmac.handlers;

import de.tr7zw.nbtapi.NBTItem;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.optimusmac.Merchant;
import net.optimusmac.npc.CustomMerchant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ListenersNPC implements Listener {

    @EventHandler
    public void onBreakBlock(EntityDamageEvent e) {
       e.setCancelled(e.getEntity().equals(
           Merchant.getInstance().getMerchant().getNPC().getEntity()
       ));
    }

    @Deprecated
    @EventHandler
    public void onPickUP(PlayerPickupItemEvent e) {
        NBTItem nbtItem = new NBTItem(e.getItem().getItemStack());
        if(nbtItem.hasTag("jack")){
            Merchant.getInstance().getMerchant().addTakePlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onRightClickNPC(NPCRightClickEvent e){
        if(e.getNPC().equals(Merchant.getInstance().getMerchant().getNPC())){
            System.out.println("click");
            CustomMerchant customMerchant = Merchant.getInstance().getMerchant();;
            if(customMerchant.canGiveItem()){
                customMerchant.giveItemJack(e.getClicker());
            }
        }
    }

}
