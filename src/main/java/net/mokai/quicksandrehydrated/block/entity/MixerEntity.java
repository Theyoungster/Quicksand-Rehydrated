package net.mokai.quicksandrehydrated.block.entity;

import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.mokai.quicksandrehydrated.block.custom.MixerBlock;
import net.mokai.quicksandrehydrated.networking.ModMessages;
import net.mokai.quicksandrehydrated.networking.packet.FluidSyncS2CPacket;
import net.mokai.quicksandrehydrated.networking.packet.ItemStackSyncS2CPacket;
import net.mokai.quicksandrehydrated.recipe.FluidMixerRecipes;
import net.mokai.quicksandrehydrated.screen.MixerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openjdk.nashorn.internal.objects.annotations.Property;

import java.util.Map;
import java.util.Optional;

public class MixerEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new ItemStackSyncS2CPacket(this, worldPosition));
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> true;
                case 1 -> true;
                case 2 -> true;
                case 3 -> true;
                default -> super.isItemValid(slot, stack);
                //stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent(); --- old check for Case 0
            };
        }

    };


    private final FluidTank FLUID_TANK = new FluidTank( 4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new FluidSyncS2CPacket(this.fluid, worldPosition));
            }
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };


    private final FluidTank OUTPUT_TANK = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new FluidSyncS2CPacket(this.fluid, worldPosition));
            }
        }
    };

    public void setInFluid(FluidStack stack) {
        this.FLUID_TANK.setFluid(stack);
    }

    public FluidStack getInFluidStack() {
        return this.FLUID_TANK.getFluid();
    }

    public void setOutFluid(FluidStack stack) {
        this.OUTPUT_TANK.setFluid(stack);
    }

    public FluidStack getOutFluidStack() {
        return this.OUTPUT_TANK.getFluid();
    }

    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0 || index == 1,
                            (index, stack) -> itemHandler.isItemValid(0, stack) || itemHandler.isItemValid(1, stack))));

    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();


    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public MixerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIXER.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MixerEntity.this.progress;
                    case 1 -> MixerEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MixerEntity.this.progress = value;
                    case 1 -> MixerEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Fluid Mixer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToClients(new FluidSyncS2CPacket(this.getInFluidStack(), worldPosition));
        return new MixerMenu(id, inventory, this, this.data);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            if(directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(MixerBlock.FACING);

                if(side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }

        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("mixing_progress", this.progress);
        //TODO: figure out how to store multiple tanks in JSON.
        // This would be too easy, but using it multiple times overrides previous uses.
        // nbt = FLUID_TANK.writeToNBT(nbt);
        // You also cannot seem to grab a FluidStack from a string.

        CompoundTag tanks = new CompoundTag();
        if (!FLUID_TANK.isEmpty()) {
            CompoundTag tankroot = new CompoundTag();
            tankroot = FLUID_TANK.writeToNBT(tankroot);
            tanks.put("Tank1", tankroot);
            //nbt = serializeNBT(FLUID_TANK.getFluid().writeToPacket());
            System.out.println(tankroot);
        }
        if (!OUTPUT_TANK.isEmpty()) {
            CompoundTag tankroot = new CompoundTag();
            tankroot = OUTPUT_TANK.writeToNBT(tankroot);
            tanks.put("Tank2", tankroot);
            System.out.println(tankroot);
        }


        //nbt.put("Tank 1", FLUID_TANK.getFluid().writeToNBT(nbt));
        //nbt.putInt("Amount 1", FLUID_TANK.getFluidAmount());
        //nbt.put("Tank 2", OUTPUT_TANK.getFluid().writeToNBT(nbt));
        //nbt.putInt("Amount 2", OUTPUT_TANK.getFluidAmount());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("mixing_progress");
        //FLUID_TANK.readFromNBT(nbt.getCompound("Tank 1"));

    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MixerEntity pEntity) {
        if(level.isClientSide()) {
            return;
        }


        if(hasRecipe(pEntity)) {
            pEntity.progress++;
            setChanged(level, pos, state);

            if(pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }

        if(hasFluidItemInSourceSlot(pEntity)) {
            transferItemFluidToFluidTank(pEntity);
        }
    }

    private static void transferItemFluidToFluidTank(MixerEntity pEntity) {
        pEntity.itemHandler.getStackInSlot(0).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            int drainAmount = Math.min(pEntity.FLUID_TANK.getSpace(), 1000);

            FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
            if(pEntity.FLUID_TANK.isFluidValid(stack)) {
                stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                fillTankWithFluid(pEntity, stack, handler.getContainer());
            }
        });
    }

    private static void fillTankWithFluid(MixerEntity pEntity, FluidStack stack, ItemStack container) {
        pEntity.FLUID_TANK.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        //pEntity.OUTPUT_TANK.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        pEntity.itemHandler.extractItem(0, 1, false);
        pEntity.itemHandler.insertItem(0, container, false);
    }

    private static boolean hasFluidItemInSourceSlot(MixerEntity pEntity) {
        return pEntity.itemHandler.getStackInSlot(0).getCount() > 0;
    }


    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(MixerEntity pEntity) {
        Level level = pEntity.level;
        SimpleContainer inventory = new SimpleContainer(pEntity.itemHandler.getSlots());
        for (int i = 0; i < pEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, pEntity.itemHandler.getStackInSlot(i));
        }

        Optional<FluidMixerRecipes> recipe = level.getRecipeManager()
                .getRecipeFor(FluidMixerRecipes.Type.INSTANCE, inventory, level);

        if(hasRecipe(pEntity)) {
            pEntity.FLUID_TANK.drain(recipe.get().getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
            pEntity.itemHandler.extractItem(1, 1, false);
            pEntity.itemHandler.setStackInSlot(4, new ItemStack(recipe.get().getResultItem().getItem(),
                    pEntity.itemHandler.getStackInSlot(4).getCount() + 1));

            pEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(MixerEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<FluidMixerRecipes> recipe = level.getRecipeManager()
                .getRecipeFor(FluidMixerRecipes.Type.INSTANCE, inventory, level);


        return recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem())
                && hasCorrectFluidInTank(entity, recipe) && hasCorrectFluidAmountInTank(entity, recipe);
    }


    private static boolean hasCorrectFluidAmountInTank(MixerEntity entity, Optional<FluidMixerRecipes> recipe) {
        return entity.FLUID_TANK.getFluidAmount() >= recipe.get().getFluid().getAmount();
    }

    private static boolean hasCorrectFluidInTank(MixerEntity entity, Optional<FluidMixerRecipes> recipe) {
        return recipe.get().getFluid().equals(entity.FLUID_TANK.getFluid());
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        return inventory.getItem(3).getItem() == stack.getItem() || inventory.getItem(3).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount();
    }
}
