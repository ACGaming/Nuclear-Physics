package org.halvors.nuclearphysics.common.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.render.particle.ParticleRadioactive;
import org.halvors.nuclearphysics.client.utility.RenderUtility;
import org.halvors.nuclearphysics.common.effect.poison.PoisonRadiation;

import java.util.Random;

public class BlockFluidRadioactive extends BlockFluidClassic {
    public BlockFluidRadioactive(final Fluid fluid, final Material material) {
        super(fluid, material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (Minecraft.getMinecraft().gameSettings.particleSetting == 0) {
            if (random.nextInt(100) == 0) {
                RenderUtility.renderParticle(new ParticleRadioactive(world, pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat(), (random.nextDouble() - 0.5) / 2, (random.nextDouble() - 0.5) / 2, (random.nextDouble() - 0.5) / 2));
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.wither, 3);
            PoisonRadiation.getInstance().poisonEntity(pos, (EntityLivingBase) entity, 4);
        }
    }
}