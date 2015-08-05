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

/**
 * Created in Jul 28, 2015 7:34:11 PM
 * 
 * @author YOLANDA
 */
public enum ResponseCode {
	/**
	 * The Request is successful</br>
	 * 请求成功
	 */
	CODE_SUCCESSFUL,
	/**
	 * Is not a correct url returns</br>
	 * URL是错误的
	 */
	CODE_ERROR_URL,
	/**
	 * Return when the server response timeout</br>
	 * 连接服务器或者读取数据超时
	 */
	CODE_ERROR_TIMEOUT,
	/**
	 * Can't find the server</br>
	 * 在网络上没有发现服务器
	 */
	CODE_ERROR_NOFIND_SERVER,
	/**
	 * The server is error</br>
	 * 服务器错误
	 */
	CODE_ERROR_SERVER,
	/**
	 * The Manifest. XML lack Intnet permissions</br>
	 * 没有访问网络的权限
	 */
	CODE_ERROR_INTNET_PERMISSION,
	/**
	 * Other abnormal returns</br>
	 * 其它错误
	 */
	CODE_ERROR_OTHER,
	/**
	 * No CODE_ occurs</br>
	 * 状态
	 */
	NONE
}
