/*
 * Copyright 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.nohttp.sample.app.form;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.app.form.entity.FileItem;
import com.yanzhenjie.nohttp.sample.util.BaseAdapter;
import com.yanzhenjie.nohttp.sample.util.DisplayUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public class ImageAdapter
  extends BaseAdapter<RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_BUTTON = 2;

    private View.OnClickListener mAddClickListener;
    private List<FileItem> mFileList;

    public ImageAdapter(Context context) {
        super(context);
    }

    public void setFileList(List<FileItem> imageList) {
        mFileList = imageList;
    }

    public void setAddClickListener(View.OnClickListener addClickListener) {
        mAddClickListener = addClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        int imageSize = 0;
        if (mFileList != null) imageSize = mFileList.size();

        if (imageSize < 3) {
            if (position < imageSize) return TYPE_IMAGE;
            else return TYPE_BUTTON;
        } else {
            return TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE: {
                return new ImageViewHolder(getInflater().inflate(R.layout.item_form_image, parent, false));
            }
            default:
            case TYPE_BUTTON: {
                ButtonViewHolder buttonViewHolder =
                  new ButtonViewHolder(getInflater().inflate(R.layout.item_form_button, parent, false));
                buttonViewHolder.mAddClickListener = mAddClickListener;
                return buttonViewHolder;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_BUTTON: {
                break;
            }
            case TYPE_IMAGE: {
                ((ImageViewHolder)holder).bindData(mFileList.get(position));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int itemSize = 0;
        if (mFileList != null) itemSize = mFileList.size();
        if (itemSize < 3) itemSize += 1;
        return itemSize;
    }

    static class ImageViewHolder
      extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_status)
        TextView mTvStatus;
        @BindView(R.id.iv_image)
        ImageView mIvImage;

        private int itemSize = (DisplayUtils.screenWidth - DisplayUtils.dip2px(30)) / 3;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.getLayoutParams().width = itemSize;
            itemView.getLayoutParams().height = itemSize;
        }

        void bindData(FileItem fileItem) {
            Album.getAlbumConfig().getAlbumLoader().load(mIvImage, fileItem.getAlbumFile().getPath());

            int progress = fileItem.getProgress();
            if (progress > 0) {
                if (progress >= 100) {
                    mTvStatus.setText(R.string.form_upload_result);
                } else {
                    mTvStatus.setText(mTvStatus.getResources().getString(R.string.form_progress, progress));
                }
            } else {
                mTvStatus.setText(R.string.form_upload_wait);
            }
        }
    }

    static class ButtonViewHolder
      extends RecyclerView.ViewHolder
      implements View.OnClickListener {

        private View.OnClickListener mAddClickListener;

        private int itemSize = (DisplayUtils.screenWidth - DisplayUtils.dip2px(30)) / 3;

        public ButtonViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.getLayoutParams().width = itemSize;
            itemView.getLayoutParams().height = itemSize;
        }

        @Override
        public void onClick(View v) {
            mAddClickListener.onClick(v);
        }
    }

}