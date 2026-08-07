package dev.anvilcraft.tofusthinking.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypeTags;
import dev.anvilcraft.tofusthinking.init.entity.AddonDamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;

public class AddonDamageTypeTagLoader {
    public static void init(RegistrumTagsProvider<DamageType> provider) {
        provider.addTag(AddonDamageTypeTags.REWIND)
                .addOptional(AddonDamageTypes.REWIND.location());

        provider.addTag(AddonDamageTypeTags.CAN_PERFECT_BLOCK)
                .add(DamageTypes.SONIC_BOOM)
                .add(DamageTypes.MAGIC)
                .add(DamageTypes.INDIRECT_MAGIC)
                .add(DamageTypes.THORNS);

        provider.addTag(AddonDamageTypeTags.SONIC_BOOM)
                .add(DamageTypes.SONIC_BOOM);

        provider.addTag(DamageTypeTags.BYPASSES_ARMOR)
                .addOptional(AddonDamageTypes.REWIND.location())
                .addOptional(AddonDamageTypes.REWIND_ATTACK.location())
                .addOptional(AddonDamageTypes.EX_REWIND.location())
                .addOptional(AddonDamageTypes.COUNTER.location());

        provider.addTag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .addOptional(AddonDamageTypes.REWIND_ATTACK.location())
                .addOptional(AddonDamageTypes.EX_REWIND.location());

        provider.addTag(DamageTypeTags.BYPASSES_EFFECTS)
                .addOptional(AddonDamageTypes.REWIND_ATTACK.location())
                .addOptional(AddonDamageTypes.EX_REWIND.location());

        provider.addTag(DamageTypeTags.BYPASSES_RESISTANCE)
                .addOptional(AddonDamageTypes.EX_REWIND.location());

        provider.addTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .addOptional(AddonDamageTypes.EX_REWIND.location());

        provider.addTag(Tags.DamageTypes.IS_TECHNICAL)
                .addOptional(AddonDamageTypes.EX_REWIND.location());

        provider.addTag(DamageTypeTags.NO_KNOCKBACK)
                .addOptional(AddonDamageTypes.REWIND.location())
                .addOptional(AddonDamageTypes.EX_REWIND.location());

        provider.addTag(DamageTypeTags.IS_PLAYER_ATTACK)
                .addOptional(AddonDamageTypes.REWIND_ATTACK.location());
    }
}
