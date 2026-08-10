package net.mokai.quicksandrehydrated.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.entity.playerStruggling;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.networking.packet.CoverageSyncS2CPacket;

import java.util.List;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent e) {
        if (e.getType() == ModEntityTypes.MUDOLOGER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = e.getTrades();
            int villagerLevel = 1;

            addSimpleTrade(e, 1,16, ModItems.MUSIC_DISC.get(), 1, 4);
            addSimpleTrade(e, 1, 4, Items.SLIME_BALL, 1, 16);
        }
    }

    public static void addTrade(VillagerTradesEvent e, int villagerLevel, ItemLike input, int inputCount, ItemLike product, int productCount, int retrades) {
        e.getTrades().get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                new ItemStack(input, inputCount), new ItemStack(product, productCount), retrades, 8, 0.02f));
    }
    public static void addSimpleTrade(VillagerTradesEvent e, int villagerLevel, int price, ItemLike product, int productCount, int retrades) {
        e.getTrades().get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, price), new ItemStack(product, productCount), retrades, 8, 0.02f));
    }

    @SubscribeEvent
    public static void playerLogIn(PlayerEvent.PlayerLoggedInEvent e) {
        // Coverage sync is temporarily disabled while network codec issues are diagnosed.
    }

    @SubscribeEvent
    public static void mossyPillowDamage(LivingHurtEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player player && player.getMainHandItem().is(ModItems.MOSSY_PILLOW.get())) {
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent
    public static void playerStartTracking(PlayerEvent.StartTracking e) {
        // Coverage sync is temporarily disabled while network codec issues are diagnosed.
    }

}
