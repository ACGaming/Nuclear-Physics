package org.halvors.quantum.client.gui.machine.old;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.halvors.quantum.client.gui.component.*;
import org.halvors.quantum.common.base.tile.ITileOwnable;
import org.halvors.quantum.common.tile.machine.old.TileEntityElectricMachine;
import org.halvors.quantum.common.utility.LanguageUtility;
import org.halvors.quantum.common.utility.energy.EnergyUtils;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiElectricMachine extends GuiMachine {
	protected GuiElectricMachine(final TileEntityElectricMachine tileEntity) {
		super(tileEntity);

		if (tileEntity instanceof ITileOwnable) {
			final ITileOwnable tileOwnable = (ITileOwnable) tileEntity;

			components.add(new GuiOwnerInfo(new IInfoHandler() {
				@Override
				public List<String> getInfo() {
					List<String> list = new ArrayList<>();
					list.add(tileOwnable.getOwnerName());

					return list;
				}
			}, this, defaultResource));
		}

		components.add(new GuiEnergyInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo() {
				List<String> list = new ArrayList<>();
				list.add(LanguageUtility.localize("gui.stored") + ": " + EnergyUtils.getEnergyDisplay(tileEntity.getStorage().getEnergyStored()));
				list.add(LanguageUtility.localize("gui.maxOutput") + ": " + EnergyUtils.getEnergyDisplay(tileEntity.getStorage().getMaxExtract()) + "/t");

				return list;
			}
		}, this, defaultResource));
		components.add(new GuiEnergyUnitType(this, defaultResource));
		components.add(new GuiRedstoneControl<>(this, tileEntity, defaultResource));
	}
}