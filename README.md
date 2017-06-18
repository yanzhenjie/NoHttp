![NoHttp Logo](./image/logo.png)  

**NoHttp使用文档：[http://doc.nohttp.net](http://doc.nohttp.net)**  
**NoHttp测试接口：[http://api.nohttp.net](http://api.nohttp.net)**  
**欢迎加入QQ技术交流群：[547839514](https://jq.qq.com/?_wv=1027&k=4Abk0YP)**  

**[查阅NoHttp升级日志](https://github.com/yanzhenjie/NoHttp/blob/master/UPGRADE.md)**  

读写SD卡需要运行时权限管理：[https://github.com/yanzhenjie/AndPermission](https://github.com/yanzhenjie/AndPermission)  

## 框架特性
* 动态配置底层框架为OkHttp、HttpURLConnection
* 与RxJava完美结合，支持异步请求、支持同步请求
* 多文件上传，支持大文件上传，表单提交数据
* 文件下载、上传下载、上传和下载的进度回调、错误回调
* 支持Json、xml、Map、List的提交
* 完美的Http缓存模式，可指定缓存到数据库、SD卡，缓存数据已安全加密
* 自定义Request，直接请求JsonObject、JavaBean等
* Cookie的持久化自动维持
* http 301 302 303 304 307重定向，支持多层嵌套重定向
* Https、自签名网站Https的访问、支持双向验证
* 内部自动修复URLConnection、OkHttp等在Android4.4及以下时不支持TLSv1.1、TLSv1.2协议的问题
* 失败重试机制，支持请求优先级
* GET、POST、PUT、PATCH、HEAD、DELETE、OPTIONS、TRACE等请求协议
* 异步模块用队列保存请求，平均分配多线程的资源，支持多个请求并发
* 队列支持取消某个请求、取消指定多个请求、取消所有请求

## 使用方法
### Gradle
* 如果使用HttpURLConnection作为网络层
```groovy
compile 'com.yanzhenjie.nohttp:nohttp:1.1.3'
```
* 如果要使用OkHttp作为网络层，请再依赖
```groovy
compile 'com.yanzhenjie.nohttp:okhttp:1.1.3'
```

> 新版NoHttp修改了包名为`com.yanzhenjie.nohttp`，开发者从旧版升级后请使用全局替换，将`com.yolanda.nohttp`替换为`com.yanzhenjie.nohttp`即可。

### Eclipse ADT
2. 自行下载上方jar包。  

## 初始化
NoHttp初始化时分两种情况，最基本的初始化仅仅需要一个Context，高级初始化需要一个Config。

### 一般初始化
直接初始化后，一切采用默认设置。
```java
NoHttp.initialize(this);
```

### 高级初始化
```java
InitializationConfig config = InitializationConfig.newBuilder(context)
    // 其它配置。
    ...
    .build();

NoHttp.initialize(config);
```

关于超时，很多人都没有彻底理解或理解有误差，我在知乎上写过一个答案，请参考：  
[HTTP 在什么情况下会请求超时？](https://www.zhihu.com/question/21609463/answer/160100810)  

下面介绍上方省略的**[其它配置]**的详情。

```java
InitializationConfig config = InitializationConfig.newBuilder(context)
    // 全局连接服务器超时时间，单位毫秒，默认10s。
    .connectionTimeout(30 * 1000)
    // 全局等待服务器响应超时时间，单位毫秒，默认10s。
    .readTimeout(30 * 1000)
    // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
    .cacheStore(
        // 如果不使用缓存，setEnable(false)禁用。
        new DBCacheStore(context).setEnable(true)
    )
    // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现CookieStore接口。
    .cookieStore(
        // 如果不维护cookie，setEnable(false)禁用。
        new DBCookieStore(context).setEnable(true)
    )
    // 配置网络层，默认URLConnectionNetworkExecutor，如果想用OkHttp：OkHttpNetworkExecutor。
    .networkExecutor()
    // 全局通用Header，add是添加，多次调用add不会覆盖上次add。
    .addHeader()
    // 全局通用Param，add是添加，多次调用add不会覆盖上次add。
    .addParam()
    .sslSocketFactory() // 全局SSLSocketFactory。
    .hostnameVerifier() // 全局HostnameVerifier。
    .retry(x) // 全局重试次数，配置后每个请求失败都会重试x次。
    .build();
```

**说明**：
1. 上方配置可以全部配置，也可以只配置其中一个或者几个。
2. addHeader()、addParam()可以调用多次，且值不会被覆盖。
3. 使用`DiskCacheStore()`时默认缓存到`context.getCacheDir()`目录，使用`DiskCacheStore(path)`指定缓存目录为`path`，不过要注意SD卡的读写权限和运行时权限：[AndPermission](https://github.com/yanzhenjie/AndPermission)。

例如：  
```java
InitializationConfig config = InitializationConfig.newBuilder(context)
    .addHeader("Token", "123") // 全局请求头。
    .addHeader("Token", "456") // 全局请求头，不会覆盖上面的。
    .addParam("AppVersion", "1.0.0") // 全局请求参数。
    .addParam("AppType", "Android") // 全局请求参数。
    .addParam("AppType", "iOS") // 全局请求参数，不会覆盖上面的两个。
    .build();
```

## 需要的权限
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

## 调试模式
```java
Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
Logger.setTag("NoHttpSample");// 打印Log的tag。
```
开启NoHttp的调试模式后可看到请求过程、日志和错误信息，基本不用抓包。可以看到请求头、请求数据、响应头、Cookie等，而且打印出的Log非常整齐。

所以说，如果你使用过程中遇到什么问题了，开启调试模式，一切妖魔鬼怪都会现形的。

##第三方异步框架

`NoHttp`的核心就是同步请求方法，NoHttp的异步方法（`RequestQueue`、`AsyncRequestExecutor`都是基于同步请求封装的），所以使用`RxJava`、`AsyncTask`等都可以很好的封装`NoHttp`，一个请求`String`的示例：
```
StringRequest request = new String(url, RequestMethod.GET);
Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
if (response.isSucceed()) {
    // 请求成功。
} else {
    // 请求失败，拿到错误：
    Exception e = response.getException();
}
```

下面是两个项目群里的基友基于RxJava + NoHttp封装的，开发者可以作为参考或者直接使用：
1. [IRequest](https://github.com/yuanshenbin/IRequest)（袁慎彬）
2. [NohttpRxUtils](https://github.com/LiqiNew/NohttpRxUtils)（李奇）

# 同步请求和异步请求
`NoHttp`的请求模块的核心其实就是同步请求，`NoHttp`的异步请求分为两个类型，一个是队列：`RequestQueue`，另一个是异步请求执行器`SyncRequestExecutor`。

## 同步请求
一个请求`String`的示例：
```java
StringRequest req = new String("http://api.nohttp.net");
Response<String> response = SyncRequestExecutor.INSTANCE.execute(req);
if (response.isSucceed()) {
    // 请求成功。
} else {
    // 请求失败，拿到错误：
    Exception e = response.getException();
}
```
当然同步请求只适合在**子线程**中使用，因为Android主线程不允许发起网络请求。当然如果使用`RxJava`、`AsyncTask`等把同步请求封装一下也可以用在主线程，不过NoHttp提供了两种异步请求的方式，可以直接用在主线程中。

## 异步请求-AsyncRequestExecutor
```java
StringRequest request = new StringRequest("http://api.nohttp.net");
AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<String>() {
    @Override
    public void onSucceed(int what, Response<String> response) {
        // 请求成功。
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        // 请求失败。
    }
});
```
这种方式是基于线程池的，它没有队列的优先级的特点了。

## 异步请求-RequestQueue
```java
RequestQueue queue = NoHttp.newRequestQueue(); // 默认三个并发，此处可以传入并发数量。

...
// 发起请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列释放CPU：
queue.stop();
```

当然你可以自己建立队列：
```java
// 你也可以自己建立队列：
RequestQueue queue = new RequestQueue(5);
queue.start(); // 开始队列。

...
// 发起请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列释放CPU：
queue.stop();
```

很多同学有一个习惯就是每发起一个请求就new一个队列，**这是绝对错误的用法**，例如某同学封装的一个方法：
```java
public <T> void request(Request<T> request, SampleResponseListener<T> listener) {
    RequestQueue queue = NoHttp.newRequestQueue(5);
    queue.add(0, request, listener);
}
```
再次声明一下，**上面的这段用法是错误的**。  

对于不按套路走的同学，我也提供了一个单例模式的用法（不提倡）：  
```java
// 比如请求队列单例模式：
NoHttp.getRequestQueueInstance().add...

...

// 比如下载队列单例模式：
NoHttp.getDownloadQueueInstance().add...
```

### 队列的正确用法
队列正确的用法有两种，一种是每一个页面使用一个队列，在页面退出时调用`queue.stop()`停止队列；另一种是全局使用同一个队列，在App退出时调用`queue.stop()`停止队列。本人比较推荐第二种方法，即全局使用同一个`RequestQueue`。

用法一，我们可以写一个`BaseActivity`，在`onCreate()`方法中建立`RequestQueue`，在`onDestory()`中销毁队列：  
```java
public class BaseActivity extends Activity {

    private RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        queue = NoHttp.newRequestQueue();
    }
    
    // 提供给子类请求使用。
    public <T> void request(int what, Request<T> request, SimpleResponseListener<T> listener) {
        queue.add(what, request, listener);
    }

    @Override
    public void onDestory() {
        queue.stop();
    }

}
```

用法二，使用单例模式封装一个全局专门负责请求的类，使全局仅仅保持一个`RequestQueue`：
```java
StringRequest request = new StringRequest("http://api.nohttp.net", RequestMethod.POST);
CallServer.getInstance().request(0, request, listener);
```

上面的`CallServer`不是`NoHttp`提供的，而是需要开发者自己封装，因为这里可以写自己App的业务，所以这里你可以尽情发挥：
```java
public class CallServer {

    private static CallServer instance;

    public static CallServer getInstance() {
        if (instance == null)
            synchronized (CallServer.class) {
                if (instance == null)
                    instance = new CallServer();
            }
        return instance;
    }

    private RequestQueue queue;

    private CallServer() {
        queue = NoHttp.newRequestQueue(5);
    }

    public <T> void request(int what, Request<T> request, SimpleResponseListener<T> listener) {
        queue.add(what, request, listener);
    }
    
    // 完全退出app时，调用这个方法释放CPU。
    public void stop() {
        queue.stop();
    }
}
```

**注意**：上面的出现的`listener`就是接受结果的回调`interface`，它实际上是`OnResponseListener`，它一种有四个方法需要实现，而有时候实现4个方法显得比较麻烦，所以`NoHttp`提供了一个默认实现类`SimpleResponseListener`，开发者可以仅仅实现自己需要实现的方法。

> 上面在添加Request到队列中时，出现了一个what参数，它相当于我们使用`Handler`时的`Message`的`what`一样，仅仅是用于当一个`OnResponseListener`接受多个Request的请求结果时区分是哪个`Request`的响应结果的。

# 其它特点和用法

## 请求不同数据的几种Request
`NoHttp`请求什么样的数据是由`Request`决定的，`NoHttp`本身已经提供了请求`String`、`Bitmap`、`JSONObject`、`JSONArray`的`Request`：
```java
// 请求String：
StringRequest request = new StringRequest(url, method);

// 请求Bitmap：
ImageRequest request = new ImageRequest(url, method);

// 请求JSONObject：
JsonObjectRequest request = new JsonObjectRequest(url, method);

// 请求JSONArray：
JsonArrayRequest request = new JsonArrayRequest(url, method);
```

## 添加参数
```java
StringRequest request = new StringRequest(url, RequestMethod.POST);
   .add("name", "yoldada") // String类型
   .add("age", 18) // int类型
   .add("sex", '0') // char类型
   .add("time", 16346468473154) // long类型

   // 添加Bitmap
   .add("head", new BitmapBinary(bitmap))
   // 添加File
   .add("head", file)
   .add("head", new FileBinary(file))
   // 添加ByteArray
   .add("head", new ByteArrayBinary(byte[]))
   // 添加InputStream
   .add("head", new InputStreamBinary(inputStream));
```

文件上传是以表单的形式上传：

* 单个文件
```java
StringRequest request = ...
request.add("file", new FileBinary(file));
```

* 多文件，以不同的`key`上传不同的多个文件  
这里可以添加各种形式的文件，`File`、`Bitmap`、`InputStream`、`ByteArray`。

```java
StringRequest request = ...
request.add("file1", new FileBinary(File));
request.add("file2", new FileBinary(File));
request.add("file3", new InputStreamBinary(InputStream));
request.add("file4", new ByteArrayBinary(byte[]));
request.add("file5", new BitmapBinary(Bitmap));
```

* 多文件，以相同的`key`上传相同的多个文件
```java
StringRequest request = ...
fileList.add("image", new FileBinary(File));
fileList.add("image", new InputStreamBinary(InputStream));
fileList.add("image", new ByteArrayBinary(byte[]));
fileList.add("image", new BitmapBinary(Bitmap));
```

或者：  
```java
StringRequest request = ...;

List<Binary> fileList = ...;
fileList.add(new FileBinary(File));
fileList.add(new InputStreamBinary(InputStream));
fileList.add(new ByteArrayBinary(byte[]));
fileList.add(new BitmapStreamBinary(Bitmap));
request.add("file_list", fileList);
```

## 提交请求包体
提交Body分为提交`Json`、提交`String`、提交`Xml`、提交流等，具体用法如下：
```java
// 提交普通String
request.setDefineRequestBody(String, ContentType);

// 提交json字符串
request.setDefineRequestBodyForJson(JsonString)

// 提交jsonObject对象，其实还是json字符串
request.setDefineRequestBodyForJson(JSONObject)

// 提交xml字符串
request.setDefineRequestBodyForXML(XmlString)

// 提交字体Body，比如File（这跟表单上传不一样的），可以转为InputStream来提交
request.setDefineRequestBody(InputStream, ContentType)
```

## 五大缓存模式
`NoHttp`支持缓存到数据库、缓存到SD卡等，并且不论缓存在数据库或者SD，`NoHttp`都把数据进行了加密，需要在初始化的时候配置缓存的位置。

需要注意的是，在6.0以上的手机中如果要缓存在SD卡，需要在请求之前，需要请求运行时权限，如果你不懂运行时权限，可以看这篇文章[Android 6.0 运行时权限管理最佳实践](http://blog.csdn.net/yanzhenjie1003/article/details/52503533)，本人推荐使用这个运行时权限管理框架：[AndPermission](https://github.com/yanzhenjie/AndPermission)。

* 1、`Default`模式，也是没有设置缓存模式时的默认模式
这个模式实现http协议中的内容，比如响应码是304时，当然还会结合E-Tag和LastModify等头。
```java
StringRequest request = new StringRequest(url, method);
request.setCacheMode(CacheMode.DEFAULT);
```

* 2、 当请求服务器失败的时候，读取缓存
请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回。
```java
StringRequest request = new StringRequest(url, method);
request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
```

* 3、如果发现有缓存直接成功，没有缓存才请求服务器
我们知道ImageLoader的核心除了内存优化外，剩下一个就是发现把内地有图片则直接使用，没有则请求服务器。  

请求`String`，缓存`String`：
```java
StringRequest request = new StringRequest(url, method);
// 非标准Http协议，改变缓存模式为IF_NONE_CACHE_REQUEST_NETWORK
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST_NETWORK);
```

请求图片，缓存图片：
```java
ImageRequest request = new ImageRequest(url, method);
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST_NETWORK);
```

* 4、仅仅请求网络
无论如何也只会请求网络，也不支持http 304这种默认行为。
```java
ImageRequest request = new ImageRequest(url, method);
request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
...
```

* 5、仅仅读取缓存
无论如何仅仅读取缓存，不会请求网络和其它操作。
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.ONLY_READ_CACHE);
```

**注意**：如果你想先得到缓存再请求网络，你可以先发起一个仅仅读取缓存的`Request`，然后发起一个仅仅请求网络的`Request`，**不过本人已经在准备NoHttp2.0了，到时候将会以一个全新的面貌和开发者们见面。**

## 文件下载
因为下载文件代码比较多，这里贴关键部分，具体的请参考demo。  

下载也分为下载队列`DownloadQueue`和同步下载执行器`SyncDownloadExecutor`，原理均和上面讲的`RequestQueue`和`RequestQueue`一样，具体请看上方详解。

### 创建队列
```java
DownloadQueue queue = NoHttp.newDownloadQueue(); // 默认三个并发，此处可以传入并发数量。

...
// 发起下载请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列释放CPU：
queue.stop();
```

当然你可以自己建立队列：
```java
// 你也可以自己建立队列：
RequestQueue queue = new RequestQueue(5);
queue.start(); // 开始队列。

...
// 发起下载请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列释放CPU：
queue.stop();
```

### 创建请求
方式一：如果不想指定文件名，根据url或者服务器相应头的`Content-Disposition`自动命名就使用这个：
```java
request = new DefaultDownloadRequest(url, RequestMethod.GET, folder, false);
```
* 参数1是文件url。
* 参数1是请求方法，一般为GET。
* 参数3是文件要保存的文件夹。
* 参数4是若遇到文件已经在指定文件夹存在，是否删除重新下载，true将会删除已存在文件重新下载新文件，false将直接通知开发者文件下载成功。

方式一也支持文件的断点续传下载，**如果遇到不支持，那么一般是服务器没支持，告诉你们服务器开发的同事，在apache、tomcat、ngnix、iis做断点续传支持即可**。

方式二：如果指定文件名，就会使用开发者指定的文件名去命名下载好的文件（**推荐**）：
```java
request = new DefaultDownloadRequest(url, RequestMethod.GET, folder, filename, true, true);
```

- 暂停或者停止下载
```java
downloadRequest.cancel();
```

- 监听下载过程
```java
private DownloadListener downloadListener = new DownloadListener() {
	@Override
	public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
	    // 下载开始
	}

	@Override
	public void onProgress(int what, int progress, long downCount, long speed) {
		// 更新下载进度和下载网速
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

## 取消请求
NoHttp支持取消某个请求、取消指定多个请求、取消所有请求。

* 取消单个请求  
直接调用请求对象的cancel方法。
```java
request.cancel();
```

* 从队列中取消指定的请求
在请求之前给请求set一个sign，取消的时候调用队列的cancelBySign就可以取消掉所有指定这个sign的请求。
```java
request1.setCancelSign(sign);
request2.setCancelSign(sign);
...

// 取消队列中多个用sign标志的请求
queue.cancelBySign(sign);
```

* 取消队列中所有请求
```java
queue.cancelAll();
```

## 自定义请求
NoHttp的所有自带请求都是继承`RestRequest`类，所以我们自定义请求也需要继承`RestRequest`，泛型写自己想要请求的数据类型，最后在`parseResponse()`方法中解析服务器数据成自己自己想要的数据类型即可。
* FastJsonRequest
```java
public class FastJsonRequest extends RestRequestor<JSONObject> {

    public FastJsonRequest(String url) {
	    this(url, RequestMethod.GET);
    }

    public FastJsonRequest(String url, RequestMethod requestMethod) {
	    super(url, requestMethod);
    }

    @Override
    public JSONObject parseResponse(Headers header, byte[] body) throws Throwable {
	    String result = StringRequest.parseResponseString(headers, body);
	    return JSON.parseObject(result);
    }
}
```

* JavaBeanRequest，利用FastJson、Gson等把数据直接转为JavaBean
```java
public class JavaBeanRequest<T> extends RestRequest<T> {
    private Class<T> clazz;

    public JavaBeanRequest(String url, Class<T> clazz) {
        this(url, RequestMethod.GET, clazz);
    }

    public JavaBeanRequest(String url, RequestMethod requestMethod, Class<T> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }

    @Override
    public T parseResponse(Headers header, byte[] body) throws Throwable {
        String response = StringRequest.parseResponseString(header, body);

        // 这里如果数据格式错误，或者解析失败，会在失败的回调方法中返回 ParseError 异常。
        return JSON.parseObject(response, clazz);
    }
}
```

* 使用自定义请求
```java
// 使用FastJson自定义请求
Request<JSONObject> request = new FastJsonRequest(url, requestMethod);
queue.add(what, mRequest, listener);

...

// 直击请求JavaBean
Request<UserInfo> request = new JavaBeanRequest(url, UserInfo.class);
queue.add(what, request, listener);
```

## 代码混淆
NoHttp设计到兼容高版本系统的api采用反射调用，所以所有类都可以被混淆，如果你非要keep的话，如下配置即可。

* 原生NoHttp混淆
```text
-dontwarn com.yanzhenjie.nohttp.**
-keep class com.yanzhenjie.nohttp.**{*;}
```
* 如果使用okhttp的版本
```text
// nohttp
-dontwarn com.yanzhenjie.nohttp.**
-keep class com.yanzhenjie.nohttp.**{*;}

// nohttp-okhttp
-dontwarn com.yanzhenjie.nohttp.**
-keep class com.yanzhenjie.nohttp.**{*;}

// okhttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *;} 
-dontwarn okio.**
-keep class okio.** { *;} 
```

## 关于我
![微信二维码](http://img.blog.csdn.net/20161020083048694)

1. 关注我的[Github](https://github.com/yanzhenjie)，了解我的最新项目。

2. 关注[我的博客](http://blog.yanzhenjie.com)，阅读我的最新文章。
3. 关注[我的微博](http://weibo.com/yanzhenjieit)，有问题随时沟通。

## License
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
