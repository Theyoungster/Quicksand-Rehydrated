package net.mokai.quicksandrehydrated.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class Keybinding {
    public static final String KEY_CATEGORY_QUICKSAND = "key.category.quicksandrehydrated.quicksand";
    public static final String KEY_STRUGGLE = "key.category.quicksandrehydrated.struggle";


    public static final KeyMapping STRUGGLE_KEY = new KeyMapping(KEY_STRUGGLE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, KEY_CATEGORY_QUICKSAND);



}
