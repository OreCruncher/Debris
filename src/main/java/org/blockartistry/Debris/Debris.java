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

package org.blockartistry.Debris;

import java.io.File;

import javax.annotation.Nonnull;

import org.blockartistry.Debris.proxy.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "debris", useMetadata = true, guiFactory = Debris.GUI_FACTORY)
public class Debris {
	public static final String GUI_FACTORY = "org.blockartistry.Debris.gui.ConfigGuiFactory";

	@Metadata
	protected static ModMetadata metadata = null;

	@Nonnull
	public static ModMetadata getModMetadata() {
		return metadata;
	}

	@Nonnull
	public static String MOD_ID() {
		return metadata.modId;
	}

	@Nonnull
	public static String RESOURCE_ID() {
		return metadata.modId;
	}

	@Nonnull
	public static String MOD_NAME() {
		return metadata.name;
	}

	@SidedProxy(clientSide = "org.blockartistry.Debris.proxy.ProxyClient", serverSide = "org.blockartistry.Debris.proxy.Proxy")
	protected static Proxy proxy;

	@Nonnull
	public static Proxy proxy() {
		return proxy;
	}

	protected static Configuration config;

	@Nonnull
	public static Configuration config() {
		return config;
	}

	protected static File dataDirectory;

	@Nonnull
	public static File dataDirectory() {
		return dataDirectory;
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	public static Profiler getProfiler() {
		return Minecraft.getMinecraft().mcProfiler;
	}

	public Debris() {

	}

	@EventHandler
	public void preInit(@Nonnull final FMLPreInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(this);

		// Load up our configuration
		dataDirectory = new File(event.getModConfigurationDirectory(), MOD_ID());
		dataDirectory.mkdirs();
		config = new Configuration(new File(dataDirectory, MOD_ID() + ".cfg"));

		config.load();
		ModOptions.load(config);
		config.save();

		ModLog.DEBUGGING = ModOptions.enableDebugLogging;

		proxy.preInit(event);
	}

	@EventHandler
	public void init(@Nonnull final FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(@Nonnull final FMLPostInitializationEvent event) {
		proxy.postInit(event);
		config.save();
	}

	@EventHandler
	public void loadCompleted(@Nonnull final FMLLoadCompleteEvent event) {
		proxy.loadCompleted(event);
	}

	////////////////////////
	//
	// Client state events
	//
	////////////////////////
	@SubscribeEvent
	public void clientConnect(@Nonnull final ClientConnectedToServerEvent event) {
		proxy.clientConnect(event);
	}

	@SubscribeEvent
	public void clientDisconnect(@Nonnull final ClientDisconnectionFromServerEvent event) {
		proxy.clientDisconnect(event);
	}

	////////////////////////
	//
	// Server state events
	//
	////////////////////////
	@EventHandler
	public void serverAboutToStart(@Nonnull final FMLServerAboutToStartEvent event) {
		proxy.serverAboutToStart(event);
	}

	@EventHandler
	public void serverStarting(@Nonnull final FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

	@EventHandler
	public void serverStopping(@Nonnull final FMLServerStoppingEvent event) {
		proxy.serverStopping(event);
	}

	@EventHandler
	public void serverStopped(@Nonnull final FMLServerStoppedEvent event) {
		proxy.serverStopped(event);
	}

}
