package org.halvors.nuclearphysics.common.block.debug.schematic;

import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import org.halvors.nuclearphysics.common.init.ModBlocks;
import org.halvors.nuclearphysics.common.type.Position;

import java.util.HashMap;

public class SchematicFissionReactor implements ISchematic {
    @Override
    public String getName() {
        return "schematic.fission_reactor.name";
    }

    @Override
    public HashMap<BlockPos, IBlockState> getStructure(EnumFacing facing, int size) {
        HashMap<BlockPos, IBlockState> map = new HashMap<>();
        int radius = 2;

        // We do not support high reactor towers yet. Forcing height.
        size = 2;

        for (int y = 0; y < size; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = new BlockPos(x, y, z);
                    Position targetPosition = new Position(targetPos);
                    Position leveledPosition = new Position(0, y, 0);

                    if (y < size - 1) {
                        if (targetPosition.distance(leveledPosition) == 2) {
                            map.put(targetPos, ModBlocks.blockControlRod.getDefaultState());

                            // Place piston base to push control rods in.
                            Position offset = new Position(x, 0, z).normalize();

                            for (EnumFacing side : EnumFacing.values()) {
                                if (offset.getX() == side.getFrontOffsetX() && offset.getY() == side.getFrontOffsetY() && offset.getZ() == side.getFrontOffsetZ()) {
                                    facing = side.getOpposite();
                                }
                            }

                            BlockPos pos = targetPosition.translate(offset).getPos();
                            map.put(pos, Blocks.STICKY_PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, facing));
                            map.put(pos.offset(facing.getOpposite()), Blocks.LEVER.getDefaultState().withProperty(BlockLever.FACING, (facing.getAxis() == Axis.X ? EnumOrientation.UP_X : EnumOrientation.UP_Z)));
                        } else if (x == -radius || x == radius || z == -radius || z == radius) {
                            map.put(targetPos, Blocks.GLASS.getDefaultState());
                        } else if (x == 0 && z == 0) {
                            map.put(targetPos, ModBlocks.blockReactorCell.getDefaultState());
                        } else {
                            map.put(targetPos, Blocks.WATER.getDefaultState());
                        }
                    } else if (targetPosition.distance(leveledPosition) < 2) {
                        map.put(targetPos, ModBlocks.blockElectricTurbine.getDefaultState());

                    }
                }
            }
        }

        return map;
    }
}