package net.mokai.quicksandrehydrated.registry;

import net.minecraftforge.common.ForgeSpawnEggItem;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.item.custom.Rope;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Iterator;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, QuicksandRehydrated.MOD_ID);


    public static final RegistryObject<Item> CRANBERRY = ITEMS.register("cranberries", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(1f).fast().build())));

    public static final RegistryObject<Item> ROPE = ITEMS.register("rope", () -> new Rope(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MUSIC_DISC = ITEMS.register("music_disc_flight", () -> new RecordItem(1, ModSounds.FLIGHT_DISK, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1540));

    public static final RegistryObject<SpawnEggItem> HUNNIBEE_SPAWN_EGG = ITEMS.register("hunnibee_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.HUNNIBEE, 0x000000, 0xFFFF00, new Item.Properties()));




    public static Iterator<RegistryObject<Item>> getItemList() {
        return ITEMS.getEntries().iterator();
    }
    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }

}
