---
navigation:
  title: "Overload Generator"
  icon: "anvilcraft_tofus_thinking:overload_generator"
items:
  - anvilcraft_tofus_thinking:overload_generator
---
# Overload Generator

<item id="anvilcraft_tofus_thinking:overload_generator"/>

> It can be understood as using an accelerating void generator, but it is extremely unstable

- When accelerated (above the activated decay beacon), increase the overload state slightly, with an initial power generation of 2 kW. Each overload point increases the power generation by 8 times, with a maximum of 8192 kW
- When overloaded, if there is no activated decay beacon below, an explosion occurs
- If the overload progress exceeds 14, it will also explode. A comparator can be used to detect the overload progress
- No matter how the overload progresses, it will not explode when it is removed

::: warning
This explosion will destroy some blocks with high explosion resistance, but you can also turn off the explosion in the configuration file
But by default, if the conditions for an explosion are met, it will first destroy itself without producing any falling objects
:::

# Craft

<recipe id="anvilcraft_tofus_thinking:overload_generator"/>