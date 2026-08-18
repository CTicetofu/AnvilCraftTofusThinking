package dev.anvilcraft.tofusthinking.client.gui.item;

public class PlanePoint {
    public static PlanePoint VANILLA_BAR_START = create(2,13);
    public static PlanePoint VANILLA_BAR_END = create(15,14);
    public static PlanePoint VANILLA_BAR_BACKGROUND_END = create(15,15);

    public static PlanePoint ENERGY_BAR_START = create(1,12);
    public static PlanePoint ENERGY_BAR_END = create(2,2);
    public static PlanePoint ENERGY_BACKGROUND_END = create(3,2);

    public final int x;
    public final int y;

    public PlanePoint(int x,int y){
        this.x = x;
        this.y = y;
    }

    public static PlanePoint create(int x,int y){
        return new PlanePoint(x,y);
    }

    public PlanePoint offset(int x, int y){
        return new PlanePoint(this.x + x,this.y + y);
    }

    public PlanePoint xOffset(int offset){
        return this.offset(offset,0);
    }

    public PlanePoint yOffset(int offset){
        return this.offset(0,offset);
    }
}
