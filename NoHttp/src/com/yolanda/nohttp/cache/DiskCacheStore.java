/**
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.cache;

import java.util.List;

import com.yolanda.nohttp.db.DBManager;
import com.yolanda.nohttp.db.Where;
import com.yolanda.nohttp.db.Where.Options;

/**
 * Created in Jan 10, 2016 12:45:34 AM
 * 
 * @author YOLANDA
 */
public enum DiskCacheStore implements Cache<CacheEntity> {

	INSTANCE;

	private DBManager<CacheEntity> mManager;

	private DiskCacheStore() {
		mManager = CacheDiskManager.getInstance();
	}

	@Override
	public CacheEntity get(String key) {
		Where where = new Where(CacheDisker.KEY, Options.EQUAL, key);
		List<CacheEntity> cacheEntities = mManager.get(CacheDisker.ALL, where.get(), null, null, null);
		return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
	}

	@Override
	public CacheEntity put(String key, CacheEntity entrance) {
		entrance.setKey(key);
		mManager.replace(entrance);
		return entrance;
	}

	@Override
	public boolean remove(String key) {
		if (key == null)
			return true;
		Where where = new Where(CacheDisker.KEY, Options.EQUAL, key);
		return mManager.delete(where.toString());
	}

	@Override
	public boolean clear() {
		return mManager.deleteAll();
	}

}
