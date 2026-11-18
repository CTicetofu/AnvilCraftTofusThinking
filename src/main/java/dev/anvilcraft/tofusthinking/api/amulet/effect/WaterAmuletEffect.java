package dev.anvilcraft.tofusthinking.api.amulet.effect;

import dev.dubhe.anvilcraft.api.amulet.ctx.AmuletEffectContext;
import dev.dubhe.anvilcraft.api.amulet.effect.IAmuletEffect;
import dev.dubhe.anvilcraft.init.item.ModAmuletEffectContextKeys;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record WaterAmuletEffect() implements IAmuletEffect {
    public static final WaterAmuletEffect INSTANCE = new WaterAmuletEffect();
    @Override
    public void trigger(@NonNull LivingEntity entity, @NonNull ItemStack amulet, @NonNull AmuletEffectContext ctx) {
        Optional<Mob> target = ctx.get(ModAmuletEffectContextKeys.TARGETING_MOB);
        target.ifPresent(mob -> {
            if(mob.isSensitiveToWater() && !mob.getType().is(Tags.EntityTypes.BOSSES)){
                ctx.set(ModAmuletEffectContextKeys.IGNORE_MOB, true);
            }
        });
        if (!(entity.level() instanceof ServerLevel) || !ctx.getOrDefault(ModAmuletEffectContextKeys.ENABLED, false)) {return;}
        if(entity.getRemainingFireTicks() > -40){entity.setRemainingFireTicks(-40);}
    }
}
