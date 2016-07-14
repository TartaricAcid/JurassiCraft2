package org.jurassicraft.server.entity.ai.animations;

import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import org.jurassicraft.client.model.animation.DinosaurAnimation;
import org.jurassicraft.server.entity.base.DinosaurEntity;

import java.util.List;

public class CallAnimationAI extends EntityAIBase
{
    protected DinosaurEntity dinosaur;

    public CallAnimationAI(IAnimatedEntity entity)
    {
        super();
        this.dinosaur = (DinosaurEntity) entity;
    }

    public List<Entity> getEntitiesWithinDistance(Entity entity, double width, double height)
    {
        return entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.posX - width, entity.posY - height, entity.posZ - width, entity.posX + width, entity.posY + height, entity.posZ + width));
    }

    @Override
    public boolean shouldExecute()
    {
        if (dinosaur.isDead || dinosaur.getAttackTarget() != null || dinosaur.isSleeping())
        {
            return false;
        }

        if (dinosaur.getRNG().nextDouble() < 0.003)
        {
            List<Entity> entities = this.getEntitiesWithinDistance(dinosaur, 50, 10);

            for (Entity entity : entities)
            {
                if (this.dinosaur.getClass().isInstance(entity))
                {
                    float soundVolume = dinosaur.getSoundVolume();
                    this.dinosaur.playSound(dinosaur.getSoundForAnimation(DinosaurAnimation.CALLING.get()), soundVolume > 0.0F ? soundVolume + 1.25F : soundVolume, dinosaur.getSoundPitch());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void startExecuting()
    {
        dinosaur.setAnimation(DinosaurAnimation.CALLING.get());
    }

    @Override
    public boolean continueExecuting()
    {
        return false;
    }
}
