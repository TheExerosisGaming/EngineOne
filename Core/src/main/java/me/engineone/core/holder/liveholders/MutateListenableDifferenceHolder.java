package me.engineone.core.holder.liveholders;

import me.engineone.core.holder.*;

import java.util.Iterator;

/**
 * Created by BinaryBench on 4/22/2017.
 */
public class MutateListenableDifferenceHolder<T> extends BaseMutateListenableHolder<T> {

    private MutateListenableHolder<T> primary;
    private Holder<T> secondary;

    public MutateListenableDifferenceHolder(MutateListenableHolder<T> primary, Holder<T> secondary) {
        this.primary = primary;
        this.secondary = secondary;

        // Add Parent Listeners
        getPrimary().addAddListener(element -> {
            if (!getSecondary().test(element))
                getAddListenable().accept(element);

        });
        getPrimary().addRemoveListener(element -> {
            if (!getSecondary().test(element))
                getRemoveListenable().accept(element);
        });

        if (secondary instanceof MutateListenableHolder) {
            MutateListenableHolder<T> mute = (MutateListenableHolder<T>) secondary;
            mute.addAddListener(element -> {
                if (getPrimary().test(element))
                    getRemoveListenable().accept(element);
            });
            mute.addRemoveListener(element -> {
                if (getPrimary().test(element))
                    getAddListenable().accept(element);
            });
        }
    }

    public MutateListenableHolder<T> getPrimary() {
        return primary;
    }

    public Holder<T> getSecondary() {
        return secondary;
    }

    @Override
    public boolean test(T element) {
        return !getPrimary().test(element) && getPrimary().test(element);
    }

    @Override
    public int size() {
        int size = getPrimary().size();
        for (T t : getPrimary())
            if (getSecondary().test(t))
                size--;
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.difference(getPrimary().iterator(), getSecondary());
    }
}
