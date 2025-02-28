package net.mokai.quicksandrehydrated.entity.coverage;

import net.minecraft.resources.ResourceLocation;

public class CoverageEntry {

    public int begin;
    public int end;

    public ResourceLocation texture;

    public CoverageEntry(int begin, int end, ResourceLocation tex) {
        this.begin = begin;
        this.end = end;
        this.texture = tex;
    }

}
