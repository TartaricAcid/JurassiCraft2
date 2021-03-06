package org.jurassicraft.server.container.slot;

import net.minecraft.inventory.SlotCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class SkeletonCraftingSlot extends SlotCrafting {
    private final InventoryCrafting craftMatrix;
    /** The player that is using the GUI where this slot resides. */
    private final EntityPlayer thePlayer;
    public SkeletonCraftingSlot(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn,
            int slotIndex, int xPosition, int yPosition) {
        super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
        // TODO Auto-generated constructor stub
        this.thePlayer = player;
        this.craftMatrix = craftingInventory;
    }
    
    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        for(int x = 0;x<craftMatrix.getSizeInventory();x++){
            craftMatrix.setInventorySlotContents(x,null);
        }
    }
}
