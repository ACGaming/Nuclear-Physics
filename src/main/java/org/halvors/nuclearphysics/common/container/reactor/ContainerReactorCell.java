package org.halvors.nuclearphysics.common.container.reactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.halvors.nuclearphysics.common.container.ContainerBase;
import org.halvors.nuclearphysics.common.tile.reactor.TileReactorCell;

public class ContainerReactorCell extends ContainerBase<TileReactorCell> {
    public ContainerReactorCell(InventoryPlayer inventoryPlayer, TileReactorCell tile) {
        super(inventoryPlayer, tile);

        addSlotToContainer(new SlotItemHandler(tile.getInventory(), 0, 79, 17));

        // Players inventory
        addPlayerInventory(inventoryPlayer.player);
    }

    /** Called to transfer a stack from one inventory to the other eg. when shift clicking. */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack copyStack = null;
        Slot slot = inventorySlots.get(slotId);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();

            if (itemStack != null) {
                copyStack = itemStack.copy();

                if (slotId >= slotCount) {
                    if (getSlot(0).isItemValid(itemStack)) {
                        if (!mergeItemStack(itemStack, 0, 1, false)) {
                            return null;
                        }
                    } else if (slotId < 27 + slotCount) {
                        if (!mergeItemStack(itemStack, 27 + slotCount, 36 + slotCount, false)) {
                            return null;
                        }
                    } else if (slotId >= 27 + slotCount && slotId < 36 + slotCount && !mergeItemStack(itemStack, 4, 30, false)) {
                        return null;
                    }
                } else if (!mergeItemStack(itemStack, slotCount, 36 + slotCount, false)) {
                    return null;
                }

                if (itemStack.stackSize == 0) {
                    slot.putStack(null);
                } else {
                    slot.onSlotChanged();
                }

                if (itemStack.stackSize == copyStack.stackSize) {
                    return null;
                }

                slot.onPickupFromSlot(player, itemStack);
            }
        }

        return copyStack;
    }
}
