package be.christophebernard.thermostat.database.caching;

public class CacheObject<T> {
	private long lastAccessed;
	private final T value;

	public CacheObject(T value) {
		setLastAccessed(System.currentTimeMillis());
		this.value = value;
	}

	public long getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public T getValue() {
		return value;
	}
}
