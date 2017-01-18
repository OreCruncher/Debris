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

package org.blockartistry.Debris.util;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum CompassFacing implements IStringSerializable {

	NORTH(0, "north", EnumFacing.NORTH),
	SOUTH(1, "south", EnumFacing.SOUTH),
	WEST(2, "east", EnumFacing.WEST),
	EAST(3, "west", EnumFacing.EAST)
	;

	private static final CompassFacing[] META_LOOKUP = new CompassFacing[values().length];

	private final String name;
	private final int meta;
	private final EnumFacing facing;

	private CompassFacing(final int meta, @Nonnull final String name, @Nonnull final EnumFacing facing) {
		this.name = name;
		this.meta = meta;
		this.facing = facing;
	}

	public int getMetadata() {
		return this.meta;
	}

	public EnumFacing getFacing() {
		return this.facing;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Nonnull
	public static CompassFacing byMetadata(int meta) {
		if (meta < 0 || meta >= META_LOOKUP.length) {
			meta = 0;
		}

		return META_LOOKUP[meta];
	}

	static {
		for (final CompassFacing f : values()) {
			META_LOOKUP[f.getMetadata()] = f;
		}
	}
}
