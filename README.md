# NoHttp
(**QQ**交流群：**46523908**, E-mail: **smallajax@foxmail.com**)  
列出几个使用sample，具体的请看Demo

**NoHttp特性:**  

* A. 支持HTTP/HTTPS, 自动维持Cookie, 异步/同步请求, 大文件/多文件上传, 文件下载. 

* B. 支持304缓存, 302/303重定向, 支持通过代理服务器访问地址(如: Google)

* C. NoHttp是队列, 自动为请求排队, 可以取消指定请求, 可以取消队列所有请求, 亦可以停止队列.  

* D. 支持请求String, Bitmap, JsonObject, 可自定义扩展请求类型  

* E. API使用简单, Request对象包涵参数, 文件, 请求头等; Response对象包涵响应内容, 响应头等信息.  

##使用Gradle构建时添加依赖:
```groovy
compile 'com.yolanda.nohttp:nohttp:1.0.+'
// or
// compile 'com.yolanda.nohttp:nohttp:1.0.0'
```

##一. 请求

###1. 请求String数据
- **a. 添加参数, 上传文件, 添加请求头等**
```java
// 创建请求队列, 正式项目中建议使用全局单例
RequestQueue queue = NoHttp.newRequestQueue();
// 请求对象
Request<String> request = NoHttp.createStringRequest(url, requestMethod);

//添加请求头
request.addHeader("AppVersioin", "2.0");

// 添加请求参数
request.add("userName", "yolanda");
//添加文件
request.add("file", new FileBinary(file));
// 添加到队列
queue.add(0, request, responseListener);
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
queue.add(what, request, responseListener);
```
###4. 取消请求
- **a. 取消单个请求**
```java
Request<String> request = NoHttp.createStringRequest(url);
...
request.cancel();
```
- **b. 从队列中取消指定的请求**
```java
Request<String> request = NoHttp.createStringRequest(url);
request.setCancelSign(sign);
...
queue.cancelBySign(sign);
```

- **c. 停止队列**
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
if (response.isSucceed()) { // 请求成功

} else {// 请求失败

}
```
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
	    // Bitmap imageHead = response.get(); // 如果是bitmap类型, 都是同样的用法
	}
		
	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message) {
	    // 请求失败或者发生异常
	}
};
```
##三. 自定义请求类型: FastJsonRequest
- **a. 定义请求对象**
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
- **b. 使用自定义请求-和NoHttp默认请求没有区别的哦**
```java
Request<JSONObject> mRequest = new FastJsonRequest(url, requestMethod);
queue.add(what, mRequest, responseListener);
```

##五. 下载文件
- **a. 发起下载请求**
```java
//下载文件
downloadRequest = NoHttp.createDownloadRequest(url, fielDir, fileName, true, false);
// what 区分下载
// downloadRequest 下载请求对象
// downloadListener 下载监听
CallServer.getDownloadInstance().add(0, downloadRequest, downloadListener);
```
- **b. 暂停或者停止下载**
```java
downloadRequest.cancel();
```
- **c. 监听下载过程**
```java
private DownloadListener downloadListener = new DownloadListener() {
	@Override
	public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
	}
		
	@Override
	public void onProgress(int what, int progress, long downCount) {
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
