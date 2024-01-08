/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.map.multimap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MultiMapMapEntry<KK, VV> {
    protected String name;
    protected Class<KK> keyClass;
    protected volatile Map<KK, VV> map;
    protected Function<VV, KK> keyGetter;

    public MultiMapMapEntry<KK, VV> setName(String name) {
        this.name = name;
        return this;
    }

    public MultiMapMapEntry<KK, VV> setKeyClass(Class<KK> keyClass) {
        this.keyClass = keyClass;
        return this;
    }

    public MultiMapMapEntry<KK, VV> setKeyGetter(Function<VV, KK> keyGetter) {
        this.keyGetter = keyGetter;
        return this;
    }

    public String getName() {
        return name;
    }

    public Class<KK> getKeyClass() {
        return keyClass;
    }

    protected synchronized Map<KK, VV> createMapIfNull() {
        if (map == null) {
            Map<KK, VV> tmp = createMap();
            map = tmp;
        }
        return map;
    }

    public Map<KK, VV> getMap() {
        if (map == null) {
            createMapIfNull();
        }
        return map;
    }

    public Function<VV, KK> getKeyGetter() {
        return keyGetter;
    }

    protected <K, V> Map<K, V> createMap() {
        return new HashMap<>();
    }
}
