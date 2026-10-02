---
navigation:
  title: "初级法杖"
  icon: "anvilcraft_tofus_thinking:conduit_staff"
items:
  - anvilcraft_tofus_thinking:conduit_staff
  - anvilcraft_tofus_thinking:sonic_boom_staff
  - anvilcraft_tofus_thinking:gem_staff
---
# 潮涌核心法杖

<item id="anvilcraft_tofus_thinking:conduit_staff"/>

> 潮涌核心既然有攻击能力，那我是不是也能拿来当武器

- 按住时每秒对目标点周围3*2*3范围的生物造成（默认）6点魔法伤害，最远范围为12格，不会攻击持有者同阵营生物
- 当在物品栏中时且持有者处于雨中或者水中会提供潮涌能量，且会为自己自动增加能量
- 如果切换为自动攻击模式且拿在手上时，则自动寻找12格内的怪物攻击，此时不再能主动攻击，且自动攻击的频率为主动攻击的一半

# 音爆法杖

<item id="anvilcraft_tofus_thinking:sonic_boom_staff"/>

> 这音波不错，现在是我的了

- 使用时蓄力，会在屏幕上方显示进度，蓄力越久，伤害和范围越长，最大为20伤害，16格范围
- 对范围内所有生物造成伤害，可穿透墙壁

::: info
你可能注意到初始是未激活的法杖，可以使用<ref item="anvilcraft_tofus_thinking:star_of_the_sea"/>格挡音爆伤害积攒能量，进度会在物品上显示，当进度为满时使用它在物品栏右键法杖即可激活
:::

# 宝石法杖

<item id="anvilcraft_tofus_thinking:gem_staff"/>

> 在此声明，我真的不知道什么是泰拉瑞亚，我用我59160153的信用分作保障

- 不同的是，它是左键发射的
- 根据宝石不同造成效果不同
- 伤害逻辑类似[局部无敌帧](https://terraria.wiki.gg/zh/wiki/%E6%97%A0%E6%95%8C%E5%B8%A7#%E5%B1%80%E9%83%A8%E6%97%A0%E6%95%8C_2)，每个射弹造成10局部无敌帧（实际上是tick?）


>- 紫水晶：追踪10格以内的目标
>- 黄玉：命中时对4格半径内的实体造成自身一半的伤害
>- 蓝宝石：单个射弹伤害 * 0.6，但是一次发射两枚，且对命中目标施加缓慢，并将其冻结（如图陷入细雪中）
>- 绿宝石：可穿透数量+1，因为命中方块而消失时会在原地留下五倍大小的滞留射弹对范围内生物造成伤害，存在50tick秒后消失
>- 红宝石：单个射弹伤害 * 0.6，命中实体时在附近生成一枚相同的射弹（复制体不能再复制）再次击中实体，同时目标的魔法保护只能发挥一半效力
>- 钻石：可穿透数量+1，射弹增大至原先的五倍（不影响方块碰撞，只影响实体碰撞），不与绿宝石叠加体积
>- 琥珀：单个射弹伤害 * 0.8，可穿透数量+2，命中方块时消耗穿透数量并反弹

# 合成

<recipe id="anvilcraft_tofus_thinking:conduit_staff"/>
<recipe id="anvilcraft_tofus_thinking:smithing/sonic_boom_staff"/>
<recipe id="anvilcraft_tofus_thinking:gem_staff"/>


