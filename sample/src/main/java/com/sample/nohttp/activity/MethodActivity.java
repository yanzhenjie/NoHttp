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
package com.sample.nohttp.activity;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.adapter.StringAbsListAdapter;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.sample.nohttp.util.OnItemClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

/**
 * <p>演示各种请求方法Demo.<p>
 * Created in Oct 23, 2015 1:13:06 PM.
 *
 * @author YOLANDA;
 */
public class MethodActivity extends BaseActivity implements HttpListener<String> {

    /**
     * 请求对象.
     */
    private Request<String> mRequest;
    /**
     * 显示请求结果.
     */
    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[2]);
        setContentView(R.layout.activity_method);

        mTvResult = findView(R.id.tv_method_result);
        String[] contentStrings = getResources().getStringArray(R.array.activity_method_item);
        StringAbsListAdapter listAdapter = new StringAbsListAdapter(this, R.layout.item_abs_grid_text, contentStrings, mItemClickListener);
        ((GridView) findView(R.id.gv)).setAdapter(listAdapter);
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            request(position);
        }
    };

    private void request(int position) {
        RequestMethod method = RequestMethod.GET;// 默认get请求
        switch (position) {
            case 0:
                break;
            case 1:
                method = RequestMethod.POST;
                break;
            case 2:
                method = RequestMethod.PUT;
                break;
            case 3:
                method = RequestMethod.HEAD;
                break;
            case 4:
                method = RequestMethod.DELETE;
                break;
            case 5:
                method = RequestMethod.OPTIONS;
                break;
            case 6:
                method = RequestMethod.TRACE;
                break;
            case 7:
                method = RequestMethod.PATCH;
                break;
            default:
                break;
        }
        mRequest = NoHttp.createStringRequest(Constants.URL_NOHTTP_METHOD, method);

        mRequest.add("userName", "yolanda");// String类型
        mRequest.add("userPass", "yolanda.pass");
        mRequest.add("userAge", 20);// int类型
        mRequest.add("userSex", '1');// char类型，还支持其它类型

        // 添加到请求队列
        CallServer.getRequestInstance().add(this, 0, mRequest, this, true, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        mTvResult.setText("请求成功：\n" + response.get());
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        mTvResult.setText("请求失败：\n" + exception.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时可以取消这个请求
        if (mRequest != null)
            mRequest.cancel(true);
    }

}