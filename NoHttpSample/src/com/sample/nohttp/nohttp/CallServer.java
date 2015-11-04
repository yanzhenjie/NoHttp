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
package com.sample.nohttp.nohttp;

import com.sample.nohttp.SampleApplication;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import android.content.Context;

/**
 * Created in Oct 23, 2015 1:00:56 PM
 * 
 * @author YOLANDA
 */
public class CallServer {

	/**
	 * 单例
	 */
	private static CallServer _instance;

	/**
	 * NoHttp请求队列
	 */
	private RequestQueue requestQueue;

	private CallServer() {
		// 创建 一个请求队列
		requestQueue = NoHttp.newRequestQueue(SampleApplication.getInstance());
	}

	/**
	 * 创建请求对象，管理请求队列
	 */
	public static CallServer getInstance() {
		if (_instance == null) {
			_instance = new CallServer();
		}
		return _instance;
	}

	/**
	 * 添加一个请求到请求队列
	 */
	public <T> void add(Context context, int what, Request<T> request, HttpCallback<T> callback) {
		// what: 用来区分请求，当多个请求使用同一个OnResponseListener时，在回调方法中会返回这个what
		// request: 请求对象，包涵Cookie、Head、请求参数、URL、请求方法
		// responseListener 请求结果监听，回调时把what原样返回
		requestQueue.add(what, request, new HttpResponseListener<T>(context, callback));
	}

	/**
	 * 取消这个sign标记的所有请求
	 * 
	 * @param sign
	 */
	public void cancelBySign(Object sign) {
		requestQueue.cancelAll(sign);
	}

	/**
	 * 退出app时停止所有请求
	 */
	public void stopAll() {
		requestQueue.stop();
	}

	/**
	 * 开始一个同步请求，推荐在子线程中这么用，主线程不允许访问网络
	 */
	public <T> Response<T> startSyncRequest(Request<T> request) {
		return NoHttp.startRequestSync(SampleApplication.getInstance(), request);
	}

}
