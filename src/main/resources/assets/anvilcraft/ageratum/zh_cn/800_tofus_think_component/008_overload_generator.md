---
navigation:
  title: "过载发电机"
  icon: "anvilcraft_tofus_thinking:overload_generator"
items:
  - anvilcraft_tofus_thinking:overload_generator
---
# 过载发电机

<item id="anvilcraft_tofus_thinking:overload_generator"/>

> 可以理解为利用加速运行的虚空发电机，但是极不稳定

- 受到加速时（在激活的腐化信标上方），增加一点过载状态，初始发电量为2 kw，每点过载提升8倍发电量，最高8192 kw
- 当已经过载时，如果下方不是激活的腐化信标则发生爆炸
- 过载进度超过14也会爆炸，可以使用比较器检测过载进度
- 无论过载进度如何，当自身被移除时不会爆炸

::: warning
该爆炸会摧毁一些爆炸抗性较高的方块，当然你也可以在配置文件关闭爆炸
但默认状态下，如果达到发生爆炸的条件会先摧毁自身并不产生掉落物
:::

# 合成

<recipe id="anvilcraft_tofus_thinking:overload_generator"/>