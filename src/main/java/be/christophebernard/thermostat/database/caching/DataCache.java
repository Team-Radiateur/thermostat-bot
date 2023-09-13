package be.christophebernard.thermostat.database.caching;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;

@SuppressWarnings("BusyWait")
public class DataCache<K, T> {
	private final long ttl;
	private final LRUMap<K, CacheObject<T>> cache;
	private final Logger logger;

	public DataCache(long ttl, final long interval, int maxItems) {
		logger = LoggerFactory.getLogger(getClass());

		this.ttl = ttl * 1000;
		cache = new LRUMap<>(maxItems);

		if (this.ttl > 0 && interval > 0) {
			Thread t = new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(interval * 1000);
					} catch (InterruptedException exception) {
						logger.error(exception.getMessage(), exception);
					}
					cleanup();
				}
			});
			t.setDaemon(true);

			t.start();
		}
	}

	public void put(K key, T value) {
		synchronized (cache) {
			logger.info("Putting {} in cache", key);
			cache.put(key, new CacheObject<>(value));
		}
	}

	public T get(K key) {
		synchronized (cache) {
			logger.info("Getting {} from cache", key);
			CacheObject<T> cacheObject = cache.get(key);

			if (cacheObject == null)
				return null;
			else {
				cacheObject.setLastAccessed(System.currentTimeMillis());
				return cacheObject.getValue();
			}
		}
	}

	public void remove(K key) {
		synchronized (cache) {
			logger.info("Removing {} from cache", key);
			cache.remove(key);
		}
	}

	public int size() {
		synchronized (cache) {
			return cache.size();
		}
	}

	public void cleanup() {
		long now = System.currentTimeMillis();
		ArrayList<K> keysToDelete;

		synchronized (cache) {
			MapIterator<K, CacheObject<T>> iterator = cache.mapIterator();

			keysToDelete = new ArrayList<>((cache.size() / 2) + 1);

			while (iterator.hasNext()) {
				K key = iterator.next();
				CacheObject<T> cacheObject = iterator.getValue();

				if (cacheObject != null && (now > (ttl + cacheObject.getLastAccessed()))) {
					keysToDelete.add(key);
				}
			}
		}

		for (K key : keysToDelete) {
			synchronized (cache) {
				logger.info("Automatic cleaning of {} from cache", key);
				cache.remove(key);
			}

			Thread.yield();
		}
	}
}
