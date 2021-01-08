package de.calltopower.raspisurveillance.impl.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.calltopower.raspisurveillance.api.config.RSConfig;
import de.calltopower.raspisurveillance.impl.properties.RSSettingsProperties;

/**
 * File content cache
 */
@Component
public class RSFileContentCache implements RSConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSFileContentCache.class);

    private Map<String, String> stringFileCache;

    private RSSettingsProperties settingsProperties;

    /**
     * Constructor
     */
    @Autowired
    public RSFileContentCache(RSSettingsProperties settingsProperties) {
        this.settingsProperties = settingsProperties;

        stringFileCache = new HashMap<>(this.settingsProperties.getMaxStringFileCacheSize());
    }

    /**
     * Returns whether the key is already contained in the cache
     * 
     * @param key The key
     * @return boolean flag whether the key is already contained in the cache
     */
    public boolean containsKey(String key) {
        return stringFileCache.containsKey(key);
    }

    /**
     * Returns the content if found
     * 
     * @param key The key
     * @return the content if found
     */
    public String get(String key) {
        return stringFileCache.get(key);
    }

    /**
     * Caches the given key-value-pair
     * 
     * @param key   The key
     * @param value the value
     * @return The value
     */
    public String cache(String key, String value) {
        if (!stringFileCache.containsKey(key)
                && stringFileCache.size() >= this.settingsProperties.getMaxStringFileCacheSize()) {
            List<String> keys = stringFileCache.keySet().stream().collect(Collectors.toList());
            Collections.shuffle(keys);
            keys.remove(keys.size() - 1);
            Map<String, String> newCacheEntries = keys.stream()
                    .collect(Collectors.toMap(k -> k, k -> stringFileCache.get(k)));
            stringFileCache.clear();
            stringFileCache.putAll(newCacheEntries);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Saving key to cache: \"%s\"", key));
        }

        stringFileCache.put(key, value);
        return value;
    }

}
