package cn.nlifew.dmzj.fragment.square;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.xdmzj.bean.SquareBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

abstract class SimpleViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "SimpleViewHolder";

    private static View makeView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_square, parent, false);
    }

    SimpleViewHolder(Fragment fragment, ViewGroup parent) {
        super(makeView(parent));
        mFragment = fragment;

        mTitleView = itemView.findViewById(R.id.fragment_square_title);
        mArrowView = itemView.findViewById(R.id.fragment_square_arrow);
        mArrowView.setOnClickListener(this::onArrowViewClick);

        GridLayout grid = itemView.findViewById(R.id.fragment_square_grid);
        onInitGridView(grid);

        final int n = grid.getRowCount() * grid.getColumnCount();
        mViews = new Holder[n];
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        for (int i = 0; i < n; i++) {
            mViews[i] = new Holder(onCreateChildView(grid, inflater));
        }
    }

    abstract void onInitGridView(GridLayout view);

    View onCreateChildView(GridLayout grid, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_home_vertical_item,
                grid, false);

        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.height = GridLayout.LayoutParams.MATCH_PARENT;
        lp.leftMargin = lp.topMargin = lp.rightMargin = lp.bottomMargin
                = DisplayUtils.dp2px(5);

        grid.addView(view, lp);
        view.setOnClickListener(this::onItemViewClick);

        return view;
    }

    private final Fragment mFragment;
    private final TextView mTitleView;
    private final TextView mArrowView;
    private final Holder[] mViews;

    void onBindViewHolder(SquareBean bean) {
        mTitleView.setText(bean.title);
        mArrowView.setTag(bean.category_id);

        RequestManager rm = Glide
                .get(mFragment.getContext())
                .getRequestManagerRetriever()
                .get(mFragment);

        for (int i = 0; i < mViews.length; i++) {
            Holder holder = mViews[i];
            SquareBean.DataType data = bean.data[i];
            holder.mView.setTag(data);

            holder.mTitleView.setText(data.title);

            if (TextUtils.isEmpty(data.sub_title)) {
                holder.mAboutView.setVisibility(View.GONE);
            }
            else {
                holder.mAboutView.setText(data.sub_title);
                holder.mAboutView.setVisibility(View.VISIBLE);
            }


            rm.asBitmap()
                    .load(NetworkUtils.imageUrl(data.cover))
                    .into(holder.mCoverView);
        }
    }

    private static final class Holder {
        Holder(View view) {
            mView = view;
            mTitleView = view.findViewById(R.id.fragment_square_horizontal_item_title);
            mAboutView = view.findViewById(R.id.fragment_square_horizontal_item_about);
            mCoverView = view.findViewById(R.id.fragment_square_horizontal_item_cover);
        }
        final View mView;
        final TextView mTitleView;
        final ImageView mCoverView;
        final TextView mAboutView;
    }

    private void onArrowViewClick(View view) {
        final int category_id = (int) view.getTag();
    }

    private void onItemViewClick(View view) {
        final SquareBean.DataType bean = (SquareBean.DataType)
                view.getTag();
        Log.d(TAG, "onItemViewClick: [" + bean.type + ", " + bean.obj_id + "]");
    }
}
