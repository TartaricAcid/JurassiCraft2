package org.jurassicraft.server.entity;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.client.model.tools.ChainBuffer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.animation.DinosaurAnimation;
import org.jurassicraft.server.block.entity.FeederBlockEntity;
import org.jurassicraft.server.damage.DinosaurDamageSource;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.ai.AdvancedSwimEntityAI;
import org.jurassicraft.server.entity.ai.AssistOwnerEntityAI;
import org.jurassicraft.server.entity.ai.DefendOwnerEntityAI;
import org.jurassicraft.server.entity.ai.DinosaurAttackMeleeEntityAI;
import org.jurassicraft.server.entity.ai.DinosaurLookHelper;
import org.jurassicraft.server.entity.ai.DinosaurWanderEntityAI;
import org.jurassicraft.server.entity.ai.FleeEntityAI;
import org.jurassicraft.server.entity.ai.FollowOwnerEntityAI;
import org.jurassicraft.server.entity.ai.Herd;
import org.jurassicraft.server.entity.ai.MateEntityAI;
import org.jurassicraft.server.entity.ai.PathNavigateDinosaur;
import org.jurassicraft.server.entity.ai.ProtectInfantEntityAI;
import org.jurassicraft.server.entity.ai.RespondToAttackEntityAI;
import org.jurassicraft.server.entity.ai.SelectTargetEntityAI;
import org.jurassicraft.server.entity.ai.SleepEntityAI;
import org.jurassicraft.server.entity.ai.TargetCarcassEntityAI;
import org.jurassicraft.server.entity.ai.TemptNonAdultEntityAI;
import org.jurassicraft.server.entity.ai.animations.CallAnimationAI;
import org.jurassicraft.server.entity.ai.animations.HeadCockAnimationAI;
import org.jurassicraft.server.entity.ai.animations.LookAnimationAI;
import org.jurassicraft.server.entity.ai.animations.RoarAnimationAI;
import org.jurassicraft.server.entity.ai.metabolism.DrinkEntityAI;
import org.jurassicraft.server.entity.ai.metabolism.EatFoodItemEntityAI;
import org.jurassicraft.server.entity.ai.metabolism.FeederEntityAI;
import org.jurassicraft.server.entity.ai.metabolism.GrazeEntityAI;
import org.jurassicraft.server.entity.ai.util.OnionTraverser;
import org.jurassicraft.server.food.FoodHelper;
import org.jurassicraft.server.genetics.GeneticsHelper;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.message.SetOrderMessage;
import org.jurassicraft.server.util.GameRuleHandler;
import org.jurassicraft.server.util.LangHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public abstract class DinosaurEntity extends EntityCreature implements IEntityAdditionalSpawnData, IAnimatedEntity {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final DataParameter<Boolean> WATCHER_IS_CARCASS = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> WATCHER_AGE = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> WATCHER_IS_SLEEPING = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WATCHER_HAS_TRACKER = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> WATCHER_OWNER = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.STRING);
    private static final DataParameter<Order> WATCHER_ORDER = EntityDataManager.createKey(DinosaurEntity.class, DinosaurSerializers.ORDER);
    private static final DataParameter<Boolean> WATCHER_IS_RUNNING = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.BOOLEAN);

    private final InventoryDinosaur inventory;
    private final MetabolismContainer metabolism;
    protected Dinosaur dinosaur;
    protected int dinosaurAge;
    protected int prevAge;
    protected EntityAITasks animationTasks;
    protected Order order = Order.WANDER;
    private int growthSpeedOffset;
    private boolean isCarcass;
    private int carcassHealth;
    private String genetics;
    private int geneticsQuality;
    private boolean isMale;
    private boolean hasTracker;
    private UUID owner;
    private boolean isSleeping;
    private int stayAwakeTime;
    private boolean useInertialTweens;
    private List<Class<? extends EntityLivingBase>> attackTargets = new ArrayList<>();

    private boolean deserializing;

    private int attackCooldown;

    @SideOnly(Side.CLIENT)
    public ChainBuffer tailBuffer;

    public Herd herd;

    private boolean isSittingNaturally;

    private Animation animation;
    private int animationTick;
    private int animationLength;

    private DinosaurLookHelper lookHelper;

    private BlockPos closestFeeder;
    private int feederSearchTick;

    private boolean inLava;

    public DinosaurEntity(World world) {
        super(world);

        this.navigator = new PathNavigateDinosaur(this, this.worldObj);

        this.setFullyGrown();
        this.updateAttributes();

        this.lookHelper = new DinosaurLookHelper(this);

        this.metabolism = new MetabolismContainer(this);
        this.inventory = new InventoryDinosaur(this);

        this.genetics = GeneticsHelper.randomGenetics(this.rand);
        this.isMale = this.rand.nextBoolean();

        this.resetAttackCooldown();

        this.animationTick = 0;
        this.setAnimation(DinosaurAnimation.IDLE.get());

        this.setUseInertialTweens(true);

        this.animationTasks = new EntityAITasks(world.theProfiler);

        //tasks.addTask(0, new EscapeBlockEntityAI(this));

        if (!this.dinosaur.isMarineAnimal()) {
//            this.tasks.addTask(0, new EntityAISwimming(this));
            this.tasks.addTask(0, new AdvancedSwimEntityAI(this));
            //this.setPathPriority(PathNodeType.WATER, 0.0F);
        }

        if (this.dinosaur.getDiet().isHerbivorous()) {
            this.tasks.addTask(1, new GrazeEntityAI(this));
        }

        if (this.dinosaur.getDiet().isCarnivorous()) {
            this.tasks.addTask(1, new TargetCarcassEntityAI(this));
        }

        this.tasks.addTask(1, new RespondToAttackEntityAI(this));

        this.tasks.addTask(1, new TemptNonAdultEntityAI(this, 0.6));

        if (this.dinosaur.shouldDefendOwner()) {
            this.tasks.addTask(2, new DefendOwnerEntityAI(this));
            this.tasks.addTask(2, new AssistOwnerEntityAI(this));
        }

        if (this.dinosaur.shouldFlee()) {
            this.tasks.addTask(2, new FleeEntityAI(this));
        }

        this.tasks.addTask(2, new ProtectInfantEntityAI<>(this));

        this.tasks.addTask(3, new DinosaurWanderEntityAI(this, 0.8F, 20));
        this.tasks.addTask(3, new FollowOwnerEntityAI(this));

        this.tasks.addTask(3, this.getAttackAI());

        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityLivingBase.class, 6.0F));

        this.animationTasks.addTask(0, new SleepEntityAI(this));

        this.animationTasks.addTask(1, new DrinkEntityAI(this));
        this.animationTasks.addTask(1, new MateEntityAI(this));
        this.animationTasks.addTask(1, new EatFoodItemEntityAI(this));
        this.animationTasks.addTask(1, new FeederEntityAI(this));

        this.animationTasks.addTask(3, new CallAnimationAI(this));
        this.animationTasks.addTask(3, new RoarAnimationAI(this));
        this.animationTasks.addTask(3, new LookAnimationAI(this));
        this.animationTasks.addTask(3, new HeadCockAnimationAI(this));

        if (world.isRemote) {
            this.initClient();
        }

        this.ignoreFrustumCheck = true;
    }

    @Override
    public EntityLookHelper getLookHelper() {
        return this.lookHelper;
    }

    private void initClient() {
        this.tailBuffer = new ChainBuffer();
    }

    public boolean shouldSleep() {
        return this.getDinosaurTime() > this.getDinosaur().getSleepingSchedule().getAwakeTime() && !this.hasPredators() && !this.metabolism.isDehydrated() && !this.metabolism.isStarving() && !(this.herd != null && this.herd.enemies.size() > 0);
    }

    private boolean hasPredators() {
        for (EntityLiving predator : this.getEntitiesWithinDistance(EntityLiving.class, 10.0F, 5.0F)) {
            boolean hasDinosaurPredator = false;

            if (predator instanceof DinosaurEntity) {
                DinosaurEntity dinosaur = (DinosaurEntity) predator;

                if (!dinosaur.isCarcass() || dinosaur.isSleeping) {
                    for (Class<? extends EntityLivingBase> target : dinosaur.getAttackTargets()) {
                        if (target.isAssignableFrom(this.getClass())) {
                            hasDinosaurPredator = true;
                            break;
                        }
                    }
                }
            }

            if (this.getLastAttacker() == predator || predator.getAttackTarget() == this || hasDinosaurPredator) {
                return true;
            }
        }

        return false;
    }

    private <T extends Entity> List<T> getEntitiesWithinDistance(Class<T> entity, double width, double height) {
        List<T> entities = this.worldObj.getEntitiesWithinAABB(entity, new AxisAlignedBB(this.posX - width, this.posY - height, this.posZ - width, this.posX + width, this.posY + height, this.posZ + width));
        entities.remove(this);
        return entities;
    }

    public int getDinosaurTime() {
        SleepingSchedule sleepingSchedule = this.getDinosaur().getSleepingSchedule();

        long time = (this.worldObj.getWorldTime() % 24000) - sleepingSchedule.getWakeUpTime();

        if (time < 0) {
            time += 24000;
        }

        return (int) time;
    }

    public boolean hasTracker() {
        return this.hasTracker;
    }

    public void setHasTracker(boolean hasTracker) {
        this.hasTracker = hasTracker;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(EntityPlayer player) {
        if (this.dinosaur.isImprintable()) {
            UUID prevOwner = this.owner;
            this.owner = player.getUniqueID();

            if (!this.owner.equals(prevOwner)) {
                player.addChatComponentMessage(new TextComponentString(new LangHelper("message.tame.name").withProperty("dinosaur", "entity.jurassicraft." + this.dinosaur.getName().toLowerCase(Locale.ENGLISH) + ".name").build()));
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (entity instanceof DinosaurEntity && ((DinosaurEntity) entity).isCarcass()) {
            this.setAnimation(DinosaurAnimation.EATING.get());
        } else {
            this.setAnimation(DinosaurAnimation.ATTACKING.get());
        }

        while (entity.getRidingEntity() != null) {
            entity = entity.getRidingEntity();
        }

        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();

        if (entity.attackEntityFrom(new DinosaurDamageSource("mob", this), damage)) {
            if (entity instanceof DinosaurEntity && ((DinosaurEntity) entity).isCarcass()) {
                DinosaurEntity dinosaur = (DinosaurEntity) entity;
                if (dinosaur.herd != null && this.herd != null && dinosaur.herd.fleeing && dinosaur.herd.enemies.contains(this)) {
                    this.herd.enemies.removeAll(dinosaur.herd.members);
                    for (DinosaurEntity member : this.herd) {
                        if (member.getAttackTarget() != null && dinosaur.herd.members.contains(member.getAttackTarget())) {
                            member.setAttackTarget(null);
                        }
                    }
                    this.herd.state = Herd.State.STATIC;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        boolean canHarmInCreative = damageSource.canHarmInCreative();

        if (!this.isCarcass()) {
            if (this.getHealth() - amount <= 0.0F) {
                if (!canHarmInCreative) {
                    this.playSound(this.getSoundForAnimation(DinosaurAnimation.DYING.get()), this.getSoundVolume(), this.getSoundPitch());
                    this.setHealth(this.getMaxHealth());
                    this.setCarcass(true);
                    return true;
                }

                return super.attackEntityFrom(damageSource, amount);
            } else {
                if (this.getAnimation() == DinosaurAnimation.RESTING.get() && !this.worldObj.isRemote) {
                    this.setAnimation(DinosaurAnimation.IDLE.get());
                    this.isSittingNaturally = false;
                }

                if (!this.worldObj.isRemote && this.getAnimation() == DinosaurAnimation.IDLE.get()) {
                    this.setAnimation(DinosaurAnimation.INJURED.get());
                }

                if (this.shouldSleep()) {
                    this.disturbSleep();
                }

                return super.attackEntityFrom(damageSource, amount);
            }
        } else if (!this.worldObj.isRemote) {
            if (canHarmInCreative) {
                return super.attackEntityFrom(damageSource, amount);
            }

            if (this.hurtResistantTime <= this.maxHurtResistantTime / 2.0F) {
                this.hurtTime = this.maxHurtTime = 10;
            }

            if (damageSource != DamageSource.drown) {
                this.carcassHealth--;

                if (!this.dead && this.carcassHealth >= 0 && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
                    this.dropMeat(damageSource.getEntity());
                }

                if (this.carcassHealth < 0) {
                    this.onDeath(damageSource);
                    this.setDead();
                }
            }
        }

        return false;
    }

    private void dropMeat(Entity attacker) {
        int fortune = 0;

        if (attacker instanceof EntityLivingBase) {
            fortune = EnchantmentHelper.getLootingModifier((EntityLivingBase) attacker);
        }

        int count = this.rand.nextInt(2) + 1 + fortune;

        boolean burning = this.isBurning();

        for (int i = 0; i < count; ++i) {
            int meta = EntityHandler.getDinosaurId(this.dinosaur);

            if (burning) {
                this.entityDropItem(new ItemStack(ItemHandler.DINOSAUR_STEAK, 1, meta), 0.0F);
            } else {
                this.dropStackWithGenetics(new ItemStack(ItemHandler.DINOSAUR_MEAT, 1, meta));
            }
        }
    }

    @Override
    public boolean canBePushed() {
        return super.canBePushed() && !this.isCarcass() && !this.isSleeping();
    }

    @Override
    public EntityItem entityDropItem(ItemStack stack, float offsetY) {
        if (stack.stackSize != 0 && stack.getItem() != null) {
            Random rand = new Random();

            EntityItem item = new EntityItem(this.worldObj, this.posX + ((rand.nextFloat() * this.width) - this.width / 2), this.posY + (double) offsetY, this.posZ + ((rand.nextFloat() * this.width) - this.width / 2), stack);
            item.setDefaultPickupDelay();

            if (this.captureDrops) {
                this.capturedDrops.add(item);
            } else {
                this.worldObj.spawnEntityInWorld(item);
            }

            return item;
        } else {
            return null;
        }
    }

    @Override
    public void knockBack(Entity entity, float p_70653_2_, double motionX, double motionZ) {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()) {
            this.isAirBorne = true;
            float distance = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            float multiplier = 0.4F;
            this.motionX /= 2.0D;
            this.motionZ /= 2.0D;
            this.motionX -= motionX / distance * multiplier;
            this.motionZ -= motionZ / distance * multiplier;

            // TODO We should make knockback bigger and into air if dino is much smaller than attacking dino
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (this.herd != null) {
            if (this.herd.leader == this) {
                this.herd.updateLeader();
            }

            this.herd.members.remove(this);
        }

        if (cause.getSourceOfDamage() instanceof EntityLivingBase) {
            this.respondToAttack((EntityLivingBase) cause.getSourceOfDamage());
        }
    }

    @Override
    public void playLivingSound() {
        if (this.getAnimation() == DinosaurAnimation.IDLE.get()) {
            this.setAnimation(DinosaurAnimation.SPEAK.get());
            super.playLivingSound();
        }
    }

    @Override
    public void entityInit() {
        super.entityInit();

        this.dataManager.register(WATCHER_IS_CARCASS, this.isCarcass);
        this.dataManager.register(WATCHER_AGE, this.dinosaurAge);
        this.dataManager.register(WATCHER_IS_SLEEPING, this.isSleeping);
        this.dataManager.register(WATCHER_HAS_TRACKER, this.hasTracker);
        this.dataManager.register(WATCHER_OWNER, "");
        this.dataManager.register(WATCHER_ORDER, Order.WANDER);
        this.dataManager.register(WATCHER_IS_RUNNING, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.dinosaur = EntityHandler.getDinosaurByClass(this.getClass());

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.updateAttributes();
        this.updateBounds();
    }

    public void updateAttributes() {
        double prevHealth = this.getMaxHealth();
        double newHealth = this.interpolate(this.dinosaur.getBabyHealth(), this.dinosaur.getAdultHealth());

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.interpolate(this.dinosaur.getBabySpeed(), this.dinosaur.getAdultSpeed()));

        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.interpolate(this.dinosaur.getBabyStrength(), this.dinosaur.getAdultStrength()));

        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);

        if (prevHealth != newHealth) {
            this.heal((float) (newHealth - prevHealth));
        }
    }

    private void updateBounds() {
        float width = Math.min(4.0F, (float) this.interpolate(this.dinosaur.getBabySizeX(), this.dinosaur.getAdultSizeX()));
        float height = Math.min(4.0F, (float) this.interpolate(this.dinosaur.getBabySizeY(), this.dinosaur.getAdultSizeY()));

        this.stepHeight = Math.max(1.0F, (float) (Math.ceil(height / 2.0F) / 2.0F));

        if (this.isCarcass) {
            this.setSize(Math.min(5.0F, height), width);
        } else {
            this.setSize(width, height);
        }
    }

    public double interpolate(double baby, double adult) {
        int dinosaurAge = this.dinosaurAge;
        int maxAge = this.dinosaur.getMaximumAge();
        if (dinosaurAge > maxAge) {
            dinosaurAge = maxAge;
        }
        return (adult - baby) / maxAge * dinosaurAge + baby;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    public void setupActionFigure(boolean isMale) {
        this.setFullyGrown();
        this.setMale(isMale);
        this.ticksExisted = 4;
    }

    @Override
    public int getTalkInterval() {
        return 200;
    }

    @Override
    public float getSoundPitch() {
        return (float) this.interpolate(2.5F, 1.0F) + ((this.rand.nextFloat() - 0.5F) * 0.125F);
    }

    @Override
    public float getSoundVolume() {
        return (this.isCarcass() || this.isSleeping) ? 0.0F : (2.0F * ((float) this.interpolate(0.2F, 1.0F)));
    }

    public String getGenetics() {
        return this.genetics;
    }

    public void setGenetics(String genetics) {
        this.genetics = genetics;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.ticksExisted % 10 == 0) {
            this.inLava = super.isInLava();
        }

        if (!this.isCarcass) {
            if (!this.worldObj.isRemote) {
                if (!this.dinosaur.isMarineAnimal()) {
                    if (this.isInsideOfMaterial(Material.WATER) || (this.getNavigator().noPath() && this.isInWater() || this.isInLava())) {
                        this.getJumpHelper().setJumping();
                    } else {
                        if (this.isSwimming()) {
                            Path path = this.getNavigator().getPath();
                            if (path != null) {
                                List<AxisAlignedBB> colliding = this.worldObj.getCollisionBoxes(this.getEntityBoundingBox().expandXyz(0.5));
                                boolean swimUp = false;
                                for (AxisAlignedBB bound : colliding) {
                                    if (bound.maxY > this.getEntityBoundingBox().minY) {
                                        swimUp = true;
                                        break;
                                    }
                                }
                                if (swimUp) {
                                    this.getJumpHelper().setJumping();
                                }
                            }
                        }
                    }
                }

                if (this.herd == null) {
                    this.herd = new Herd(this);
                }

                if (this.order == Order.WANDER) {
                    if (this.herd.state == Herd.State.STATIC && this.getAttackTarget() == null && !this.metabolism.isThirsty() && !this.metabolism.isHungry() && this.getNavigator().noPath()) {
                        if (!this.isSleeping && !this.isInWater() && !this.isInLava() && this.onGround && this.getAnimation() == DinosaurAnimation.IDLE.get() && this.rand.nextInt(400) == 0) {
                            this.setAnimation(DinosaurAnimation.RESTING.get());
                            this.isSittingNaturally = true;
                        }
                    } else if (this.getAnimation() == DinosaurAnimation.RESTING.get()) {
                        this.setAnimation(DinosaurAnimation.IDLE.get());
                        this.isSittingNaturally = false;
                    }
                }

                if (this == this.herd.leader) {
                    this.herd.onUpdate();
                }

                if (!this.getNavigator().noPath()) {
                    if (this.isSittingNaturally && this.getAnimation() == DinosaurAnimation.RESTING.get()) {
                        this.setAnimation(DinosaurAnimation.IDLE.get());
                        this.isSittingNaturally = false;
                    }
                }
            }

            if (this.firstUpdate) {
                this.updateAttributes();
            }

            this.updateGrowth();

            if (!this.worldObj.isRemote) {
                if (this.metabolism.isHungry()) {
                    List<EntityItem> entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0, 1.0, 1.0));
                    for (EntityItem itemEntity : entitiesWithinAABB) {
                        Item item = itemEntity.getEntityItem().getItem();

                        if (FoodHelper.isEdible(this.dinosaur.getDiet(), item)) {
                            this.setAnimation(DinosaurAnimation.EATING.get());

                            if (itemEntity.getEntityItem().stackSize > 1) {
                                itemEntity.getEntityItem().stackSize--;
                            } else {
                                itemEntity.setDead();
                            }

                            this.getMetabolism().eat(FoodHelper.getHealAmount(item));
                            FoodHelper.applyEatEffects(this, item);
                            this.heal(10.0F);

                            break;
                        }
                    }
                }

                this.metabolism.update();
            }

            if (this.ticksExisted % 62 == 0) {
                this.playSound(this.getBreathingSound(), this.getSoundVolume(), this.getSoundPitch());
            }
        }

        if (this.isServerWorld()) {
            this.lookHelper.onUpdateLook();
        }
    }

    private void updateGrowth() {
        if (!this.isDead && this.ticksExisted % 8 == 0 && !this.worldObj.isRemote) {
            if (GameRuleHandler.DINO_GROWTH.getBoolean(this.worldObj)) {
                this.dinosaurAge += Math.min(this.growthSpeedOffset, 960) + 1;
                this.metabolism.decreaseEnergy((int) ((Math.min(this.growthSpeedOffset, 960) + 1) * 0.1));
            }

            if (this.growthSpeedOffset > 0) {
                this.growthSpeedOffset -= 10;

                if (this.growthSpeedOffset < 0) {
                    this.growthSpeedOffset = 0;
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.animation != null && this.animation != DinosaurAnimation.IDLE.get()) {
            boolean shouldHold = DinosaurAnimation.getAnimation(this.animation).shouldHold();

            if (this.animationTick < this.animationLength) {
                this.animationTick++;
            } else if (!shouldHold) {
                this.animationTick = 0;
                this.setAnimation(DinosaurAnimation.IDLE.get());
            } else {
                this.animationTick = this.animationLength - 1;
            }
        }

        if (!this.worldObj.isRemote) {
            this.dataManager.set(WATCHER_AGE, this.dinosaurAge);
            this.dataManager.set(WATCHER_IS_SLEEPING, this.isSleeping);
            this.dataManager.set(WATCHER_IS_CARCASS, this.isCarcass);
            this.dataManager.set(WATCHER_HAS_TRACKER, this.hasTracker);
            this.dataManager.set(WATCHER_ORDER, this.order);
            this.dataManager.set(WATCHER_OWNER, this.owner != null ? this.owner.toString() : "");
            this.dataManager.set(WATCHER_IS_RUNNING, this.getAIMoveSpeed() > this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        } else {
            this.updateTailBuffer();

            this.dinosaurAge = this.dataManager.get(WATCHER_AGE);
            this.isSleeping = this.dataManager.get(WATCHER_IS_SLEEPING);
            this.isCarcass = this.dataManager.get(WATCHER_IS_CARCASS);
            this.hasTracker = this.dataManager.get(WATCHER_HAS_TRACKER);
            this.order = this.dataManager.get(WATCHER_ORDER);

            String owner = this.dataManager.get(WATCHER_OWNER);

            if (owner.length() > 0 && (this.owner == null || !owner.equals(this.owner.toString()))) {
                this.owner = UUID.fromString(owner);
            } else if (owner.length() == 0) {
                this.owner = null;
            }
        }

        if (this.ticksExisted % 20 == 0) {
            this.updateAttributes();
            this.updateBounds();
        }

        if (this.isCarcass) {
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.rotationYaw;
        }

        if (this.isSleeping) {
            if (this.getAnimation() != DinosaurAnimation.SLEEPING.get()) {
                this.setAnimation(DinosaurAnimation.SLEEPING.get());
            }
        } else if (this.getAnimation() == DinosaurAnimation.SLEEPING.get()) {
            this.setAnimation(DinosaurAnimation.IDLE.get());
        }

        if (!this.worldObj.isRemote) {
            if (this.isCarcass) {
                if (this.getAnimation() != DinosaurAnimation.DYING.get()) {
                    this.setAnimation(DinosaurAnimation.DYING.get());
                }

                if (this.ticksExisted % 1000 == 0) {
                    this.attackEntityFrom(DamageSource.generic, 1.0F);
                }
            } else {
                if (this.isSleeping) {
                    if (this.ticksExisted % 20 == 0) {
                        if (this.stayAwakeTime <= 0 && this.hasPredators()) {
                            this.disturbSleep();
                        }
                    }

                    if (!this.shouldSleep() && !this.worldObj.isRemote) {
                        this.isSleeping = false;
                    }
                } else if (this.getAnimation() == DinosaurAnimation.SLEEPING.get()) {
                    this.setAnimation(DinosaurAnimation.IDLE.get());
                }

                if (!this.isSleeping) {
                    if (this.order == Order.SIT) {
                        if (this.getAnimation() != DinosaurAnimation.RESTING.get()) {
                            this.setAnimation(DinosaurAnimation.RESTING.get());
                        }
                    } else if (!this.isSittingNaturally && this.getAnimation() == DinosaurAnimation.RESTING.get() && !this.worldObj.isRemote) {
                        this.setAnimation(DinosaurAnimation.IDLE.get());
                    }
                }
            }
        }

        if (!this.shouldSleep() && !this.isSleeping) {
            this.stayAwakeTime = 0;
        }

        if (this.isServerWorld()) {
            this.animationTasks.onUpdateTasks();
        }

        if (this.stayAwakeTime > 0) {
            this.stayAwakeTime--;
        }

        this.prevAge = this.dinosaurAge;
    }

    private void updateTailBuffer() {
        this.tailBuffer.calculateChainSwingBuffer(68.0F, 3, 7.0F, this);
    }

    @Override
    public boolean isMovementBlocked() {
        return this.isCarcass() || this.isSleeping() || (this.animation != null && DinosaurAnimation.getAnimation(this.animation).doesBlockMovement());
    }

    public int getDaysExisted() {
        return (int) Math.floor((this.dinosaurAge * 8.0F) / 24000.0F);
    }

    public void setFullyGrown() {
        this.setAge(this.dinosaur.getMaximumAge());
    }

    public Dinosaur getDinosaur() {
        return this.dinosaur;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    public int getDinosaurAge() {
        return this.dinosaurAge;
    }

    public void setAge(int age) {
        this.dinosaurAge = age;
        if (!this.worldObj.isRemote) {
            this.dataManager.set(WATCHER_AGE, this.dinosaurAge);
        }
    }

    @Override
    public float getEyeHeight() {
        return (float) this.interpolate(this.dinosaur.getBabyEyeHeight(), this.dinosaur.getAdultEyeHeight());
    }

    @Override
    protected void dropFewItems(boolean playerAttack, int looting) {
        for (String bone : this.dinosaur.getBones()) {
            if (this.rand.nextInt(10) != 0) {
                this.dropStackWithGenetics(new ItemStack(ItemHandler.FRESH_FOSSILS.get(bone), 1, EntityHandler.getDinosaurId(this.dinosaur)));
            }
        }
    }

    private void dropStackWithGenetics(ItemStack stack) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("DNAQuality", this.geneticsQuality);
        nbt.setString("Genetics", this.genetics);
        stack.setTagCompound(nbt);

        this.entityDropItem(stack, 0.0F);
    }

    public boolean isCarcass() {
        return this.isCarcass;
    }

    public void setCarcass(boolean carcass) {
        this.isCarcass = carcass;
        if (!this.worldObj.isRemote) {
            this.dataManager.set(WATCHER_IS_CARCASS, this.isCarcass);
        }
        if (carcass) {
            this.setAnimation(DinosaurAnimation.DYING.get());
            this.carcassHealth = (int) Math.sqrt(this.width * this.height) * 2;
            this.ticksExisted = 0;
            this.inventory.dropItems(this.worldObj, this.rand);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (player.isSneaking() && hand == EnumHand.MAIN_HAND) {
            if (this.isOwner(player)) {
                if (this.getAgePercentage() > 75) {
                    player.displayGUIChest(this.inventory);
                } else {
                    if (this.worldObj.isRemote) {
                        player.addChatComponentMessage(new TextComponentTranslation("message.too_young.name"));
                    }
                }
            } else {
                if (this.worldObj.isRemote) {
                    player.addChatComponentMessage(new TextComponentTranslation("message.not_owned.name"));
                }
            }
        } else {
            if (stack == null && hand == EnumHand.MAIN_HAND && this.worldObj.isRemote) {
                if (this.isOwner(player)) {
                    JurassiCraft.PROXY.openOrderGui(this);
                } else {
                    player.addChatComponentMessage(new TextComponentTranslation("message.not_owned.name"));
                }
            } else if (stack != null && (this.metabolism.isThirsty() || this.metabolism.isHungry())) {
                if (!this.worldObj.isRemote) {
                    Item item = stack.getItem();
                    boolean fed = false;
                    if (item == Items.POTIONITEM) {
                        fed = true;
                        this.metabolism.increaseWater(1000);
                        this.setAnimation(DinosaurAnimation.DRINKING.get());
                    } else if (FoodHelper.isEdible(this.dinosaur.getDiet(), item)) {
                        fed = true;
                        this.metabolism.eat(FoodHelper.getHealAmount(item));
                        this.setAnimation(DinosaurAnimation.EATING.get());
                        FoodHelper.applyEatEffects(this, item);
                    }
                    if (fed) {
                        if (!player.capabilities.isCreativeMode) {
                            stack.stackSize--;
                            if (item == Items.POTIONITEM) {
                                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                            }
                        }
                        if (!this.isOwner(player)) {
                            if (this.rand.nextFloat() < 0.30) {
                                if (this.dinosaur.getDinosaurType() == Dinosaur.DinosaurType.AGGRESSIVE) {
                                    if (this.rand.nextFloat() * 4.0F < (float) this.herd.members.size() / this.dinosaur.getMaxHerdSize()) {
                                        this.herd.enemies.add(player);
                                    } else {
                                        this.attackEntityAsMob(player);
                                    }
                                } else if (this.dinosaur.getDinosaurType() == Dinosaur.DinosaurType.SCARED) {
                                    this.herd.fleeing = true;
                                    this.herd.enemies.add(player);
                                }
                            }
                        }
                        player.swingArm(hand);
                    }
                }
            }
        }

        return false;
    }

    public boolean isOwner(EntityPlayer player) {
        return player.getUniqueID().equals(this.getOwner());
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return !this.getLeashed() && (this.width < 1.5);
    }

    public int getDNAQuality() {
        return this.geneticsQuality;
    }

    public void setDNAQuality(int quality) {
        this.geneticsQuality = quality;
    }

    @Override
    public Animation[] getAnimations() {
        return DinosaurAnimation.getAnimations();
    }

    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public void setAnimation(Animation newAnimation) {
        if (this.isSleeping()) {
            newAnimation = DinosaurAnimation.SLEEPING.get();
        }

        if (this.isCarcass()) {
            newAnimation = DinosaurAnimation.DYING.get();
        }

        Animation oldAnimation = this.animation;

        this.animation = newAnimation;

        if (oldAnimation != newAnimation) {
            this.animationTick = 0;
            this.animationLength = (int) this.dinosaur.getPoseHandler().getAnimationLength(this.animation, this.getGrowthStage());

            AnimationHandler.INSTANCE.sendAnimationMessage(this, newAnimation);
        }
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return this.getSoundForAnimation(DinosaurAnimation.SPEAK.get());
    }

    @Override
    public SoundEvent getHurtSound() {
        return this.getSoundForAnimation(DinosaurAnimation.INJURED.get());
    }

    @Override
    public SoundEvent getDeathSound() {
        return this.getSoundForAnimation(DinosaurAnimation.DYING.get());
    }

    public SoundEvent getSoundForAnimation(Animation animation) {
        return null;
    }

    public SoundEvent getBreathingSound() {
        return null;
    }

    public double getAttackDamage() {
        return this.interpolate(this.dinosaur.getBabyStrength(), this.dinosaur.getAdultStrength());
    }

    public boolean isMale() {
        return this.isMale;
    }

    public void setMale(boolean male) {
        this.isMale = male;
    }

    public int getAgePercentage() {
        int age = this.getDinosaurAge();
        return age != 0 ? age * 100 / this.getDinosaur().getMaximumAge() : 0;
    }

    public GrowthStage getGrowthStage() {
        int percent = this.getAgePercentage();

        return percent > 75 ? GrowthStage.ADULT : percent > 50 ? GrowthStage.ADOLESCENT : percent > 25 ? GrowthStage.JUVENILE : GrowthStage.INFANT;
    }

    public void increaseGrowthSpeed() {
        this.growthSpeedOffset += 240;
    }

    public boolean isSwimming() {
        return (this.isInWater() || this.inLava) && !this.onGround;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);

        nbt.setInteger("DinosaurAge", this.dinosaurAge);
        nbt.setBoolean("IsCarcass", this.isCarcass);
        nbt.setInteger("DNAQuality", this.geneticsQuality);
        nbt.setString("Genetics", this.genetics);
        nbt.setBoolean("IsMale", this.isMale);
        nbt.setInteger("GrowthSpeedOffset", this.growthSpeedOffset);
        nbt.setInteger("StayAwakeTime", this.stayAwakeTime);
        nbt.setBoolean("IsSleeping", this.isSleeping);
        nbt.setByte("Order", (byte) this.order.ordinal());
        nbt.setInteger("CarcassHealth", this.carcassHealth);

        this.metabolism.writeToNBT(nbt);

        if (this.owner != null) {
            nbt.setString("OwnerUUID", this.owner.toString());
        }

        this.inventory.writeToNBT(nbt);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.deserializing = true;

        super.readFromNBT(nbt);
        this.setAge(nbt.getInteger("DinosaurAge"));
        this.setCarcass(nbt.getBoolean("IsCarcass"));
        this.geneticsQuality = nbt.getInteger("DNAQuality");
        this.genetics = nbt.getString("Genetics");
        this.isMale = nbt.getBoolean("IsMale");
        this.growthSpeedOffset = nbt.getInteger("GrowthSpeedOffset");
        this.stayAwakeTime = nbt.getInteger("StayAwakeTime");
        this.setSleeping(nbt.getBoolean("IsSleeping"));
        this.carcassHealth = nbt.getInteger("CarcassHealth");
        this.order = Order.values()[nbt.getByte("Order")];

        this.metabolism.readFromNBT(nbt);

        String ownerUUID = nbt.getString("OwnerUUID");

        if (ownerUUID.length() > 0) {
            this.owner = UUID.fromString(ownerUUID);
        }

        this.inventory.readFromNBT(nbt);

        this.updateAttributes();
        this.updateBounds();

        this.deserializing = false;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.dinosaurAge);
        buffer.writeBoolean(this.isCarcass);
        buffer.writeInt(this.geneticsQuality);
        buffer.writeBoolean(this.isMale);
        buffer.writeInt(this.growthSpeedOffset);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.dinosaurAge = additionalData.readInt();
        this.isCarcass = additionalData.readBoolean();
        this.geneticsQuality = additionalData.readInt();
        this.isMale = additionalData.readBoolean();
        this.growthSpeedOffset = additionalData.readInt();

        if (this.isCarcass) {
            this.setAnimation(DinosaurAnimation.DYING.get());
        }

        this.updateAttributes();
        this.updateBounds();
    }

    public MetabolismContainer getMetabolism() {
        return this.metabolism;
    }

    public boolean setSleepLocation(BlockPos sleepLocation, boolean moveTo) {
        return !moveTo || this.getNavigator().tryMoveToXYZ(sleepLocation.getX(), sleepLocation.getY(), sleepLocation.getZ(), 1.0);
    }

    public boolean isSleeping() {
        return this.isSleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.isSleeping = sleeping;
        if (!this.worldObj.isRemote) {
            this.dataManager.set(WATCHER_IS_SLEEPING, this.isSleeping);
        }
    }

    public int getStayAwakeTime() {
        return this.stayAwakeTime;
    }

    public void disturbSleep() {
        this.isSleeping = false;
        this.stayAwakeTime = 400;
    }

    public void writeStatsToLog() {
        LOGGER.info(this);
    }

    @Override
    public String toString() {
        return "DinosaurEntity{ " +
                this.dinosaur.getName() +
                ", id=" + this.getEntityId() +
                ", remote=" + this.getEntityWorld().isRemote +
                ", isDead=" + this.isDead +
                ", isCarcass=" + this.isCarcass +
                ", isSleeping=" + this.isSleeping +
                ", stayAwakeTime=" + this.stayAwakeTime +
                "\n    " +
                ", dinosaurAge=" + this.dinosaurAge +
                ", prevAge=" + this.prevAge +
                ", maxAge" + this.dinosaur.getMaximumAge() +
                ", ticksExisted=" + this.ticksExisted +
                ", entityAge=" + this.entityAge +
                ", isMale=" + this.isMale +
                ", growthSpeedOffset=" + this.growthSpeedOffset +
                "\n    " +
                ", food=" + this.metabolism.getEnergy() + " / " + this.metabolism.getMaxEnergy() + " (" + this.metabolism.getMaxEnergy() * 0.875 + ")" +
                ", water=" + this.metabolism.getWater() + " / " + this.metabolism.getMaxWater() + " (" + this.metabolism.getMaxWater() * 0.875 + ")" +
                ", digestingFood=" + this.metabolism.getDigestingFood() + " / " + MetabolismContainer.MAX_DIGESTION_AMOUNT +
                ", health=" + this.getHealth() + " / " + this.getMaxHealth() +
                "\n    " +
                ", pos=" + this.getPosition() +
                ", eyePos=" + this.getHeadPos() +
                ", eyeHeight=" + this.getEyeHeight() +
                ", lookX=" + this.getLookHelper().getLookPosX() + ", lookY=" + this.getLookHelper().getLookPosY() + ", lookZ=" + this.getLookHelper().getLookPosZ() +
                "\n    " +
                ", width=" + this.width +
                ", bb=" + this.getEntityBoundingBox() +
//                "\n    " +
//                ", anim=" + animation + (animation != null ? ", duration" + animation.duration : "" ) +

//                "dinosaur=" + dinosaur +
//                ", genetics=" + genetics +
//                ", geneticsQuality=" + geneticsQuality +
//                ", currentAnim=" + currentAnim +
//                ", animation=" + animation +
//                ", animTick=" + animTick +
//                ", hasTracker=" + hasTracker +
//                ", tailBuffer=" + tailBuffer +
//                ", owner=" + owner +
//                ", inventory=" + inventory +
//                ", metabolism=" + metabolism +
                " }";
    }

    public Vec3d getHeadPos() {
        double scale = this.interpolate(this.dinosaur.getScaleInfant(), this.dinosaur.getScaleAdult());

        double[] headPos = this.dinosaur.getHeadPosition(this.getGrowthStage(), ((360 - this.rotationYawHead)) % 360 - 180);

        double headX = ((headPos[0] * 0.0625F) - this.dinosaur.getOffsetX()) * scale;
        double headY = (((24 - headPos[1]) * 0.0625F) - this.dinosaur.getOffsetY()) * scale;
        double headZ = ((headPos[2] * 0.0625F) - this.dinosaur.getOffsetZ()) * scale;

        return new Vec3d(this.posX + headX, this.posY + (headY), this.posZ + headZ);
    }

    public boolean areEyelidsClosed() {
        return (this.isCarcass || this.isSleeping) || this.ticksExisted % 100 < 4;
    }

    public boolean getUseInertialTweens() {
        return this.useInertialTweens;
    }

    public void setUseInertialTweens(boolean parUseInertialTweens) {
        this.useInertialTweens = parUseInertialTweens;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(ItemHandler.SPAWN_EGG, 1, EntityHandler.getDinosaurId(this.dinosaur));
    }

    @Override
    public void collideWithEntity(Entity entity) {
        super.collideWithEntity(entity);

        if (this.isSleeping && !this.isRidingSameEntity(entity)) {
            if (!entity.noClip && !this.noClip) {
                if (entity.getClass() != this.getClass()) {
                    this.disturbSleep();
                }
            }
        }
    }

    @Override
    protected void setSize(float width, float height) {
        if (width != this.width || height != this.height) {
            float prevWidth = this.width;
            this.width = width;
            this.height = height;
            if (!this.deserializing) {
                AxisAlignedBB bounds = this.getEntityBoundingBox();
                this.setEntityBoundingBox(new AxisAlignedBB(bounds.minX, bounds.minY, bounds.minZ, bounds.minX + this.width, bounds.minY + this.height, bounds.minZ + this.width));
                if (this.width > prevWidth && !this.firstUpdate && !this.worldObj.isRemote) {
                    this.moveEntity(prevWidth - this.width, 0.0F, prevWidth - this.width);
                }
            } else {
                float halfWidth = this.width / 2.0F;
                this.setEntityBoundingBox(new AxisAlignedBB(this.posX - halfWidth, this.posY, this.posZ - halfWidth, this.posX + halfWidth, this.posY + this.height, this.posZ + halfWidth));
            }
        }
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;

        if (this.worldObj.isRemote) {
            if (this.owner != null) {
                EntityPlayer player = this.worldObj.getPlayerEntityByUUID(this.owner);

                if (player != null) {
                    player.addChatComponentMessage(new TextComponentString(new LangHelper("message.set_order.name").withProperty("order", "order." + order.name().toLowerCase(Locale.ENGLISH) + ".name").build()));
                }
            }

            JurassiCraft.NETWORK_WRAPPER.sendToServer(new SetOrderMessage(this));
        }
    }

    @SafeVarargs
    public final void target(Class<? extends EntityLivingBase>... targets) {
        for (Class<? extends EntityLivingBase> target : targets) {
            this.targetTasks.addTask(1, new SelectTargetEntityAI<>(this, target));
        }

        this.attackTargets.addAll(Lists.newArrayList(targets));
    }

    public EntityAIBase getAttackAI() {
        return new DinosaurAttackMeleeEntityAI(this, this.dinosaur.getAttackSpeed(), false);
    }

    public List<Class<? extends EntityLivingBase>> getAttackTargets() {
        return this.attackTargets;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data) {
        this.metabolism.setEnergy(this.metabolism.getMaxEnergy());
        this.metabolism.setWater(this.metabolism.getMaxWater());
        this.genetics = GeneticsHelper.randomGenetics(this.rand);
        this.setFullyGrown();
        this.setMale(this.rand.nextBoolean());
        this.setDNAQuality(100);
        return data;
    }

    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    public void resetAttackCooldown() {
        this.attackCooldown = 100 + this.getRNG().nextInt(20);
    }

    public void respondToAttack(EntityLivingBase attacker) {
        if (attacker != null && !attacker.isDead && !(attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.isCreativeMode)) {
            List<EntityLivingBase> enemies = new LinkedList<>();

            if (attacker instanceof DinosaurEntity) {
                DinosaurEntity enemyDinosaur = (DinosaurEntity) attacker;

                if (enemyDinosaur.herd != null) {
                    enemies.addAll(enemyDinosaur.herd.members);
                }
            } else {
                enemies.add(attacker);
            }

            if (enemies.size() > 0) {
                Herd herd = this.herd;

                if (herd != null) {
                    herd.fleeing = !herd.shouldDefend(enemies) || this.dinosaur.shouldFlee();

                    for (EntityLivingBase entity : enemies) {
                        if (!herd.enemies.contains(entity)) {
                            herd.enemies.add(entity);
                        }
                    }
                } else {
                    this.setAttackTarget(enemies.get(this.getRNG().nextInt(enemies.size())));
                }
            }
        }
    }

    public int getAnimationLength() {
        return this.animationLength;
    }

    public boolean isRunning() {
        return this.dataManager.get(WATCHER_IS_RUNNING);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.dimension == 0;
    }

    public BlockPos getClosestFeeder() {
        if (this.closestFeeder == null || this.ticksExisted - this.feederSearchTick > 40) {
            this.feederSearchTick = this.ticksExisted;
            OnionTraverser traverser = new OnionTraverser(this.getPosition(), 32);
            for (BlockPos pos : traverser) {
                TileEntity tile = this.worldObj.getTileEntity(pos);
                if (tile instanceof FeederBlockEntity) {
                    FeederBlockEntity feeder = (FeederBlockEntity) tile;
                    if (feeder.canFeedDinosaur(this.getDinosaur()) && feeder.getFeeding() == null && feeder.openAnimation == 0) {
                        Path path = this.getNavigator().getPathToPos(pos);
                        if (path != null && path.getCurrentPathLength() != 0) {
                            return this.closestFeeder = pos;
                        }
                    }
                }
            }
        }
        return this.closestFeeder;
    }

    @Override
    public boolean isInLava() {
        return this.inLava;
    }

    public static class FieldGuideInfo {
        public int hunger;
        public int thirst;
        public boolean flocking;
        public boolean scared;
        public boolean hungry;
        public boolean thirsty;
        public boolean poisoned;

        public static FieldGuideInfo deserialize(ByteBuf buf) {
            FieldGuideInfo info = new FieldGuideInfo();
            info.flocking = buf.readBoolean();
            info.scared = buf.readBoolean();
            info.hunger = buf.readInt();
            info.thirst = buf.readInt();
            info.hungry = buf.readBoolean();
            info.thirsty = buf.readBoolean();
            info.poisoned = buf.readBoolean();
            return info;
        }

        public static FieldGuideInfo serialize(ByteBuf buf, DinosaurEntity entity) {
            MetabolismContainer metabolism = entity.getMetabolism();
            Herd herd = entity.herd;
            FieldGuideInfo info = new FieldGuideInfo();
            info.flocking = herd != null && herd.members.size() > 1 && herd.state == Herd.State.MOVING;
            info.scared = herd != null && herd.fleeing;
            info.hunger = metabolism.getEnergy();
            info.thirst = metabolism.getWater();
            info.hungry = metabolism.isHungry();
            info.thirsty = metabolism.isThirsty();
            info.poisoned = entity.isPotionActive(MobEffects.POISON);
            buf.writeBoolean(info.flocking);
            buf.writeBoolean(info.scared);
            buf.writeInt(info.hunger);
            buf.writeInt(info.thirst);
            buf.writeBoolean(info.hungry);
            buf.writeBoolean(info.thirsty);
            buf.writeBoolean(info.poisoned);
            return info;
        }
    }

    public enum Order {
        WANDER, FOLLOW, SIT
    }
}
