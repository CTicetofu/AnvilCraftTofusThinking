package dev.anvilcraft.tofusthinking.item.weapon;

public abstract class RightSpellItem extends SpellItem{
    public static final String RIGHT = "RIGHT_CLICK";
    public RightSpellItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getGroupSpellId() {
        return RIGHT;
    }
}
