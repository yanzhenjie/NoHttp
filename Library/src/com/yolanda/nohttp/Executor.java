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
package com.yolanda.nohttp;

import java.io.Serializable;

/**
 * Created in Jul 28, 2015 7:31:22 PM
 * 
 * @author YOLANDA
 */
class Executor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 103L;
	/**
	 * What content from the HTTP request
	 */
	int command;
	/**
	 * Used to mark a request
	 */
	int what;
	/**
	 * Listen to the results of a request
	 */
	OnResponseListener responseListener;
}
