# NoHttp

QQ技术交流群：[46505645](https://jq.qq.com/?_wv=1027&k=5ImVHCl)

**特别说明**：强烈建议开发者切换到另一个网络框架[Kalle](https://github.com/yanzhenjie/Kalle)，Kalle在架构设计上、Api设计上、功能实现上都更加健壮和完善，文档也比较全面。

Kalle开源地址：[https://github.com/yanzhenjie/Kalle](https://github.com/yanzhenjie/Kalle)  
Kalle文档地址：[http://yanzhenjie.github.io/Kalle](http://yanzhenjie.github.io/Kalle)

**NoHttp依旧正常维护**，正在使用和即将要使用的同学可以放心使用。

## 添加依赖
如果使用HttpURLConnection作为网络层
```groovy
implementation 'com.yanzhenjie.nohttp:nohttp:1.1.11'
```

如果要使用OkHttp作为网络层，请再依赖
```groovy
implementation 'com.yanzhenjie.nohttp:okhttp:1.1.11'
```

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

关于超时，很多人都没有彻底理解或理解有误差，本人在知乎上写过一个答案，请参考：  
[HTTP 在什么情况下会请求超时？](https://www.zhihu.com/question/21609463/answer/160100810)  

下面介绍上方省略的**其它配置**的详情。

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

配置缓存位置为SD卡示例：
```java
InitializationConfig config = InitializationConfig.newBuilder(context)
    .cacheStore(
        new DiskCacheStore(context) // 保存在context.getCahceDir()文件夹中。
        // new DiskCacheStore(path) // 保存在path文件夹中，path是开发者指定的绝对路径。
    )
    .build();
```

添加全局请求头、参数示例：   
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

所以说，如果开发者使用过程中遇到什么问题了，开启调试模式，一切妖魔鬼怪都会现形的。

## 第三方异步框架

`NoHttp`的核心就是同步请求方法，`NoHttp`的异步方法（`AsyncRequestExecutor`、`RequestQueue`都是基于同步请求封装的），所以使用`RxJava`、`AsyncTask`等都可以很好的封装`NoHttp`，一个请求`String`的示例：
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
`NoHttp`的请求模块的核心其实就是同步请求：`SyncRequestExecutor`；`NoHttp`的异步请求分为两个类型，一个是异步请求执行器：`AsyncRequestExecutor`，另一个是请求队列：`RequestQueue`。

## 同步请求
一个请求`String`的示例：
```java
StringRequest req = new String("http://api.nohttp.net"， RequestMethod.POST);
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
Cancelable cancel = AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<String>() {
    @Override
    public void onSucceed(int what, Response<String> response) {
        // 请求成功。
    }

    @Override
    public void onFailed(int what, Response<String> response) {
        // 请求失败。
    }
});

// 如果想取消请求：
cancel.cancel();

// 判断是否取消：
boolean isCancelled = cancel.isCancelled();
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

也可以自己建立队列：
```java
// 也可以自己建立队列：
RequestQueue queue = new RequestQueue(5);
queue.start(); // 开始队列。

...
// 发起请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列：
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

对于想直接调用队列就能请求的开发者，`NoHttp`也提供了一个单例模式的用法：  
```java
// 比如请求队列单例模式：
NoHttp.getRequestQueueInstance().add...

...

// 比如下载队列单例模式：
NoHttp.getDownloadQueueInstance().add...
```

当然开发者可以直接使用上面讲到的异步请求执行器：`AsyncRequestExecutor`，这个是比较推荐的。

### 队列的正确用法
队列正确的用法有两种，一种是每一个页面使用一个队列，在页面退出时调用`queue.stop()`停止队列；另一种是全局使用同一个队列，在App退出时调用`queue.stop()`停止队列。本人比较推荐第二种方法，即全局使用同一个`RequestQueue`。

用法一，开发者可以写一个`BaseActivity`，在`onCreate()`方法中建立`RequestQueue`，在`onDestory()`中销毁队列：  
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

上面的`CallServer`不是`NoHttp`提供的，而是需要开发者自己封装，因为这里可以写自己App的业务，所以这里开发者可以尽情发挥：
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

> 上面在添加Request到队列中时，出现了一个`what`参数，它相当于使用`Handler`时的`Message`的`what`一样，仅仅是用于当一个`OnResponseListener`接受多个Request的请求结果时区分是哪个`Request`的响应结果的。

# 其它特点和用法
下面将会介绍`NoHttp`默认的几种请求，比如`String`、`Bitmap`、`JSONObject`等，一般清情况下，一部分开发者都是直接请求`String`，然后进行解析成`JSON`、`XML`、`JavaBean`等，无论使用任何网络框架，这都不是最好的办法，原因如下：

1. 每一个请求都需要解析`String`成`XML`、`JSON`等，逻辑判断麻烦，代码冗余。
2. 解析过程在主线程进行，数据量过大时解析过程必将耗时，会造成不好的用户体验（App假死）。

所以本人写了一片如何结合业务直接请求`JavaBean`、`List`、`Map`、`Protobuf`的博文：  
[http://blog.csdn.net/yanzhenjie1003/article/details/70158030](http://blog.csdn.net/yanzhenjie1003/article/details/70158030)

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

## 拼装URL
这个能力是在1.1.3开始增加的，也是本次升级的一个亮点，增加拼装URL的方法，比如服务器是RESTFUL风格的API，请求用户信息时可能是这样一个URL：  
```
http://api.nohttp.net/rest/<userid>/userinfo
```
这里的`<userid>`就是用户名或者用户id，需要开发者动态替换，然后获取用户信息。以前是这样做的：
```
String userName = AppConfig.getUserName();

String url = "http://api.nohttp.net/rest/%1$s/userinfo";
url = String.format(Locale.getDefault(), url, userName);

StringRequest request = new StringRequest(url);
...
```
现在可以这样做：
```
String url = "http://api.nohttp.net/rest/";

StringRequest request = new StringRequest(url)
request.path(AppConfig.getUserName())
request.path("userinfo")
...
```

也就是说开发者可以动态拼装URL了。

## 添加请求头
请求头支持添加各种类型，比如`String`、`int`、`long`、`double`、`float`等等。
```java
StringRequest request = new StringRequest(url, RequestMethod.POST);
   .addHeader("name", "yanzhenjie") // String类型。
   .addHeader("age", "18") // int类型。
   .setHeader("sex", "男") // setHeader将会覆盖已经存在的key。
   ...
```

## 添加参数
请求头支持添加各种类型，比如`Binary`、`File`、`String`、`int`、`long`、`double`、`float`等等。
```java
StringRequest request = new StringRequest(url, RequestMethod.POST);
   .add("name", "严振杰") // String类型
   .add("age", 18) // int类型
   .add("age", "20") // add方法不会覆盖已经存在key，所以age将会有两个值：18, 20。
   .set("sex", "女") // set会覆盖已存在的key。
   .set("sex", "男") // 比如最终sex就只有一个值：男。

    // 添加File
   .add("head", file)
   .add("head", new FileBinary(file))
   // 添加Bitmap
   .add("head", new BitmapBinary(bitmap))
   // 添加ByteArray
   .add("head", new ByteArrayBinary(byte[]))
   // 添加InputStream
   .add("head", new InputStreamBinary(inputStream));
```

另外需要说明原来的`Request#add(Map<String, String>)`更新为`Request#add(Map<String, Object>)`，这样做的好处是喜欢使用`Map`封装参数的同学，可以在`Map`中添加以下几种类型的参数了：  
```java
String、File、Binary、List<String>、List<Binary>、List<File>、List<Object>
```

代码举例说明：
```java
Map<String, Object> params = new HashMap<>();

params.put("name", "yanzhenjie");
params.put("head", new File(path));
params.put("logo", new FileBinary(file));
params.put("age", 18);
params.put("height", 180.5);

List<String> hobbies = new ArrayList<>();
hobbies.add("篮球");
hobbies.add("帅哥");
params.put("hobbies", hobbies);

List<File> goods = new ArrayList<>();
goods.add(file1);
goods.add(file2);
params.put("goods", goods);

List<Object> otherParams = new ArrayList<>();
otherParams.add("yanzhenjie");
otherParams.add(1);
otherParams.add(file);
otherParams.add(new FileBinary(file));

params.put("other", otherParams);
```

当然，真实开发中第三种和文件一起使用同一个`key`请求，几乎不会存在，但是难免会`String`、`int`等使用同一个`key`请求。

文件上传有两种形式，第一种：以表单的形式上传，第二种：以`request body`的形式上传，下面先介绍第一种表单的形式：

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

第二种`request body`的形式是多种多样的，同时不仅可以提交文件，也可以提交任何流的数据，详情看下面**提交请求包体**的内容。  

## 提交请求包体
提交Body分为提交`Json`、提交`String`、提交`Xml`、提交流等，其实最终都是转成流提交的，所以开发者可以用这种方式提交文件。

具体用法如下：  
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

举一个提交文件的例子：
```java
File file = ...;
FileInputStream fileStream = new FileInputStream(file);

StringRequest request = new StringRequest(url, RequestMethod.POST);
request.setDefineRequestBody(fileStream, Headers.HEAD_VALUE_CONTENT_TYPE_OCTET_STREAM);
```

## 五大缓存模式
`NoHttp`支持缓存到数据库、缓存到SD卡等，并且不论缓存在数据库或者SD，`NoHttp`都把数据进行了加密，需要在初始化的时候配置缓存的位置。

需要注意的是，在6.0以上的手机中如果要缓存在SD卡，需要在请求之前，需要请求运行时权限，如果开发者不懂运行时权限，可以看这篇文章[Android 6.0 运行时权限管理最佳实践](http://blog.csdn.net/yanzhenjie1003/article/details/52503533)，本人推荐使用这个运行时权限管理框架：[AndPermission](https://github.com/yanzhenjie/AndPermission)。

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
ImageLoader的核心除了内存优化外，剩下一个就是发现把内地有图片则直接使用，没有则请求服务器。  

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

**注意**：如果开发者想先得到缓存再请求网络，开发者可以先发起一个仅仅读取缓存的`Request`，然后发起一个仅仅请求网络的`Request`，**不过本人已经在准备NoHttp2.0了，到时候将会以一个全新的面貌和开发者们见面。**

缓存模式支持缓存任何数据，因为`NoHttp`保存数据是转为`byte[]`，读取数据时是把`byte[]`转为开发者想要的数据，因此`NoHttp`的缓存可以支持任何自定义的`Request`。

## 自定义请求
`NoHttp`的所有自带请求都是继承`RestRequest`类，所以自定义请求也需要继承`RestRequest`，泛型写自己想要请求的数据类型，最后在`parseResponse()`方法中解析服务器数据成自己自己想要的数据类型即可。

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
这只是一个自定义请求的演示，比如开发者还可以结合业务封装`Request`，可以直接请求到业务的`JavaBean`、`List`等复杂数据，具体请参考这篇博文：  
[http://blog.csdn.net/yanzhenjie1003/article/details/70158030](http://blog.csdn.net/yanzhenjie1003/article/details/70158030)

# 文件下载
因为下载文件代码比较多，这里贴关键部分，具体的请参考demo。  

`NoHttp`的下载模块的核心也是同步请求：`SyncDownloadExecutor`；`NoHttp`的异步异步下载只有下载队列一种方式：`DownloadQueue`，当然也可以使用`SyncDownloadExecutor`结合`RxJava`、`AsyncTask`封装其它形式的异步下载。

## 同步下载-SyncDownloadExecutor
```java
DownloadRequest request = new DownloadRequest(url, RequestMethod.GET, fileFolder, true, true);
    SyncDownloadExecutor.INSTANCE.execute(0, request, new SimpleDownloadListener() {
        @Override
        public void onStart(int what, boolean resume, long range, Headers headers, long size) {
            // 开始下载，回调的时候说明文件开始下载了。
            // 参数1：what。
            // 参数2：是否是断点续传，从中间开始下载的。
            // 参数3：如果是断点续传，这个参数非0，表示之前已经下载的文件大小。
            // 参数4：服务器响应头。
            // 参数5：文件总大小，可能为0，因为服务器可能不返回文件大小。
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            // 进度发生变化，服务器不返回文件总大小时不回调，因为没法计算进度。
            // 参数1：what。
            // 参数2：进度，[0-100]。
            // 参数3：文件总大小，可能为0，因为服务器可能不返回文件大小。
            // 参数4：下载的速度，含义为1S下载的byte大小，计算下载速度时：
            //        int xKB = (int) speed / 1024; // 单位：xKB/S
            //        int xM = (int) speed / 1024 / 1024; // 单位：xM/S
        }

        @Override
        public void onFinish(int what, String filePath) {
            // 下载完成，参数2为保存在本地的文件路径。
        }
});
```

必须要介绍一下`DownloadListener`，它是文件下载状态的监听器，`NoHttp`提供了一个默认实现，就是上面看到的`SimpleDownloadListener`，`DownloadListener`的完成实现如下：
```java
private DownloadListener downloadListener = new DownloadListener() {
	@Override
	public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
	    // 下载开始。
	}

	@Override
	public void onProgress(int what, int progress, long downCount, long speed) {
		// 更新下载进度和下载网速。
	}

 	@Override
	public void onFinish(int what, String filePath) {
	    // 下载完成。
 	}

	@Override
	public void onDownloadError(int what, StatusCode code, CharSequence message) {
	    // 下载发生错误。
	    // 参数2：错误类型，是枚举值，每一个枚举的具体请看javaDoc或者demo。
	    // 参数三：错误信息。
	}

	@Override
	public void onCancel(int what) {
	    // 下载被取消或者暂停。
	}
};
```

## 异步下载-DownloadQueue
```java
DownloadQueue queue = NoHttp.newDownloadQueue(); // 默认三个并发，此处可以传入并发数量。

...
// 发起下载请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列释放CPU：
queue.stop();
```

当然开发者可以自己建立队列：
```java
// 也可以自己建立队列：
RequestQueue queue = new RequestQueue(5);
queue.start(); // 开始队列。

...
// 发起下载请求：
queue.add(what, request, listener);

...
// 使用完后需要关闭队列释放CPU：
queue.stop();
```

其它的使用方法和封装和上面的`RequestQueue`相同，请参考上面`RequestQueue`用法。

## 创建请求
`NoHttp`提供了两种构造下载请求的方法，第一种：手动指定下载文件名；第二种：由`NoHttp`根据服务器响应头、URL等自动确定文件名。

### 方式一：指定文件名
如果指定文件名，就会使用开发者指定的文件名去命名下载的文件（**推荐**）：
```java
DownloadRequest req = new DownloadRequest(url, method, folder, filename, range, deleteOld);
// 参数1，文件的url。
// 参数2，请求方法，一般为GET。
// 参数3，要保存的文件名路径，须是绝对路径。
// 参数4，文件最终的文件名，最终会用这个文件命名下载好的文件。
// 参数5，是否断点续传，比如之前已经下载了50%，是否继续从50%处开始下载，否则从0开始下载。
// 参数6，下载前检测到已存在你指定的相同文件名的文件时，是否删除重新下载，否则直接回调下载成功。
```

示例：
```java
String url = "http://...";
String folder = ...;
String filename = "xx.apk";
DownloadRequest req = new DownloadRequest(url, RequestMethod.GET, folder, filename, true, true);
```

### 方式一：不指定文件名
`NoHttp`会根据`url`或者服务器响应头的`Content-Disposition`自动命名文件：
```java
DownloadRequest req = new DownloadRequest(url, method, folder, range, deleteOld);
// 参数含义同上。
```

**注意**：两种方式都是支持断点续传的。如果开发者设置了使用断点续传，但是文件服务器不支持，那么`NoHttp`会先尝试以断点的请求一次，如果请求失败，则再以普通下载的方式请求下载。

## 暂停、继续、取消下载
特别注意：`Http`下载其实没有暂停下载一说，其本质就是取消下载，继续下载其实利用的就是上面说的断点续传技术，断点续传需要服务器支持，一般`tomcat`、`apache`、`nginx`、`iis`都是支持的。

### Nohttp暂停下载继续下载原理介绍
`NoHttp`的demo中演示了暂停下载，继续下载等功能，其实就是下载到中途，暂停下载时调用取消下载，然后继续下载时重新建一个`DownloadRequest`并且使用断点续传下载，此时服务器就会从客户端上次取消下载时客户端已经接受的byte数处开始写出文件，客户端也从上次已经接受的byte数处开始接受并写入文件。

示例：
```java
DownloadRequest request;
String url = "http://...";

// 开始或者继续一个下载。
public void startDownload() {
    if(request != null)
        request = new DownloadRequest(url, RequestMethod.GET, "/sdcard/", "xx.apk", true, true);
    // 注意第5个参数，true表示断点续传。
}

// 暂停或者取消一个下载。
public void stopDownload() {
    if(downloadRequest != null)
        downloadRequest.cancel();
}
```

更多的使用请参考sample。

## 代码混淆
如果你没有使用Https，NoHttp可以随意混淆，如果使用了Https，请添加如下混淆规则：
```
-keepclassmembers class ** {
    private javax.net.ssl.SSLSocketFactory delegate;
}
```

## 关于我
:smile:关注一下我的微信公众号支持我一波  
![微信二维码](http://img.blog.csdn.net/20161020083048694)  

## License
```text
Copyright 2015 Yan Zhenjie

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