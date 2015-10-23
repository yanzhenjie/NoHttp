# NoHttp

这里简单的列出几个使用Demo，具体的请看Demo

##NoHttp特性：

* NoHttp支持HTTP/HTTPS, Https证书自定义, 异步请求, 同步请求, 大文件, 多文件上传, 文件下载, 添加请求头, 添加请求参数, 你不用理睬复杂的Http的API, 所以它的名字是NoHttp.

* 项目中使用请求队列, 自动为请求排队, 可以取消所有请求, 可以取消指定请求, 可以取消当前请求

* 支持请求String, Bitmap, JsonObject, 自定义请求等, 使用灵活.

* API非常样简单, 一个Request对象提供所有请求参数, 一个Response对象提供了所有响应.

##请求String类型数据

上传文件, 提交普通参数, 添加请求头, 添加COokie等

```
RequestQueue requestQueue = NoHttp.newRequestQueue(SampleApplication.getInstance());
// 初始化需要url, method
Request<String> mRequest = NoHttp.createStringRequest(url, RequstMethod.POST);

// 上传文件
mRequest.add("head", new FileBinary(file, "head.png"));
mRequest.add("head_small", new FileBinary(new File(filePath), "head_small.png"));
	
// 添加普通参数
mRequest.add("userName", "yolanda");// String类型
mRequest.add("userAge", 20);// int类型
mRequest.add("userSex", '1');// char类型
	
// 添加请求头
reqeustQueue.addHead("Author", "user=yolanda");

//添加到请求队列
requestQueue.add(what, mRequest, OnResponseListener<String>);
```

##请求图片类型数据
```	
Request<Bitmap> mRequest = NoHttp.createStringRequest(url, RequstMethod.GET);
requestQueue.add(what, mRequest, OnResponseListener<Bitmap>);
```

##取消一个请求
```
request.cancel();	
```

**从队列中取消指定的请求**

```
requestQueue.cancelBySign(Object);
```

##自定义请求类型: JsonObject
```
// 使用自定义JsonObject的请求
Request<JsonObject> mRequest = new JsonRequest(url, RequestMethod.GET);
mReqeust.add("count", 3);
	
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
		if (byteArray != null && byteArray.length > 0) {
			try {
				String charset = HeaderParser.parseHeadValue(contentType, "charset", "");
				jsonString = new String(byteArray, charset);
			} catch (UnsupportedEncodingException e) {
				Logger.w("Charset error in ContentType returned by the server：" + contentType);
				jsonString = new String(byteArray);
			}
		}
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return jsonObject;
	}
}
```

**发送一个同步请求**
```
// 这里直接发起一个同步请求，建议在子线程这么使用
Request<String> request = NoHttp.createStringRequest("http://www.baidu.com", RequestMethod.POST);
Response<String> response = NoHttp.startRequestSync(getApplicationContext(), request);
if (response.isSucceed()) {
	Logger.i("响应消息： " + response.get());
} else {
	Logger.i("错误信息： " + response.getErrorMessage());
}
```

**下载文件**
```
//下载文件
mDownloadQueue = NoHttp.newDownloadQueue(getApplicationContext());

// what 区分下载
// url 下载地址
// fileFloader 保存的文件夹
// fileName 文件名
// isRange 是否断点续传下载
// DownloadListener 下载状态接受: 开始下载、下载出错，下载进度变化，下载完成
DownloadRequest downloadRequest = new DownloadRequestor(0, url, fileFloder, filename, true, DownloadListener);
mDownloadQueue.add(downloadRequest);

//取消或者暂停下载
downloadRequest.cancel();
```
