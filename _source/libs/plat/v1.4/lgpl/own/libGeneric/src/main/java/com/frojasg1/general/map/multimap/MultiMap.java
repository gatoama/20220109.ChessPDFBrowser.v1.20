/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.map.multimap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class MultiMap<VV> {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MultiMap.class);

    protected Map<String, MultiMapMapEntry<?, VV>> mapByName;

    protected void init() {
        mapByName = fillMultiMapMapEntryMap();
    }

    protected abstract Map<String, MultiMapMapEntry<?, VV>> fillMultiMapMapEntryMap();

    protected <K> void addMultiMapMapEntry( Map<String, MultiMapMapEntry<?, VV>> map,
											String name, Class<K> keyClass,
											Function<VV, K> keyGetter)
	{
		map.put(name, createMultiMapMapEntry( name, keyClass, keyGetter ) );
	}

	protected <K> MultiMapMapEntry<K, VV> createMultiMapMapEntry( String name,
																Class<K> keyClass,
																Function<VV, K> keyGetter )
	{
		MultiMapMapEntry<K, VV> result = new MultiMapMapEntry();
		
		result.setName(name);
		result.setKeyClass(keyClass);
		result.setKeyGetter(keyGetter);

		return( result );
	}

	protected <K, V> Map<K, V> createMap() {
        return new HashMap<>();
    }

    public <K> Map<K, VV> getMapByName(String mapName, Class<K> keyClass) {
        return (Map<K, VV>) mapByName.get(mapName).getMap();
    }

    public <K> VV remove(String mapName, K key) {
        MultiMapMapEntry<?, VV> me = getAndCheckMultiMapMapEntryForKey(mapName, key);
        VV result = remove((VV) me.getMap().get(key));

        return result;
    }

    public VV remove(VV value) {
        VV result = null;
        if (value != null) {
            for (Map.Entry<String, MultiMapMapEntry<?, VV>> entry : mapByName.entrySet()) {
                MultiMapMapEntry me = entry.getValue();
                Object key = me.getKeyGetter().apply(value);
                VV tmp = (VV) me.getMap().remove(me.getKeyClass().cast(key));
                if (tmp != null) {
                    if (tmp != value) {
                        throw new RuntimeException(String.format("Error trying to remove value: %s. " +
                                        "Existing different element found: '%s' " +
                                        "for mapName='%s', cannot remove",
                                value, tmp, me.getName()));
                    }
                    result = value;
                }
            }
        }

        return result;
    }

    public <K> VV get(String mapName, K key) {
        MultiMapMapEntry<?, VV> me = getAndCheckMultiMapMapEntryForKey(mapName, key);
        VV result = (VV) me.getMap().get(key);

        return result;
    }

    public <K> MultiMapMapEntry<? super K, VV> getAndCheckMultiMapMapEntryForKey(String mapName, K key) {
        MultiMapMapEntry<? super K, VV> result = null;
        if (key != null) {
            result = ( MultiMapMapEntry<? super K, VV> ) mapByName.get(mapName);

            if (result == null) {
                throw new IllegalArgumentException(String.format("Map with name: %s not found", mapName));
            } else if (!result.getKeyClass().isInstance(key)) {
                throw new IllegalArgumentException(String.format("key(%s) was not instance of %s",
                        key.getClass(), result.getKeyClass()));
            }
        } else {
            throw new IllegalArgumentException("key cannot be null");
        }

        return result;
    }

    public void put(VV value) {
        if (value != null) {
            for (Map.Entry<String, MultiMapMapEntry<?, VV>> entry : mapByName.entrySet()) {
                MultiMapMapEntry me = entry.getValue();
                Object key = checkThatDoesNotExistKeyAndGetKey(me, value);
                me.getMap().put(me.getKeyClass().cast(key), value);
            }
        } else {
            throw new IllegalArgumentException("value was null, cannot put");
        }
    }

    protected Object checkThatDoesNotExistKeyAndGetKey(MultiMapMapEntry me, VV value) {
        Object key = me.getKeyGetter().apply(value);
        Map map = me.getMap();
        VV existing = (VV) map.get(key);
        if ((existing != null) && !existing.equals(value)) {
            throw new IllegalArgumentException(String.format("Cannot insert value='%s', as key existed for mapName=%s " +
                            "existing pair in that map: ( %s, %s )",
                    value, me.getName(), key, existing));
        }
        return key;
    }
	
	public void clear()
	{
		for( MultiMapMapEntry<?, VV> mmme : mapByName.values() )
			mmme.getMap().clear();
	}
}
