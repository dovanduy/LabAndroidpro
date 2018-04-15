package com.dkzy.areaparty.phone.model_comman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.fragment02.ui.BreakTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Created by Jay on 2015/9/22 0022.
 */
public abstract class MyAdapter<T> extends BaseAdapter {

    private ArrayList<T> mData;
    private int mLayoutRes;           //布局id


    public MyAdapter() {
    }

    public MyAdapter(ArrayList<T> mData, int mLayoutRes) {
        this.mData = mData;
        this.mLayoutRes = mLayoutRes;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, mLayoutRes
                , position);
        bindView(holder, getItem(position));
        return holder.getItemView();
    }

    public abstract void bindView(ViewHolder holder, T obj);

    //添加一个元素
    public void add(T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    //往特定位置，添加一个元素
    public void add(int position, T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    public void remove(T data) {
        if (mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public void turnAppGrey() {
        for (T item : mData) {

        }
    }


    public static class ViewHolder {

        private SparseArray<View> mViews;   //存储ListView 的 item中的View
        private View item;                  //存放convertView
        private int position;               //游标
        private Context context;            //Context上下文

        //构造方法，完成相关初始化
        private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
            mViews = new SparseArray<>();
            this.context = context;
            View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
            item = convertView;
        }

        //绑定ViewHolder与item
        public static ViewHolder bind(Context context, View convertView, ViewGroup parent,
                                      int layoutRes, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, layoutRes);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int id) {
            T t = (T) mViews.get(id);
            if (t == null) {
                t = (T) item.findViewById(id);
                mViews.put(id, t);
            }
            return t;
        }


        /**
         * 获取当前条目
         */
        public View getItemView() {
            return item;
        }

        /**
         * 获取条目位置
         */
        public int getItemPosition() {
            return position;
        }

        /**
         * 设置文字
         */
        public ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof BreakTextView) {
                ((BreakTextView) view).setText(text);
            }else if (view instanceof TextView){
                ((TextView) view).setText(text);
            }
            return this;
        }

        /**
         * 设置图片
         */
        public ViewHolder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }

        public ViewHolder setImage(int id, String url, int default_thumbnail, Context mContext) {
            View view = getView(id);
            RequestBuilder<Bitmap> requestBuilder = Glide.with(mContext).asBitmap();
            requestBuilder.load(url).apply(new RequestOptions().centerCrop().dontAnimate().error(default_thumbnail)).into((ImageView)view);
//            Glide
//                    .with(mContext)
//                    .load(url)
//                    .asBitmap()
//                    .centerCrop()
//                    .dontAnimate()
//                    .placeholder(default_thumbnail)
//                    .into((ImageView)view);
            //Log.w("setImage",url);
            return this;
        }

        public ViewHolder setImageWithUri(int id, Uri uri) {
            ImageView view = getView(id);
            view.setImageURI(uri);
            return this;
        }
        public ViewHolder setImageWithPath(int id, String path, int default_thumbnail,Context mContext) {
            ImageView view = getView(id);
           // Glide.with(mContext).load(Uri.fromFile(new File(path))).apply(new RequestOptions().placeholder(default_thumbnail)).into((ImageView)view);
//            MediaMetadataRetriever media = new MediaMetadataRetriever();
//            media.setDataSource(path);
//            Bitmap bitmap = media.getFrameAtTime();
            view.setImageResource(default_thumbnail);
            return this;
        }



        /**
         * 设置点击监听
         */
        public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }
        public ViewHolder setOnClickListener(View.OnClickListener listener) {
            item.setOnClickListener(listener);
            return this;
        }

        /*设置长按监听*/
        public ViewHolder setOnLongClickListener(View.OnLongClickListener listener) {
            item.setOnLongClickListener(listener);
            return this;
        }
        /*设置背景*/
        public ViewHolder setBackgroundSelect() {
            item.setBackgroundColor(Color.parseColor("#D4D4D4"));
            return this;
        }
        public ViewHolder clearBackgroundSelect() {
            item.setBackgroundColor(Color.parseColor("#F2F2F2"));
            return this;
        }

        /**
         * 设置可见
         */
        public ViewHolder setVisibility(int id, int visible) {
            getView(id).setVisibility(visible);
            return this;
        }

        /**
         * 设置标签
         */
        public ViewHolder setTag(int id, Object obj) {
            getView(id).setTag(obj);
            return this;
        }

        //其他方法可自行扩展

    }

    public void setmData(ArrayList<T> mData) {
        if (mData == null){
            mData = new ArrayList<>();
        }
        this.mData.addAll(mData);
    }
}