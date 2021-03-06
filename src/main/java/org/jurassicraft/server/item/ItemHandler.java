package org.jurassicraft.server.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.sound.SoundHandler;
import org.jurassicraft.server.api.Hybrid;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.tree.TreeType;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.item.block.AncientDoorItem;
import org.jurassicraft.server.item.vehicles.HelicopterItem;
import org.jurassicraft.server.item.vehicles.HelicopterModuleItem;
import org.jurassicraft.server.tab.TabHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemHandler {
    public static final Map<TreeType, AncientDoorItem> ANCIENT_DOORS = new HashMap<>();

    public static final PlasterAndBandageItem PLASTER_AND_BANDAGE = new PlasterAndBandageItem();
    public static final DinosaurSpawnEggItem SPAWN_EGG = new DinosaurSpawnEggItem();

    public static final DNAItem DNA = new DNAItem();
    public static final DinosaurEggItem EGG = new DinosaurEggItem();
    public static final HatchedEggItem HATCHED_EGG = new HatchedEggItem();
    public static final SoftTissueItem SOFT_TISSUE = new SoftTissueItem();
    public static final PlantSoftTissueItem PLANT_SOFT_TISSUE = new PlantSoftTissueItem();

    public static final DinosaurMeatItem DINOSAUR_MEAT = new DinosaurMeatItem();
    public static final DinosaurSteakItem DINOSAUR_STEAK = new DinosaurSteakItem();

    public static final PaddockSignItem PADDOCK_SIGN = new PaddockSignItem();
    public static final AttractionSignItem ATTRACTION_SIGN = new AttractionSignItem();

    public static final AmberItem AMBER = new AmberItem();
    public static final BasicItem PETRI_DISH = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem PETRI_DISH_AGAR = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem EMPTY_TEST_TUBE = new BasicItem(TabHandler.ITEMS);

    public static final SyringeItem SYRINGE = new SyringeItem();
    public static final EmptySyringeItem EMPTY_SYRINGE = new EmptySyringeItem();

    public static final StorageDiscItem STORAGE_DISC = new StorageDiscItem();
    public static final BasicItem DNA_NUCLEOTIDES = new BasicItem(TabHandler.ITEMS);

    public static final PlantDNAItem PLANT_DNA = new PlantDNAItem();

    public static final BasicItem SEA_LAMPREY = new BasicItem(TabHandler.ITEMS);

    public static final BasicItem IRON_BLADES = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem IRON_ROD = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem DISC_DRIVE = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem LASER = new BasicItem(TabHandler.ITEMS);

    public static final GrowthSerumItem GROWTH_SERUM = new GrowthSerumItem();

    public static final BasicItem PLANT_CELLS = new BasicItem(TabHandler.ITEMS);
    public static final PlantCallusItem PLANT_CALLUS = new PlantCallusItem();
    public static final BasicItem PLANT_CELLS_PETRI_DISH = new BasicItem(TabHandler.ITEMS);
    public static final HelicopterItem HELICOPTER = new HelicopterItem();
    public static final HelicopterModuleItem MINIGUN_MODULE = new HelicopterModuleItem("minigun");

    public static final BasicItem TRACKER = new BasicItem(TabHandler.ITEMS);

    public static final AncientRecordItem JURASSICRAFT_THEME_DISC = new AncientRecordItem("jurassicraft_theme", SoundHandler.JURASSICRAFT_THEME);
    public static final AncientRecordItem TROODONS_AND_RAPTORS_DISC = new AncientRecordItem("troodons_and_raptors", SoundHandler.TROODONS_AND_RAPTORS);
    public static final AncientRecordItem DONT_MOVE_A_MUSCLE_DISC = new AncientRecordItem("dont_move_a_muscle", SoundHandler.DONT_MOVE_A_MUSCLE);

    public static final DisplayBlockItem DISPLAY_BLOCK = new DisplayBlockItem();

    public static final BasicItem AMBER_KEYCHAIN = new BasicItem(TabHandler.DECORATIONS);
    public static final BasicItem AMBER_CANE = new BasicItem(TabHandler.DECORATIONS);
    public static final BasicItem MR_DNA_KEYCHAIN = new BasicItem(TabHandler.DECORATIONS);

    public static final BasicItem BASIC_CIRCUIT = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem ADVANCED_CIRCUIT = new BasicItem(TabHandler.ITEMS);

    public static final BasicItem IRON_NUGGET = new BasicItem(TabHandler.ITEMS);

    public static final Item AJUGINUCULA_SMITHII_SEEDS = new ItemSeeds(BlockHandler.AJUGINUCULA_SMITHII, Blocks.FARMLAND).setUnlocalizedName("ajuginucula_smithii_seeds").setCreativeTab(TabHandler.PLANTS);
    public static final Item AJUGINUCULA_SMITHII_LEAVES = new ItemFood(1, 0.5F, false).setUnlocalizedName("ajuginucula_smithii_leaves").setCreativeTab(TabHandler.PLANTS);
    public static final BasicItem AJUGINUCULA_SMITHII_OIL = new BasicItem(TabHandler.PLANTS);

    public static final Item WILD_ONION = new ItemSeedFood(3, 0.3F, BlockHandler.WILD_ONION, Blocks.FARMLAND).setUnlocalizedName("wild_onion").setCreativeTab(TabHandler.PLANTS);
    public static final Item WILD_POTATO_SEEDS = new ItemSeeds(BlockHandler.WILD_POTATO_PLANT, Blocks.FARMLAND).setCreativeTab(TabHandler.PLANTS);
    public static final Item RHAMNUS_SALIFOCIFIUS_SEEDS = new ItemSeeds(BlockHandler.RHAMNUS_SALICIFOLIUS_PLANT, Blocks.FARMLAND).setCreativeTab(TabHandler.PLANTS);
    public static final Item WILD_POTATO = new ItemFood(2, 0.2F, false).setCreativeTab(TabHandler.FOODS);
    public static final Item WILD_POTATO_COOKED = new ItemFood(6, 0.6F, false).setCreativeTab(TabHandler.FOODS);
    public static final Item RHAMNUS_SALIFOCIFIUS_BERRIES = new ItemFood(5, 0.5F, false).setCreativeTab(TabHandler.FOODS);

    public static final GracilariaItem GRACILARIA = (GracilariaItem) new GracilariaItem(BlockHandler.GRACILARIA).setCreativeTab(TabHandler.PLANTS);
    public static final BasicItem LIQUID_AGAR = new BasicItem(TabHandler.PLANTS);

    public static final DinoScannerItem DINO_SCANNER = new DinoScannerItem();

    public static final PlantFossilItem PLANT_FOSSIL = new PlantFossilItem();
    public static final TwigFossilItem TWIG_FOSSIL = new TwigFossilItem();

    public static final Map<String, FossilItem> FOSSILS = new HashMap<>();
    public static final Map<String, FossilItem> FRESH_FOSSILS = new HashMap<>();

    public static final FossilizedEggItem FOSSILIZED_EGG = new FossilizedEggItem();

    public static final BasicItem GYPSUM_POWDER = new BasicItem(TabHandler.ITEMS);

    public static final BasicItem COMPUTER_SCREEN = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem KEYBOARD = new BasicItem(TabHandler.ITEMS);

    public static final BasicItem DNA_ANALYZER = new BasicItem(TabHandler.ITEMS);

    public static final BasicFoodItem CHILEAN_SEA_BASS = new BasicFoodItem(10, 1.0F, false, TabHandler.FOODS);
    public static final BasicFoodItem FUN_FRIES = new BasicFoodItem(4, 2.0F, false, TabHandler.FOODS);
    public static final BasicFoodItem OILED_POTATO_STRIPS = new BasicFoodItem(1, 0.0F, false, TabHandler.FOODS);

    public static final FieldGuideItem FIELD_GUIDE = new FieldGuideItem();

    public static final BasicItem LUNCH_BOX = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem STAMP_SET = new BasicItem(TabHandler.ITEMS);

    public static final BasicItem CAR_CHASSIS = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem CAR_ENGINE_SYSTEM = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem CAR_SEATS = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem CAR_TIRE = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem CAR_WINDSCREEN = new BasicItem(TabHandler.ITEMS);
    public static final BasicItem UNFINISHED_CAR = new BasicItem(TabHandler.ITEMS);

    public static final JeepWranglerItem JEEP_WRANGLER = new JeepWranglerItem();
    public static final FordExplorerItem FORD_EXPLORER = new FordExplorerItem();

    public static final MuralItem MURAL = new MuralItem();

    public static final SaplingSeedItem PHOENIX_SEEDS = (SaplingSeedItem) new SaplingSeedItem(BlockHandler.ANCIENT_SAPLINGS.get(TreeType.PHOENIX));
    public static final SeededFruitItem PHOENIX_FRUIT = (SeededFruitItem) new SeededFruitItem(PHOENIX_SEEDS, 4, 0.4F).setCreativeTab(TabHandler.FOODS);

    public static final BugItem CRICKETS = new BugItem(stack -> {
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (item == Items.WHEAT_SEEDS) {
            return 1;
        } else if (block == Blocks.TALLGRASS) {
            return 2;
        } else if (item == Items.WHEAT) {
            return 3;
        } else if (block == Blocks.LEAVES || block == Blocks.LEAVES2) {
            return 7;
        } else if (block == Blocks.HAY_BLOCK) {
            return 27;
        }
        return 0;
    });

    public static final BugItem COCKROACHES = new BugItem(stack -> {
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (item == Items.WHEAT_SEEDS || item == Items.MELON_SEEDS) {
            return 1;
        } else if (item == Items.WHEAT || item == Items.PUMPKIN_SEEDS) {
            return 2;
        } else if (item == Items.MELON || item == Items.POTATO) {
            return 3;
        } else if (item == Items.CARROT) {
            return 4;
        } else if (item == Items.BREAD || item == Items.FISH) {
            return 6;
        } else if (item == Items.CHICKEN || item == Items.COOKED_CHICKEN) {
            return 7;
        } else if (item == Items.PORKCHOP || item == Items.COOKED_PORKCHOP) {
            return 8;
        } else if (item == Items.BEEF || item == Items.COOKED_BEEF) {
            return 10;
        } else if (item == ItemHandler.DINOSAUR_MEAT || item == ItemHandler.DINOSAUR_STEAK) {
            return 12;
        } else if (block == Blocks.HAY_BLOCK || block == Blocks.PUMPKIN) {
            return 16;
        } else if (block == Blocks.MELON_BLOCK) {
            return 27;
        }
        return 0;
    });

    public static final BugItem MEALWORM_BEETLES = new BugItem(stack -> {
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (item == Items.WHEAT_SEEDS || item == Items.MELON_SEEDS) {
            return 1;
        } else if (item == Items.PUMPKIN_SEEDS || item == Items.WHEAT) {
            return 2;
        } else if (item == Items.POTATO) {
            return 3;
        } else if (block == Blocks.CARROTS) {
            return 4;
        } else if (item == Items.BREAD) {
            return 6;
        } else if (block == Blocks.HAY_BLOCK) {
            return 16;
        }
        return 0;
    });

    public static final FineNetItem FINE_NET = new FineNetItem();
    public static final SwarmItem PLANKTON = new SwarmItem(BlockHandler.PLANKTON_SWARM::getDefaultState);
    public static final SwarmItem KRILL = new SwarmItem(BlockHandler.KRILL_SWARM::getDefaultState);

    public static final BasicFoodItem GOAT_RAW = new BasicFoodItem(3, 0.3F, true, TabHandler.FOODS);
    public static final BasicFoodItem GOAT_COOKED = new BasicFoodItem(6, 1.0F, true, TabHandler.FOODS);

    public static void init() {
        registerItem(FOSSILIZED_EGG, "Fossilized Egg");

        for (Map.Entry<Integer, Dinosaur> entry : EntityHandler.getDinosaurs().entrySet()) {
            Dinosaur dinosaur = entry.getValue();

            String[] boneTypes = dinosaur.getBones();

            for (String boneType : boneTypes) {
                if (!(dinosaur instanceof Hybrid)) {
                    if (!FOSSILS.containsKey(boneType)) {
                        FossilItem fossil = new FossilItem(boneType, false);
                        FOSSILS.put(boneType, fossil);
                        registerItem(fossil, boneType);
                    }
                }

                if (!FRESH_FOSSILS.containsKey(boneType)) {
                    FossilItem fossil = new FossilItem(boneType, true);
                    FRESH_FOSSILS.put(boneType, fossil);
                    registerItem(fossil, boneType + " Fresh");
                }
            }
        }

        registerItem(SPAWN_EGG, "Dino Spawn Egg");
        registerItem(FIELD_GUIDE, "Field Guide");
        registerItem(AMBER, "Amber");
        registerItem(SEA_LAMPREY, "Sea Lamprey");
        registerItem(PLASTER_AND_BANDAGE, "Plaster And Bandage");
        registerItem(EMPTY_TEST_TUBE, "Empty Test Tube");
        registerItem(EMPTY_SYRINGE, "Empty Syringe");
        registerItem(GROWTH_SERUM, "Growth Serum");
        registerItem(STORAGE_DISC, "Storage Disc");
        registerItem(DISC_DRIVE, "Disc Reader");
        registerItem(LASER, "Laser");
        registerItem(DNA_NUCLEOTIDES, "DNA Base Material");
        registerItem(PETRI_DISH, "Petri Dish");
        registerItem(PETRI_DISH_AGAR, "Petri Dish Agar");
        registerItem(PLANT_CELLS_PETRI_DISH, "Plant Cells Petri Dish");
        registerItem(PADDOCK_SIGN, "Paddock Sign");
        registerItem(ATTRACTION_SIGN, "Attraction Sign");
        registerItem(MURAL, "Mural");
        registerItem(DNA, "DNA");
        registerItem(SOFT_TISSUE, "Soft Tissue");
        registerItem(SYRINGE, "Syringe");
        registerItem(EGG, "Dino Egg");
        registerItem(HATCHED_EGG, "Hatched Egg");
        registerItem(PLANT_SOFT_TISSUE, "Plant Soft Tissue");
        registerItem(PLANT_DNA, "Plant DNA");
        registerItem(IRON_BLADES, "Iron Blades");
        registerItem(IRON_ROD, "Iron Rod");
        registerItem(PLANT_CELLS, "Plant Cells");
        registerItem(PLANT_CALLUS, "Plant Callus");
//        registerItem(TRACKER, "Tracker");
        registerItem(BASIC_CIRCUIT, "Basic Circuit");
        registerItem(ADVANCED_CIRCUIT, "Advanced Circuit");
        registerItemOreDict(IRON_NUGGET, "Iron Nugget", "nuggetIron");
        registerItem(COMPUTER_SCREEN, "Computer Screen");
        registerItem(KEYBOARD, "Keyboard");
        registerItem(DNA_ANALYZER, "DNA Analyzer");

        registerItem(DINOSAUR_MEAT, "Dinosaur Meat");
        registerItem(DINOSAUR_STEAK, "Dinosaur Steak");

        registerItem(PLANT_FOSSIL, "Plant Fossil");
        registerItem(TWIG_FOSSIL, "Twig Fossil");

//        registerItem(HELICOPTER, "Helicopter Spawner");   
//        registerItem(MINIGUN_MODULE, "Helicopter Minigun");

        registerItem(AMBER_CANE, "Amber Cane");
        registerItem(AMBER_KEYCHAIN, "Amber Keychain");
        registerItem(MR_DNA_KEYCHAIN, "Mr DNA Keychain");

        registerItem(DISPLAY_BLOCK, "Display Block Item");

//      registerItem(DINO_SCANNER, "Dino Scanner");

        registerItem(GYPSUM_POWDER, "Gypsum Powder");

        registerItem(AJUGINUCULA_SMITHII_SEEDS, "Ajuginucula Smithii Seeds");
        registerItem(AJUGINUCULA_SMITHII_LEAVES, "Ajuginucula Smithii Leaves");
        registerItem(AJUGINUCULA_SMITHII_OIL, "Ajuginucula Smithii Oil");

        registerItem(WILD_ONION, "Wild Onion");
        registerItem(WILD_POTATO_SEEDS, "Wild Potato Seeds");
        registerItem(WILD_POTATO, "Wild Potato");
        registerItem(WILD_POTATO_COOKED, "Wild Potato Cooked");

        registerItem(RHAMNUS_SALIFOCIFIUS_SEEDS, "Rhamnus Salicifolius Seeds");
        registerItem(RHAMNUS_SALIFOCIFIUS_BERRIES, "Rhamnus Salicifolius Berries");

        registerItem(GRACILARIA, "Gracilaria");
        registerItem(LIQUID_AGAR, "Liquid Agar");

        registerItem(CHILEAN_SEA_BASS, "Chilean Sea Bass");
        registerItem(PHOENIX_FRUIT, "Phoenix Fruit");
        registerItem(PHOENIX_SEEDS, "Phoenix Seeds");

        registerItem(FINE_NET, "Fine Net");
        registerItem(PLANKTON, "Plankton");
        registerItem(KRILL, "Krill");

        registerItem(CRICKETS, "Crickets");
        registerItem(COCKROACHES, "Cockroaches");
        registerItem(MEALWORM_BEETLES, "Mealworm Beetles");

        registerItem(CAR_CHASSIS, "Car Chassis");
        registerItem(CAR_ENGINE_SYSTEM, "Car Engine System");
        registerItem(CAR_SEATS, "Car Seats");
        registerItem(CAR_TIRE, "Car Tire");
        registerItem(CAR_WINDSCREEN, "Car Windscreen");
        registerItem(UNFINISHED_CAR, "Unfinished Car");
        registerItem(JEEP_WRANGLER, "Jeep Wrangler");
        registerItem(FORD_EXPLORER, "Ford Explorer");

        registerItem(JURASSICRAFT_THEME_DISC, "Disc JurassiCraft Theme");
        registerItem(TROODONS_AND_RAPTORS_DISC, "Disc Troodons And Raptors");
        registerItem(DONT_MOVE_A_MUSCLE_DISC, "Disc Don't Move A Muscle");

        registerItem(GOAT_RAW, "Goat Raw");
        registerItem(GOAT_COOKED, "Goat Cooked");

        registerItem(FUN_FRIES, "Fun Fries");
        registerItem(OILED_POTATO_STRIPS, "Oiled Potato Strips");

        registerItem(LUNCH_BOX, "Lunch Box");
        registerItem(STAMP_SET, "Stamp Set");

        for (TreeType type : TreeType.values()) {
            registerTreeType(type);
        }
    }

    public static void registerTreeType(TreeType type) {
        String typeName = type.name();

        AncientDoorItem door = new AncientDoorItem(BlockHandler.ANCIENT_DOORS.get(type));

        ANCIENT_DOORS.put(type, door);

        registerItem(door, typeName + " Door Item");
    }

    public static void registerItemOreDict(Item item, String name, String oreDict) {
        registerItem(item, name);
        OreDictionary.registerOre(oreDict, item);
    }

    public static void registerItem(Item item, String name) {
        String formattedName = name.toLowerCase(Locale.ENGLISH).replaceAll(" ", "_").replaceAll("'", "");
        item.setUnlocalizedName(formattedName);

        GameRegistry.register(item, new ResourceLocation(JurassiCraft.MODID, formattedName));
    }
}
