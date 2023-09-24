package de.fwatermann.bungeecord.bansystem.database;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache implementation.
 *
 * @param <K> Key type
 * @param <T> Value type
 */
public class Cache<K, T> {

    private final Map<K, CacheEntry<T>> cache = new HashMap<>();
    private long expiryTime;
    private int maxEntries;

    /**
     * Create a new Cache object.
     *
     * @param expiryTime Time in milliseconds after which the cache entry expires
     * @param maxEntries Maximum number of entries in the cache
     */
    public Cache(long expiryTime, int maxEntries) {
        this.expiryTime = expiryTime;
        this.maxEntries = maxEntries;
    }

    /**
     * Create a new Cache object. The default maximum number of entries is 5000.
     *
     * @param expiryTime Time in milliseconds after which the cache entry expires
     */
    public Cache(long expiryTime) {
        this(expiryTime, 5000);
    }

    /**
     * Create a new Cache object. The default expiry time is 5 minutes and the default maximum
     * number of entries is 5000.
     */
    public Cache() {
        this(5 * 60 * 1000, 5000);
    }

    /**
     * Get the expiry time of the cache entries.
     *
     * @return time in milliseconds
     */
    public long expireTime() {
        return this.expiryTime;
    }

    /**
     * Set the expiry time of the cache entries.
     *
     * @param expireTime time in milliseconds
     */
    public void expireTime(long expireTime) {
        this.expiryTime = expireTime;
    }

    /**
     * Get the maximum number of entries in the cache.
     *
     * @return maximum number of entries
     */
    public int maxEntries() {
        return this.maxEntries;
    }

    /**
     * Set the maximum number of entries in the cache.
     *
     * @param maxEntries maximum number of entries
     */
    public void maxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * Get a value from the cache. If the value does not exist, the callback is called and the value
     * is added to the cache.
     *
     * @param key key of the value
     * @param getCallback callback to get the value if it does not exist
     * @param forceUpdate whether to force an update of the value
     * @return value
     */
    public T lookup(K key, Callback<T> getCallback, boolean forceUpdate) {
        CacheEntry<T> entry = this.cache.get(key);
        if (entry == null || forceUpdate || entry.expireTime < System.currentTimeMillis()) {
            entry =
                    new CacheEntry<T>(
                            getCallback.get(), System.currentTimeMillis() + this.expiryTime);
            this.cache.put(key, entry);
            if (this.cache.size() > this.maxEntries) {
                // Later: May cause lag spikes -> Async garbage collection via scheduler?
                this.cache
                        .entrySet()
                        .removeIf(e -> e.getValue().expireTime < System.currentTimeMillis());
            }
        }
        return entry.value;
    }

    /**
     * Get a value from the cache. If the value does not exist, the callback is called and the value
     * is added to the cache.
     *
     * @param key key of the value
     * @param getCallback callback to get the value if it does not exist
     * @return value
     */
    public T lookup(K key, Callback<T> getCallback) {
        return this.lookup(key, getCallback, false);
    }

    /** Clear the cache. */
    public void clear() {
        this.cache.clear();
    }

    /**
     * Remove a value from the cache.
     *
     * @param key key of the value
     */
    public void remove(K key) {
        this.cache.remove(key);
    }

    /**
     * Add a value to the cache.
     *
     * @param key key of the value
     * @param value value
     */
    public void add(K key, T value) {
        this.cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + this.expiryTime));
    }

    public interface Callback<T> {
        T get();
    }

    private record CacheEntry<T>(T value, long expireTime) {}
}
