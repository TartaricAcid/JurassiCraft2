package org.jurassicraft.server.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.achievements.AchievementHandler;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.entity.BugCrateBlockEntity;
import org.jurassicraft.server.block.entity.CleaningStationBlockEntity;
import org.jurassicraft.server.block.entity.CultivatorBlockEntity;
import org.jurassicraft.server.block.entity.DNACombinatorHybridizerBlockEntity;
import org.jurassicraft.server.block.entity.DNAExtractorBlockEntity;
import org.jurassicraft.server.block.entity.DNASequencerBlockEntity;
import org.jurassicraft.server.block.entity.DNASynthesizerBlockEntity;
import org.jurassicraft.server.block.entity.EmbryoCalcificationMachineBlockEntity;
import org.jurassicraft.server.block.entity.EmbryonicMachineBlockEntity;
import org.jurassicraft.server.block.entity.FeederBlockEntity;
import org.jurassicraft.server.block.entity.FossilGrinderBlockEntity;
import org.jurassicraft.server.block.entity.IncubatorBlockEntity;
import org.jurassicraft.server.command.KeyBindingHandler;
import org.jurassicraft.server.container.CleaningStationContainer;
import org.jurassicraft.server.container.CultivateContainer;
import org.jurassicraft.server.container.DNACombinatorHybridizerContainer;
import org.jurassicraft.server.container.DNAExtractorContainer;
import org.jurassicraft.server.container.DNASequencerContainer;
import org.jurassicraft.server.container.DNASynthesizerContainer;
import org.jurassicraft.server.container.EmbryoCalcificationMachineContainer;
import org.jurassicraft.server.container.EmbryonicMachineContainer;
import org.jurassicraft.server.container.FeederContainer;
import org.jurassicraft.server.container.FossilGrinderContainer;
import org.jurassicraft.server.container.IncubatorContainer;
import org.jurassicraft.server.container.SkeletonAssemblyContainer;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.DinosaurSerializers;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.villager.VillagerHandler;
import org.jurassicraft.server.event.ServerEventHandler;
import org.jurassicraft.server.food.FoodHelper;
import org.jurassicraft.server.genetics.StorageTypeRegistry;
import org.jurassicraft.server.item.FossilItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.plant.PlantHandler;
import org.jurassicraft.server.recipe.RecipeHandler;
import org.jurassicraft.server.world.WorldGenerator;
import org.jurassicraft.server.world.structure.StructureGenerationHandler;

public class ServerProxy implements IGuiHandler {
    public static final int GUI_CLEANING_STATION_ID = 0;
    public static final int GUI_FOSSIL_GRINDER_ID = 1;
    public static final int GUI_DNA_SEQUENCER_ID = 2;
    public static final int GUI_EMBRYONIC_MACHINE_ID = 3;
    public static final int GUI_EMBRYO_CALCIFICATION_MACHINE_ID = 4;
    public static final int GUI_DNA_SYNTHESIZER_ID = 5;
    public static final int GUI_INCUBATOR_ID = 6;
    public static final int GUI_DNA_COMBINATOR_HYBRIDIZER_ID = 7;
    public static final int GUI_DNA_EXTRACTOR_ID = 8;
    public static final int GUI_CULTIVATOR_ID = 9;
    public static final int GUI_FEEDER_ID = 10;
    public static final int GUI_BUG_CRATE = 11;
    public static final int GUI_SKELETON_ASSEMBLER = 12;

    public void onPreInit(FMLPreInitializationEvent event) {
        EntityHandler.init();
        DinosaurSerializers.register();

        FossilItem.init();

        PlantHandler.init();
        BlockHandler.init();
        ItemHandler.init();
        RecipeHandler.init();
        AchievementHandler.init();
        StorageTypeRegistry.init();
        KeyBindingHandler.init();
        VillagerHandler.init();

        StructureGenerationHandler.register();

        GameRegistry.registerWorldGenerator(WorldGenerator.INSTANCE, 0);

        NetworkRegistry.INSTANCE.registerGuiHandler(JurassiCraft.INSTANCE, this);

        ServerEventHandler eventHandler = new ServerEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    public void onPostInit(FMLPostInitializationEvent event) {
        FoodHelper.init();
    }

    public void onInit(FMLInitializationEvent event) {

    }

    public EntityPlayer getPlayer() {
        return null;
    }

    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            switch(id) {
                case GUI_CLEANING_STATION_ID:
                    if (tile instanceof CleaningStationBlockEntity)
                        return new CleaningStationContainer(player.inventory, (CleaningStationBlockEntity) tile);
                    break;
                case GUI_FOSSIL_GRINDER_ID:
                    if (tile instanceof FossilGrinderBlockEntity)
                        return new FossilGrinderContainer(player.inventory, tile);
                    break;
                case GUI_DNA_SEQUENCER_ID:
                    if (tile instanceof DNASequencerBlockEntity)
                        return new DNASequencerContainer(player.inventory, tile);
                    break;
                case GUI_EMBRYONIC_MACHINE_ID:
                    if (tile instanceof EmbryonicMachineBlockEntity)
                        return new EmbryonicMachineContainer(player.inventory, tile);
                    break;
                case GUI_EMBRYO_CALCIFICATION_MACHINE_ID:
                    if (tile instanceof EmbryoCalcificationMachineBlockEntity)
                        return new EmbryoCalcificationMachineContainer(player.inventory, tile);
                    break;
                case GUI_DNA_SYNTHESIZER_ID:
                    if (tile instanceof DNASynthesizerBlockEntity)
                        return new DNASynthesizerContainer(player.inventory, tile);
                    break;
                case GUI_INCUBATOR_ID:
                    if (tile instanceof IncubatorBlockEntity)
                        return new IncubatorContainer(player.inventory, tile);
                    break;
                case GUI_DNA_COMBINATOR_HYBRIDIZER_ID:
                    if (tile instanceof DNACombinatorHybridizerBlockEntity)
                        return new DNACombinatorHybridizerContainer(player.inventory, tile);
                    break;
                case GUI_DNA_EXTRACTOR_ID:
                    if (tile instanceof DNAExtractorBlockEntity)
                        return new DNAExtractorContainer(player.inventory, tile);
                    break;
                case GUI_CULTIVATOR_ID:
                    if (tile instanceof CultivatorBlockEntity)
                        return new CultivateContainer(player.inventory, tile);
                    break;
                case GUI_FEEDER_ID:
                    if (tile instanceof FeederBlockEntity)
                        return new FeederContainer(player.inventory, (FeederBlockEntity) tile);
                    break;
                case GUI_BUG_CRATE:
                    if (tile instanceof BugCrateBlockEntity)
                        return ((BugCrateBlockEntity) tile).createContainer(player.inventory, player);
                    break;
                default:
                    return null;
                }
        }
        switch(id){
            case GUI_SKELETON_ASSEMBLER:
                return new SkeletonAssemblyContainer(player.inventory, world, pos);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void openSelectDino(BlockPos pos, EnumFacing facing, EnumHand hand) {
    }

    public void openOrderGui(DinosaurEntity entity) {
    }

    public void openFieldGuide(DinosaurEntity entity, DinosaurEntity.FieldGuideInfo fieldGuideInfo) {
    }
}
