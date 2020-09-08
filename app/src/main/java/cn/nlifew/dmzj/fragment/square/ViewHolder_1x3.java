package cn.nlifew.dmzj.fragment.square;

import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.fragment.app.Fragment;

final class ViewHolder_1x3 extends SimpleViewHolder {
    private static final String TAG = "ViewHolder_1x3";

    ViewHolder_1x3(Fragment fragment, ViewGroup parent) {
        super(fragment, parent);
    }

    @Override
    void onInitGridView(GridLayout view) {
        view.setRowCount(1);
        view.setColumnCount(3);
    }
}
