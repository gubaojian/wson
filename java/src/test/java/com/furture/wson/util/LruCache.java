package com.furture.wson.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/3.
 */
public class LruCache<K,V> extends LinkedHashMap<K,V> {
    private int cacheSize;


    public LruCache(int cacheSize) {
        super(cacheSize, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > cacheSize;
    }
}
