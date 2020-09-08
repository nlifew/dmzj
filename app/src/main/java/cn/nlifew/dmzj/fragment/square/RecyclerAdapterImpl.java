package cn.nlifew.dmzj.fragment.square;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.nlifew.xdmzj.bean.BatchBean;
import cn.nlifew.xdmzj.bean.SquareBean;

final class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    private static final int TYPE_UNKNOWN   = 0;
    private static final int TYPE_BANNER    = 1;
    private static final int TYPE_1x3       = 2;
    private static final int TYPE_2x2       = 3;
    private static final int TYPE_2x3       = 4;


    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
    }

    private final Fragment mFragment;
    private final List<SquareBean> mDataSet = new ArrayList<>(12);

    void updateSquareList(List<SquareBean> list) {
        mDataSet.clear();
        mDataSet.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BANNER;
        }
        final SquareBean obj = mDataSet.get(position);
        switch (obj.data.length) {
            case 3: return TYPE_1x3;
            case 4: return TYPE_2x2;
            case 6: return TYPE_2x3;
        }
        return TYPE_UNKNOWN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BANNER: return new ViewHolder_Banner(mFragment, parent);
            case TYPE_1x3: return new ViewHolder_1x3(mFragment, parent);
            case TYPE_2x2: return new ViewHolder_2x2(mFragment, parent);
            case TYPE_2x3: return new ViewHolder_2x3(mFragment, parent);
        }
        View view = new View(mFragment.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new EmptyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SquareBean obj = mDataSet.get(position);
        switch (getItemViewType(position)) {
            case TYPE_BANNER:
                ((ViewHolder_Banner) holder).onBindViewHolder(obj);
                break;
            case TYPE_1x3:
                ((ViewHolder_1x3) holder).onBindViewHolder(obj);
                break;
            case TYPE_2x2:
                ((ViewHolder_2x2) holder).onBindViewHolder(obj);
                break;
            case TYPE_2x3:
                ((ViewHolder_2x3) holder).onBindViewHolder(obj);
                break;
        }
    }

    private static final class EmptyHolder extends RecyclerView.ViewHolder {
        EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
