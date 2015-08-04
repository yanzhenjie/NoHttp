# NoHttp
开发这个项目的宗旨就是让API使用简单起来

它为开发人员提供了非常简单的API来访问HTTP/HTTPS、异步请求、同步请求、文件上传、自定义请求头、自定义参数，
你不用理睬复杂的HttpURLConnection,所以它的名字是NoHttp。

使用了我的另一个开源项目MultiAsynctask，支持高并发的异步任务框架，在响应方法可以直接更新UI,不用再使用Handler。Github地址：https://github.com/Y0LANDA/MultiAsynctask

目前的功能有：Get、Post、文件上传、文件下载、自定义请求头、自定义参数，响应结果包括:是否成功、响应数据、数据长度、HTTP响应头集合。

API非常样简单，一个Request对象提供所有请求参数，一个Response对象提供了所有响应。

这是一个简单的Demo，有很多用法需要自己去探索:
```
		// 请求一个http连接|Request a HTTP connection
		
		Request request = new Request("http://www.baidu.com", RequestMethod.GET);
		NoHttp.getInstance().requestAsync(request, REQUST_BAIDU, new OnResponseListener() {
			//以下两个方法在主线程执行
			@Override
			public void onNoHttpResponse(int what, Response response) {
				// Response Result|响应结果
				if(what == REQUST_BAIDU) {
					String resStr = response.string();
					byte[] resByte = response.bytes();
				}
			}

			@Override
			public void onNoHttpError(int what, ResponseError responseError) {
				String error = responseError.getErrorInfo();
			}
		});
		
		//发送一个同步请求，建议在子线程使用的时候用同步请求
		Request request = buildRequest();
		BaseResponse baseResponse = noHttp.requestSync(request);
		if (baseResponse.isSuccessful()) {
			Response response = (Response) baseResponse;
			Logger.i("Sync Request Result:" + response);
		} else {
			ResponseError responseError = (ResponseError) baseResponse;
			Logger.i("Sync Request filed:" + responseError.getErrorInfo());
		}
		
		//下载文件
		String url = "http://p.gdown.baidu.com/bd163bef80e2074cdba62af336c33103a781d0491eed87c";
		DownloadRequest downloadRequest = new DownloadRequest(url, RequestMethod.GET);
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
		String filename = "YOLANDA.apk";
		downloadRequest.setDownloadAttribute(dir, filename, false);
		DownloadManager.getInstance(this).download(downloadRequest, 1, this);
		
		——————————————————————————————————————————————————————————————————————
		
		// 从URL获取文件名称，支持静态链接和动态链接
		// From the URL for the file name,Support the static and dynamic links
		
		// String url = "http://ota.53iq.com/static/file/kitchen_14379835129655595.apk";
		String url = "http://cdn3.ops.baidu.com/new-repackonline/appsearch/AndroidPhone/1.0.31.191/1/1012271a/"
				+ "20150723235831/appsearch_AndroidPhone_1-0-31-191_1012271a.apk?response-content-disposition="
				+ "attachment;filename=appsearch_AndroidPhone_1012271a.apk&amp;response-content-type="
				+ "application/vnd.android.package-archive&amp;request_id=1438133342_2816171802&amp;type=static";
		Request request = new Request("http://www.baidu.com", RequestMethod.GET);
		NoHttp.getInstance().requestAsync(request, REQUEST_FILENAME, new OnResponseListener() {
			//以下方法在主线程执行
			@Override
			public void onNoHttpResponse(int what, Response response) {
				// Filename|文件名
				String filename = response.string();
			}

			@Override
			public void onNoHttpError(int what, ResponseError responseError) {
				String error = responseError.getErrorInfo();
			}
		});

```
