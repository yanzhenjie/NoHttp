#NoHttp
技术交流1群：46523908  
技术交流2群：46505645  
E-mail：nohttp@foxmail.com    
NoHttp 教程博客：[http://blog.csdn.net/yanzhenjie1003][1]  
Android直播地址：[http://www.huya.com/yolanda][2]
直播视频下载地址：[http://pan.baidu.com/s/1bnM0CxD][3]

NoHttp是专门做Android网络请求与下载的框架。

----
##NoHttp特性:
> * 支持HTTP/HTTPS，自动维持Cookie，异步/同步请求，大文件/多文件上传，文件下载，上传下载均有进度。
> * 支持304缓存，自定义缓存，302/303重定向，支持代理服务器访问地址(如: Google)。
> * NoHttp是队列，自动为请求排队，可以取消指定请求, 可以取消队列所有请求，亦可以停止队列。
> * 支持请求String、Bitmap、Json、JavaBean，可自定义扩展请求类型。
> * Request对象包涵参数、文件、请求头等；Response对象包涵响应内容，响应头等信息，Cookie。
  
##使用Gradle构建时添加依赖：
```groovy
// 引用最新版
compile 'com.yolanda.nohttp:nohttp:+'
// 或则引用指定版本
compile 'com.yolanda.nohttp:nohttp:1.0.0'
```

##一. 请求
###1. 请求String数据
```java
// 请求对象
Request<String> request = NoHttp.createStringRequest(url, requestMethod);
//添加请求头
request.addHeader("AppVersioin", "2.0");
// 添加请求参数
request.add("userName", "yolanda");
//上传文件
request.add("file", new FileBinary(file));
...
```

###2. 请求Json数据
```java
// JsonObject
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, reqeustMethod);
queue.add(what, request, responseListener);
...
// JsonArray
Request<JSONArray> request = NoHttp.createJsonArrayRequest(url, reqeustMethod);
queue.add(what, request, responseListener);
```

###3. 请求Bitmap数据
```	java
Request<Bitmap> request = NoHttp.createImageRequest(url, requestMethod);
...
```

###4. 取消请求
####取消单个请求
```java
Request<String> request = NoHttp.createStringRequest(url);
...
request.cancel();
```

####从队列中取消指定的请求
```java
Request<String> request = NoHttp.createStringRequest(url);
request.setCancelSign(sign);
...
queue.cancelBySign(sign);
```

####取消队列中所有请求
```java
queue.cancelAll();
```

####停止队列
```java
RequestQueue queue = NoHttp.newRequestQueue();
...
queue.stop();
```

###5. 同步请求
```java
// 在当前线程发起请求，在线程这么使用
Request<String> request = NoHttp.createStringRequest(url);
Response<String> response = NoHttp.startRequestSync(request);
if (response.isSucceed()) {
    // 请求成功
} else {
    // 请求失败
}
```

##二. 缓存
###1. Http标准协议的缓存，比如响应码是304时
　　现在很多公司使用了RESTFUL风格来写Http API，所以这个是必须有的。
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
// NoHttp本身是RESTFUL风格的标准Http协议，所以这里不用设置或者设置为DEFAULT
request.setCacheMode(CacheMode.DEFAULT);
...
```

###2. 当请求服务器失败的时候，读取缓存
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
// 非标准Http协议，改变缓存模式为REQUEST_FAILED_READ_CACHE
request.setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE);
...
```

###3. 如果发现有缓存直接成功，没有缓存才请求服务器
　　我们知道ImageLoader的核心除了内存优化外，剩下一个就是发现把内地有图片则直接使用，没有则请求服务器，所以NoHttp这一点非常使用做一个ImageLoader。
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
// 非标准Http协议，改变缓存模式为IF_NONE_CACHE_REQUEST
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST);
...
```
　　请求图片，缓存图片。
```java
// 如果没有缓存才去请求服务器，否则使用缓存，缓存图片演示
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST);
...
```

###3. 仅仅读取缓存
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
// 非标准Http协议，改变缓存模式为ONLY_READ_CACHE
request.setCacheMode(CacheMode.ONLY_READ_CACHE);
...
```

　　**注意：缓存不管是String、Json、图片还是任何请求都可以被NoHttp缓存**

##二. 响应
```java
OnResponseListener<String> responseListener = new OnResponseListener<String>() {
    @Override
	public void onStart(int what) {
	    // 请求开始时，可以显示一个Dialog
	}
	
	@Override
	public void onFinish(int what) {
	    // 请求接受时，关闭Dialog
	}
	
	@Override
	public void onSucceed(int what, Response<String> response) {
	    // 接受请求结果
	    String result = response.get();
	    // Bitmap imageHead = response.get(); // 如果是bitmap类型，都是同样的用法
	}
		
	@Override
    public void onFailed(int what, String url, Object tag, Exception exception, ...) {
	    // 请求失败或者发生异常
	    // 这里根据exception处理不同的错误，比如超时、网络不好等
	}
};
```

##三. 自定义请求类型: FastJsonRequest
### 定义请求对象
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
	// 告诉服务器你接受什么类型的数据, 会添加到请求头的Accept中
	return "application/json;q=1";
}

}
```

###b. 使用自定义请求-和NoHttp默认请求没有区别的哦
```java
Request<JSONObject> mRequest = new FastJsonRequest(url, requestMethod);
queue.add(what, mRequest, responseListener);
```

##五. 下载文件
###发起下载请求
```java
//下载文件
downloadRequest = NoHttp.createDownloadRequest(url, fielDir, fileName, true, false);
// what 区分下载
// downloadRequest 下载请求对象
// downloadListener 下载监听
CallServer.getDownloadInstance().add(0, downloadRequest, downloadListener);
```

###暂停或者停止下载
```java
downloadRequest.cancel();
```

###监听下载过程
```java
private DownloadListener downloadListener = new DownloadListener() {
	@Override
	public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
	}
		
	@Override
	public void onProgress(int what, int progress, long downCount) {
		// 更新下载进度
	}
		
 	@Override
	public void onFinish(int what, String filePath) {
 	}
		
	@Override
	public void onDownloadError(int what, StatusCode code, CharSequence message) {
	}
		
	@Override
	public void onCancel(int what) {
	}
};
```

[1]: http://blog.csdn.net/yanzhenjie1003
[2]: http://www.huya.com/yolanda
[3]: http://pan.baidu.com/s/1bnM0CxD
