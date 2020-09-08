package cn.nlifew.dmzj.fragment.comic;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.dmzj.utils.TimeUtils;
import cn.nlifew.xdmzj.bean.comic.NoticeBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class ViewHolder_Notice extends RecyclerView.ViewHolder {


    ViewHolder_Notice(Fragment fragment, ViewGroup parent) {
        super(LayoutInflater.from(fragment.getContext()).inflate(
                R.layout.fragment_comic_notice,
                parent, false
        ));
        mFragment = fragment;

        mHeadView = itemView.findViewById(R.id.fragment_detail_notice_head);
        mNameView = itemView.findViewById(R.id.fragment_detail_notice_name);
        mTextView = itemView.findViewById(R.id.fragment_detail_notice_text);
        mImagesView = itemView.findViewById(R.id.fragment_detail_notice_images);
        mTimeView = itemView.findViewById(R.id.fragment_detail_notice_time);
    }

    private final Fragment mFragment;

    private ImageView mHeadView;
    private TextView mNameView;
    private TextView mTextView;
    private GridLayout mImagesView;
    private TextView mTimeView;

    void onBindViewHolder(NoticeBean bean) {
        if (bean == null) {
            return;
        }

        mNameView.setText(bean.nickname);
        mTextView.setText(bean.content);
        mTimeView.setText(TimeUtils.formatDate(bean.create_time * 1000));

        mImagesView.removeAllViews();
        if (bean.upload_images != null) {
            String[] images = bean.upload_images.split(",");
            addImageViews(images);
        }

        Glide.get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment)
                .asBitmap()
                .load(NetworkUtils.imageUrl(bean.avatar_url))
                .into(mHeadView);
    }

    private void addImageViews(String[] ss) {
        mImagesView.setColumnCount(Math.min(ss.length, 3));

        Context context = mFragment.getContext();
        int DP5 = DisplayUtils.dp2px(5);
        RequestManager rm = Glide.get(context)
                .getRequestManagerRetriever()
                .get(mFragment);

        for (String s : ss) {
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.leftMargin = lp.topMargin = lp.rightMargin = lp.bottomMargin = DP5;
            lp.setGravity(Gravity.TOP);

            ImageView iv = new ImageView(context);
            mImagesView.addView(iv, lp);

            rm.asBitmap().load(NetworkUtils.imageUrl(
                    "http://images.dmzj.com/commentImg/13/" + s)
            ).into(iv);
        }
    }
}
