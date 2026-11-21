package dev.anvilcraft.tofusthinking.saved.storage;

import dev.anvilcraft.lib.v2.util.stack.UnlimitedItemStack;
import dev.anvilcraft.tofusthinking.init.storage.AddonStorageTypes;
import dev.dubhe.anvilcraft.api.itemhandler.unlimited.OverflowDisposalItemStacksResourceHandler;
import dev.dubhe.anvilcraft.saved.storage.BaseStorage;
import dev.dubhe.anvilcraft.saved.storage.IStorageType;
import net.minecraft.core.Holder;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.function.BiConsumer;

public class SteelBarrelStorage extends BaseStorage<OverflowDisposalItemStacksResourceHandler> {
    public SteelBarrelStorage(UUID id) {
        super(id);
    }

    @Override
    protected @NonNull OverflowDisposalItemStacksResourceHandler constructItemHandler(@NonNull BiConsumer<Integer, UnlimitedItemStack> onContentsChanged) {
        return new OverflowDisposalItemStacksResourceHandler(27648) {
            @Override
            protected void onContentsChanged(int index, @NonNull UnlimitedItemStack original) {
                onContentsChanged.accept(index, original);
            }
        };
    }

    @Override
    public @NonNull Holder<IStorageType<?>> getTypeHolder() {
        return AddonStorageTypes.STEEL_BARREL;
    }
}
