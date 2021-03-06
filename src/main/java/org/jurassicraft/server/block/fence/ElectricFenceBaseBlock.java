package org.jurassicraft.server.block.fence;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jurassicraft.server.block.entity.ElectricFenceBaseBlockEntity;
import org.jurassicraft.server.tab.TabHandler;

public class ElectricFenceBaseBlock extends BlockContainer {
    public static final PropertyBool POLE = PropertyBool.create("pole");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyInteger CONNECTIONS = PropertyInteger.create("connections", 0, 4);

    public ElectricFenceBaseBlock() {
        super(Material.IRON);
        this.setHardness(3.5F);
        this.setCreativeTab(TabHandler.BLOCKS);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(EAST, false));
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new ElectricFenceBaseBlockEntity();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        boolean north = this.canConnect(world.getBlockState(pos.offset(EnumFacing.NORTH)));
        boolean south = this.canConnect(world.getBlockState(pos.offset(EnumFacing.SOUTH)));
        boolean west = this.canConnect(world.getBlockState(pos.offset(EnumFacing.EAST)));
        boolean east = this.canConnect(world.getBlockState(pos.offset(EnumFacing.WEST)));
        int connections = 0;
        if (north) {
            connections++;
        }
        if (south) {
            connections++;
        }
        if (west) {
            connections++;
        }
        if (east) {
            connections++;
        }
        boolean pole = world.getBlockState(pos.up()).getBlock() instanceof ElectricFencePoleBlock;
        return state.withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(EAST, east).withProperty(POLE, pole).withProperty(CONNECTIONS, connections);
    }

    protected boolean canConnect(IBlockState state) {
        return state.getBlock() instanceof ElectricFenceBaseBlock;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, SOUTH, EAST, WEST, POLE, CONNECTIONS);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getActualState(world, pos).getValue(POLE) ? 3 : super.getLightOpacity(state, world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        if (!world.isRemote) {
            IBlockState poleState = world.getBlockState(pos.up());
            if (poleState.getBlock() instanceof ElectricFencePoleBlock) {
                ((ElectricFencePoleBlock) poleState.getBlock()).updateConnectedWires(world, pos.up());
            }
        }
    }
}
