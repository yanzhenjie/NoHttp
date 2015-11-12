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
package com.yolanda.nohttp.able;

/**
 * Created in Nov 12, 2015 5:11:56 PM
 * 
 * @author YOLANDA;
 */
public abstract interface Cancelable {

	/**
	 * Cancel request
	 */
	public abstract void cancel();

	/**
	 * Query whether to cancel the state
	 */
	public abstract boolean isCanceled();

	/**
	 * Cancel request based on sign
	 */
	public abstract void cancelBySign(Object sign);

}
