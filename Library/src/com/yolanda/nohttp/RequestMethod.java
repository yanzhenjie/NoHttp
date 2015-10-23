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

/**
 * HTTP request method</br>
 * Created in Oct 10, 2015 8:00:48 PM
 * 
 * @author YOLANDA
 */
public abstract interface RequestMethod {
	final int GET = 0;
	final int POST = 1;
	final int PUT = 2;
	final int DELETE = 3;
	final int HEAD = 4;
	final int OPTIONS = 5;
	final int TRACE = 6;
	final int PATCH = 7;
	final String[] METHOD = new String[] { "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "PATCH" };
}
