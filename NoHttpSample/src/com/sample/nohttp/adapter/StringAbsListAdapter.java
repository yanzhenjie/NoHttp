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
package com.sample.nohttp.adapter;

import com.sample.nohttp.R;
import com.sample.nohttp.util.OnItemClickListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * </br>
 * Created in Jan 28, 2016 5:04:03 PM
 * 
 * @author YOLANDA;
 */
public class StringAbsListAdapter extends BaseAdapter {

	private final Context mContext;

	private final String[] mData;

	private final OnItemClickListener mClickListener;

	private final int itemLayoutId;

	public StringAbsListAdapter(Context context, int itemLayoutId, String[] data, OnItemClickListener clickListener) {
		this.mContext = context;
		this.itemLayoutId = itemLayoutId;
		this.mData = data;
		this.mClickListener = clickListener;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.length;
	}

	@Override
	public String getItem(int position) {
		return mData[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(itemLayoutId, parent, false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.setPosition(position);
		return convertView;
	}

	private class ViewHolder implements View.OnClickListener {
		private TextView mTextView;

		private int position;

		public ViewHolder(View itemView) {
			itemView.setOnClickListener(this);
			mTextView = (TextView) itemView.findViewById(R.id.item_list_activity_start_title);
		}

		public void setPosition(int position) {
			this.position = position;
			mTextView.setText(mData[position]);
		}

		@Override
		public void onClick(View v) {
			mClickListener.onItemClick(v, position);
		}

	}

}
