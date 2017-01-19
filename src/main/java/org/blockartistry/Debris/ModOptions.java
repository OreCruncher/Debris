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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.Debris.util.ConfigProcessor;
import org.blockartistry.Debris.util.ConfigProcessor.Comment;
import org.blockartistry.Debris.util.ConfigProcessor.MinMaxInt;
import org.blockartistry.Debris.util.ConfigProcessor.Parameter;
import org.blockartistry.Debris.util.ConfigProcessor.RestartRequired;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class ModOptions {

	private ModOptions() {
	}

	public static final String CATEGORY_LOGGING_CONTROL = "logging";
	public static final String CONFIG_ENABLE_DEBUG_LOGGING = "Enable Debug Logging";
	public static final String CONFIG_ENABLE_ONLINE_VERSION_CHECK = "Enable Online Version Check";
	private static final List<String> loggingSort = Arrays.asList(CONFIG_ENABLE_ONLINE_VERSION_CHECK,
			CONFIG_ENABLE_DEBUG_LOGGING);

	@Parameter(category = CATEGORY_LOGGING_CONTROL, property = CONFIG_ENABLE_DEBUG_LOGGING, defaultValue = "false", lang = "cfg.logging.EnableDebug")
	@Comment("Enables/disables debug logging of the mod")
	@RestartRequired
	public static boolean enableDebugLogging = false;
	@Parameter(category = CATEGORY_LOGGING_CONTROL, property = CONFIG_ENABLE_ONLINE_VERSION_CHECK, defaultValue = "true", lang = "cfg.logging.VersionCheck")
	@Comment("Enables/disables display of version check information")
	@RestartRequired
	public static boolean enableVersionChecking = true;

	public static final String CATEGORY_GENERAL = "general";
	public static final String CONFIG_ENABLE = "Enable Debris";
	public static final String CONFIG_DIMENSION_LIST = "Dimensions";
	public static final String CONFIG_AS_BLACKLIST = "Dimensions as Blacklist";
	public static final String CONFIG_RUBBLE_DENSITY = "Rubble Density";
	public static final String CONFIG_RUBBLE_ROLLS_MIN = "Rolls Min";
	public static final String CONFIG_RUBBLE_ROLLS_MAX = "Rolls Max";
	public static final String CONFIG_BONUS_ROLLS_MIN = "Bonus Rolls Min";
	public static final String CONFIG_BONUS_ROLLS_MAX = "Bonus Rolls Max";
	public static final String CONFIG_USE_LUCK = "Use Player Luck";
	public static final String CONFIG_EXTERNAL_SCRIPTS = "External Configuration Files";

	private static final List<String> generalSort = Arrays.asList(CONFIG_ENABLE, CONFIG_DIMENSION_LIST,
			CONFIG_AS_BLACKLIST, CONFIG_RUBBLE_DENSITY, CONFIG_RUBBLE_ROLLS_MIN, CONFIG_RUBBLE_ROLLS_MAX,
			CONFIG_BONUS_ROLLS_MIN, CONFIG_BONUS_ROLLS_MAX, CONFIG_USE_LUCK, CONFIG_EXTERNAL_SCRIPTS);

	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_ENABLE, defaultValue = "true", lang = "cfg.general.Enable")
	@Comment("Enables Debris in world gen")
	@RestartRequired
	public static boolean enableDebris = true;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_DIMENSION_LIST, defaultValue = "-1,1", lang = "cfg.general.Dimensions")
	@Comment("List of dimensions that will be black/white listed for generation")
	@RestartRequired
	public static String dimensionList = "-1,1";
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_AS_BLACKLIST, defaultValue = "true", lang = "cfg.general.BlackList")
	@Comment("Indicates whether the dimension list should be treated as a black or white list")
	@RestartRequired
	public static boolean dimensionListAsBlack = true;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_RUBBLE_DENSITY, defaultValue = "80", lang = "cfg.general.Density")
	@Comment("Density of rubble within a chunk")
	@MinMaxInt(min = 0)
	public static float rubbleDensity = 80;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_RUBBLE_ROLLS_MIN, defaultValue = "3", lang = "cfg.general.RollsMin")
	@Comment("The minimum number of rolls when rubble is broken")
	@MinMaxInt(min = 0)
	public static int rubbleRollsMin = 3;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_RUBBLE_ROLLS_MAX, defaultValue = "3", lang = "cfg.general.RollsMax")
	@Comment("The maximum number of rolls when rubble is broken")
	@MinMaxInt(min = 0)
	public static int rubbleRollsMax = 3;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_BONUS_ROLLS_MIN, defaultValue = "0", lang = "cfg.general.BonusRollsMin")
	@Comment("The minimum number of bonus rolls because of player luck")
	@MinMaxInt(min = 0)
	public static int bonusRollsMin = 0;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_BONUS_ROLLS_MAX, defaultValue = "1", lang = "cfg.general.BonusRollsMax")
	@Comment("The maximum number of bonus rolls because of player luck")
	@MinMaxInt(min = 0)
	public static int bonusRollsMax = 1;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_USE_LUCK, defaultValue = "true", lang = "cfg.general.UseLuck")
	@Comment("Player Luck can influence the drops from rubble")
	@RestartRequired
	public static boolean useLuck = true;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_EXTERNAL_SCRIPTS, defaultValue = "", lang = "cfg.general.ExternalScripts")
	@Comment("Configuration files for customization")
	@RestartRequired
	public static String[] externalScriptFiles = {};

	public static void load(final Configuration config) {

		ConfigProcessor.process(config, ModOptions.class);

		// CATEGORY: General
		config.setCategoryRequiresMcRestart(CATEGORY_GENERAL, true);
		config.setCategoryRequiresWorldRestart(CATEGORY_GENERAL, true);
		config.setCategoryComment(CATEGORY_GENERAL, "General Options");
		config.setCategoryPropertyOrder(CATEGORY_GENERAL, new ArrayList<String>(generalSort));
		config.setCategoryLanguageKey(CATEGORY_GENERAL, "cfg.general.cat.General");

		// CATEGORY: Logging
		config.setCategoryRequiresMcRestart(CATEGORY_LOGGING_CONTROL, false);
		config.setCategoryRequiresWorldRestart(CATEGORY_LOGGING_CONTROL, false);
		config.setCategoryComment(CATEGORY_LOGGING_CONTROL, "Defines how Debris logging will behave");
		config.setCategoryPropertyOrder(CATEGORY_LOGGING_CONTROL, new ArrayList<String>(loggingSort));
		config.setCategoryLanguageKey(CATEGORY_LOGGING_CONTROL, "cfg.logging.cat.Logging");

		// Iterate through the config list looking for properties without
		// comments. These will be scrubbed.
		for (final String cat : config.getCategoryNames())
			scrubCategory(config.getCategory(cat));
	}

	private static void scrubCategory(final ConfigCategory category) {
		final List<String> killList = new ArrayList<String>();
		for (final Entry<String, Property> entry : category.entrySet())
			if (StringUtils.isEmpty(entry.getValue().getComment()))
				killList.add(entry.getKey());

		for (final String kill : killList)
			category.remove(kill);
	}
}
