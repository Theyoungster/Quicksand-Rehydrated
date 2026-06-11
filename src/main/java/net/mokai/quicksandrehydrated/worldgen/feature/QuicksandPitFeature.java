package net.mokai.quicksandrehydrated.worldgen.feature;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Feature that generates a quicksand pit with customizable block type and shape.
 * The shape is highly irregular and random, based on noise and configuration parameters.
 */
public class QuicksandPitFeature extends Feature<QuicksandPitConfiguration> {
    public QuicksandPitFeature(Codec<QuicksandPitConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<QuicksandPitConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        QuicksandPitConfiguration config = context.config();

        // We remove the check that verifies whether the block below the origin is sand.
        // Now the feature can be generated on any type of terrain.
        BlockState belowState = level.getBlockState(origin.below());
        if (belowState.isAir()) {
            System.out.println("[QuicksandPit] Generation failed: Air below origin at " + origin);
            return false;
        }

        // Get the block to use for the pit from the configuration
        Block pitBlock = config.block;
        BlockState pitBlockState = pitBlock.defaultBlockState();

        // Get the surface block, if it exists
        Block surfaceBlock;
        if (config.hasSurface) {
            surfaceBlock = config.surfaceBlock;
        }
        else {
            surfaceBlock = config.block;
        }
        BlockState surfaceBlockState = surfaceBlock.defaultBlockState();

        // Generate a quicksand pit with random size based on configuration
        // Use a wider range for more variable sizes
        int radius = random.nextIntBetweenInclusive(config.minRadius, config.maxRadius);
        
        // Determine the pit depth based on configuration (minimum of 3)
        int pitDepth = Math.max(3, random.nextIntBetweenInclusive(config.minDepth, config.maxDepth));
        
        // Find the surface level at the origin
        int originSurfaceY = findSurfaceY(level, origin);
        if (originSurfaceY == -1) {
            return false; // Couldn't find a valid surface
        }
        
        // Verify that the surface is within the specified height range.
        if (originSurfaceY < config.minHeight || originSurfaceY > config.maxHeight) {
            System.out.println("[QuicksandPit] Surface outside height range at " + origin + ", y=" + originSurfaceY + 
                               " (range: " + config.minHeight + "-" + config.maxHeight + ")");
            return false;
        }
        
        // Check that the area is flat - we use a simpler check
        if (!isAreaFlat(level, origin, radius, originSurfaceY)) {
            System.out.println("[QuicksandPit] Area not flat enough at " + origin);
            return false;
        }
        
        // Verify that the area is surrounded by the blocks specified in the JSON file.
        if (!isAreaSurroundedByReplaceableBlocks(level, origin, radius, originSurfaceY, config.replaceableBlocks)) {
            System.out.println("[QuicksandPit] Area not surrounded by replaceable blocks at " + origin);
            return false;
        }
        
        // Set the pit level AT the surface level (not affossato)
        // We record the exact height of the surface to ensure that the puddle is completely flat.
        int pitLevel = originSurfaceY;
        
        System.out.println("[QuicksandPit] Setting pit level to exact surface height: " + pitLevel);
        
        // We no longer check that the area is surrounded by sand.
        // Now the feature can be generated in any area.
        System.out.println("[QuicksandPit] Attempting to generate at " + origin);
        
        boolean placed = false;
        
        // Generate 3D Perlin-like noise for more natural, lake-like shapes
        // The size of the noise map depends on the radius
        int noiseSize = radius * 2 + 5;
        double[][][] noise3D = new double[noiseSize][noiseSize][8]; // 3D noise for more natural shapes
        
        // Generate initial 3D random noise
        for (int x = 0; x < noiseSize; x++) {
            for (int z = 0; z < noiseSize; z++) {
                for (int y = 0; y < 8; y++) {
                    // Generate a random value between 0.0 and 1.0
                    noise3D[x][z][y] = random.nextDouble();
                }
            }
        }
        
        // Smooth the 3D noise
        for (int smoothPass = 0; smoothPass < 2; smoothPass++) {
            double[][][] smoothedNoise = new double[noiseSize][noiseSize][8];
            
            for (int x = 0; x < noiseSize; x++) {
                for (int z = 0; z < noiseSize; z++) {
                    for (int y = 0; y < 8; y++) {
                        double sum = 0;
                        int count = 0;
                        
                        // Average with neighbors for smoothing
                        for (int nx = -1; nx <= 1; nx++) {
                            for (int nz = -1; nz <= 1; nz++) {
                                for (int ny = -1; ny <= 1; ny++) {
                                    int idx = x + nx;
                                    int idz = z + nz;
                                    int idy = y + ny;
                                    
                                    if (idx >= 0 && idx < noiseSize && 
                                        idz >= 0 && idz < noiseSize &&
                                        idy >= 0 && idy < 8) {
                                        sum += noise3D[idx][idz][idy];
                                        count++;
                                    }
                                }
                            }
                        }
                        
                        smoothedNoise[x][z][y] = sum / count;
                    }
                }
            }
            
            noise3D = smoothedNoise;
        }
        
        // Create a 2D noise map for horizontal shape
        double[][] horizontalNoise = new double[noiseSize][noiseSize];
        for (int x = 0; x < noiseSize; x++) {
            for (int z = 0; z < noiseSize; z++) {
                // Use the middle layer of 3D noise for horizontal shape
                horizontalNoise[x][z] = noise3D[x][z][3];
            }
        }
        
        // Apply irregularity factor from configuration
        float irregularity = config.irregularity;
        
        // Second pass: Determine the shape of the pit first (top-down approach)
        // Create a 2D map of the pit shape and depths
        int mapSize = radius * 2 + 11; // Larger to accommodate the extended radius
        int[][] depthMap = new int[mapSize][mapSize];
        boolean[][] pitShape = new boolean[mapSize][mapSize];
        
        // First determine the 2D shape of the pit (flat top layer)
        System.out.println("[QuicksandPit] Determining pit shape with radius " + radius);
        for (int x = -radius - 5; x <= radius + 5; x++) {
            for (int z = -radius - 5; z <= radius + 5; z++) {
                // Get noise value for this position
                double noiseValue = getNoise(x, z, horizontalNoise, radius);
                
                // Add more chaos to the noise value
                noiseValue = noiseValue * 0.6 + random.nextDouble() * 0.4;
                
                // Calculate distance with a more natural influence
                double distortionFactor = 0.8 + noiseValue * 0.4; // Less extreme variation
                double distance = Math.sqrt(x * x + z * z) / (radius * distortionFactor);
                
                // Apply gentle distortion based on angle for slightly non-circular shapes
                double angle = Math.atan2(z, x);
                double angleDistortion = Math.sin(angle * 2 + noiseValue * Math.PI) * 0.15;
                distance *= (1.0 + angleDistortion);
                
                // Determine if this position is part of the pit
                boolean isPitBlock = distance < 1.0 + (noiseValue - 0.5) * 0.2;
                
                // Store in our 2D map
                int mapX = x + radius + 5;
                int mapZ = z + radius + 5;
                
                if (mapX >= 0 && mapX < mapSize && mapZ >= 0 && mapZ < mapSize) {
                    pitShape[mapX][mapZ] = isPitBlock;
                    
                    if (isPitBlock) {
                        // Calculate depth based on distance from center
                        double normalizedDistance = Math.min(1.0, distance);
                        
                        // Invert the distance so center is deepest
                        double depthFactor = 1.0 - normalizedDistance;
                        
                        // Apply a curve to make the depth change more gradual at edges and steeper in center
                        depthFactor = Math.pow(depthFactor, 1.5);
                        
                        // Calculate local depth based on distance from center
                        int localDepth = (int) Math.max(3, Math.round(pitDepth * depthFactor));
                        
                        // Add a small amount of noise to avoid perfectly smooth depth changes
                        if (random.nextFloat() < 0.3) {
                            localDepth += random.nextIntBetweenInclusive(-1, 1);
                            localDepth = Math.max(3, localDepth); // Ensure minimum depth of 3
                        }
                        
                        depthMap[mapX][mapZ] = localDepth;
                    }
                }
            }
        }
        
        // We create the pool in a single step, not layer by layer.
        System.out.println("[QuicksandPit] Generating pit in a single pass");
        
        // For each position on our map
        for (int mapX = 0; mapX < mapSize; mapX++) {
            for (int mapZ = 0; mapZ < mapSize; mapZ++) {
                // If this position is part of the pool
                if (pitShape[mapX][mapZ]) {
                    // Calculate the actual coordinates
                    int worldX = origin.getX() + (mapX - (radius + 5));
                    int worldZ = origin.getZ() + (mapZ - (radius + 5));
                    
                    // Get deep into this position
                    int localDepth = depthMap[mapX][mapZ];
                    
                    int surfaceY = findSurfaceY(level, new BlockPos(worldX, 0, worldZ));

                    // Verify that the height of the ground at this location is exactly equal to pitLevel.
                    // This prevents the puddle from spreading over different height levels.
                    int localSurfaceY = findSurfaceY(level, new BlockPos(worldX, 0, worldZ));
                    if (localSurfaceY != pitLevel) {
                        // If the height is different, we skip this position.
                        continue;
                    }
                    
                    BlockPos surfacePos = new BlockPos(worldX, surfaceY, worldZ);

                    level.setBlock(surfacePos, surfaceBlockState, 3);
                    
                    // Now let's place all the quicksand blocks at once, from top to bottom.
                    for (int depth = 1; depth < localDepth; depth++) {
                        int worldY = pitLevel - depth;
                        BlockPos pos = new BlockPos(worldX, worldY, worldZ);
                        
                        // Place the quicksand block
                        level.setBlock(pos, pitBlockState, 3);
                        placed = true;
                        
                        // Debug
                        if (depth == 0) {
                            System.out.println("[QuicksandPit] Placed surface quicksand at [" + worldX + ", " + worldY + ", " + worldZ + "]");
                        } else {
                            System.out.println("[QuicksandPit] Placed depth " + depth + " quicksand at [" + worldX + ", " + worldY + ", " + worldZ + "]");
                        }
                    }
                }
            }
        }
        
        // Let's add transitions to the edges in a single step
        System.out.println("[QuicksandPit] Adding edge transitions in a single pass");
        
        // Create a map for the edge blocks
        boolean[][] edgeMap = new boolean[mapSize][mapSize];
        int[][] edgeDepthMap = new int[mapSize][mapSize];
        
        // First determine which blocks are edge blocks
        for (int x = -radius - 3; x <= radius + 3; x++) {
            for (int z = -radius - 3; z <= radius + 3; z++) {
                // Calculate distance from center
                double baseDistance = Math.sqrt(x * x + z * z) / radius;
                
                // Apply a small amount of distortion for natural-looking edges
                double angle = Math.atan2(z, x);
                double distortedDistance = baseDistance * (1.0 + Math.sin(angle * 2) * 0.1);
                
                // Work only on the edge area
                if (distortedDistance >= 0.9 && distortedDistance < 1.1) {
                    // Get noise value for this position
                    double noiseValue = getNoise(x, z, horizontalNoise, radius);
                    
                    // Determine if this is an edge block
                    boolean isEdgeBlock = noiseValue < 0.5 && random.nextFloat() < 0.7;
                    
                    // Store in our edge map
                    int mapX = x + radius + 5;
                    int mapZ = z + radius + 5;
                    
                    if (mapX >= 0 && mapX < mapSize && mapZ >= 0 && mapZ < mapSize) {
                        edgeMap[mapX][mapZ] = isEdgeBlock;
                        
                        if (isEdgeBlock) {
                            // Add a shallow depth at the edges (1-2 blocks)
                            int depth = 1;
                            if (distortedDistance < 1.0 && random.nextFloat() < 0.5) {
                                depth = 2;
                            }
                            
                            edgeDepthMap[mapX][mapZ] = depth;
                        }
                    }
                }
            }
        }
        
        // We place the edge blocks in a single step.
        for (int mapX = 0; mapX < mapSize; mapX++) {
            for (int mapZ = 0; mapZ < mapSize; mapZ++) {
                // We place the edge blocks in a single step.
                if (edgeMap[mapX][mapZ]) {
                    // Get deep into this position
                    int edgeDepth = edgeDepthMap[mapX][mapZ];
                    
                    // Calculate the actual coordinates
                    int worldX = origin.getX() + (mapX - (radius + 5));
                    int worldZ = origin.getZ() + (mapZ - (radius + 5));
                    
                    // Verify that the ground height at this location is exactly equal to pitLevel.
                    int localSurfaceY = findSurfaceY(level, new BlockPos(worldX, 0, worldZ));
                    if (localSurfaceY != pitLevel) {
                        // If the height is different, we skip this position.
                        continue;
                    }
                    
                    // Place all edge blocks at once
                    for (int depth = 0; depth <= edgeDepth; depth++) {
                        int worldY = pitLevel - depth;
                        BlockPos edgePos = new BlockPos(worldX, worldY, worldZ);
                        
                        // Place the quicksand block
                        level.setBlock(edgePos, pitBlockState, 3);
                        placed = true;
                        
                        // Debug
                        System.out.println("[QuicksandPit] Placed edge quicksand at depth " + depth + 
                                           " at [" + worldX + ", " + worldY + ", " + worldZ + "]");
                    }
                }
            }
        }
        
        // Add a border around the quicksand pool if enabled in the configuration
        if (config.hasBorder) {
            System.out.println("[QuicksandPit] Adding complete border around the quicksand pit");
            
            // Determine which block to use for the border
            BlockState borderBlockState;
            if (config.borderBlock != null) {
                // Use the specified border block
                borderBlockState = config.borderBlock.defaultBlockState();
            } else {
                // Use the same block as the pit
                borderBlockState = pitBlockState;
            }
            
            // New approach: let's create a complete border around the pool
            // We iterate over all blocks in the pool and check their neighbors.
            for (int mapX = 0; mapX < mapSize; mapX++) {
                for (int mapZ = 0; mapZ < mapSize; mapZ++) {
                    // If this position is a blockage of the pool
                    if (pitShape[mapX][mapZ]) {
                        // We check all adjacent blocks (8 directions).
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                // Let's skip the central block (the pool itself).
                                if (dx == 0 && dz == 0) continue;
                                
                                int neighborX = mapX + dx;
                                int neighborZ = mapZ + dz;
                                
                                // We verify that the neighbor is within the map
                                if (neighborX >= 0 && neighborX < mapSize && neighborZ >= 0 && neighborZ < mapSize) {
                                    // If the neighbor is NOT part of the pool, it is a potential edge block.
                                    if (!pitShape[neighborX][neighborZ]) {
                                        // Calculate the actual coordinates in the world
                                        int worldX = origin.getX() + (neighborX - (radius + 5));
                                        int worldZ = origin.getZ() + (neighborZ - (radius + 5));
                                        
                                        // We find the height of the surface at this position
                                        int localSurfaceY = findSurfaceY(level, new BlockPos(worldX, 0, worldZ));
                                        
                                        // If we haven't found a suitable surface, we skip it.
                                        if (localSurfaceY == -1) continue;
                                        
                                        // We check that the height is reasonably close to the pool.
                                        if (Math.abs(localSurfaceY - pitLevel) > 2) continue;
                                        
                                        // We determine the depth of the edge based on the distance from the center.
                                        int borderDepth = 3; // Profondità predefinita
                                        
                                        // We place the edge blocks from the surface to the specified depth.
                                        for (int depth = 0; depth <= borderDepth; depth++) {
                                            int worldY = localSurfaceY - depth;
                                            BlockPos borderPos = new BlockPos(worldX, worldY, worldZ);
                                            
                                            // We verify that the block is replaceable.
                                            BlockState existingState = level.getBlockState(borderPos);
                                            if (isReplaceable(existingState, config.replaceableBlocks)) {
                                                // Place the edge block
                                                level.setBlock(borderPos, borderBlockState, 3);
                                                placed = true;
                                                
                                                // Debug
                                                System.out.println("[QuicksandPit] Placed border block at depth " + depth + 
                                                                   " at [" + worldX + ", " + worldY + ", " + worldZ + "]");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return placed;
    }
    
    /**
     * Check if the area is surrounded by the blocks specified in the JSON file
     * @return true if the area is surrounded by the specified blocks
     */
    private boolean isAreaSurroundedByReplaceableBlocks(WorldGenLevel level, BlockPos origin, int radius, int surfaceY, List<Block> replaceableBlocks) {
        // Check the outer perimeter
        int checkRadius = radius + 2; // Check a little further outside.
        int validBlockCount = 0;
        int totalChecks = 0;
        
        System.out.println("[QuicksandPit] Checking if area is surrounded by replaceable blocks: " + replaceableBlocks);
        
        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                // Just check the perimeter
                if (Math.abs(x) == checkRadius || Math.abs(z) == checkRadius) {
                    BlockPos checkPos = new BlockPos(origin.getX() + x, surfaceY, origin.getZ() + z);
                    BlockState state = level.getBlockState(checkPos);
                    totalChecks++;
                    
                    // Check if the block is in the list of replaceable blocks
                    boolean isValidBlock = false;
                    for (Block block : replaceableBlocks) {
                        if (state.is(block)) {
                            isValidBlock = true;
                            break;
                        }
                    }
                    
                    if (isValidBlock) {
                        validBlockCount++;
                    }
                }
            }
        }
        
        // Require that at least 70% of the perimeter be composed of the specified blocks.
        float validBlockPercentage = (float) validBlockCount / totalChecks;
        boolean result = validBlockPercentage >= 0.7f;
        
        if (!result) {
            System.out.println("[QuicksandPit] Valid block percentage too low: " + (validBlockPercentage * 100) + 
                               "% (need 70%). Found " + validBlockCount + " valid blocks out of " + totalChecks);
        } else {
            System.out.println("[QuicksandPit] Area is surrounded by replaceable blocks: " + 
                               (validBlockPercentage * 100) + "% valid blocks");
        }
        
        return result;
    }
    
    /**
     * Check if the area is surrounded by sand (legacy method)
     * @return true if the area is surrounded by sand
     */
    private boolean isSurroundedBySand(WorldGenLevel level, BlockPos origin, int radius, int surfaceY) {
        // Check the outer perimeter
        int checkRadius = radius + 2; // Check a little further outside
        int sandCount = 0;
        int totalChecks = 0;
        
        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                // Just check the perimeter
                if (Math.abs(x) == checkRadius || Math.abs(z) == checkRadius) {
                    BlockPos checkPos = new BlockPos(origin.getX() + x, surfaceY, origin.getZ() + z);
                    BlockState state = level.getBlockState(checkPos);
                    totalChecks++;
                    
                    // Count how many sand blocks there are on the perimeter.
                    if (state.is(BlockTags.SAND)) {
                        sandCount++;
                    }
                }
            }
        }
        
        // Require that at least 70% of the perimeter be sand.
        float sandPercentage = (float) sandCount / totalChecks;
        boolean result = sandPercentage >= 0.7f;
        
        if (!result) {
            System.out.println("[QuicksandPit] Sand percentage too low: " + (sandPercentage * 100) + "% (need 70%)");
        }
        
        return result;
    }
    
    /**
     * Applies multiple smoothing passes to the noise map
     */
    private double[][] smoothNoise(double[][] noiseMap, int passes) {
        double[][] result = noiseMap;
        
        for (int pass = 0; pass < passes; pass++) {
            double[][] smoothed = new double[noiseMap.length][noiseMap[0].length];
            
            for (int x = 0; x < noiseMap.length; x++) {
                for (int z = 0; z < noiseMap[0].length; z++) {
                    // Average with neighbors for smoothing
                    double sum = 0;
                    int count = 0;
                    
                    for (int nx = -1; nx <= 1; nx++) {
                        for (int nz = -1; nz <= 1; nz++) {
                            int idx = x + nx;
                            int idz = z + nz;
                            
                            if (idx >= 0 && idx < noiseMap.length && idz >= 0 && idz < noiseMap[0].length) {
                                sum += result[idx][idz];
                                count++;
                            }
                        }
                    }
                    
                    smoothed[x][z] = sum / count;
                }
            }
            
            result = smoothed;
        }
        
        return result;
    }
    
    /**
     * Gets the noise value at the given position
     */
    private double getNoise(int x, int z, double[][] noiseMap, int radius) {
        int idx = x + radius + 2;
        int idz = z + radius + 2;
        
        if (idx >= 0 && idx < noiseMap.length && idz >= 0 && idz < noiseMap[0].length) {
            return noiseMap[idx][idz];
        }
        
        return 0.5; // Default value if out of bounds
    }
    
    /**
     * Calculates a noise-modified distance for more irregular shapes
     * This is used by the old algorithm and kept for compatibility
     */
    private double getDistanceWithNoise(int x, int z, double[][] noiseMap, int radius, float irregularity) {
        // Get the base Euclidean distance
        double baseDistance = Math.sqrt(x * x + z * z);
        
        // Get noise value for this position
        double noise = getNoise(x, z, noiseMap, radius);
        
        // Apply noise to the distance based on irregularity factor
        // Higher irregularity means more influence from the noise
        return baseDistance * (1.0 - irregularity * (noise - 0.5));
    }
    
    /**
     * Finds the Y coordinate of the surface at the given x,z position
     * Simplified version that uses the world heightmap
     */
    private int findSurfaceY(WorldGenLevel level, BlockPos pos) {
        // Use the world heightmap to find the surface
        int surfaceY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
        
        // Verify that there's a solid block at this position
        BlockPos surfacePos = new BlockPos(pos.getX(), surfaceY, pos.getZ());
        BlockState state = level.getBlockState(surfacePos);
        
        if (!state.isAir() && state.isSolid()) {
            return surfaceY;
        }
        
        // If the surface block isn't solid, try to find a solid block below
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ());
        
        // Look for a solid block up to 5 blocks below the surface
        for (int y = 0; y < 5; y++) {
            mutablePos.move(0, -1, 0);
            state = level.getBlockState(mutablePos);
            
            if (!state.isAir() && state.isSolid()) {
                return mutablePos.getY();
            }
        }
        
        // No solid surface found
        return -1;
    }
    

    
    /**
     * Check whether the area is sufficiently flat to create the trench.
     * This method is simpler and allows for small variations in height.
     * @return true if the area is sufficiently flat
     */
    private boolean isAreaFlat(WorldGenLevel level, BlockPos origin, int radius, int centerY) {
        int checkRadius = radius + 2; // Check a little further outside
        int maxHeightDifference = 2; // Maximum permissible height difference
        
        for (int x = -checkRadius; x <= checkRadius; x += 2) { // Sample every 2 blocks for efficiency
            for (int z = -checkRadius; z <= checkRadius; z += 2) {
                // Calculate the distance from the center
                double distance = Math.sqrt(x*x + z*z);
                
                // Only check points within the radius or slightly beyond
                if (distance <= checkRadius) {
                    BlockPos checkPos = new BlockPos(origin.getX() + x, 0, origin.getZ() + z);
                    int surfaceY = findSurfaceY(level, checkPos);
                    
                    if (surfaceY == -1) {
                        return false; // No valid surface could be found.
                    }
                    
                    // Check that the difference in height is not too great.
                    if (Math.abs(surfaceY - centerY) > maxHeightDifference) {
                        return false;
                    }
                    
                    // We check that the block is solid.
                    BlockPos surfacePos = new BlockPos(origin.getX() + x, surfaceY, origin.getZ() + z);
                    BlockState surfaceState = level.getBlockState(surfacePos);
                    
                    if (!surfaceState.isSolid()) {
                        return false; // It is not a solid block.
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Reshapes the terrain by removing blocks to create depressions
     * Ensures there are no floating blocks
     */
    private void reshapeTerrain(WorldGenLevel level, BlockPos pos, int currentY, int targetY) {
        if (currentY <= targetY) {
            // We don't want to add blocks to create raised areas anymore
            // Just ensure the top block is sand
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), currentY, pos.getZ());
            
            // Check if there's solid ground below
            BlockPos belowPos = mutablePos.below();
            BlockState belowReshapeState = level.getBlockState(belowPos);
            
            // Only place sand if there's solid ground below
            if (!belowReshapeState.isAir() && belowReshapeState.isSolid()) {
                level.setBlock(mutablePos, Blocks.SAND.defaultBlockState(), 3);
            }
            return;
        }
        
        // If we're here, currentY > targetY, so we need to remove blocks to create a depression
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), 0, pos.getZ());
        
        // Remove blocks to create the depression
        for (int y = currentY; y > targetY; y--) {
            mutablePos.setY(y);
            level.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
        }
        
        // Ensure the top block is sand
        mutablePos.setY(targetY);
        
        // Check if there's solid ground below
        BlockPos belowPos = mutablePos.below();
        BlockState belowDepressionState = level.getBlockState(belowPos);
        
        // Only place sand if there's solid ground below
        if (!belowDepressionState.isAir() && belowDepressionState.isSolid()) {
            level.setBlock(mutablePos, Blocks.SAND.defaultBlockState(), 3);
        }
    }
    
    /**
     * Check whether a block can be replaced based on the list of replaceable blocks
     * @param state The status of the block to be verified
     * @param replaceableBlocks The list of replaceable blocks
     * @return true if the block can be replaced
     */
    private boolean isReplaceable(BlockState state, List<Block> replaceableBlocks) {
        // If the block is air, it can be replaced.
        if (state.isAir()) {
            return true;
        }
        
        // If the block is on the list of replaceable blocks, it can be replaced.
        for (Block block : replaceableBlocks) {
            if (state.is(block)) {
                return true;
            }
        }
        
        // Otherwise, it cannot be replaced.
        return false;
    }
}