package com.simibubi.create.foundation.config;

import java.util.Collection;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.utility.CreateRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public enum ContraptionMovementSetting {
	MOVABLE, NO_PICKUP, UNMOVABLE;

	private static final CreateRegistry<Block, Supplier<ContraptionMovementSetting>> SETTING_SUPPLIERS = new CreateRegistry<>(Registry.BLOCK);

	public static void register(ResourceLocation block, Supplier<ContraptionMovementSetting> settingSupplier) {
		SETTING_SUPPLIERS.register(block, settingSupplier);
	}

	public static void register(Block block, Supplier<ContraptionMovementSetting> settingSupplier) {
		SETTING_SUPPLIERS.register(block, settingSupplier);
	}

	@Nullable
	public static ContraptionMovementSetting get(Block block) {
		if (block instanceof IMovementSettingProvider provider)
			return provider.getContraptionMovementSetting();
		Supplier<ContraptionMovementSetting> supplier = SETTING_SUPPLIERS.get(block);
		if (supplier == null)
			return null;
		return supplier.get();
	}

	public static boolean allAre(Collection<StructureTemplate.StructureBlockInfo> blocks, ContraptionMovementSetting are) {
		return blocks.stream().anyMatch(b -> get(b.state.getBlock()) == are);
	}

	public static boolean isNoPickup(Collection<StructureTemplate.StructureBlockInfo> blocks) {
		return allAre(blocks, ContraptionMovementSetting.NO_PICKUP);
	}

	public static void registerDefaults() {
		register(Blocks.BUDDING_AMETHYST, () -> AllConfigs.SERVER.kinetics.amethystMovement.get());
		register(Blocks.SPAWNER, () -> AllConfigs.SERVER.kinetics.spawnerMovement.get());
		register(Blocks.OBSIDIAN, () -> AllConfigs.SERVER.kinetics.obsidianMovement.get());
		register(Blocks.CRYING_OBSIDIAN, () -> AllConfigs.SERVER.kinetics.obsidianMovement.get());
	}

	public interface IMovementSettingProvider /* extends Block */ {
		ContraptionMovementSetting getContraptionMovementSetting();
	}
}
