package org.jurassicraft.server.block.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.api.IncubatorEnvironmentItem;
import org.jurassicraft.server.container.IncubatorContainer;
import org.jurassicraft.server.item.DinosaurEggItem;
import org.jurassicraft.server.item.ItemHandler;

public class IncubatorBlockEntity extends MachineBaseBlockEntity {
    private static final int[] INPUTS = new int[] { 0, 1, 2, 3, 4 };
    private static final int[] ENVIRONMENT = new int[] { 5 };

    private int[] temperature = new int[5];

    private ItemStack[] slots = new ItemStack[6];

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        for (int i = 0; i < this.getProcessCount(); i++) {
            this.temperature[i] = compound.getShort("Temperature" + i);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        for (int i = 0; i < this.getProcessCount(); i++) {
            compound.setShort("Temperature" + i, (short) this.temperature[i]);
        }

        return compound;
    }

    @Override
    protected int getProcess(int slot) {
        if (slot == 5) {
            return -1;
        } else {
            return slot;
        }
    }

    @Override
    protected boolean canProcess(int process) {
        ItemStack environment = this.slots[5];
        boolean hasEnvironment = false;

        if (environment != null) {
            Item item = environment.getItem();

            if (item instanceof IncubatorEnvironmentItem || Block.getBlockFromItem(item) instanceof IncubatorEnvironmentItem) {
                hasEnvironment = true;
            }
        }

        return hasEnvironment && this.slots[process] != null && this.slots[process].stackSize > 0 && this.slots[process].getItem() instanceof DinosaurEggItem;
    }

    @Override
    protected void processItem(int process) {
        if (this.canProcess(process) && !this.world.isRemote) {
            ItemStack egg = this.slots[process];

            ItemStack incubatedEgg = new ItemStack(ItemHandler.HATCHED_EGG, 1, egg.getItemDamage());
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("Gender", this.temperature[process] > 50);

            if (egg.getTagCompound() != null) {
                compound.setString("Genetics", egg.getTagCompound().getString("Genetics"));
                compound.setInteger("DNAQuality", egg.getTagCompound().getInteger("DNAQuality"));
            }

            incubatedEgg.setTagCompound(compound);

            this.decreaseStackSize(5);

            this.slots[process] = incubatedEgg;
        }
    }

    @Override
    protected int getMainOutput(int process) {
        return 0;
    }

    @Override
    protected int getStackProcessTime(ItemStack stack) {
        return 8000;
    }

    @Override
    protected int getProcessCount() {
        return 5;
    }

    @Override
    protected int[] getInputs() {
        return INPUTS;
    }

    @Override
    protected int[] getInputs(int process) {
        return new int[] { process };
    }

    @Override
    protected int[] getOutputs() {
        return ENVIRONMENT;
    }

    @Override
    protected ItemStack[] getSlots() {
        return this.slots;
    }

    @Override
    protected void setSlots(ItemStack[] slots) {
        this.slots = slots;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new IncubatorContainer(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return JurassiCraft.MODID + ":incubator";
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.incubator";
    }

    @Override
    public int getField(int id) {
        if (id < 5) {
            return this.processTime[id];
        } else if (id < 10) {
            return this.totalProcessTime[id - 5];
        } else if (id < 15) {
            return this.temperature[id - 10];
        }

        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id < 5) {
            this.processTime[id] = value;
        } else if (id < 10) {
            this.totalProcessTime[id - 5] = value;
        } else if (id < 15) {
            this.temperature[id - 10] = value;
        }
    }

    @Override
    protected boolean shouldResetProgress() {
        return false;
    }
}
