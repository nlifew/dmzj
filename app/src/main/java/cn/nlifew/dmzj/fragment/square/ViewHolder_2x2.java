package cn.nlifew.dmzj.fragment.square;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.fragment.app.Fragment;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.widget.PercentImageView;

final class ViewHolder_2x2 extends SimpleViewHolder {
    private static final String TAG = "ViewHolder_2x2";

    ViewHolder_2x2(Fragment fragment, ViewGroup parent) {
        super(fragment, parent);
    }

    @Override
    void onInitGridView(GridLayout view) {
        view.setColumnCount(2);
        view.setRowCount(2);
    }

    @Override
    View onCreateChildView(GridLayout grid, LayoutInflater inflater) {
        View view = super.onCreateChildView(grid, inflater);

        PercentImageView iv = view.findViewById(R.id.fragment_square_horizontal_item_cover);
        iv.setAlignSite(PercentImageView.FLAG_ALIGN_NULL);

        return view;
    }
}
