# NoHttp
(**QQ**交流群：**46523908**, E-mail: **smallajax@foxmail.com**)  
这里简单的列出几个使用Demo，具体的请看Demo

**NoHttp特性:**  

* A. 支持HTTP/HTTPS, Https自定义证书, 自动维持Cookie, 异步/同步请求, 大文件/多文件上传, 文件下载, 你不用理睬复杂的Http的API, 所以它的名字是NoHttp.  

* B. NoHttp支持请求队列, 自动为请求排队, 可以取消指定请求, 可以取消队列所有请求  

* C. 支持请求String, Bitmap, JsonObject, 可自定义扩展请求类型  

* D. API使用简单, Request对象包涵参数, 请求头, Cookie, 上传文件等, Response对象包涵响应， 响应头等信息.  

##一. 请求Http接口

**1. 请求String类型数据**
上传文件, 提交普通参数, 添加请求头, 添加Cookie等
```
//创建请求队列，正式项目中应该全局单例
RequestQueue requestQueue = NoHttp.newRequestQueue(SampleApplication.getInstance());
// 初始化需要url, method
Request<String> mRequest = NoHttp.createStringRequest(url, RequstMethod.POST);

// 上传文件
mRequest.add("head", new FileBinary(file, "head.png"));
	
// 添加普通参数
mRequest.add("userName", "yolanda");// String类型

// 添加Cookie
mRequest.addCookie(HttpCookie);
mRequest.addCookie(CookieStore);

// 添加请求头
reqeustQueue.addHead("Author", "user=yolanda");

//添加到请求队列
requestQueue.add(what, mRequest, OnResponseListener<String>);
```
**2. 请求Bitmap类型数据**
```	
Request<Bitmap> mRequest = NoHttp.createImageRequest(url);
requestQueue.add(what, mRequest, OnResponseListener<Bitmap>);
```
**3. 取消一个请求**
```
request.cancel();
```
**4. 从队列中取消指定的请求**
```
requestQueue.cancelBySign(Object);
```
**5. 发送一个同步请求**
```
// 在当前线程发起请求，在线程这么使用
Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
Response<String> response = NoHttp.startRequestSync(context, request);
if (response.isSucceed()) {
	Logger.i("响应消息： " + response.get());
} else {
	Logger.i("错误信息： " + response.getErrorMessage());
}
```
##二. 接受响应
```
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
##三. 自定义请求类型: JsonObject
```
// 使用自定义JsonObject的请求
Request<JsonObject> mRequest = new JsonRequest(url, RequestMethod.GET);
requestQueue.add(what, mRequest, OnResponseListener<JsonObject>);
	
//自定义请求类型：继承RestRequestor<T>后可自定义请求对象
public class JsonRequest extends RestRequestor<JSONObject> {

	public JsonRequest(String url, int requestMethod) {
		super(url, requestMethod);
	}

	public JsonRequest(String url) {
		super(url);
	}

	@Override
	public JSONObject parseResponse(String url, String contentType, byte[] byteArray) {
		String jsonString = null;
		JSONObject jsonObject = null;
		try {
			String charset = HeaderParser.parseHeadValue(contentType, "charset", "");
			jsonString = new String(byteArray, charset);
		} catch (UnsupportedEncodingException e) {
			jsonString = new String(byteArray);
		}
	    try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e1) {
			e1.printStackTrace();
			//这里你可以定义的错误格式的json数据, 例如:
			jsonString = {"code":0, "error":"服务器数据错误"};
			jsonObject = new JsonObject(jsonString);
		}
		return jsonObject;
	}
}
```

##五. 下载文件
```
//下载文件
mDownloadQueue = NoHttp.newDownloadQueue(context);

// what 区分下载
// url 下载地址
// fileFloader 保存的文件夹
// fileName 文件名
// isRange 是否断点续传下载
// DownloadListener 下载状态接受: 开始下载、下载出错，下载进度变化，下载完成
DownloadRequest request = new DownloadRequestor(0, url, dir, fName, true, listener);
mDownloadQueue.add(downloadRequest);
```

**1. 取消或者暂停下载**
```
downloadRequest.cancel();
```
