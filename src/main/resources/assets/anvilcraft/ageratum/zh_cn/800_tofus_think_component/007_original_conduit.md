---
navigation:
  title: "原初化潮涌核心"
  icon: "anvilcraft_tofus_thinking:original_conduit"
items:
  - anvilcraft_tofus_thinking:original_conduit
---
# 原初化潮涌核心

<item id="anvilcraft_tofus_thinking:original_conduit"/>
<item id="anvilcraft_tofus_thinking:stable_prismarine_bricks"/>

> 既然信标可以受到时间之力变异，为什么潮涌核心不行呢？

- 基本能力等同潮涌核心
- 可用于执行回溯或者重铸配方
- 只需要本格有水和八个框架方块即可启动
- 可修复附近水中或雨中玩家快捷栏和装备栏的物品
- 对附近水中或雨中敌对生物造成伤害时同时对附近2格范围生物造成伤害，且伤害值提升到15
- 如果框架内有8个以上坚固海晶石砖，则所有对生物效果无需在水中或者雨中
- 每有一个框架，增加2kw的发电

## 重铸图示

<structure id="../../structures/tofus_thinking/reforge_structure.nbt"/>
该方块可容纳物品，右键或者自动化输入均可，完成的物品可以被输出
材料放置在该物品上方一格的鱼缸中，也可直接作为掉落物形式，如果需要流体，则只能是鱼缸
需要该方块四个方向中任意两个方向有坚固海晶石砖，并在10tick内分别受到铁砧砸击以完成重铸，重铸可赋予物品新的属性

::: warning
如果一个原初化潮涌核心十秒内没有尝试执行配方，则下次尝试执行配方时会尝试对工作范围内生物进行回溯，脱去其所有装备（即使有绑定诅咒）并杀死它
:::
::: danger
千万不要让它受到另一种时间之力的影响，如激活的腐化信标上方，会造成无法挽回的后果！！！
:::
# 合成

<recipe id="anvilcraft_tofus_thinking:anvil_collision/original_conduit"/>

>当然，你要是有闲心，可以尝试另一种获取办法
这是临时想的，实际上我也感觉问题很多
使用<ref item="anvilcraft_tofus_thinking:star_of_the_sea"/>格挡时移伤害，并把它拍在凋灵脸上，即可让凋灵变异，此时用它完美格挡该凋灵发射的蓝色凋灵之首即可，该凋灵会自我降低血量直至死亡，接受不同凋灵的发射物会重置进度。