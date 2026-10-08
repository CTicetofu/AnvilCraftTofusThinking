package dev.anvilcraft.tofusthinking.item.weapon;

import com.google.common.collect.Lists;
import dev.anvilcraft.tofusthinking.entity.projectile.ElectromagneticArrow;
import dev.anvilcraft.tofusthinking.entity.projectile.ElectromagneticProjectile;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItemTags;
import dev.anvilcraft.tofusthinking.item.property.component.ProjectileInfo;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.dubhe.anvilcraft.api.item.ICapacitorChargeable;
import dev.dubhe.anvilcraft.init.recipe.ModRecipeTypes;
import dev.dubhe.anvilcraft.recipe.anvil.MassInjectRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

//很多是从原版弩复制的，这似乎是个错误的决定
public class ElectromagneticCrossbow extends ProjectileWeaponItem implements ICapacitorChargeable {
    public static final int MAX_ENERGY = 16000000;
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK_OR_METAL_OR_GEM = ARROW_ONLY.or(stack -> stack.is(AddonItemTags.ELECTROMAGNETIC_CROSSBOW_AMMO) || (stack.getItem() instanceof ProjectileItem && !stack.isDamageableItem()));
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;
    private static final CrossbowItem.ChargingSounds DEFAULT_SOUNDS = new CrossbowItem.ChargingSounds(Optional.of(SoundEvents.CROSSBOW_LOADING_START), Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE), Optional.of(SoundEvents.CROSSBOW_LOADING_END));

    public ElectromagneticCrossbow(Properties properties) {
        super(properties.component(AddonComponents.MAX_ENERGY,MAX_ENERGY));
    }

    @Override
    public @NotNull Predicate<ItemStack> getSupportedHeldProjectiles(@NotNull ItemStack stack) {
        return ARROW_OR_FIREWORK_OR_METAL_OR_GEM;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }
    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ChargedProjectiles chargedprojectiles = itemstack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedprojectiles != null && !chargedprojectiles.isEmpty()) {
            if(canLoadFromOtherHand(player,itemstack,hand,chargedprojectiles)){
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemstack);
            }
            this.performShooting(level, player, hand, itemstack, getShootingPower(chargedprojectiles), 0.1F, null);
            return InteractionResultHolder.consume(itemstack);
        }
        if (!player.getProjectile(itemstack).isEmpty()) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 20;
    }

    private static float getShootingPower(ChargedProjectiles projectile) {
        return projectile.contains(Items.FIREWORK_ROCKET) ? 2F : 4F;
    }

    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        ChargedProjectiles chargedprojectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if(entityLiving instanceof Player && chargedprojectiles != null && !chargedprojectiles.isEmpty()){
            if(canLoadFromOtherHand(entityLiving,stack,entityLiving.getUsedItemHand(),chargedprojectiles)){
                loadFromOtherHand(entityLiving,stack,entityLiving.getUsedItemHand(),chargedprojectiles);
                return;
            }
        }
        int i = this.getUseDuration(stack, entityLiving) - timeLeft;
        float f = getPowerForTime(i, stack, entityLiving);
        if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(entityLiving, stack)) {
            CrossbowItem.ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
            crossbowitem$chargingsounds.end().ifPresent((p_352852_) -> level.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), p_352852_.value(), entityLiving.getSoundSource(), 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F));
        }
    }

    private static boolean canLoadFromOtherHand(LivingEntity shooter, ItemStack crossbowStack,@NotNull InteractionHand hand,@NotNull ChargedProjectiles projectiles){
        ItemStack stack = projectiles.getItems().getFirst().copy();
        int left = crossbowStack.getOrDefault(AddonComponents.LEFT_COUNT,1);
        int max = stack.getMaxStackSize();
        if(stack.has(DataComponents.INTANGIBLE_PROJECTILE) || left >= max){return false;}
        stack.remove(AddonComponents.MAIN_AMMO);
        ItemStack will = shooter.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return ItemStack.isSameItemSameComponents(stack, will);
    }
    private static void loadFromOtherHand(LivingEntity shooter, ItemStack crossbowStack,@NotNull InteractionHand hand,@NotNull ChargedProjectiles projectiles){
        ItemStack stack = projectiles.getItems().getFirst();
        int left = crossbowStack.getOrDefault(AddonComponents.LEFT_COUNT,1);
        int max = stack.getMaxStackSize();
        ItemStack will = shooter.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        int need = max - left;
        int canLoad = Math.min(will.getCount(),need);
        will.split(canLoad);
        crossbowStack.set(AddonComponents.LEFT_COUNT,left + canLoad);
    }

    private static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbowStack) {
        List<ItemStack> list = mutiDraw(crossbowStack, shooter.getProjectile(crossbowStack), shooter);
        if (!list.isEmpty()) {
            crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
            ItemStack stack = list.getFirst();
            float base = 5F;
            if(stack.is(Tags.Items.NUGGETS)){
                if(shooter.level() instanceof ServerLevel level){
                    Optional<MassInjectRecipe> opt = level.getRecipeManager().getRecipeFor(ModRecipeTypes.MASS_INJECT_TYPE.get(), new SingleRecipeInput(stack), level)
                            .map(RecipeHolder::value);
                    if(opt.isPresent()){base = getDamageScaleFromMass(opt.get().getMass() * 0.01F);}
                }
            }
            String type = ElectromagneticProjectile.getTypeFormStack(stack).name();
            crossbowStack.set(AddonComponents.PROJECTILE_INFO,new ProjectileInfo(base,type));
            return true;
        } else {
            return false;
        }
    }

    public static float getDamageScaleFromMass(float mass){
        if(mass <= 0.2){return 0.5F + mass * 20;}
        if(mass <= 0.5){return (4.5F + (mass - 0.2F) * 5);}
        if(mass <= 1){return (6F + (mass - 0.5F) * 2);}
        if(mass <= 2){return (7F + (mass - 1F));}
        return (8F + (mass - 2) * 0.5F);
    }

    protected static List<ItemStack> mutiDraw(ItemStack weapon, ItemStack ammo, LivingEntity shooter) {
        if (ammo.isEmpty()) {
            return List.of();
        } else {
            int shootCount;
            if (shooter.level() instanceof ServerLevel serverlevel && ammo.is(AddonItemTags.ELECTROMAGNETIC_CROSSBOW_CAN_MUTI)) {
                shootCount = EnchantmentHelper.processProjectileCount(serverlevel, weapon, shooter, 1);
            } else {
                shootCount = 1;
            }

            int i = shootCount;
            List<ItemStack> list = new ArrayList<>(i);
            ItemStack stack = ammo.copy();

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = mutiUseAmmo(weapon, j == 0 ? ammo : stack, shooter, j > 0);
                if (!itemstack.isEmpty()) {
                    list.add(itemstack);
                }
            }

            return list;
        }
    }

    protected static ItemStack mutiUseAmmo(ItemStack weapon, ItemStack ammo, LivingEntity shooter, boolean shadow) {
        int consumeCount = 0;
        if(!shadow && shooter.level() instanceof ServerLevel level){
            if(!shooter.hasInfiniteMaterials() && (!(ammo.getItem() instanceof ArrowItem arrowItem) || !arrowItem.isInfinite(ammo,weapon,shooter))){
                if(!ammo.is(Tags.Items.NUGGETS_IRON) || weapon.getEnchantmentLevel(level.holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY)) <= 0){
                    consumeCount = EnchantmentHelper.processAmmoUse(level, weapon, ammo, 1);
                }
            }
        }
        int totalCount = ammo.getCount();

        if (consumeCount > totalCount) {
            return ItemStack.EMPTY;
        }
        ItemStack ammoCopy = ammo.copy();
        if(!shadow){
            int insideCount = totalCount;
            if(consumeCount == 0 && !shooter.hasInfiniteMaterials()){insideCount = ammo.getMaxStackSize();}
            weapon.set(AddonComponents.LEFT_COUNT, insideCount);
            ammoCopy.set(AddonComponents.MAIN_AMMO, Unit.INSTANCE);
        }

        if (consumeCount == 0) {
            ItemStack stack = ammoCopy.copyWithCount(1);
            stack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
            return stack;
        }
        ammo.split(totalCount);
        if (ammo.isEmpty() && shooter instanceof Player player) {
            player.getInventory().removeItem(ammo);
        }
        return ammoCopy.copyWithCount(1);
    }

    public static boolean isCharged(ItemStack crossbowStack) {
        ChargedProjectiles chargedprojectiles = crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return !chargedprojectiles.isEmpty();
    }

    protected void shootProjectile(@NotNull LivingEntity shooter, @NotNull Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d0 = target.getX() - shooter.getX();
            double d1 = target.getZ() - shooter.getZ();
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            double d3 = target.getY(0.3333333333333333) - projectile.getY() + d2 * (double)0.2F;
            vector3f = getProjectileShotVector(shooter, new Vec3(d0, d3, d1), angle);
        } else {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(angle * ((float)Math.PI / 180F), vec3.x, vec3.y, vec3.z);
            Vec3 vec31 = shooter.getViewVector(1.0F);
            vector3f = vec31.toVector3f().rotate(quaternionf);
        }

        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), velocity, inaccuracy);
        float f = getShotPitch(shooter.getRandom(), index);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, shooter.getSoundSource(), 1.0F, f);
    }

    private static Vector3f getProjectileShotVector(LivingEntity shooter, Vec3 distance, float angle) {
        Vector3f vector3f = distance.toVector3f().normalize();
        Vector3f vector3f1 = (new Vector3f(vector3f)).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double)vector3f1.lengthSquared() <= 1.0E-7) {
            Vec3 vec3 = shooter.getUpVector(1.0F);
            vector3f1 = (new Vector3f(vector3f)).cross(vec3.toVector3f());
        }

        Vector3f vector3f2 = (new Vector3f(vector3f)).rotateAxis(((float)Math.PI / 2F), vector3f1.x, vector3f1.y, vector3f1.z);
        return (new Vector3f(vector3f)).rotateAxis(angle * ((float)Math.PI / 180F), vector3f2.x, vector3f2.y, vector3f2.z);
    }

    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack weapon, ItemStack ammo, boolean isCrit) {
        Unit unit = ammo.remove(AddonComponents.MAIN_AMMO);
        boolean isMain = unit != null;
        Vec3 spawnPos = shooter.getEyePosition().subtract(0,0.15,0);
        if (ammo.is(Items.FIREWORK_ROCKET)) {
            return new FireworkRocketEntity(level, ammo, shooter, spawnPos.x, spawnPos.y, spawnPos.z, true);
        }
        if(ammo.is(Items.ARROW) || ammo.is(Items.TIPPED_ARROW)){
            ElectromagneticArrow arrow = new ElectromagneticArrow(level,shooter,ammo,weapon);
            arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
            arrow.setPos(spawnPos);
            arrow.setCritArrow(true);
            arrow.setMain(isMain);
            return arrow;
        }
        if(ammo.is(Tags.Items.NUGGETS) || ammo.is(Tags.Items.GEMS)){
            ElectromagneticProjectile electromagneticProjectile = new ElectromagneticProjectile(level,shooter,ammo,weapon);
            electromagneticProjectile.setPos(spawnPos);
            electromagneticProjectile.setMain(isMain);
            return electromagneticProjectile;
        }
        if(ammo.is(Items.ENDER_PEARL)){
            ThrownEnderpearl thrownenderpearl = new ThrownEnderpearl(level, shooter);
            thrownenderpearl.setItem(ammo);
            return thrownenderpearl;
        }
        if(!(ammo.getItem() instanceof ArrowItem) && ammo.getItem() instanceof ProjectileItem projectileItem){
            Projectile thrown = projectileItem.asProjectile(level,spawnPos,ammo, Direction.DOWN);
            thrown.setOwner(shooter);
            if(thrown instanceof AbstractArrow arrow){if(shooter.hasInfiniteMaterials()){arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;}}
            return thrown;
        }
        Projectile projectile = super.createProjectile(level, shooter, weapon, ammo, isCrit);
        if (projectile instanceof AbstractArrow abstractarrow) {
            abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        }
        return projectile;
    }

    protected int getDurabilityUse(ItemStack stack) {
        return stack.is(Items.FIREWORK_ROCKET) ? 3 : 1;
    }

    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target) {

        if (level instanceof ServerLevel serverlevel) {
            if (shooter instanceof Player player) {
                if (EventHooks.onArrowLoose(weapon, shooter.level(), player, 1, true) < 0) {
                    return;
                }
            }
            if(!ItemUtil.consumeEnergy(shooter,weapon,50000)){return;}
            ChargedProjectiles projectiles = weapon.get(DataComponents.CHARGED_PROJECTILES);
            int leftCount = weapon.getOrDefault(AddonComponents.LEFT_COUNT,1);
            leftCount--;
            weapon.set(AddonComponents.LEFT_COUNT,leftCount);
            if(leftCount <= 0){
                weapon.set(DataComponents.CHARGED_PROJECTILES,ChargedProjectiles.EMPTY);
            }
            if (projectiles != null && !projectiles.isEmpty()) {
                this.shoot(serverlevel, shooter, hand, weapon, projectiles.getItems(), velocity, inaccuracy, shooter instanceof Player, target);
                if (shooter instanceof ServerPlayer serverplayer) {
                    serverplayer.getCooldowns().addCooldown(this,10);
                    CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, weapon);
                    serverplayer.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
                }
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            ChargedProjectiles projectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
            if(projectiles != null && !projectiles.isEmpty()){
                ItemStack inside = projectiles.getItems().getFirst();
                if(!inside.has(DataComponents.INTANGIBLE_PROJECTILE)){
                    int count = stack.getOrDefault(AddonComponents.LEFT_COUNT,0);
                    if(count > 0){
                        inside.remove(AddonComponents.MAIN_AMMO);
                        player.containerMenu.setCarried(inside.copyWithCount(count));
                    }
                }
                stack.set(DataComponents.CHARGED_PROJECTILES,ChargedProjectiles.EMPTY);
                if(player.level().isClientSide){
                    player.level().playLocalSound(player, SoundEvents.STONE_BUTTON_CLICK_OFF,player.getSoundSource(),1,1);
                }
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    private static float getShotPitch(RandomSource random, int index) {
        return index == 0 ? 1.0F : getRandomShotPitch((index & 1) == 1, random);
    }

    private static float getRandomShotPitch(boolean isHighPitched, RandomSource random) {
        float f = isHighPitched ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    public void onUseTick(Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int count) {
        if (!level.isClientSide) {
            CrossbowItem.ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
            float f = (float)(stack.getUseDuration(livingEntity) - count) / (float)getChargeDuration(stack, livingEntity);
            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                crossbowitem$chargingsounds.start().ifPresent((p_352849_) -> level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), p_352849_.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
            }

            if (f >= 0.5F && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                crossbowitem$chargingsounds.mid().ifPresent((p_352855_) -> level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), p_352855_.value(), SoundSource.PLAYERS, 0.5F, 1.0F));
            }
        }

    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return getChargeDuration(stack, entity) + 3;
    }

    public static int getChargeDuration(ItemStack stack, LivingEntity shooter) {
        float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 1.25F);
        return Mth.floor(f * 20.0F);
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    CrossbowItem.ChargingSounds getChargingSounds(ItemStack stack) {
        return EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
    }

    private static float getPowerForTime(int timeLeft, ItemStack stack, LivingEntity shooter) {
        float f = (float)timeLeft / (float)getChargeDuration(stack, shooter);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        boolean shift = Screen.hasShiftDown();
        tooltipComponents.add(TooltipUtil.getItemEnergyTooltip(stack,shift));
        int leftShoot = stack.getOrDefault(AddonComponents.STORED_ENERGY,0)/50000;
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.crossbow_left_count",Component.literal(String.valueOf(leftShoot)).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
        ChargedProjectiles chargedprojectiles = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedprojectiles != null && !chargedprojectiles.isEmpty()) {
            ItemStack inside = chargedprojectiles.getItems().getFirst();
            tooltipComponents.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(inside.getDisplayName()).append(String.format(" x %d",stack.getOrDefault(AddonComponents.LEFT_COUNT,1))));
            if (tooltipFlag.isAdvanced() && inside.is(Items.FIREWORK_ROCKET)) {
                List<Component> list = Lists.newArrayList();
                Items.FIREWORK_ROCKET.appendHoverText(inside, context, list, tooltipFlag);
                if (!list.isEmpty()) {
                    list.replaceAll(sibling -> Component.literal("  ").append(sibling).withStyle(ChatFormatting.GRAY));
                    tooltipComponents.addAll(list);
                }
            }
            if(inside.is(Tags.Items.NUGGETS) || inside.is(Tags.Items.GEMS)){
                ProjectileInfo info = stack.getOrDefault(AddonComponents.PROJECTILE_INFO,ProjectileInfo.DEFAULT);
                tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.damage_scale",Component.literal(String.format("%.2f",info.base())).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
                if(!info.type().equals("NONE")){
                    tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.crossbow_" + info.type().toLowerCase()).withStyle(ChatFormatting.GREEN));
                }
            }
            tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.crossbow_take_out").withStyle(ChatFormatting.AQUA));
        }

    }

    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }
}
