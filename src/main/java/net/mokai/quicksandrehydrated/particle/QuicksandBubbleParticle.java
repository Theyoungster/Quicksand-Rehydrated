package net.mokai.quicksandrehydrated.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class QuicksandBubbleParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected QuicksandBubbleParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                                      SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.friction = 0.8F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= (float) (1.5F + (this.random.nextDouble()*0.5));;
        this.lifetime = 15 + this.random.nextInt(10);
        this.setSpriteFromAge(spriteSet);
        this.sprites = spriteSet;

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new QuicksandBubbleParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
