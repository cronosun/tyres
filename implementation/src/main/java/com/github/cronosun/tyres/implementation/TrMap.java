package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.TyResException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A map implementation that is optimized for reads.
 * <p>
 * Note 1: Unlike other map implementations, this map expects the entry to
 * be present. This means: If the hashes match (and there's no second matching hash), this implementation
 * expects this to be the correct entry ({@link Object#equals(Object)} is not called in that case).
 * {@link Object#equals(Object)} is only called if there are conflicting hashes in this map.
 * <p>
 * Note 2: This implementation only works efficiently if the produces hashes are evenly distributed within
 * the int-range ({@link Integer#MIN_VALUE} {@link Integer#MAX_VALUE}).
 * <p>
 * Note 3: The capacity must be at least as large as the number of elements. A bigger capacity uses more memory
 * but reduces the likelyhood of conflicts (improves performance).
 */
final class TrMap<K, V> {
    private final HashFunction<K> hashFunction;
    private final AbstractInternalEntry<K, V>[] array;
    private final int length;
    private final int numberOfConflicts;

    public static <K, V> TrMap<K, V> createInstance(HashFunction<K> hashFunction, int capacity, Stream<Entry<K, V>> stream) {
        return internalCreateInstance(hashFunction, capacity, stream);
    }

    private TrMap(HashFunction<K> hashFunction, AbstractInternalEntry<K, V>[] array, int length, int numberOfConflicts) {
        this.hashFunction = hashFunction;
        this.array = array;
        this.length = length;
        this.numberOfConflicts = numberOfConflicts;
    }

    public interface HashFunction<T> {
        int hash(T value);
    }

    public static final class Entry<K, V> {
        private final K key;
        private final V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @Nullable
    public V get(K key) {
        return this.internalGet(key);
    }

    public int numberOfConflicts() {
        return numberOfConflicts;
    }

    public int length() {
        return length;
    }

    private static <K, V> TrMap<K, V> internalCreateInstance(HashFunction<K> hashFunction, int capacity, Stream<Entry<K, V>> stream) {
        var nonGenericArray = new AbstractInternalEntry[capacity];
        @SuppressWarnings("unchecked")
        var array = (AbstractInternalEntry<K, V>[]) nonGenericArray;
        var length = 0;
        var numberOfConflicts = 0;
        var iterator = stream.iterator();
        while (iterator.hasNext()) {
            length++;
            var kvEntry = iterator.next();
            var conflict = setEntryToArray(hashFunction, capacity, array, kvEntry);
            if (conflict) {
                numberOfConflicts++;
            }
        }
        if (length > capacity) {
            throw new TyResException("Implementation error: Capacity is smaller than the number of elements in stream.");
        }
        finishArray(array);
        return new TrMap<>(hashFunction, array, length, numberOfConflicts);
    }

    private static <K, V> void finishArray(AbstractInternalEntry<K, V>[] array) {
        for (var entry : array) {
            if (entry != null) {
                entry.finish();
            }
        }
    }

    @Nullable
    private V internalGet(K key) {
        var hash = hashFunction.hash(key);
        var capacity = this.array.length;
        var index = indexFromHash(hash, capacity);
        var entry = this.array[index];
        if (entry != null) {
            return entry.get(hash, key);
        } else {
            return null;
        }
    }

    private static <K, V> boolean setEntryToArray(
            HashFunction<K> hashFunction,
            int capacity,
            AbstractInternalEntry<K, V>[] array,
            Entry<K, V> entry) {
        var key = entry.key;
        var hash = hashFunction.hash(key);
        var index = indexFromHash(hash, capacity);

        var existingEntry = array[index];
        if (existingEntry == null) {
            array[index] = new NonConflictingInternalEntry<>(hash, key, entry.value);
            return false;
        } else {
            // there's a conflict
            var maybeNewValue = existingEntry.add(hash, key, entry.value);
            if (maybeNewValue != null) {
                array[index] = maybeNewValue;
            }
            return true;
        }
    }

    private static final long TO_ADD = 2147483648L;

    private static int indexFromHash(int hash, int capacity) {
        var longHash = (long) hash + TO_ADD;
        return (int) (longHash % capacity);
    }

    private static abstract class AbstractInternalEntry<K, V> {
        @Nullable
        abstract AbstractInternalEntry<K, V> add(int hash, K key, V value);

        abstract void finish();

        @Nullable
        public abstract V get(int hash, K key);
    }

    private static final class NonConflictingInternalEntry<K, V> extends AbstractInternalEntry<K, V> {
        private final int hash;
        private final K key;
        private final V value;

        private NonConflictingInternalEntry(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        @Override
        AbstractInternalEntry<K, V> add(int hash, K key, V value) {
            var conflicting = new ConflictingInternalEntry<K, V>();
            conflicting.entries.add(this);
            conflicting.add(hash, key, value);
            return conflicting;
        }

        @Override
        void finish() {
            // nothing to do here
        }

        @Nullable
        @Override
        public V get(int hash, K key) {
            return value;
        }
    }

    private static final class ConflictingInternalEntry<K, V> extends AbstractInternalEntry<K, V> {

        private final ArrayList<NonConflictingInternalEntry<K, V>> entries = new ArrayList<>(2);

        @Nullable
        @Override
        AbstractInternalEntry<K, V> add(int hash, K key, V value) {
            this.entries.add(new NonConflictingInternalEntry<>(hash, key, value));
            return null;
        }

        @Override
        void finish() {
            entries.sort(Comparator.comparingInt(o -> o.hash));
            entries.trimToSize();
        }

        @Nullable
        @Override
        public V get(int hash, K key) {
            for (var candidate : entries) {
                var candidateHash = candidate.hash;
                if (candidateHash == hash) {
                    var candidateKey = candidate.key;
                    if (Objects.equals(candidateKey, key)) {
                        return candidate.value;
                    }
                } else if (candidateHash > hash) {
                    return null;
                }
            }
            return null;
        }
    }
}
