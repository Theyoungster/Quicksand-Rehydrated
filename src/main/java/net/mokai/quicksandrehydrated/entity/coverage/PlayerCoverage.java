package net.mokai.quicksandrehydrated.entity.coverage;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.Mth.clamp;

public class PlayerCoverage {

    public List<CoverageEntry> coverageEntries;
    public boolean requiresUpdate = false;

    public PlayerCoverage() {
        this.coverageEntries = new ArrayList<>();
    }

    public void addCoverageEntry(CoverageEntry newEntry) {

        // TODO this should be probably optimized in terms of determining
        //  what part of the coverage texture needs updating.
        // Also, it should combine similar coverage entries instead of just replacing.

        // this function is called every tick a player is in a quicksand
        // that itself isn't too terribly bad ... but what IS bad is that requiresUpdate is also always set to true,
        // and the entire coverage texture is redone every tick.

        // my idea was to track *which* coordinates were changed, and then feed those into the update function, but ...
        // never got around to that.

        // See CoverageLayer for the code that actually manages the texture
        // updateTexture(PlayerCoverage cov)

        newEntry.begin = clamp(newEntry.begin, 0, 32);
        newEntry.end = clamp(newEntry.end, 0, 32);

        // sort through the list and decide where to put it

        requiresUpdate = true;
        clearCoverage(newEntry.begin, newEntry.end);
        if (coverageEntries.size() == 0) {
            coverageEntries.add(0, newEntry);
            return;
        }

        int insertionIndex = 0;
        for (int i = 0; i < coverageEntries.size(); i++) {
            CoverageEntry currentEntry = coverageEntries.get(i);
            if (currentEntry.begin > newEntry.begin) {
                insertionIndex = i;
                break;
            }
        }

        coverageEntries.add(insertionIndex, newEntry);

    }



    public void clearCoverage(int begin, int end) {
        // Clears the coverage in the given area

        // Bottom coord is Inclusive
        // Top coord is NOT !

        List<CoverageEntry> entriesToRemove = new ArrayList<>();

        for (CoverageEntry entry : coverageEntries) {

            boolean entryBeginOverlap = entry.begin > begin && entry.begin <= end;
            boolean entryEndOverlap = entry.end < end && entry.end >= begin;

            // this doesn't account correctly for if a coverage needs to be *split* (see below)
            // ‾‾‾\ A
            //    |
            //    | ‾‾\ B
            //    |   |
            //    | __/
            //    |
            // ___/
            // A will just be removed in this case

            if (entryBeginOverlap && entryEndOverlap) {
                entriesToRemove.add(entry);
            }
            else if (entryBeginOverlap) {
                truncateUpperHalf(entry, end);
            }
            else if (entryEndOverlap) {
                truncateLowerHalf(entry, begin);
            }

        }

        coverageEntries.removeAll(entriesToRemove);
    }

    public void truncateLowerHalf(CoverageEntry entry, int pix) {
        // remove the pixels below this. the coordinate at pix is *Removed*.
        // moves bottom coordinate UP
        pix = clamp(pix+1, 0, 31);
        entry.begin = pix;
    }
    public void truncateUpperHalf(CoverageEntry entry, int pix) {
        // remove the pixels above this. the coordinate at pix is *Removed*
        // Moves top coordinate DOWN
        pix = clamp(pix-1, 0, 31);
        entry.end = pix;
    }


}
