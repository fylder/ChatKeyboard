package fylder.keyboard.lib.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.List;

import fylder.keyboard.lib.R;
import fylder.keyboard.lib.bean.EmoticonBean;
import fylder.keyboard.lib.utils.imageloader.ImageBase;
import fylder.keyboard.lib.utils.imageloader.ImageLoader;
import fylder.keyboard.lib.view.imp.IView;


public class EmoticonsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;

    private List<EmoticonBean> data;
    private int mItemHeight = 0;
    private int mImgHeight = 0;

    public EmoticonsAdapter(Context context, List<EmoticonBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_emoticon, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight));
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
            viewHolder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            viewHolder.rl_parent = (RelativeLayout) convertView.findViewById(R.id.rl_parent);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImgHeight, mImgHeight);

            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            viewHolder.iv_face.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final EmoticonBean emoticonBean = data.get(position);
        if (emoticonBean != null) {
            // viewHolder.rl_parent.setBackgroundResource(R.drawable.iv_face);

            //暂时不显示
            if (mOnItemListener != null) {
                if (ImageBase.Scheme.ofUri(emoticonBean.getIconUri()) == ImageBase.Scheme.UNKNOWN) {
                    if (mOnItemListener != null) {
                        mOnItemListener.onItemDisplay(emoticonBean);
                    }
                } else {
                    try {
                        ImageLoader.getInstance(mContext).displayImage(emoticonBean.getIconUri(), viewHolder.iv_face);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) {
                        mOnItemListener.onItemClick(emoticonBean);
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_face;
        public RelativeLayout rl_parent;
        public RelativeLayout rl_content;
    }

    public void setHeight(int height, int padding) {
        mItemHeight = height;
        mImgHeight = mItemHeight - padding;
        Log.i("fylderLog", "mItemHeight:" + mItemHeight + "\tmImgHeight:" + mImgHeight);
        notifyDataSetChanged();
    }

    IView mOnItemListener;

    public void setOnItemListener(IView listener) {
        this.mOnItemListener = listener;
    }
}