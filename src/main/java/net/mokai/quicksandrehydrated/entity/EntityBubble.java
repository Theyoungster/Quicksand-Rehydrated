package net.mokai.quicksandrehydrated.entity;


import java.util.List;
import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.SoundDefinition;

public class EntityBubble extends Entity
{
    public long ticksWorld;
    public int liveTime;
    public float size;
    public Block block;
    public int meta;
    public float rotate;
    public double scale;

    public EntityBubble(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }




/*
    public EntityBubble(World world){
        super(world);
        this.setSize(1.0F, 1.0F);
        this.ticksWorld = Sys.getTime();
        if(!world.isRemote){
            this.setDead();
        }
    }

    public EntityBubble(World world, double x, double y, double z, Block bl, int mt, float sz, int tim){
        this(world);
        block=bl;
        meta=mt;
        size=sz;
        rotate=world.rand.nextFloat()*360;
        liveTime=tim;
        double yy=y;
        int xx=(int)Math.floor(x);
        int zz=(int)Math.floor(z);
        for (int i = 0; i <= 5; ++i){
            if ((!world.getBlock(xx,(int)Math.floor(y+0.5D+i),zz).getMaterial().isOpaque())&&(!world.getBlock(xx,(int)Math.floor(y+0.5D+i),zz).getMaterial().isLiquid())){
                yy=y+i;
                this.ticksWorld += i*400;
                break;
            }
            if (world.getBlock(xx,(int)Math.floor(y+0.5D+i),zz) != bl){
                this.setDead();
                break;
            }
        }
        int yyy=(int)Math.floor(yy-0.5D);
        if ((world.getBlock(xx,yyy,zz)).getMaterial().isLiquid()){
            if (world.getBlockMetadata(xx,yyy,zz)!=0){
                this.setDead();
            }
        }
        this.setPosition(x, yy, z);
    }

    public EntityBubble(World world, double x, double y, double z, Block bl, int mt, float sz, int tim, int dly){
        this(world,x,y,z,bl,mt,sz,tim);
        this.ticksWorld += dly;
    }
*/

    protected void entityInit() {}

    //Called to update the entity's position/logic.
    //
    public void tick(){
        super.tick();
        if(this.level.getGameTime() % 32 == 0){
            if (this.level.getBlockState(blockPosition()).getBlock() != this.block){ // Every 32 ticks, test the block beneath us; if it isn't our block anymore, discard the bubble.
                this.discard();
                return;
            }
        //}
        //if(Sys.getTime()-this.ticksWorld>liveTime){
            //for (int i = 0; i < 3*size; ++i){
              //  double xx=this.worldObj.rand.nextFloat()*0.2*size-0.1*size;
                //double zz=this.worldObj.rand.nextFloat()*0.2*size-0.1*size;
                //this.level.spawnParticle("blockcrack_" +Block.getIdFromBlock(this.block) +"_" + this.meta,this.posX+xx,this.posY,this.posZ+zz,0,0,0);
            //}
            this.level.playSound(null,
                    this.position().x,
                    this.position().y,
                    this.position().z,
                    SoundEvent.createFixedRangeEvent(ResourceLocation.tryBuild("minecraft", "lava_bubble"), 1f),
                    SoundSource.AMBIENT,
                    0.5F + rand() * 0.25F,
                    (0.5F + rand() * 0.5F)/size);
            //this.level.playSound(this.posX,this.posY,this.posZ, "mob.slime.attack", 0.15F, (1.25F + this.worldObj.rand.nextFloat() * 0.5F)/size, false);
            this.discard();
        }
    }

    private float rand() {
        return RandomSource.createNewThreadLocalInstance().nextFloat();
    }

    public float getShadowSize(){
        return 0.0F;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }
}

