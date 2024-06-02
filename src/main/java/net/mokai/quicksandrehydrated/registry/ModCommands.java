package net.mokai.quicksandrehydrated.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mokai.quicksandrehydrated.QuicksandRehydrated;
import net.mokai.quicksandrehydrated.commands.ForceEnchantCommand;
import net.mokai.quicksandrehydrated.commands.QuicksandifyCommand;

@Mod.EventBusSubscriber(modid = QuicksandRehydrated.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        Commands.CommandSelection selection = event.getCommandSelection();
        CommandBuildContext ctx = event.getBuildContext();
        ForceEnchantCommand.register(dispatcher, ctx);
        QuicksandifyCommand.register(dispatcher, ctx);
    }

}
