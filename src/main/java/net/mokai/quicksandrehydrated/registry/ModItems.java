package net.mokai.quicksandrehydrated.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.item.custom.QuicksandBook;
import net.mokai.quicksandrehydrated.item.custom.Rope;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mokai.quicksandrehydrated.item.custom.potion.QuicksandPotion;
import net.mokai.quicksandrehydrated.item.custom.potion.QuicksandPotionThrowable;

import javax.management.ObjectInstance;
import java.util.Iterator;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<Item> CRANBERRY = ITEMS.register("cranberries", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(1f).fast().build())));
    public static final RegistryObject<Item> ROPE = ITEMS.register("rope", () -> new Rope(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MUSIC_DISC = ITEMS.register("music_disc_flight", () -> new RecordItem(1, ModSounds.FLIGHT_DISK, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1540));
    public static final RegistryObject<SpawnEggItem> HUNNIBEE_SPAWN_EGG = ITEMS.register("hunnibee_spawn_egg", () -> new ForgeSpawnEggItem(ModEntityTypes.HUNNIBEE, 0x010101, 0xFFFF00, new Item.Properties()));
    public static final RegistryObject<Item> QUICKSAND_BOOK = ITEMS.register("quicksand_book", () -> new QuicksandBook(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> QUICKSAND_POTION = ITEMS.register("potion_of_sinking", () -> new QuicksandPotion(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> QUICKSAND_SPLASH_POTION = ITEMS.register("splash_potion_of_sinking", () -> new QuicksandPotionThrowable(new Item.Properties().stacksTo(16)));

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, QuicksandRehydrated.MOD_ID);


    //public static final RegistryObject<Potion> QUICKSAND_POTION = POTIONS.register("sinking_potion", () -> new Potion(new MobEffectInstance(MobEffects.WEAKNESS, 900, 1), new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900, 1)));
    public static final RegistryObject<Potion> LONG_QUICKSAND_POTION = POTIONS.register("long_sinking_potion", () -> new Potion(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 1), new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1)));
    public static final RegistryObject<Potion> STRONG_QUICKSAND_POTION = POTIONS.register("strong_sinking_potion", () -> new Potion(new MobEffectInstance(MobEffects.WEAKNESS, 900, 2), new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900, 2)));

    public static Iterator<RegistryObject<Item>> getItemList() {
        return ITEMS.getEntries().iterator();
    }
    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); POTIONS.register(eventBus);}



}
