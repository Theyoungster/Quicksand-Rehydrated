package net.mokai.quicksandrehydrated.item;

import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.block.ModBlocks;
import net.mokai.quicksandrehydrated.entity.ModEntityTypes;
import net.mokai.quicksandrehydrated.fluid.ModFluids;
import net.mokai.quicksandrehydrated.item.custom.Rope;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, QuicksandRehydrated.MOD_ID);

    public static final RegistryObject<Item> CRANBERRY = ITEMS.register("cranberry",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(1).saturationMod(1f).fast().build())));

    public static final RegistryObject<Item> ROPE = ITEMS.register("rope",
            () -> new Rope(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DRY_QUICKSAND_BUCKET = ITEMS.register("dry_quicksand_bucket",
            () -> new BucketItem(ModFluids.DRY_QUICKSAND,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
