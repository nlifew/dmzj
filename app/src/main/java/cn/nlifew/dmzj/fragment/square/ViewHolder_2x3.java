package cn.nlifew.dmzj.fragment.square;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.dmzj.R;
import cn.nlifew.xdmzj.bean.SquareBean;

final class ViewHolder_2x3 extends SimpleViewHolder {
    private static final String TAG = "ViewHolder_2x3";

    ViewHolder_2x3(Fragment fragment, ViewGroup parent) {
        super(fragment, parent);
    }

    @Override
    void onInitGridView(GridLayout view) {
        view.setRowCount(2);
        view.setColumnCount(3);
    }
}
