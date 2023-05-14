package net.optimusmac.Utils;

import de.tr7zw.nbtapi.NBTItem;
import net.optimusmac.Merchant;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {


    public static ItemStack getItemBounty(){
        ItemStack itemStack = new ItemStack(Merchant.getInstance().MaterialBounty());
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.addCompound("jack");
        return nbtItem.getItem();
    }

}
