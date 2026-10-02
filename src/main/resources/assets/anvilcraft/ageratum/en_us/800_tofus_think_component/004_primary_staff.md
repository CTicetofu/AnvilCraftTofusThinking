---
navigation:
  title: "Primary Staff"
  icon: "anvilcraft_tofus_thinking:conduit_staff"
items:
  - anvilcraft_tofus_thinking:conduit_staff
  - anvilcraft_tofus_thinking:sonic_boom_staff
  - anvilcraft_tofus_thinking:gem_staff
---
# Conuit Staff

<item id="anvilcraft_tofus_thinking:conduit_staff"/>

> Since conduit has attack capability, can I also use it as a weapon

- When held down, deal 6 magic damage per second (default) to creatures within a 3 * 2 * 3 range around the target point, with a maximum range of 12 squares, and will not attack mob of the same faction as the holder
- When in the inventory and the holder is in rain or water, it will provide conduit energy and automatically increase energy for itself
- If switched to automatic attack mode and held in hand, it automatically searches for monsters within 12 squares to attack, and can no longer actively attack. The frequency of automatic attacks is half of that of active attacks

# Sonic Boom Staff

<item id="anvilcraft_tofus_thinking:sonic_boom_staff"/>

> This sound boom is good, it's mine now

- When used, it will accumulate power and display progress at the top of the screen. The longer the power is accumulated, the longer the damage and range, with a maximum of 20 damage and a 16 grid range
- Causing damage to all creatures within the range, able to penetrate walls

::: info
You may have noticed that this is an inactive Staff, which can be used to accumulate energy by blocking sonic boom damage using<ref item="anvilcraft_tofus_thinking: star_of_the_dea"/>. The progress will be displayed on the item, and when the progress is full, use it to activate the staff by right clicking on it in the item bar
:::

# Gem Staff

<item id="anvilcraft_tofus_thinking:gem_staff"/>

> I hereby declare that I truly do not know what Terraria is, and I use my credit score of 59160153 as a guarantee

- Differently, it is launched with the left button
- The effect varies depending on the gemstone
- The damage logic is similar to [Local invincibility](https://terraria.wiki.gg/wiki/Invincibility_frame#Local_invincibility_2), with each projectile dealing 10 local invincibility frames (actually tick?)

>-Amethyst: Track targets within 10 grids
>-Huang Yu: When hit, deals half of its own damage to entities within a radius of 4 squares
>-Sapphire: Single projectile damage * 0.6, but firing two at once and applying slow force to the hit target, freezing it (as shown in the picture sinking into fine snow)
>-Emerald: Pierce Level +1, when it disappears due to hitting a block, it will leave five times the size of a delayed projectile in place, causing damage to creatures within its range. It will disappear after 50 tick seconds
>-Ruby: Single projectile damage * 0.6. When hitting an entity, it generates an identical projectile nearby (replicas cannot be replicated again) and hits the entity again. At the same time, the target's magic protection can only be half effective
>-Diamond: Pierce Level +1, projectile size increased by five times (does not affect block collision, only affects solid collision), does not stack volume with emerald
>-Amber: Single projectile damage * 0.8, Pierce Level +2, consumes Pierce Level and rebounds when hitting a block

# Craft

<recipe id="anvilcraft_tofus_thinking:conduit_staff"/>
<recipe id="anvilcraft_tofus_thinking:smithing/sonic_boom_staff"/>
<recipe id="anvilcraft_tofus_thinking:gem_staff"/>


