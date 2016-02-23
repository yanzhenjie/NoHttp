/**
 * Copyright © YOLANDA. All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Can save multiple the value of the map
 * </br>
 * Created in Jan 10, 2016 5:00:07 PM
 *
 * @author YOLANDA
 */
public interface MultiValueMap<K, V> {

    /**
     * Add a value for a key
     */
    void add(K key, V value);

    /**
     * Add more value to a key
     */
    void add(K key, List<V> values);

    /**
     * Set the value for a key, if the key has the value, delete all of the old value, then the new value added
     */
    void set(K key, V value);

    /**
     * @see #set(Object, Object)
     */
    void set(K key, List<V> values);

    /**
     * The removal of all key/value pair, add new keys to enter
     */
    void set(Map<K, List<V>> values);

    /**
     * Delete a key-value
     */
    void remove(K key);

    /**
     * Remove all key-value
     */
    void clear();

    /**
     * Get the key set
     */
    Set<K> keySet();

    /**
     * To get all key of all values
     */
    List<V> values();

    /**
     * To get the key of the at index value
     */
    V getValue(K key, int index);

    /**
     * To get key of all values
     */
    List<V> getValues(K key);

    /**
     * The size of the map
     */
    int size();

    /**
     * If the map has no value
     */
    boolean isEmpty();

    /**
     * Whether the map with a key
     */
    boolean containsKey(K key);

}
