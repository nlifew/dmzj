package cn.nlifew.dmzj.fragment.comic;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.ui.chapter.ChapterActivity;
import cn.nlifew.dmzj.ui.space.SpaceActivity;
import cn.nlifew.xdmzj.bean.comic.ComiclBean;

import static cn.nlifew.dmzj.fragment.comic.Helper.toAuthor;
import static cn.nlifew.dmzj.fragment.comic.Helper.toCategory;
import static cn.nlifew.dmzj.fragment.comic.Helper.toSpanText;

final class ViewHolder_Header extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ViewHolder_Header";

    ViewHolder_Header(Fragment fragment, ViewGroup parent) {
        super(LayoutInflater.from(fragment.getContext()).inflate(
                R.layout.fragment_comic_header, parent, false
        ));
        mFragment = fragment;
        onViewCreated(itemView);
    }

    private final Fragment mFragment;

    private TextView mHitNumView;
    private TextView mHotNumView;
    private TextView mSubscribeNumView;
    private TextView mSummaryView;
    private TextView mCategoryView;
    private TextView mAuthorView;

    private ComiclBean mData;

    private void onViewCreated(View view) {
        mHitNumView = view.findViewById(R.id.fragment_detail_header_hit_num);
        mHotNumView = view.findViewById(R.id.fragment_detail_header_hot_num);
        mSubscribeNumView = view.findViewById(R.id.fragment_detail_header_subscribe_num);

        mSummaryView = view.findViewById(R.id.fragment_detail_header_summary);
        mCategoryView = view.findViewById(R.id.fragment_detail_header_category);
        mAuthorView = view.findViewById(R.id.fragment_detail_header_author);

        mCategoryView.setOnClickListener(this);

        TextView more = view.findViewById(R.id.fragment_detail_header_author_more);
        more.setOnClickListener(this);
    }

    void onBindViewHolder(ComiclBean bean) {
        if ((mData = bean) == null) {
            return;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        mHitNumView.setText(toSpanText(ssb, bean.hit_num, "点击量"));
        mHotNumView.setText(toSpanText(ssb, bean.hot_num, "热度"));
        mSubscribeNumView.setText(toSpanText(ssb, bean.subscribe_num, "订阅数"));

        mSummaryView.setText(bean.description);
        mCategoryView.setText(toCategory(bean));
        mAuthorView.setText(toAuthor(bean));
    }

    @Override
    public void onClick(View v) {
        if (mData == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.fragment_detail_header_category:
                onCategoryViewClick(mData);
                break;
            case R.id.fragment_detail_header_author_more:
                onAuthorViewClick(mData);
                break;
        }
    }


    private void onCategoryViewClick(ComiclBean bean) {
        ArrayList<ComiclBean.ChapterType> list = new ArrayList<>(
                Arrays.asList(bean.chapters));

        Intent intent = new Intent(mFragment.getContext(), ChapterActivity.class);
        intent.putExtra(ChapterActivity.EXTRA_BOOK_ID, bean.id);
        intent.putParcelableArrayListExtra(ChapterActivity.EXTRA_CHAPTERS, list);
        mFragment.startActivity(intent);
    }


    private void onAuthorViewClick(ComiclBean bean) {
        if (bean.authors.length == 1) {
            onAuthorViewClick(bean.authors[0]);
            return;
        }
        CharSequence[] choices = new CharSequence[bean.authors.length];
        for (int i = 0; i < choices.length; i++) {
            choices[i] = bean.authors[i].tag_name;
        }

        DialogInterface.OnClickListener cli = (dialog, which) -> {
            dialog.dismiss();
            onAuthorViewClick(bean.authors[which]);
        };

        new AlertDialog.Builder(mFragment.getContext())
                .setTitle("作者列表")
                .setSingleChoiceItems(choices, 0, cli)
                .show();
    }

    private void onAuthorViewClick(ComiclBean.AuthorType author) {
        String uri = (mData.uid == 0 ? "dmzj://author?id=" : "dmzj://user?id=")
                + author.tag_id;
        Intent intent = new Intent(mFragment.getContext(), SpaceActivity.class);
        intent.setData(Uri.parse(uri));
        mFragment.startActivity(intent);
    }
}
