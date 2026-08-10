package net.mokai.quicksandrehydrated.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/** Packet helpers for this mod's recipes, whose JSON outputs contain only an item and count. */
final class RecipeNetworkUtil {
    private RecipeNetworkUtil() {
    }

    static void writeOutput(FriendlyByteBuf buf, ItemStack output) {
        buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(output.getItem()));
        buf.writeVarInt(output.getCount());
    }

    static ItemStack readOutput(FriendlyByteBuf buf) {
        return new ItemStack(BuiltInRegistries.ITEM.get(buf.readResourceLocation()), buf.readVarInt());
    }
}
