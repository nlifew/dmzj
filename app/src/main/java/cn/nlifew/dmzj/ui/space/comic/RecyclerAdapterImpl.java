package cn.nlifew.dmzj.ui.space.comic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.utils.DisplayUtils;
import cn.nlifew.xdmzj.bean.space.SpaceBean;
import cn.nlifew.xdmzj.utils.NetworkUtils;

final class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";


    RecyclerAdapterImpl(Fragment fragment) {
        mFragment = fragment;
    }

    private final Fragment mFragment;
    private SpaceBean.ComicType[] mCommicList;

    void updateComicList(SpaceBean.ComicType[] list) {
        mCommicList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCommicList == null ? 0 : mCommicList.length;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getContext())
                .inflate(R.layout.fragment_space_comic_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Holder) holder).onBindViewHolder(mCommicList[position]);
    }


    private final class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleView = itemView.findViewById(R.id.fragment_space_comic_item_title);
            mAuthorView = itemView.findViewById(R.id.fragment_space_comic_item_author);
            mCoverView = itemView.findViewById(R.id.fragment_space_comic_item_cover);
            mReadingButton = itemView.findViewById(R.id.fragment_space_comic_item_reading);

            mReadingButton.setOnClickListener(this);
        }

        private final TextView mTitleView;
        private final TextView mAuthorView;
        private final ImageView mCoverView;
        private final Button mReadingButton;

        void onBindViewHolder(SpaceBean.ComicType comic) {
            itemView.setTag(comic);

            mTitleView.setText(comic.name);
            mAuthorView.setText(comic.status);

            Glide.get(mFragment.getContext())
                    .getRequestManagerRetriever()
                    .get(mFragment)
                    .asBitmap()
                    .load(NetworkUtils.imageUrl(comic.cover))
                    .into(mCoverView);
        }

        @Override
        public void onClick(View v) {
            SpaceBean.ComicType comic = (SpaceBean.ComicType) itemView.getTag();
            if (v == itemView) {
                catComicDetail(comic);
            }
            else if (v == mReadingButton) {
                readingComic(comic);
            }
        }

        private void catComicDetail(SpaceBean.ComicType comic) {

        }

        private void readingComic(SpaceBean.ComicType comic) {

        }
    }
}
