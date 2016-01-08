/*
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

import com.yolanda.nohttp.Headers;

/**
 * </br>
 * Created in Dec 14, 2015 5:52:41 PM
 * 
 * @author YOLANDA;
 */
public interface Cache {

	/**
	 * Retrieves an entry from the cache.
	 * 
	 * @param key Cache key
	 * @return An {@link Entrance} or null in the event of a cache miss
	 */
	public Entrance get(String key);

	/**
	 * Adds or replaces an entry to the cache.
	 * 
	 * @param key Cache key
	 * @param entry Data to store and metadata for cache coherency, TTL, etc.
	 */
	public void put(String key, Entrance entry);

	/**
	 * Performs any potentially long-running actions needed to initialize the cache;
	 * will be called from a worker thread.
	 */
	public void initialize();

	/**
	 * Removes an entry from the cache.
	 * 
	 * @param key Cache key
	 */
	public void remove(String key);

	/**
	 * Empties the cache.
	 */
	public void clear();

	/**
	 * Data and metadata for an entry returned by the cache.
	 */
	public static class Entrance {

		/**
		 * The data returned from cache.
		 */
		public byte[] data;

		/**
		 * ETag for cache coherency.
		 */
		public String etag;

		/**
		 * Date of this response as reported by the server.
		 */
		public long serverDate;

		/**
		 * The last modified date for the requested object.
		 */
		public long lastModified;

		/**
		 * TTL for this record.
		 */
		public long ttl;

		/**
		 * Soft TTL for this record.
		 */
		public long softTtl;

		/**
		 * Immutable response headers as received from server; must be non-null.
		 */
		public Headers responseHeaders = new Headers();

		/**
		 * True if the entry is expired.
		 */
		public boolean isExpired() {
			return this.ttl < System.currentTimeMillis();
		}

		/**
		 * True if a refresh is needed from the original data source.
		 */
		public boolean refreshNeeded() {
			return this.softTtl < System.currentTimeMillis();
		}
	}

}
