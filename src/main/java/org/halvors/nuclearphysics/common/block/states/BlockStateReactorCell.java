package org.halvors.nuclearphysics.common.block.states;

import net.minecraft.block.properties.PropertyEnum;
import org.halvors.nuclearphysics.common.block.reactor.fission.BlockReactorCell;
import org.halvors.nuclearphysics.common.block.reactor.fission.BlockReactorCell.EnumReactorCell;

public class BlockStateReactorCell extends BlockStateFacing {
    public static final PropertyEnum<EnumReactorCell> TYPE = PropertyEnum.create("type", EnumReactorCell.class);

    public BlockStateReactorCell(BlockReactorCell block) {
        super(block, TYPE);
    }
}