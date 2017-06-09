package org.halvors.quantum;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.halvors.quantum.client.render.machine.RenderCentrifuge;
import org.halvors.quantum.client.render.machine.RenderChemicalExtractor;
import org.halvors.quantum.client.render.machine.RenderNuclearBoiler;
import org.halvors.quantum.client.render.particle.RenderQuantumAssembler;
import org.halvors.quantum.client.render.reactor.fusion.RenderFusionReactor;
import org.halvors.quantum.common.CommonProxy;
import org.halvors.quantum.common.ConfigurationManager;
import org.halvors.quantum.common.ConfigurationManager.Integration;
import org.halvors.quantum.common.QuantumCreativeTab;
import org.halvors.quantum.common.Reference;
import org.halvors.quantum.common.base.IUpdatableMod;
import org.halvors.quantum.common.block.*;
import org.halvors.quantum.common.block.accelerator.BlockAccelerator;
import org.halvors.quantum.common.block.machine.BlockCentrifuge;
import org.halvors.quantum.common.block.machine.BlockChemicalExtractor;
import org.halvors.quantum.common.block.machine.BlockNuclearBoiler;
import org.halvors.quantum.common.block.reactor.fusion.BlockFusionReactor;
import org.halvors.quantum.common.block.reactor.fusion.BlockPlasma;
import org.halvors.quantum.common.debug.block.BlockCreativeBuilder;
import org.halvors.quantum.common.event.PlayerEventHandler;
import org.halvors.quantum.common.item.*;
import org.halvors.quantum.common.item.armor.ItemArmorHazmat;
import org.halvors.quantum.common.item.particle.ItemAntimatter;
import org.halvors.quantum.common.item.reactor.fission.*;
import org.halvors.quantum.common.block.reactor.BlockElectricTurbine;
import org.halvors.quantum.client.render.reactor.RenderElectricTurbine;
import org.halvors.quantum.common.tile.particle.TileAccelerator;
import org.halvors.quantum.common.tile.particle.TileQuantumAssembler;
import org.halvors.quantum.common.tile.reactor.TileElectricTurbine;
import org.halvors.quantum.common.block.reactor.fission.BlockControlRod;
import org.halvors.quantum.common.block.reactor.fission.BlockReactorCell;
import org.halvors.quantum.client.render.reactor.fission.RenderReactorCell;
import org.halvors.quantum.common.tile.reactor.fission.TileReactorCell;
import org.halvors.quantum.common.block.TileElectromagnet;
import org.halvors.quantum.common.schematic.SchematicAccelerator;
import org.halvors.quantum.common.schematic.SchematicBreedingReactor;
import org.halvors.quantum.common.schematic.SchematicFissionReactor;
import org.halvors.quantum.common.schematic.SchematicFusionReactor;
import org.halvors.quantum.common.tile.machine.TileCentrifuge;
import org.halvors.quantum.common.tile.machine.TileChemicalExtractor;
import org.halvors.quantum.common.tile.machine.TileNuclearBoiler;
import org.halvors.quantum.common.tile.reactor.fusion.TileFusionReactor;
import org.halvors.quantum.common.tile.reactor.fusion.TilePlasma;
import org.halvors.quantum.common.tile.sensor.TileSiren;
import org.halvors.quantum.common.transform.vector.Vector3;
import org.halvors.quantum.common.transform.vector.VectorWorld;
import org.halvors.quantum.common.updater.UpdateManager;
import org.halvors.quantum.lib.event.PlasmaEvent;
import org.halvors.quantum.lib.event.ThermalEvent;
import org.halvors.quantum.lib.render.BlockRenderingHandler;
import org.halvors.quantum.lib.tile.BlockDummy;
import org.halvors.quantum.lib.tile.TileBlock;
import org.halvors.quantum.lib.utility.RenderUtility;

/**
 * This is the Quantum class, which is the main class of this mod.
 *
 * @author halvors
 */
