package net.mokai.quicksandrehydrated.registry;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.item.MossyPillowItem;
import net.mokai.quicksandrehydrated.item.QuicksandBook;
import net.mokai.quicksandrehydrated.item.Rope;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.item.potion.QuicksandPotion;
import net.mokai.quicksandrehydrated.item.potion.QuicksandPotionThrowable;
import net.mokai.quicksandrehydrated.item.CuriosCharmItem;
import net.mokai.quicksandrehydrated.item.CharmItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<Item> CRANBERRY = ITEMS.register("cranberries",
            () -> new ItemNameBlockItem(ModBlocks.CRANBERRY_BUSH.get(),
                    new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(1f).fast().build())));
    public static final RegistryObject<Item> ROPE = ITEMS.register("rope", () -> new Rope(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MOSSY_PILLOW =
            ITEMS.register("mossy_pillow", () -> new MossyPillowItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MUSIC_DISC =
            ITEMS.register("music_disc_flight", () -> new RecordItem(1, ModSounds.FLIGHT_DISK, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1540));
    public static final RegistryObject<Item> QUICKSAND_BOOK =
            ITEMS.register("quicksand_book", () -> new QuicksandBook(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> QUICKSAND_POTION =
            ITEMS.register("potion_of_sinking", () -> new QuicksandPotion(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> QUICKSAND_SPLASH_POTION =
            ITEMS.register("splash_potion_of_sinking", () -> new QuicksandPotionThrowable(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BREATHING_REED =
            ITEMS.register("breathing_reed", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SNORKEL_MASK =
            ITEMS.register("snorkel",
                    () -> new ArmorItem(ArmorMaterials.IRON , ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1) ));

    public static final RegistryObject<Item> HEAVY_CHARM =
            ITEMS.register("heavy_charm", () -> createCharmItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHT_CHARM =
            ITEMS.register("light_charm", () -> createCharmItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<SpawnEggItem> HUNNIBEE_SPAWN_EGG =
            ITEMS.register("hunnibee_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.HUNNIBEE, 0x1B1B1B, 0xFFFF00, new Item.Properties()));


    public static final RegistryObject<SpawnEggItem> TAR_SLIME_SPAWN_EGG =
            ITEMS.register("tar_slime_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.TAR_SLIME, 0x0A0A0A, 0x2A2A2A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> MUDDY_BLOB_SPAWN_EGG =
            ITEMS.register("muddy_blob_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.MUDDY_BLOB, 0x5A402A, 0x3B2A1A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> SAND_BLOB_SPAWN_EGG =
            ITEMS.register("sand_blob_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.SAND_BLOB, 0xD8C58B, 0xB9A36A, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> CAVE_BLOB_SPAWN_EGG =
            ITEMS.register("cave_blob_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.CAVE_BLOB, 0x38CE33, 0xFFFF00, new Item.Properties()));

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, QuicksandRehydrated.MOD_ID);

    public static Iterator<RegistryObject<Item>> getItemList() {
        return ITEMS.getEntries().iterator();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        // POTIONS.register(eventBus);
    }

    private static Collection<ItemStack> REGLIST;

    public static Collection<ItemStack> setupCreativeGroups() {
        REGLIST = new ArrayList<>();
        addItem(CRANBERRY);
        addItem(ROPE);
        addItem(MOSSY_PILLOW);
        addItem(MUSIC_DISC);
        addItem(QUICKSAND_BOOK);
        addItem(QUICKSAND_POTION);
        addItem(QUICKSAND_SPLASH_POTION);
        addItem(BREATHING_REED);
        addItem(SNORKEL_MASK);
        addItem(HEAVY_CHARM);
        addItem(LIGHT_CHARM);
        addEggItem(HUNNIBEE_SPAWN_EGG);
        addEggItem(TAR_SLIME_SPAWN_EGG);
        addEggItem(MUDDY_BLOB_SPAWN_EGG);
        addEggItem(SAND_BLOB_SPAWN_EGG);
        addEggItem(CAVE_BLOB_SPAWN_EGG);
        return REGLIST;
    }

    public static void addItem(RegistryObject<Item> b) { REGLIST.add(b.get().getDefaultInstance()); }
    public static void addEggItem(RegistryObject<SpawnEggItem> b) { REGLIST.add(b.get().getDefaultInstance()); }

    private static Item createCharmItem(Item.Properties properties) {
        if (ModList.get().isLoaded("curios")) {
            return new CuriosCharmItem(properties);
        }
        return new CharmItem(properties);
    }
}
