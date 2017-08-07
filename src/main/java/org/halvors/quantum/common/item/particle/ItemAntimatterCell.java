package org.halvors.quantum.common.item.particle;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.quantum.common.Quantum;
import org.halvors.quantum.common.item.ItemTooltip;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemAntimatterCell extends ItemTooltip {
    public ItemAntimatterCell() {
        super("antimatter_cell");

        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void registerItemModel() {
        for (EnumAntimatterCell type : EnumAntimatterCell.values()) {
            Quantum.getProxy().registerItemRenderer(this, type.ordinal(), name + "_" + type.getName());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        for (EnumAntimatterCell type : EnumAntimatterCell.values()) {
            items.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 160;
    }

    public enum EnumAntimatterCell {
        MILLIGRAM("milligram"),
        GRAM("gram");

        private String name;

        EnumAntimatterCell(String name) {
            this.name = name;
        }

        public String getName() {
            return name.toLowerCase();
        }
    }
}
