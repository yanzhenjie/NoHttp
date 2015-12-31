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

/**
 * </br>
 * Created in Dec 31, 2015 4:32:50 PM
 * 
 * @author YOLANDA;
 */
public enum CacheMode {

	/**
	 * First request network, after the failure of the operation to load the cache
	 */
	LOAD_CACHE_REQUEST_FAILED,

	/**
	 * Request for success after the cache
	 */
	SAVE_CACHE_REQUEST_SUCCEED,

	/**
	 * First load the cache, after the failure of the operation of the network request
	 */
	REQUEST_NETWORK_LOAD_FAILED,

	/**
	 * Just request network
	 */
	REQUEST_NETWORK_ONLY

}
