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
package com.yolanda.nohttp;

import java.util.Set;

/**
 * Ful rest interface to write parameters of the interface<br/>
 * Created in Oct 16, 2015 8:16:55 PM
 * 
 * @author YOLANDA
 */
public abstract interface AnalyzeRequest extends BasicAnalyzeRequest {

	/**
	 * Get the output request package body
	 */
	public abstract byte[] getRequestBody();

	/**
	 * Get the parameters set
	 */
	public abstract Set<String> keySet();

	/**
	 * Return {@link #keySet()} key corresponding to value
	 * 
	 * @param key from {@link #keySet()}
	 */
	public abstract Object value(String key);

	/**
	 * Get get of this request
	 */
	public abstract Object getTag();

}