@Mod(modid = Reference.ID,
     name = Reference.NAME,
     version = Reference.VERSION,
     dependencies = "after:CoFHCore;" +
                    "after:Mekanism",
     guiFactory = "org.halvors." + Reference.ID + ".client.gui.configuration.GuiConfiguationFactory")
public class Quantum implements IUpdatableMod {
	// The instance of your mod that Forge uses.
	@Instance(value = Reference.ID)
	public static Quantum instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "org.halvors." + Reference.ID + ".client.ClientProxy", serverSide = "org.halvors." + Reference.ID + ".common.CommonProxy")
	public static CommonProxy proxy;

	// Logger
	private static final Logger logger = LogManager.getLogger(Reference.ID);

	// ConfigurationManager
	private static Configuration configuration;

	// Creative Tab
	private static final QuantumCreativeTab creativeTab = new QuantumCreativeTab();

	// Fluids
	public static final Fluid fluidDeuterium = new Fluid("deuterium").setGaseous(true);
	public static final Fluid fluidUraniumHexaflouride = new Fluid("uraniumhexafluoride").setGaseous(true);
	public static final Fluid fluidPlasma = new Fluid("plasma").setGaseous(true);
	public static final Fluid fluidSteam = new Fluid("steam").setGaseous(true);
	public static final Fluid fluidTritium = new Fluid("tritium").setGaseous(true);
	public static final Fluid fluidToxicWaste = new Fluid("toxicwaste");

	public static FluidStack fluidStackDeuterium;
	public static FluidStack fluidStackUraniumHexaflouride;
	public static FluidStack fluidStackSteam;
	public static FluidStack fluidStackTritium;
	public static FluidStack fluidStackToxicWaste;
	public static FluidStack fluidStackWater;

	// Blocks
	public static Block blockAccelerator;
	public static Block blockChemicalExtractor;
	public static Block blockCentrifuge;
	public static Block blockControlRod;
	public static TileBlock blockElectromagnet;
	public static Block blockFulmination;
	public static Block blockFusionReactor;
	public static Block blockNuclearBoiler;
	public static TileBlock blockSiren;
	public static Block blockUraniumOre;
	public static Block blockPlasma;
	public static TileBlock blockQuantumAssembler;
	public static Block blockRadioactiveGrass;
	public static Block blockReactorCell;
	public static BlockFluidClassic blockToxicWaste;
	public static Block blockElectricTurbine;

	public static Block blockCreativeBuilder;

	//blockThermometer = contentRegistry.newBlock(TileThermometer.class);
	//blockSteamFunnel = contentRegistry.newBlock(TileFunnel.class);

	// Items
	// Cells
	public static Item itemAntimatter;
	public static Item itemBreedingRod;
	public static Item itemCell;
	public static Item itemDarkMatter;
	public static Item itemDeuteriumCell;
	public static Item itemFissileFuel;
	public static Item itemTritiumCell;
	public static Item itemWaterCell;

	// Buckets
	public static Item itemBucketToxicWaste;

	// Uranium
	public static Item itemUranium;
	public static Item itemYellowCake;

	// Hazmat
	public static ItemArmor itemHazmatMask;
	public static ItemArmor itemHazmatBody;
	public static ItemArmor itemHazmatLeggings;
	public static ItemArmor itemHazmatBoots;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// Initialize configuration.
        configuration = new Configuration(event.getSuggestedConfigurationFile());

		// Load the configuration.
		ConfigurationManager.loadConfiguration(configuration);

		// Check for updates.
		FMLCommonHandler.instance().bus().register(new UpdateManager(this, Reference.RELEASE_URL, Reference.DOWNLOAD_URL));

		// Mod integration.
		logger.log(Level.INFO, "CoFHCore integration is " + (Integration.isCoFHCoreEnabled ? "enabled" : "disabled") + ".");
		logger.log(Level.INFO, "Mekanism integration is " + (Integration.isMekanismEnabled ? "enabled" : "disabled") + ".");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Register the our EventHandler.
		FMLCommonHandler.instance().bus().register(new PlayerEventHandler());

		// Register the proxy as our GuiHandler to NetworkRegistry.
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

		// Register event bus.
		MinecraftForge.EVENT_BUS.register(this);

		// Register block handler.
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());

		// Call functions for adding blocks, items, etc.
		registerFluids();
		registerBlocks();
		registerTileEntities();
		registerTileEntitySpecialRenders();
		registerItems();
		registerFluidContainers();
		registerRecipes();

		BlockCreativeBuilder.registerSchematic(new SchematicAccelerator());
		BlockCreativeBuilder.registerSchematic(new SchematicBreedingReactor());
		BlockCreativeBuilder.registerSchematic(new SchematicFissionReactor());
		BlockCreativeBuilder.registerSchematic(new SchematicFusionReactor());
	}

	private void registerFluids() {
		// Register fluids.
		FluidRegistry.registerFluid(fluidDeuterium);
		FluidRegistry.registerFluid(fluidUraniumHexaflouride);
		FluidRegistry.registerFluid(fluidPlasma);
		FluidRegistry.registerFluid(fluidSteam);
		FluidRegistry.registerFluid(fluidTritium);
		FluidRegistry.registerFluid(fluidToxicWaste);

		fluidStackDeuterium = new FluidStack(FluidRegistry.getFluid("deuterium"), 0);
		fluidStackUraniumHexaflouride = new FluidStack(fluidUraniumHexaflouride, 0);
		fluidStackSteam = new FluidStack(FluidRegistry.getFluid("steam"), 0);
		fluidStackTritium = new FluidStack(FluidRegistry.getFluid("tritium"), 0);
		fluidStackToxicWaste = new FluidStack(FluidRegistry.getFluid("toxicwaste"), 0);
		fluidStackWater = new FluidStack(FluidRegistry.WATER, 0);
		fluidStackWater = new FluidStack(FluidRegistry.WATER, 0);
	}

	private void registerBlocks() {
		// Register blocks.
		blockAccelerator = new BlockAccelerator();
		blockChemicalExtractor = new BlockChemicalExtractor();
		blockCentrifuge = new BlockCentrifuge();
		blockControlRod = new BlockControlRod();

		blockElectromagnet = new TileElectromagnet();
		blockElectromagnet.block = new BlockDummy(Reference.DOMAIN, Quantum.getCreativeTab(), blockElectromagnet);

		blockFusionReactor = new BlockFusionReactor();
		blockNuclearBoiler = new BlockNuclearBoiler();

		blockSiren = new TileSiren();
		blockSiren.block = new BlockDummy(Reference.DOMAIN, Quantum.getCreativeTab(), blockSiren);

		blockUraniumOre = new BlockUraniumOre();
		blockPlasma = new BlockPlasma();
		fluidPlasma.setBlock(blockPlasma);

		blockQuantumAssembler = new TileQuantumAssembler();
		blockQuantumAssembler.block = new BlockDummy(Reference.DOMAIN, Quantum.getCreativeTab(), blockQuantumAssembler);

		blockRadioactiveGrass = new BlockRadioactiveGrass();
		blockReactorCell = new BlockReactorCell();
		blockToxicWaste = new BlockToxicWaste();
		blockElectricTurbine = new BlockElectricTurbine();

		GameRegistry.registerBlock(blockAccelerator, "blockAccelerator;");
		GameRegistry.registerBlock(blockChemicalExtractor, "blockChemicalExtractor");
		GameRegistry.registerBlock(blockCentrifuge, "blockCentrifuge");
		GameRegistry.registerBlock(blockControlRod, "blockControlRod");
		GameRegistry.registerBlock(blockElectromagnet.block, "blockElectromagnet");
		GameRegistry.registerBlock(blockFusionReactor, "blockFusionReactor");
		GameRegistry.registerBlock(blockNuclearBoiler, "blockNuclearBoiler");
		GameRegistry.registerBlock(blockSiren.block, "blockSiren");
		GameRegistry.registerBlock(blockUraniumOre, "blockUraniumOre");
		GameRegistry.registerBlock(blockPlasma, "blockPlasma");
		GameRegistry.registerBlock(blockQuantumAssembler.block, "blockQuantumAssembler");
		GameRegistry.registerBlock(blockRadioactiveGrass, "blockRadioactiveGrass");
		GameRegistry.registerBlock(blockReactorCell, "blockReactorCell");
		GameRegistry.registerBlock(blockToxicWaste, "blockToxicWaste");
		GameRegistry.registerBlock(blockElectricTurbine, "blockElectricTurbine");

		blockCreativeBuilder = new BlockCreativeBuilder();
		GameRegistry.registerBlock(blockCreativeBuilder, "blockCreativeBuilder");
	}

	private void registerTileEntities() {
		// Register tile entities.
		GameRegistry.registerTileEntity(TileAccelerator.class, "tileAccelerator");
		GameRegistry.registerTileEntity(TileChemicalExtractor.class, "tileChemicalExtractor");
		GameRegistry.registerTileEntity(TileCentrifuge.class, "tileCentrifuge");
		GameRegistry.registerTileEntity(TileElectricTurbine.class, "tileElectricTurbine");
		GameRegistry.registerTileEntity(TileElectromagnet.class, "tileElectromagnet");
		GameRegistry.registerTileEntity(TileNuclearBoiler.class, "tileNuclearBoiler");
		GameRegistry.registerTileEntity(TilePlasma.class, "tilePlasma");
		GameRegistry.registerTileEntity(TileQuantumAssembler.class, "tileQuantumAssembler");
		GameRegistry.registerTileEntity(TileFusionReactor.class, "tileFusionCore");
		GameRegistry.registerTileEntity(TileReactorCell.class, "tileReactorCell");
	}

	private void registerTileEntitySpecialRenders() {
		// Register special renderers.
		ClientRegistry.bindTileEntitySpecialRenderer(TileChemicalExtractor.class, new RenderChemicalExtractor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCentrifuge.class, new RenderCentrifuge());
		ClientRegistry.bindTileEntitySpecialRenderer(TileElectricTurbine.class, new RenderElectricTurbine());
		ClientRegistry.bindTileEntitySpecialRenderer(TileNuclearBoiler.class, new RenderNuclearBoiler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileQuantumAssembler.class, new RenderQuantumAssembler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFusionReactor.class, new RenderFusionReactor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileReactorCell.class, new RenderReactorCell());
	}

	private void registerItems() {
		// Register items.
		// Cells
		itemAntimatter = new ItemAntimatter();
		itemBreedingRod = new ItemBreederFuel();
		itemCell = new ItemCell("cellEmpty");
		itemDarkMatter = new ItemCell("darkMatter");
		itemDeuteriumCell = new ItemCell("cellDeuterium");
		itemFissileFuel = new ItemFissileFuel();
		itemTritiumCell = new ItemCell("cellTritium");
		itemWaterCell = new ItemCell("cellWater");

		GameRegistry.registerItem(itemAntimatter, "antimatter");
		GameRegistry.registerItem(itemBreedingRod, "rodBreedingFuel");
		GameRegistry.registerItem(itemCell, "cellEmpty");
		GameRegistry.registerItem(itemDarkMatter, "darkMatter");
		GameRegistry.registerItem(itemDeuteriumCell, "cellDeuterium");
		GameRegistry.registerItem(itemFissileFuel, "rodFissileFuel");
		GameRegistry.registerItem(itemTritiumCell, "cellTritium");
		GameRegistry.registerItem(itemWaterCell, "cellWater");

		// Buckets
		itemBucketToxicWaste = new ItemBucketToxicWaste();

		GameRegistry.registerItem(itemBucketToxicWaste, "bucketToxicWaste");

		// Uranium
		itemUranium = new ItemUranium();
		itemYellowCake = new ItemRadioactive("yellowcake");

		GameRegistry.registerItem(itemUranium, "uranium");
		GameRegistry.registerItem(itemYellowCake, "yellowcake");

		// Hazmat
		itemHazmatMask = new ItemArmorHazmat("hazmatMask", 0);
		itemHazmatBody = new ItemArmorHazmat("hazmatBody", 1);
		itemHazmatLeggings = new ItemArmorHazmat("hazmatLeggings", 2);
		itemHazmatBoots = new ItemArmorHazmat("hazmatBoots", 3);

		GameRegistry.registerItem(itemHazmatMask, "itemHazmatMask");
		GameRegistry.registerItem(itemHazmatBody, "itemHazmatBody");
		GameRegistry.registerItem(itemHazmatLeggings, "itemHazmatLeggings");
		GameRegistry.registerItem(itemHazmatBoots, "itemHazmatBoots");

		// Debug
		GameRegistry.registerItem(new ItemScrewdriver(), "itemScrewdriver");
	}

	private void registerFluidContainers() {
		// Register fluid containers.
		FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidRegistry.getFluid("deuterium"), 200), new ItemStack(itemDeuteriumCell), new ItemStack(itemCell));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidRegistry.getFluid("tritium"), 200), new ItemStack(itemTritiumCell), new ItemStack(itemCell));
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("toxicwaste"), new ItemStack(itemBucketToxicWaste), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.WATER, new ItemStack(itemWaterCell), new ItemStack(itemCell));
	}

	private void registerRecipes() {

	}

	@SubscribeEvent
	public void fillBucketEvent(FillBucketEvent event) {
		if (!event.world.isRemote && event.target != null && event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			VectorWorld pos = new VectorWorld(event.world, event.target);

			if (pos.getBlock() == blockToxicWaste) {
				pos.setBlock(Blocks.air);

				event.result = new ItemStack(itemBucketToxicWaste);
				event.setResult(Event.Result.ALLOW);
			}
		}
	}

	@SubscribeEvent
	public void plasmaEvent(PlasmaEvent.SpawnPlasmaEvent event) {
		Vector3 position = new Vector3(event.x, event.y, event.z);
		Block block = position.getBlock(event.world);

		if (block != null) {
			TileEntity tile = position.getTileEntity(event.world);

			if (block == Blocks.bedrock || block == Blocks.iron_block) {
				return;
			}

			if (tile instanceof TilePlasma) {
				((TilePlasma) tile).setTemperature(event.temperature);

				return;
			}

			if (tile instanceof IElectromagnet) {
				return;
			}
		}

		position.setBlock(event.world, blockPlasma);

		TileEntity tile = position.getTileEntity(event.world);

		if (tile instanceof TilePlasma) {
			((TilePlasma) tile).setTemperature(event.temperature);
		}
	}

	@SubscribeEvent
	public void thermalEventHandler(ThermalEvent.ThermalEventUpdate event) {
		VectorWorld position = event.position;
		Block block = position.getBlock();

		if (block == blockElectromagnet.block) {
			event.heatLoss = event.deltaTemperature * 0.6F;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void preTextureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 0) {
			RenderUtility.registerIcon(Reference.PREFIX + "atomic_edge", event.map);
		}
	}

	public static Quantum getInstance() {
		return instance;
	}

	public static CommonProxy getProxy() {
		return proxy;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static QuantumCreativeTab getCreativeTab() {
		return creativeTab;
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public String getModId() {
		return Reference.ID;
	}

	@Override
	public String getModName() {
		return Reference.NAME;
	}

	@Override
	public String getModVersion() {
		return Reference.VERSION;
	}
}