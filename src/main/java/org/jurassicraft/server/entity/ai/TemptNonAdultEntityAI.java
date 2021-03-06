package org.jurassicraft.server.entity.ai;

import com.google.common.collect.Sets;
import net.minecraft.entity.ai.EntityAITempt;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.food.FoodHelper;
import org.jurassicraft.server.food.FoodType;

public class TemptNonAdultEntityAI extends EntityAITempt {
    private DinosaurEntity dinosaur;

    public TemptNonAdultEntityAI(DinosaurEntity dinosaur, double speed) {
        super(dinosaur, speed, !dinosaur.getDinosaur().getDiet().canEat(dinosaur, FoodType.MEAT), Sets.newHashSet(FoodHelper.getEdibleFoods(dinosaur, dinosaur.getDinosaur().getDiet())));
        this.dinosaur = dinosaur;
        this.setMutexBits(Mutex.METABOLISM | Mutex.MOVEMENT);
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute() && !this.dinosaur.isBusy() && this.dinosaur.getAgePercentage() < 50;
    }
}
