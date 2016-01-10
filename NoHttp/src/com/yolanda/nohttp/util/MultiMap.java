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
package com.yolanda.nohttp.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created in Jan 10, 2016 5:00:07 PM
 * 
 * @author YOLANDA
 */
public interface MultiMap<K, V> {

	void add(K key, V value);

	void set(K key, V value);

	void set(Map<K, List<V>> headers);
	
	void remove(K key);
	
	void clear();

	Set<K> keySet();

	List<V> values();

	List<V> getValues(K key);

	V getValue(K key, int index);

	int size();

	boolean isEmpt();

	boolean containsKey(K key);

}
