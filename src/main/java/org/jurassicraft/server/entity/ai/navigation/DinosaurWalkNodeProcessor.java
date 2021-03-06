package org.jurassicraft.server.entity.ai.navigation;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jurassicraft.server.dinosaur.Dinosaur;

import javax.annotation.Nullable;

public class DinosaurWalkNodeProcessor extends WalkNodeProcessor {
    private Dinosaur dinosaur;

    public DinosaurWalkNodeProcessor(Dinosaur dinosaur) {
        this.dinosaur = dinosaur;
    }


    @Override
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int optionIndex = 0;
        int stepHeight = 0;
        PathNodeType type = this.getPathNodeType(this.entity, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord);

        if (this.entity.getPathPriority(type) >= 0.0F) {
            stepHeight = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
        }

        int jumpHeight = this.dinosaur.getJumpHeight();
        if (!this.entity.isInWater() && !this.entity.isInLava() && this.entity.onGround && jumpHeight > 0 && jumpHeight > stepHeight) {
            stepHeight = jumpHeight + 1;
        }

        BlockPos ground = (new BlockPos(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord)).down();
        double groundY = currentPoint.yCoord - (1.0D - this.blockaccess.getBlockState(ground).getBoundingBox(this.blockaccess, ground).maxY);
        PathPoint south = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, stepHeight, groundY, EnumFacing.SOUTH);
        PathPoint west = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, stepHeight, groundY, EnumFacing.WEST);
        PathPoint east = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, stepHeight, groundY, EnumFacing.EAST);
        PathPoint north = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, stepHeight, groundY, EnumFacing.NORTH);

        if (south != null && !south.visited && south.distanceTo(targetPoint) < maxDistance) {
            pathOptions[optionIndex++] = south;
        }

        if (west != null && !west.visited && west.distanceTo(targetPoint) < maxDistance) {
            pathOptions[optionIndex++] = west;
        }

        if (east != null && !east.visited && east.distanceTo(targetPoint) < maxDistance) {
            pathOptions[optionIndex++] = east;
        }

        if (north != null && !north.visited && north.distanceTo(targetPoint) < maxDistance) {
            pathOptions[optionIndex++] = north;
        }

        boolean canMoveNorth = north == null || north.nodeType == PathNodeType.OPEN || north.costMalus != 0.0F;
        boolean canMoveSouth = south == null || south.nodeType == PathNodeType.OPEN || south.costMalus != 0.0F;
        boolean canMoveEast = east == null || east.nodeType == PathNodeType.OPEN || east.costMalus != 0.0F;
        boolean canMoveWest = west == null || west.nodeType == PathNodeType.OPEN || west.costMalus != 0.0F;

        if (canMoveNorth && canMoveWest) {
            PathPoint northWest = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord - 1, stepHeight, groundY, EnumFacing.NORTH);

            if (northWest != null && !northWest.visited && northWest.distanceTo(targetPoint) < maxDistance) {
                pathOptions[optionIndex++] = northWest;
            }
        }

        if (canMoveNorth && canMoveEast) {
            PathPoint northEast = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord - 1, stepHeight, groundY, EnumFacing.NORTH);

            if (northEast != null && !northEast.visited && northEast.distanceTo(targetPoint) < maxDistance) {
                pathOptions[optionIndex++] = northEast;
            }
        }

        if (canMoveSouth && canMoveWest) {
            PathPoint southWest = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord + 1, stepHeight, groundY, EnumFacing.SOUTH);

            if (southWest != null && !southWest.visited && southWest.distanceTo(targetPoint) < maxDistance) {
                pathOptions[optionIndex++] = southWest;
            }
        }

        if (canMoveSouth && canMoveEast) {
            PathPoint southEast = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord + 1, stepHeight, groundY, EnumFacing.SOUTH);

            if (southEast != null && !southEast.visited && southEast.distanceTo(targetPoint) < maxDistance) {
                pathOptions[optionIndex++] = southEast;
            }
        }

        return optionIndex;
    }

    @Nullable
    private PathPoint getSafePoint(int x, int y, int z, int stepHeight, double currentGroundY, EnumFacing facing) {
        PathPoint point = null;
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos ground = pos.down();
        double groundY = y - (1.0 - this.blockaccess.getBlockState(ground).getBoundingBox(this.blockaccess, ground).maxY);

        if (groundY - currentGroundY > stepHeight + 0.125) {
            return null;
        } else {
            PathNodeType type = this.getPathNodeType(this.entity, x, y, z);
            float priority = this.entity.getPathPriority(type);
            double halfWidth = this.entity.width / 2.0;

            if (priority >= 0.0F) {
                point = this.openPoint(x, y, z);
                point.nodeType = type;
                point.costMalus = Math.max(point.costMalus, priority);
            }

            if (type == PathNodeType.WALKABLE) {
                return point;
            } else {
                if (point == null && stepHeight > 0 && type != PathNodeType.FENCE && type != PathNodeType.TRAPDOOR) {
                    point = this.getSafePoint(x, y + 1, z, stepHeight - 1, currentGroundY, facing);

                    if (point != null && (point.nodeType == PathNodeType.OPEN || point.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
                        double pointX = (x - facing.getFrontOffsetX()) + 0.5;
                        double pointZ = (z - facing.getFrontOffsetZ()) + 0.5;
                        AxisAlignedBB boundsAtPoint = new AxisAlignedBB(pointX - halfWidth, y + 0.001, pointZ - halfWidth, pointX + halfWidth, (y + this.entity.height), pointZ + halfWidth);
                        AxisAlignedBB pointBlockBounds = this.blockaccess.getBlockState(pos).getBoundingBox(this.blockaccess, pos);
                        AxisAlignedBB boundsAtGroundPoint = boundsAtPoint.addCoord(0.0, pointBlockBounds.maxY - 0.002, 0.0);

                        if (this.entity.world.collidesWithAnyBlock(boundsAtGroundPoint)) {
                            point = null;
                        }
                    }
                }

                if (type == PathNodeType.OPEN) {
                    AxisAlignedBB boundsAtPoint = new AxisAlignedBB(x - halfWidth + 0.5, y + 0.001, z - halfWidth + 0.5, x + halfWidth + 0.5, (y + this.entity.height), z + halfWidth + 0.5);

                    if (this.entity.world.collidesWithAnyBlock(boundsAtPoint)) {
                        return null;
                    }

                    if (this.entity.width >= 1.0F) {
                        PathNodeType groundType = this.getPathNodeType(this.entity, x, y - 1, z);

                        if (groundType == PathNodeType.BLOCKED) {
                            point = this.openPoint(x, y, z);
                            point.nodeType = PathNodeType.WALKABLE;
                            point.costMalus = Math.max(point.costMalus, priority);
                            return point;
                        }
                    }

                    int i = 0;

                    while (y > 0 && type == PathNodeType.OPEN) {
                        --y;

                        if (i++ >= this.entity.getMaxFallHeight()) {
                            return null;
                        }

                        type = this.getPathNodeType(this.entity, x, y, z);
                        priority = this.entity.getPathPriority(type);

                        if (type != PathNodeType.OPEN && priority >= 0.0F) {
                            point = this.openPoint(x, y, z);
                            point.nodeType = type;
                            point.costMalus = Math.max(point.costMalus, priority);
                            break;
                        }

                        if (priority < 0.0F) {
                            return null;
                        }
                    }
                }

                return point;
            }
        }
    }

    private PathNodeType getPathNodeType(EntityLiving entity, int x, int y, int z) {
        return this.getPathNodeType(this.blockaccess, x, y, z, entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanBreakDoors(), this.getCanEnterDoors());
    }
}
