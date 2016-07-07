#NoHttp-cn
> NoHttp，一个有情怀的框架。  

![NoHttp Logo][10]  

[**English document**][1]  

技术交流1群：46523908  
技术交流2群：46505645  
[群行为规范][2]  

严振杰的主页：[http://www.yanzhenjie.com][12]  
严振杰的博客：[http://blog.csdn.net/yanzhenjie1003][3]  
严振杰的Android直播视频下载：[http://pan.baidu.com/s/1miEOtwG][4]  

----
#NoHttp主页和文档地址
NoHttp主页：[http://www.nohttp.net][5]  
NoHttp文档：[http://doc.nohttp.net][6]  
NoHttp源码：[https://github.com/yanzhenjie/NoHttp][1]  
文档还在继续完善，有问题可以加上面的群，或者发[issues][7]，或者直接发邮件给我：smallajax@foxmail.com。

#使用方法
* Eclipse使用Jar包，如果需要依赖源码，请自行下载。
> [下载Jar包 [含源码，274k]][11]  
> [下载Jar包 [不含源码，147k]][8]  

* AndroidStudio使用Gradle构建添加依赖（推荐）
```groovy
compile 'com.yolanda.nohttp:nohttp:1.0.4'
```

#下载Demo
[下载NoHttp源码 Demo源码][13]  
[下载演示Demo APK][9]  

#权限
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#NoHttp特性
　　NoHttp实现了Http1.1（RFC2616），一个标准的Http框架。

* 请求和下载都是队列，平均分配每个线程的资源，支持多个请求并发。
* 支持GET、POST、PUT、PATCH、HEAD、DELETE、OPTIONS、TRACE等请求协议。
* 支持基于POST、PUT、PATCH、DELETE的文件上传（Html表单原理）。
* 文件下载、上传下载、上传和下载的进度回调、错误回调。
* 提供了五种数据缓存策略供开发者选择使用（详细看下文）。
* 支持取消某个请求、取消指定多个请求、取消所有请求。
* 支持自定义Request，利用NoHttp泛型可以解析成你想要的任何数据格式（String、Json、JavaBean等）。
* 支持Session、Cookie的自动维持，App重启、关开机后还持续维持。
* 支持Https、自签名网站Https的访问、支持双向验证。

##友好的调试模式
　　NoHttp提供了调试模式，打开后可以清晰的看到请求过程、怎么传递数据等，基本不用抓包。可以看到请求头、请求数据、响应头、Cookie等的过程。你也不用担心Log太多会让你眼花缭乱，想象不到的整洁。

##请求
>* 支持请求String、Json、FastJson、Gson、Bitmap、JavaBean、XML等扩展。
>* 异步请求，拿到结果直接更新UI，支持同步请求。

##多文件上传
　　所有下载均有进度回调、错误回调等友好的接口。
>* 大文件上传，不会发生OOM。
>* 多文件上传，多个key多个文件，一个key多个文件（`List<File>`）。
>* 支持File、InputStream、ByteArray、Bitmap，实现NoHttp的Binary接口，理论上任何东西都可以传。
>* 支持取消上传。

##文件下载
>* 文件下载，支持多个文件同时下载，并且有进度回调、错误回调等。
>* 支持暂停继续下载，支持取消下载，支持断点续传。
>* 利用NoHttp的多文件下载可以做一个下载管理器。

##缓存模式
>* 仅仅请求网络。
>* 仅仅读取缓存。
>* 标准Http协议缓存(比如响应码是304的情况)，需要服务器支持，如果服务器不支持就和普通请求一样。
>* 先请求网络，请求失败后返回缓存。
>* 先读取缓存，缓存不存在再请求网络。

##取消请求
　　所有取消都支持正在执行的请求。
>* 支持取消某个请求。
>* 支持取消用sign指定的几个请求。
>* 支持取消所有的请求。

##请求自动维持Cookie
>* 支持Session、Cookie、临时Cookie的维持。
>* 支持App重启、关机开机后继续持久化维持。
>* 提供了接口，允许开发者监听Cookie的变化，也可以改变某个Cookie的值。

##重定向
>* 对于Http301、302、303、307等重定向的支持。
>* 支持多级重定向嵌套。
>* 支持禁用重定向、NoHttp提供了操作重定向的接口。

##代理
>* 标准的Java的Api，ProXy：指定代理的IP和Port。
>* 比如调试时代理到自己电脑进行抓包，比如用代理访问Google。

#一. 请求
##String请求
```java
// String 请求对象
Request<String> request = NoHttp.createStringRequest(url, requestMethod);

```

##Json请求
```java
// JsonObject
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, reqeustMethod);
...
// JsonArray
Request<JSONArray> request = NoHttp.createJsonArrayRequest(url, reqeustMethod);
```

##Bitmap请求
```	java
Request<Bitmap> request = NoHttp.createImageRequest(url, requestMethod);
```

##添加参数
```java
Request<JSONObject> request = ...
request.add("name", "yoldada");// String类型
request.add("age", 18);// int类型
request.add("sex", '0')// char类型
request.add("time", 16346468473154); // long类型
...
```

##添加到队列
```java
RequestQueue requestQueue = NoHttp.newRequestQueue();
// 或者传一个并发值，允许三个请求同时并发
// RequestQueue requestQueue = NoHttp.newRequestQueue(3);

// 发起请求
requestQueue.add(what, request, responseListener);
```
　　上面添加到队列时有一个what，这个what会在`responseLisetener`响应时回调给开发者，所以我们可以用一个`responseLisetener`接受多个请求的响应，用what来区分结果。而不用像有的框架一样，每一个请求都要new一个回调。

## 同步请求
　　在当前线程发起请求，在线程这么使用。
```java
Request<String> request = ...
Response<String> response = NoHttp.startRequestSync(request);
if (response.isSucceed()) {
    // 请求成功
} else {
    // 请求失败
}
```

#二. 文件上传
　　支持多文件上传，多个key多个文件，一个key多个文件（`List<File>`）。支持File、InputStream、ByteArray、Bitmap，实现NoHttp的Binary接口，理论上任何东西都可以传。
##单个文件
```java
Request<String> request = ...
request.add("file", new FileBinary(file));
```

##上传多个文件、多个Key多个文件形式
　　这里可以添加各种形式的文件，File、Bitmap、InputStream、ByteArray：
```java
Request<String> request = ...
request.add("file1", new FileBinary(File));
request.add("file2", new FileBinary(File));
request.add("file3", new InputStreamBinary(InputStream));
request.add("file4", new ByteArrayBinary(byte[]));
request.add("file5", new BitmapStreamBinary(Bitmap));
```

##上传多个文件、一个Key多个文件形式
　　用同一个key添加，如果请求方法是POST、PUT、PATCH、DELETE，同一个key不会被覆盖。
```java
Request<String> request = ...
fileList.add("image", new FileBinary(File));
fileList.add("image", new InputStreamBinary(InputStream));
fileList.add("image", new ByteArrayBinary(byte[]));
fileList.add("image", new BitmapStreamBinary(Bitmap));
```
　　或者：
```java
Request<String> request = ...

List<Binary> fileList = ...
fileList.add(new FileBinary(File));
fileList.add(new InputStreamBinary(InputStream));
fileList.add(new ByteArrayBinary(byte[]));
fileList.add(new BitmapStreamBinary(Bitmap));
request.add("file_list", fileList);
```

#三. 下载文件
　　因为下载文件代码比较多，这里贴关键部分，具体的请参考sample。
##发起下载请求
```java
//下载文件
downloadRequest = NoHttp.createDownloadRequest...
// what 区分下载
// downloadRequest 下载请求对象
// downloadListener 下载监听
downloadQueue.add(0, downloadRequest, downloadListener);
```

##暂停或者停止下载
```java
downloadRequest.cancel();
```

##监听下载过程
```java
private DownloadListener downloadListener = new DownloadListener() {
	@Override
	public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
	    // 下载开始
	}

	@Override
	public void onProgress(int what, int progress, long downCount) {
		// 更新下载进度
	}

 	@Override
	public void onFinish(int what, String filePath) {
	    // 下载完成
 	}

	@Override
	public void onDownloadError(int what, StatusCode code, CharSequence message) {
	    // 下载发生错误
	}

	@Override
	public void onCancel(int what) {
	    // 下载被取消或者暂停
	}
};
```

#四. 缓存模式
##1. Http标准协议的缓存，比如响应码是304时
　　NoHttp本身是实现了RFC2616，所以这里不用设置或者设置为DEFAULT。
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
request.setCacheMode(CacheMode.DEFAULT);
```

##2. 当请求服务器失败的时候，读取缓存
　　请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回。
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
```

##3. 如果发现有缓存直接成功，没有缓存才请求服务器
　　我们知道ImageLoader的核心除了内存优化外，剩下一个就是发现把内地有图片则直接使用，没有则请求服务器，所以NoHttp这一点非常使用做一个ImageLoader。
　　如果没有缓存才去请求服务器，否则使用缓存：
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
// 非标准Http协议，改变缓存模式为IF_NONE_CACHE_REQUEST_NETWORK
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST_NETWORK);
```
　　请求图片，缓存图片：
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST_NETWORK);
```

##4. 仅仅请求网络
　　这里不会读取缓存，也不会使用Http304：
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
...
```

##5. 仅仅读取缓存
　　仅仅读取缓存，不会请求网络和其它操作：
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.ONLY_READ_CACHE);
```

#五. 取消请求
##取消单个请求
　　直接调用请求对象的cancel方法。
```java
request.cancel();
```

##从队列中取消指定的请求
　　给请求set一个sign，取消的时候调用队列的cancelBySign就可以取消掉所有指定这个sign的请求。
```java
request.setCancelSign(sign);
...
queue.cancelBySign(sign);
```

##取消队列中所有请求
```java
queue.cancelAll();
```

##停止队列
　　队列停止后再添加请求到队列后，请求不会被执行。
```java
RequestQueue queue = NoHttp.newRequestQueue();
...
queue.stop();
```

#六. 自定义请求类型: FastJsonRequest
## 定义请求对象
```java
public class FastJsonRequest extends RestRequestor<JSONObject> {

    public FastJsonRequest(String url) {
	    super(url);
    }

    public FastJsonRequest(String url, RequestMethod requestMethod) {
	    super(url, requestMethod);
    }

    @Override
    public JSONObject parseResponse(String url, Headers headers, byte[] responseBody) {
	    String result = StringRequest.parseResponseString(url, headers, responseBody);
	    JSONObject jsonObject = null;
	    if (!TextUtils.isEmpty(result)) {
		    jsonObject = JSON.parseObject(result);
	    } else {
		    // 这里默认的错误可以定义为你们自己的数据格式
		    jsonObject = JSON.toJSON("{}");
	    }
	    return jsonObject;
    }

    @Override
    public String getAccept() {
	    // 告诉服务器你接受什么类型的数据
	    return "application/json";
    }
}
```

##b. 使用自定义请求-和NoHttp默认请求没有区别的哦
```java
Request<JSONObject> mRequest = new FastJsonRequest(url, requestMethod);
queue.add(what, mRequest, responseListener);
```

#七. 混淆

##需要知道的
    NoHttp全部的类都可以混淆。
　　NoHttp1.0.0使用了leve23的api，所以打包的时候要用leve23才行。
　　NoHttp1.0.1及以上所有版本使用了反射调用了高级或者低级的api，所以只要是leve9以上的sdk都可以编译。

##如果你非要keep
```text
-dontwarn com.yolanda.nohttp.**
-keep class com.yolanda.nohttp.**{*;}
```

#License
```text
Copyright 2016 Yan Zhenjie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: https://github.com/yanzhenjie/NoHttp/
[2]: https://github.com/yanzhenjie/SkillGroupRule
[3]: http://blog.csdn.net/yanzhenjie1003
[4]: http://pan.baidu.com/s/1miEOtwG
[5]: http://www.nohttp.net
[6]: http://doc.nohttp.net
[7]: https://github.com/yanzhenjie/NoHttp/issues
[8]: https://github.com/yanzhenjie/NoHttp/blob/master/Jar/nohttp1.0.4.jar?raw=true
[9]: https://github.com/yanzhenjie/NoHttp/blob/master/nohttp_sample.apk?raw=true
[10]: http://www.nohttp.net/image/nohttp_logo.svg
[11]: https://github.com/yanzhenjie/NoHttp/blob/master/Jar/nohttp1.0.4-include-source.jar?raw=true
[12]: http://www.yanzhenjie.com
[13]: https://codeload.github.com/yanzhenjie/NoHttp/zip/1.0.4