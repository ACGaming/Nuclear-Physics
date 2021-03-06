package org.halvors.nuclearphysics.client.gui.particle;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.GuiMachine;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot.SlotType;
import org.halvors.nuclearphysics.common.container.particle.ContainerQuantumAssembler;
import org.halvors.nuclearphysics.common.tile.particle.TileQuantumAssembler;
import org.halvors.nuclearphysics.common.type.Resource;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;
import org.halvors.nuclearphysics.common.utility.ResourceUtility;

@SideOnly(Side.CLIENT)
public class GuiQuantumAssembler extends GuiMachine<TileQuantumAssembler> {
    public GuiQuantumAssembler(InventoryPlayer inventoryPlayer, TileQuantumAssembler tile) {
        super(tile, new ContainerQuantumAssembler(inventoryPlayer, tile));

        defaultResource = ResourceUtility.getResource(Resource.GUI, "quantum_assembler.png");
        ySize = 230;
        titleOffset = -7;

        components.add(new GuiSlot(SlotType.NORMAL, this, 79, 39));
        components.add(new GuiSlot(SlotType.NORMAL, this, 52, 55));
        components.add(new GuiSlot(SlotType.NORMAL, this, 106, 55));
        components.add(new GuiSlot(SlotType.NORMAL, this, 52, 87));
        components.add(new GuiSlot(SlotType.NORMAL, this, 106, 87));
        components.add(new GuiSlot(SlotType.NORMAL, this, 79, 102));
        components.add(new GuiSlot(SlotType.NORMAL, this, 79, 71));
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String displayText;

        if (tile.getOperatingTicks() > 0) {
            displayText = LanguageUtility.transelate("gui.process") + ": " + (int) (((float) tile.getOperatingTicks() / (float) TileQuantumAssembler.ticksRequired) * 100) + "%";
        } else if (tile.canProcess()) {
            displayText = LanguageUtility.transelate("gui.ready");
        } else {
            displayText = LanguageUtility.transelate("gui.idle");
        }

        fontRendererObj.drawString(displayText, (xSize / 2) - 80, ySize - 106, 0x404040);

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}