package org.halvors.quantum.client.gui.particle;

import net.minecraft.entity.player.InventoryPlayer;
import org.halvors.quantum.common.entity.particle.EntityParticle;
import org.halvors.quantum.common.tile.particle.TileAccelerator;
import org.halvors.quantum.common.container.particle.ContainerAccelerator;
import org.halvors.quantum.common.transform.vector.Vector3;
import org.halvors.quantum.lib.gui.GuiContainerBase;
import universalelectricity.api.energy.UnitDisplay;

public class GuiAccelerator extends GuiContainerBase {
    private TileAccelerator tile;

    public GuiAccelerator(InventoryPlayer inventoryPlayer, TileAccelerator tile) {
        super(new ContainerAccelerator(inventoryPlayer, tile));

        this.tile = tile;
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        fontRendererObj.drawString(tile.getInventoryName(), 40, 10, 4210752);

        String status;
        Vector3 position = new Vector3(tile);
        position.translate(tile.getDirection().getOpposite());

        if (!EntityParticle.canRenderAcceleratedParticle(tile.getWorld(), position)) {
            status = "\u00a74Fail to emit; try rotating.";
        } else if (tile.entityParticle != null && tile.velocity > 0) {
            status = "\u00a76Accelerating";
        } else {
            status = "\u00a72Idle";
        }

        fontRendererObj.drawString("Velocity: " + Math.round((tile.velocity / TileAccelerator.clientParticleVelocity) * 100) + "%", 8, 27, 4210752);
        fontRendererObj.drawString("Energy Used:", 8, 38, 4210752);
        fontRendererObj.drawString(UnitDisplay.getDisplay(tile.totalEnergyConsumed, UnitDisplay.Unit.JOULES), 8, 49, 4210752);
        fontRendererObj.drawString(UnitDisplay.getDisplay(TileAccelerator.energyPerTick * 20, UnitDisplay.Unit.WATT), 8, 60, 4210752);
        fontRendererObj.drawString(UnitDisplay.getDisplay(tile.getVoltageInput(null), UnitDisplay.Unit.VOLTAGE), 8, 70, 4210752);
        fontRendererObj.drawString("Antimatter: " + tile.antimatter + " mg", 8, 80, 4210752);
        fontRendererObj.drawString("Status:", 8, 90, 4210752);
        fontRendererObj.drawString(status, 8, 100, 4210752);
        fontRendererObj.drawString("Buffer: " + UnitDisplay.getDisplayShort(this.tile.getEnergyHandler().getEnergy(), UnitDisplay.Unit.JOULES) + "/" + UnitDisplay.getDisplayShort(tile.getEnergyHandler().getEnergyCapacity(), UnitDisplay.Unit.JOULES), 8, 110, 4210752);
        fontRendererObj.drawString("Facing: " + tile.getDirection().getOpposite(), 100, 123, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

        drawSlot(131, 25);
        drawSlot(131, 50);
        drawSlot(131, 74);
        drawSlot(105, 74);
    }
}