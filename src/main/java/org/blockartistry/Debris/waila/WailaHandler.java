/* This file is part of Debris, licensed under the MIT License (MIT).
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

package org.blockartistry.Debris.waila;

import java.util.ArrayList;
import java.util.List;

import org.blockartistry.Debris.ModLog;
import org.blockartistry.Debris.ModOptions;
import org.blockartistry.Debris.util.MCHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public final class WailaHandler implements IWailaDataProvider {

	private List<String> gatherOreNames(final ItemStack stack) {
		final List<String> result = new ArrayList<String>();
		for (int i : OreDictionary.getOreIDs(stack))
			result.add(OreDictionary.getOreName(i));
		return result;
	}

	private List<String> gatherText(final ItemStack stack, final List<String> text, final IWailaDataAccessor accessor,
			final IWailaConfigHandler config) {

		text.add(TextFormatting.GOLD + "----------");

		if (stack != null) {
			final Item item = stack.getItem();
			final String itemName = MCHelper.nameOf(item);

			if (itemName != null) {
				final StringBuilder builder = new StringBuilder();
				builder.append("ITEM: ").append(itemName);
				if (stack.getHasSubtypes())
					builder.append(':').append(stack.getItemDamage());
				text.add(builder.toString());
			}
		}

		final List<String> oreNames = gatherOreNames(stack);
		if (oreNames.size() > 0) {
			text.add(TextFormatting.GREEN + "Dictionary Names");
			for (final String ore : gatherOreNames(stack))
				text.add(TextFormatting.GREEN + " " + ore);
		}

		text.add(TextFormatting.GOLD + "----------");

		return text;
	}

	@Optional.Method(modid = "Waila")
	public static void callbackRegister(final IWailaRegistrar register) {
		ModLog.info("Registering Waila Block/Item handler...");
		final WailaHandler instance = new WailaHandler();
		register.registerTailProvider(instance, Block.class);
	}

	public WailaHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@Optional.Method(modid = "Waila")
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip,
			final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return gatherText(itemStack, currenttip, accessor, config);
	}

	@Override
	@Optional.Method(modid = "Waila")
	public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip,
			final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return gatherText(itemStack, currenttip, accessor, config);
	}

	@Override
	@Optional.Method(modid = "Waila")
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip,
			final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return gatherText(itemStack, currenttip, accessor, config);
	}

	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

		return null;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP arg0, final TileEntity arg1, final NBTTagCompound arg2,
			final World arg3, final BlockPos pos) {
		return null;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
	public void onToolTipEvent(final ItemTooltipEvent event) {

		if (event == null || event.getItemStack() == null || event.getToolTip() == null)
			return;

		gatherText(event.getItemStack(), event.getToolTip(), null, null);
	}

	public static void register() {
		if (ModOptions.enableDebugLogging)
			FMLInterModComms.sendMessage("Waila", "register", WailaHandler.class.getName() + ".callbackRegister");
	}
}