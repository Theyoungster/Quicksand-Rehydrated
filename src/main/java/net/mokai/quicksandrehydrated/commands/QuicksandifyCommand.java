package net.mokai.quicksandrehydrated.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

// TODO: clean this up a bit.

public abstract class QuicksandifyCommand {
    private static final int MAX_FILL_AREA = 32768;
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137392_, p_137393_) -> {
        return Component.translatable("commands.fill.toobig", p_137392_, p_137393_);
    });
    static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.fill.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext pContext) {
        pDispatcher.register(Commands.literal("quicksandify").requires((p_137384_) -> {
            return p_137384_.hasPermission(2);
        }).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(Commands.argument("block", BlockStateArgument.block(pContext)).executes((p_137405_) -> {
            return fillBlocks(p_137405_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137405_, "from"), BlockPosArgument.getLoadedBlockPos(p_137405_, "to")), BlockStateArgument.getBlock(p_137405_, "block"), Mode.REPLACE, (Predicate<BlockInWorld>)null);
        }).then(Commands.literal("replace").executes((p_137403_) -> {
            return fillBlocks(p_137403_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137403_, "from"), BlockPosArgument.getLoadedBlockPos(p_137403_, "to")), BlockStateArgument.getBlock(p_137403_, "block"), Mode.REPLACE, (Predicate<BlockInWorld>)null);
        }))))));
    }

    private static int fillBlocks(CommandSourceStack pSource, BoundingBox pArea, BlockInput pNewBlock, Mode pMode, @Nullable Predicate<BlockInWorld> pReplacingPredicate) throws CommandSyntaxException {
        int i = pArea.getXSpan() * pArea.getYSpan() * pArea.getZSpan();
        if (i > 32768) {
            throw ERROR_AREA_TOO_LARGE.create(32768, i);
        } else {
            List<BlockPos> list = Lists.newArrayList();
            ServerLevel serverlevel = pSource.getLevel();
            int j = 0;

            for(BlockPos blockpos : BlockPos.betweenClosed(pArea.minX(), pArea.minY(), pArea.minZ(), pArea.maxX(), pArea.maxY(), pArea.maxZ())) {
                if (pReplacingPredicate == null || pReplacingPredicate.test(new BlockInWorld(serverlevel, blockpos, true))) {
                    BlockInput blockinput = pMode.filter.filter(pArea, blockpos, pNewBlock, serverlevel);
                    if (blockinput != null) {
                        BlockEntity blockentity = serverlevel.getBlockEntity(blockpos);
                        Clearable.tryClear(blockentity);
                        if (blockinput.place(serverlevel, blockpos, 2)) {
                            list.add(blockpos.immutable());
                            ++j;
                        }
                    }
                }
            }

            for(BlockPos blockpos1 : list) {
                Block block = serverlevel.getBlockState(blockpos1).getBlock();
                serverlevel.blockUpdated(blockpos1, block);
            }

            if (j == 0) {
                throw ERROR_FAILED.create();
            } else {
                int finalJ = j;
                pSource.sendSuccess(() -> {
                    return Component.translatable("commands.fill.success", finalJ);
                }, true);
                return j;
            }
        }
    }

    static enum Mode {
        REPLACE((A, B, C, D) -> {return C;});

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter pFilter) {
            this.filter = pFilter;
        }
    }
}