package org.halvors.quantum.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import org.halvors.quantum.Quantum;
import org.halvors.quantum.common.ConfigurationManager;
import org.halvors.quantum.lib.IRotatable;
import universalelectricity.api.electricity.IVoltageInput;
import universalelectricity.api.energy.EnergyStorageHandler;

/** Chemical extractor TileEntity */
public class TileChemicalExtractor extends TileProcess implements ISidedInventory, IFluidHandler, IRotatable, IVoltageInput {
    public static final int TICK_TIME = 20 * 14;
    public static final int EXTRACT_SPEED = 100;
    public static final long ENERGY = 5000;

    public final FluidTank inputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);
    public final FluidTank outputTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);

    // How many ticks has this item been extracting for?
    public int time = 0;
    public float rotation = 0;

    public TileChemicalExtractor() {
        energy = new EnergyStorageHandler(ENERGY * 2);
        maxSlots = 7;
        inputSlot = 1;
        outputSlot = 2;
        tankInputFillSlot = 3;
        tankInputDrainSlot = 4;
        tankOutputFillSlot = 5;
        tankOutputDrainSlot = 6;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (time > 0) {
            rotation += 0.2f;
        }

        if (!worldObj.isRemote) {
            if (canUse()) {
                discharge(getStackInSlot(0));

                if (energy.checkExtract(ENERGY)) {
                    if (time == 0) {
                        time = TICK_TIME;
                    }

                    if (time > 0) {
                        time--;

                        if (time < 1) {
                            if (!refineUranium()) {
                                if (!extractTritium()) {
                                    extractDeuterium();
                                }
                            }

                            time = 0;
                        }
                    } else {
                        time = 0;
                    }
                }

                energy.extractEnergy(ENERGY, true);
            } else {
                time = 0;
            }

            if (ticks % 10 == 0) {
                for (EntityPlayer player : getPlayersUsing()) {
                    // TODO: Fix this.
                    //PacketDispatcher.sendPacketToPlayer(getDescriptionPacket(), (EntityPlayer) player);
                }
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        // TODO: Fox this.
        //return ResonantInduction.PACKET_ANNOTATION.getPacket(this);

        return null;
    }

    public boolean canUse() {
        if (inputTank.getFluid() != null) {
            if (inputTank.getFluid().amount >= FluidContainerRegistry.BUCKET_VOLUME && getStackInSlot(inputSlot).getItem() == new ItemBlock(Quantum.blockUraniumOre)) {
                if (isItemValidForSlot(outputSlot, new ItemStack(Quantum.itemYellowCake))) {
                    return true;
                }
            }

            if (outputTank.getFluidAmount() < outputTank.getCapacity()) {
                if (inputTank.getFluid().getFluid() == Quantum.fluidDeuterium && inputTank.getFluid().amount >= ConfigurationManager.General.deutermiumPerTritium * EXTRACT_SPEED) {
                    if (outputTank.getFluid() == null || Quantum.fluidStackTritium.equals(outputTank.getFluid())) {
                        return true;
                    }
                }

                if (inputTank.getFluid().getFluid().getID() == FluidRegistry.WATER.getID() && inputTank.getFluid().amount >= ConfigurationManager.General.waterPerDeutermium * EXTRACT_SPEED) {
                    if (outputTank.getFluid() == null || Quantum.fluidDeuterium.equals(outputTank.getFluid())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /** Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack. */
    public boolean refineUranium() {
        if (canUse()) {
            if (getStackInSlot(inputSlot).getItem() == new ItemBlock(Quantum.blockUraniumOre)) {
                inputTank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
                incrStackSize(outputSlot, new ItemStack(Quantum.itemYellowCake, 3));
                decrStackSize(inputSlot, 1);

                return true;
            }
        }

        return false;
    }

    public boolean extractDeuterium() {
        if (canUse()) {
            FluidStack drain = inputTank.drain(ConfigurationManager.General.waterPerDeutermium * EXTRACT_SPEED, false);

            if (drain != null && drain.amount >= 1 && drain.getFluid().getID() == FluidRegistry.WATER.getID()) {
                if (outputTank.fill(new FluidStack(Quantum.fluidStackDeuterium, EXTRACT_SPEED), true) >= EXTRACT_SPEED) {
                    inputTank.drain(ConfigurationManager.General.waterPerDeutermium * EXTRACT_SPEED, true);

                    return true;
                }
            }
        }

        return false;
    }

    public boolean extractTritium() {
        if (canUse()) {
            int waterUsage = ConfigurationManager.General.deutermiumPerTritium;

            FluidStack drain = inputTank.drain(ConfigurationManager.General.deutermiumPerTritium * EXTRACT_SPEED, false);

            if (drain != null && drain.amount >= 1 && drain.getFluid() == Quantum.fluidStackDeuterium.getFluid()) {
                if (outputTank.fill(new FluidStack(Quantum.fluidStackTritium, EXTRACT_SPEED), true) >= EXTRACT_SPEED) {
                    inputTank.drain(ConfigurationManager.General.deutermiumPerTritium * EXTRACT_SPEED, true);
                    return true;
                }
            }
        }

        return false;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        time = nbt.getInteger("time");
        NBTTagCompound water = nbt.getCompoundTag("inputTank");
        inputTank.setFluid(FluidStack.loadFluidStackFromNBT(water));
        NBTTagCompound deuterium = nbt.getCompoundTag("outputTank");
        outputTank.setFluid(FluidStack.loadFluidStackFromNBT(deuterium));
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("time", time);

        if (inputTank.getFluid() != null) {
            NBTTagCompound compound = new NBTTagCompound();
            inputTank.getFluid().writeToNBT(compound);
            nbt.setTag("inputTank", compound);
        }

        if (outputTank.getFluid() != null) {
            NBTTagCompound compound = new NBTTagCompound();
            outputTank.getFluid().writeToNBT(compound);
            nbt.setTag("outputTank", compound);
        }
    }

    /** Tank Methods */
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource != null && canFill(from, resource.getFluid())) {
            return inputTank.fill(resource, doFill);
        }

        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return outputTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return FluidRegistry.WATER == fluid || Quantum.fluidDeuterium == fluid;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return outputTank.getFluid() != null && outputTank.getFluid().getFluid().getID() == fluid.getID();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { inputTank.getInfo(), outputTank.getInfo() };
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        // Water input for machine.
        if (slot == 0) {
            // TODO: Fix this.
            //return CompatibilityModule.isHandler(itemStack.getItem());
        }

        if (slot == 1) {
            return itemStack.getItem() == Quantum.itemWaterCell;
        }

        // Empty cell to be filled with deuterium or tritium.
        if (slot == 2) {
            return itemStack.getItem() == Quantum.itemDeuteriumCell || itemStack.getItem() == Quantum.itemTritiumCell;
        }

        // Uranium to be extracted into yellowcake.
        if (slot == 3) {
            return itemStack.getItem() == Quantum.itemCell || itemStack.getItem() == new ItemBlock(Quantum.blockUraniumOre) || itemStack.getItem() == Quantum.itemDeuteriumCell;
        }

        return false;
    }

    @Override
    public int[] getSlotsForFace(int face) {
        return new int[] { 1, 2, 3 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return this.isItemValidForSlot(slot, itemStack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        return slot == 2;
    }

    @Override
    public long onExtractEnergy(ForgeDirection from, long extract, boolean doExtract) {
        return 0;
    }

    @Override
    public long onReceiveEnergy(ForgeDirection from, long receive, boolean doReceive) {
        if (canUse()) {
            return super.onReceiveEnergy(from, receive, doReceive);
        } else {
            return 0;
        }
    }

    @Override
    public long getVoltageInput(ForgeDirection from) {
        return 1000;
    }

    @Override
    public void onWrongVoltage(ForgeDirection direction, long voltage) {

    }

    @Override
    public FluidTank getInputTank() {
        return inputTank;
    }

    @Override
    public FluidTank getOutputTank() {
        return outputTank;
    }
}
