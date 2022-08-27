package com.simibubi.create.foundation.item;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TagDependentIngredientItem extends Item {

	private List<TagKey<Item>> tag;

	private static void setIfFalse(boolean inputValue, boolean setValue) {
		if (!inputValue) inputValue = setValue;
	}

	public TagDependentIngredientItem(Properties properties, List<TagKey<Item>> tag) {
		super(properties);
		this.tag = tag;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (!shouldHide())
			super.fillItemCategory(tab, list);
	}

	public boolean shouldHide() {
		boolean tagMissing = !Registry.ITEM.isKnownTagName(this.tag.get(0));
		boolean tagEmpty = tagMissing || !Registry.ITEM.getTagOrEmpty(this.tag.get(0)).iterator().hasNext();

		setIfFalse(tagMissing, !Registry.ITEM.isKnownTagName(this.tag.get(1)));
		setIfFalse(tagEmpty, tagMissing || !Registry.ITEM.getTagOrEmpty(this.tag.get(1)).iterator().hasNext());

		return tagMissing || tagEmpty;
	}

}
