package org.sudu.protogen.generator;

import org.sudu.protogen.descriptors.Descriptor;

import java.util.WeakHashMap;

public interface DescriptorGenerator<D extends Descriptor, T> {

    T generate(D descriptor);

    default DescriptorGenerator<D, T> withCache() {
        return new CachedGenerator<>(this);
    }

    class CachedGenerator<D extends Descriptor, T> implements DescriptorGenerator<D, T> {

        private final WeakHashMap<D, T> cache;

        private final DescriptorGenerator<D, T> generator;

        public CachedGenerator(DescriptorGenerator<D, T> generator, WeakHashMap<D, T> cache) {
            this.cache = cache;
            this.generator = generator;
        }

        public CachedGenerator(DescriptorGenerator<D, T> generator) {
            this(generator, new WeakHashMap<>());
        }

        public final T generate(D descriptor) {
            return cache.computeIfAbsent(descriptor, generator::generate);
        }
    }

}
