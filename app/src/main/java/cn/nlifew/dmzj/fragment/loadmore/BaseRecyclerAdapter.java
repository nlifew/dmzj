package cn.nlifew.dmzj.fragment.loadmore;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter {
    private static final String TAG = "BaseRecyclerAdapter";


    public BaseRecyclerAdapter(Fragment fragment) {
        mFragment = fragment;
    }

    protected final Fragment mFragment;
    protected final List<Object> mDataSet = new ArrayList<>(64);

    public void refreshDataSet(Object dataSet) {
        if (dataSet instanceof Object[]) {
            Object[] o = (Object[]) dataSet;
            mDataSet.clear();
            mDataSet.addAll(Arrays.asList(o));
            notifyDataSetChanged();
        }
    }

    public void appendDataSet(Object dataSet) {
        if (dataSet instanceof Object[]) {
            Object[] o = (Object[]) dataSet;

            int old = getItemCount();
            mDataSet.addAll(Arrays.asList(o));
            notifyItemRangeInserted(old, o.length);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
