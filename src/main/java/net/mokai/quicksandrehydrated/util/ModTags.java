package net.mokai.quicksandrehydrated.util;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> QUICKSAND_DROWNABLE
                = tag("quicksand_drownable");


        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(QuicksandRehydrated.MOD_ID, name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }
}
