/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.cache;

import com.yolanda.nohttp.db.DBManager;
import com.yolanda.nohttp.db.Where;
import com.yolanda.nohttp.db.Where.Options;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Http cache interface implementation.</p>
 * Created in Jan 10, 2016 12:45:34 AM.
 *
 * @author Yan Zhenjie;
 */
public enum DiskCacheStore implements Cache<CacheEntity> {

    INSTANCE;

    /**
     * Database sync lock.
     */
    private Lock mLock;
    /**
     * Database manager.
     */
    private DBManager<CacheEntity> mManager;

    DiskCacheStore() {
        mLock = new ReentrantLock();
        mManager = CacheDiskManager.getInstance();
    }

    @Override
    public CacheEntity get(String key) {
        mLock.lock();
        try {
            Where where = new Where(CacheDisk.KEY, Options.EQUAL, key);
            List<CacheEntity> cacheEntities = mManager.get(CacheDisk.ALL, where.get(), null, null, null);
            return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public CacheEntity replace(String key, CacheEntity entrance) {
        mLock.lock();
        try {
            entrance.setKey(key);
            mManager.replace(entrance);
            return entrance;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean remove(String key) {
        mLock.lock();
        try {
            if (key == null)
                return true;
            Where where = new Where(CacheDisk.KEY, Options.EQUAL, key);
            return mManager.delete(where.toString());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean clear() {
        mLock.lock();
        try {
            return mManager.deleteAll();
        } finally {
            mLock.unlock();
        }
    }

}
