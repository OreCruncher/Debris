/*
 * This file is part of Debris, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.Debris.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.util.RegistryHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;

public final class LootEntryBuilder {

	public static final LootCondition[] EMPTY_CONDITIONS = {};

	private String name;
	private Item item;
	private int metaMin = RegistryHelper.NO_SUBTYPE;
	private int metaMax = RegistryHelper.NO_SUBTYPE;
	private float damageMin = -1;
	private float damageMax = -1;
	private int quality = 0;

	private int weight = 1;
	private int min = 1;
	private int max = 1;

	public LootEntryBuilder setName(@Nonnull final String name) {
		this.name = name;
		return this;
	}
	
	public LootEntryBuilder setItem(@Nonnull final Item item) {
		this.item = item;
		return this;
	}

	public LootEntryBuilder setWeight(final int weight) {
		this.weight = weight;
		return this;
	}
	
	public LootEntryBuilder setQuality(final int quality) {
		this.quality = quality;
		return this;
	}

	public LootEntryBuilder setDamage(final float damageMin, final float damageMax) {
		this.damageMin = damageMin;
		this.damageMax = damageMax;
		return this;
	}

	public LootEntryBuilder setMin(final int min) {
		this.min = min;
		return this;
	}

	public LootEntryBuilder setMax(final int max) {
		this.max = max;
		return this;
	}

	public LootEntryBuilder setMinMax(final int min, final int max) {
		this.min = min;
		this.max = max;
		return this;
	}

	public LootEntryBuilder setMetadata(final int minMeta, final int maxMeta) {
		this.metaMin = minMeta;
		this.metaMax = maxMeta;
		return this;
	}

	public LootEntryBuilder setItemStack(@Nonnull final ItemStack stack) {
		this.item = stack.getItem();
		this.metaMin = this.metaMax = this.item.getHasSubtypes() ? stack.getMetadata() : RegistryHelper.NO_SUBTYPE;
		this.damageMin = this.damageMax = this.item.isDamageable()
				? ((float) stack.getItemDamage() / (float) stack.getMaxDamage()) : -1;
		return this;
	}

	public LootEntry build() {
		final List<LootFunction> func = new ArrayList<LootFunction>();
		func.add(new SetCount(EMPTY_CONDITIONS, new RandomValueRange(this.min, this.max)));
		if (this.metaMin != RegistryHelper.NO_SUBTYPE)
			func.add(new SetMetadata(EMPTY_CONDITIONS, new RandomValueRange(this.metaMin, this.metaMax)));
		if (this.damageMin != -1)
			func.add(new SetDamage(EMPTY_CONDITIONS, new RandomValueRange(this.damageMin, this.damageMax)));
		
		final LootFunction[] f = func.toArray(new LootFunction[func.size()]);
		return new LootEntryItem(this.item, this.weight, this.quality, f, EMPTY_CONDITIONS, this.name);
	}

}
