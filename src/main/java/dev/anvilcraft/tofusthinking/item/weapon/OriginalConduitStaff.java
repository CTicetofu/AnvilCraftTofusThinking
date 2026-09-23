package dev.anvilcraft.tofusthinking.item.weapon;

import com.mojang.datafixers.util.Pair;
import dev.anvilcraft.tofusthinking.AnvilCraftTofusThinking;
import dev.anvilcraft.tofusthinking.entity.ExtraDamageSource;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import dev.anvilcraft.tofusthinking.init.item.AddonComponents;
import dev.anvilcraft.tofusthinking.init.item.AddonItems;
import dev.anvilcraft.tofusthinking.item.ExtendItem;
import dev.anvilcraft.tofusthinking.item.weapon.StaffProcess.Abilities;
import dev.anvilcraft.tofusthinking.item.weapon.StaffProcess.AbilityHandler;
import dev.anvilcraft.tofusthinking.item.weapon.StaffProcess.StaffContext;
import dev.anvilcraft.tofusthinking.util.ItemUtil;
import dev.anvilcraft.tofusthinking.util.RayDetectionUtil;
import dev.anvilcraft.tofusthinking.util.TooltipUtil;
import dev.anvilcraft.tofusthinking.util.UnclassifiedUtil;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

//我想我以后会挑个时间重构，这啥玩意
public class OriginalConduitStaff extends ExtendItem {
    public static final int MAX_ENERGY = 16000000;
    public static ResourceLocation KNOCKBACK_ID = AnvilCraftTofusThinking.of("knockback");
    public static ResourceLocation ENTITY_RANGE_ID = AnvilCraftTofusThinking.of("entity_range");
    public OriginalConduitStaff(Properties properties) {
        super(properties.component(AddonComponents.MAX_ENERGY,MAX_ENERGY).component(AddonComponents.AUTO_HUNT,false).attributes(createAttributes()));
        permanent = true;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) || enchantment.getKey() == Enchantments.LOOTING;
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return 20;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(itemstack.getOrDefault(AddonComponents.AUTO_HUNT,false)){return InteractionResultHolder.fail(itemstack);}
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        int useTick = getUseDuration(stack,livingEntity) - remainingUseDuration + 1;
        if(useTick % 20 == 0 && !level.isClientSide && ItemUtil.consumeEnergy(livingEntity,stack,getNeedEnergy(stack))){
            if(isAutoHunting(stack)){return;}
            HitResult hitResult = RayDetectionUtil.create(level).endByLook(livingEntity,20).raycast();
            dealAttack(level,livingEntity,stack,hitResult);
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if(!level.isClientSide && entity instanceof ServerPlayer player && entity.tickCount % 20 == 0){
            if(player.isInWaterRainOrBubble()){
                ItemUtil.addEnergy(stack,40000);
            }
            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER,400,0,false,false,true));
            getGripFromItem(stack).preSec().apply(stack,level,player,slotId,isSelected);
            int need = getNeedEnergy(stack);
            if(isAutoHunting(stack) && canAutoAttack(stack,player, player.tickCount % 40 == 0,slotId,isSelected) && ItemUtil.hasEnoughEnergy(stack,need)){
                List<Monster> monsters = level.getEntitiesOfClass(Monster.class,new AABB(player.blockPosition()).inflate(16), monster -> !monster.isInvulnerable() && monster.isAlive());
                if(!monsters.isEmpty() && ItemUtil.consumeEnergy(player,stack,need)){
                    Monster monster = monsters.getFirst();
                    EntityHitResult result = new EntityHitResult(monster);
                    dealAttack(level,player,stack,result);
                }
            }
        }
    }

    private static boolean canAutoAttack(ItemStack stack, Player player, boolean offhand, int slotId, boolean isSelected){
        if(slotId < 0 || slotId > 8){return false;}
        if(offhand){return player.getOffhandItem() == stack;}
        if(isSelected){return true;}
        if(player.getMainHandItem().is(stack.getItem()) || player.getMainHandItem().is(AddonItems.CONDUIT_STAFF)){return false;}
        NonNullList<ItemStack> items = player.getInventory().items;
        for (int i = 0; i < slotId;i++){
            if(items.get(i).is(AddonItems.ORIGINAL_CONDUIT_STAFF)){return false;}
        }
        return true;
    }

    private static void dealAttack(Level level, LivingEntity attacker, ItemStack stack, HitResult hitResult){
        StaffContext context = new StaffContext(level, attacker, hitResult);
        AbilityHandler headHandler = getHeadFromItem(stack);
        AbilityHandler gripHandler = getGripFromItem(stack);

        headHandler.onRaycast().andThen(gripHandler.onRaycast()).accept(context);
        HitResult result = context.getHitResult();
        Vec3 targetPos = result.getLocation();

        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class,new AABB(targetPos.add(2.5,2.5,2.5),targetPos.subtract(2.5,2.5,2.5))
                ,entity -> entity != attacker && !entity.isAlliedTo(attacker) && !(entity instanceof Player));
        if(result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity hit){
            hit.addEffect(new MobEffectInstance(MobEffects.GLOWING,200));
            if(hit instanceof Player){list.add(hit);}
        }
        context.setTargets(list);
        headHandler.preHit().andThen(gripHandler.preHit()).accept(context);

        ParticleOptions options = headHandler.particle();
        UnclassifiedUtil.spawnCenterParticles(level, ParticleTypes.NAUTILUS,targetPos.x,targetPos.y,targetPos.z,4,0.5F,3F,0.4F,30,false);
        UnclassifiedUtil.spawnCenterParticles(level, options,targetPos.x,targetPos.y,targetPos.z,3,0.5F,0.5F,0.2F,30,false);
        level.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.CONDUIT_ATTACK_TARGET, attacker.getSoundSource(), 1.6F, 1);

        list.removeIf(entity -> !entity.isAlive());
        Consumer<ExtraDamageSource> sourceConsumer = headHandler.modifyDamageSource().andThen(gripHandler.modifyDamageSource());
        list.forEach(entity -> {
            ExtraDamageSource source = AddonDamageTypes.rewindAttack(level,null,attacker);
            sourceConsumer.accept(source);
            entity.hurt(source,15);
            entity.invulnerableTime = 0;
        });

        headHandler.postHit().andThen(gripHandler.postHit()).accept(context);
    }

    public boolean overrideOtherStackedOnMe(@NotNull ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if(action == ClickAction.SECONDARY){
            if(other.isEmpty()){
                boolean auto = !isAutoHunting(stack);
                stack.set(AddonComponents.AUTO_HUNT,auto);
                if(player.level().isClientSide){
                    SoundEvent soundEvent = auto ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
                    player.level().playLocalSound(player, soundEvent,player.getSoundSource(),1,1);
                }
                return true;
            } else {
                Item item = other.getItem();
                String type = HEAD_ITEM_MAP.get(item);
                if(type != null){
                    stack.set(AddonComponents.HEAD_TYPE,type);
                } else {
                    type = GRIP_ITEM_MAP.get(item);
                    if(type != null){stack.set(AddonComponents.GRIP_TYPE,type);}
                }
                return type != null;
            }
        }
        return false;
    }

    @Override
    public @NotNull AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
        return target.getBoundingBox().inflate(2.0D, 0.5D, 2.0D);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SWORD_SWEEP;
    }

    public static boolean isAutoHunting(ItemStack stack){
        return stack.getOrDefault(AddonComponents.AUTO_HUNT,false);
    }

    public static int getNeedEnergy(ItemStack stack){
        int base = isAutoHunting(stack) ? 80 : 40;
        base = (int) (base * getHeadFromItem(stack).discount() * getGripFromItem(stack).discount());
        return base * 1000;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        boolean shift = Screen.hasShiftDown();
        tooltipComponents.add(TooltipUtil.getItemEnergyTooltip(stack, shift));

        boolean auto = isAutoHunting(stack);
        tooltipComponents.add(TooltipUtil.getItemNeedEnergy(getNeedEnergy(stack), shift));
        tooltipComponents.add(Component.translatable("tooltip.anvilcraft_tofus_thinking.right_switch_in_inventory",Component.translatable("tooltip.anvilcraft_tofus_thinking.auto_hunting_mode").withStyle(auto ? ChatFormatting.AQUA : ChatFormatting.STRIKETHROUGH)).withStyle(ChatFormatting.GRAY));

        MutableComponent self = Component.translatable("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff", AddonItems.CONDUIT_STAFF.asItem().getDescription().plainCopy().withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
        MutableComponent head = getHeadItemFromItem(stack).getDescription().plainCopy().withColor(TooltipUtil.KHAKI1);
        MutableComponent grip = getGripItemFromItem(stack).getDescription().plainCopy().withColor(TooltipUtil.KHAKI1);
        if(shift){
            Component headAbility = Component.translatable(String.format("tooltip.anvilcraft_tofus_thinking.ability_%s",getHeadFromItem(stack).name())).withStyle(ChatFormatting.YELLOW);
            head.append(Component.literal(" -> ").withColor(TooltipUtil.ROYAL_BLUE)).append(headAbility);
            Component gripAbility = Component.translatable(String.format("tooltip.anvilcraft_tofus_thinking.ability_%s",getGripFromItem(stack).name())).withStyle(ChatFormatting.YELLOW);
            grip.append(Component.literal(" -> ").withColor(TooltipUtil.ROYAL_BLUE)).append(gripAbility);
            self.append(Component.translatable("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff_more_info"));
        }
        tooltipComponents.add(self);
        tooltipComponents.add(head);
        tooltipComponents.add(grip);
        if(permanent){tooltipComponents.add(TooltipUtil.PERMANENT);}
        if(!shift){
            tooltipComponents.add(TooltipUtil.HOLD_SHIFT_FOR_MORE);
        }
        if(Screen.hasControlDown()){
            //也许应该提前算？
            MutableComponent availableHead = Component.empty().withStyle(ChatFormatting.ITALIC).withColor(TooltipUtil.NAVAJO_WHITE_3);
            for(Item item:HEAD_ITEM_MAP.keySet()){
                availableHead.append(" ").append(item.getDescription());
            }
            MutableComponent availableGrip = Component.empty().withStyle(ChatFormatting.ITALIC).withColor(TooltipUtil.NAVAJO_WHITE_3);
            for(Item item:GRIP_ITEM_MAP.keySet()){
                availableGrip.append(" ").append(item.getDescription());
            }
            tooltipComponents.add(Component.translatable("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff_available_head",availableHead).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("tooltip.anvilcraft.anvilcraft_tofus_thinking.original_conduit_staff_available_grip",availableGrip).withStyle(ChatFormatting.GRAY));
        }
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 14, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(KNOCKBACK_ID,1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(ENTITY_RANGE_ID,1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    private static final HashMap<String, Pair<Item, AbilityHandler>> HEAD_MAP = new HashMap<>();
    private static final LinkedHashMap<Item,String> HEAD_ITEM_MAP = new LinkedHashMap<>();
    private static final HashMap<String, Pair<Item, AbilityHandler>> GRIP_MAP = new HashMap<>();
    private static final LinkedHashMap<Item,String> GRIP_ITEM_MAP = new LinkedHashMap<>();

    public static AbilityHandler getHeadFromItem(ItemStack stack){
        Pair<Item, AbilityHandler> pair = HEAD_MAP.get(stack.getOrDefault(AddonComponents.HEAD_TYPE, "none"));
        return pair == null ? Abilities.NONE : pair.getSecond();
    }

    public static Item getHeadItemFromItem(ItemStack stack){
        Pair<Item, AbilityHandler> pair = HEAD_MAP.get(stack.getOrDefault(AddonComponents.HEAD_TYPE, "none"));
        return pair == null ? Items.GLASS : pair.getFirst();
    }

    public static AbilityHandler getGripFromItem(ItemStack stack){
        Pair<Item, AbilityHandler> pair = GRIP_MAP.get(stack.getOrDefault(AddonComponents.GRIP_TYPE, "none"));
        return pair == null ? Abilities.NONE : pair.getSecond();
    }

    public static Item getGripItemFromItem(ItemStack stack){
        Pair<Item, AbilityHandler> pair = GRIP_MAP.get(stack.getOrDefault(AddonComponents.GRIP_TYPE, "none"));
        return pair == null ? Items.IRON_BLOCK : pair.getFirst();
    }

    public static void initAbility(){
        if(!HEAD_ITEM_MAP.isEmpty()){return;}
        addHead(Abilities.NONE, Items.GLASS);
        addHead(Abilities.TINNED_GLASS, Items.TINTED_GLASS);
        addHead(Abilities.ROYAL_GLASS, ModBlocks.TEMPERING_GLASS.asItem());
        addHead(Abilities.FROST_GLASS, ModBlocks.FROST_GLASS.asItem());
        addHead(Abilities.EMBER_GLASS, ModBlocks.EMBER_GLASS.asItem());

        addGrip(Abilities.NONE,Items.IRON_BLOCK);
        addGrip(Abilities.CURSE_GOLD_BLOCK,ModBlocks.CURSED_GOLD_BLOCK.asItem());
        addGrip(Abilities.ROYAL_STEEL_BLOCK,ModBlocks.ROYAL_STEEL_BLOCK.asItem());
        addGrip(Abilities.FROST_METAL_BLOCK,ModBlocks.FROST_METAL_BLOCK.asItem());
        addGrip(Abilities.EMBER_METAL_BLOCK,ModBlocks.EMBER_METAL_BLOCK.asItem());
    }
    private static void addHead(AbilityHandler handler,Item item){
        HEAD_MAP.put(handler.name(),Pair.of(item,handler));
        HEAD_ITEM_MAP.put(item,handler.name());
    }
    private static void addGrip(AbilityHandler handler,Item item){
        GRIP_MAP.put(handler.name(),Pair.of(item,handler));
        GRIP_ITEM_MAP.put(item,handler.name());
    }
}
