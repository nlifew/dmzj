package cn.nlifew.dmzj.ui.comment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.nlifew.dmzj.R;
import cn.nlifew.dmzj.utils.ToastUtils;

import static android.app.Activity.RESULT_OK;

class Helper {
    private static final String TAG = "Helper";

    static final class KeyboardPopupListener implements ViewTreeObserver.OnGlobalLayoutListener {

        KeyboardPopupListener(View view) {
            mView = view;
        }

        private final View mView;
        private final Rect mRect = new Rect();
        private int mLastHeight = -1;

        @Override
        public void onGlobalLayout() {
            mView.getWindowVisibleDisplayFrame(mRect);

            int height = mView.getRootView().getHeight() - mRect.bottom;

            Log.d(TAG, "onGlobalLayout: " + height);

            if (mLastHeight == -1) {
                // nothing to do.
            }
            else if (height - mLastHeight > 100) {
                Log.d(TAG, "onGlobalLayout: open");

                mView.setTranslationY(- Math.min(
                        height - mLastHeight, mView.getTop()));
            }
            else if (mLastHeight - height > 100) {
                Log.d(TAG, "onGlobalLayout: close");

                mView.setTranslationY(0);
            }
            mLastHeight = height;
        }
    }

    Helper(CommentActivity activity) {
        mActivity = activity;
    }

    private final CommentActivity mActivity;

    private LinearLayout mImagesView;
    private TextView mImageInfoView;
    private ImageView mImageAddView;
    private EditText mEditView;
    private List<Uri> mImages;
    private int mMaxImages;

    View makeView(CommentActivity.Builder builder) {
        View view = LayoutInflater.from(mActivity).inflate(
                R.layout.activity_comment_2,
                mActivity.findViewById(android.R.id.content),
                false);

        CharSequence text;
        view.setOnClickListener(v -> close());


        TextView titleView = view.findViewById(R.id.activity_comment_close);
        titleView.setOnClickListener(v -> close());
        if ((text = builder.getTitle()) != null) {
            titleView.setText(text);
        }

        TextView submitView = view.findViewById(R.id.activity_comment_btn);
        submitView.setOnClickListener(v -> submit());
        if ((text = builder.getSubmitText()) != null) {
            submitView.setText(text);
        }

        mEditView = view.findViewById(R.id.activity_comment_edit);
        if ((text = builder.getEditHint()) != null) {
            mEditView.setHint(text);
        }

        mImagesView = view.findViewById(R.id.activity_comment_images);
        mImageInfoView = mImagesView.findViewById(R.id.activity_comment_image_info);

        mImageAddView = mImagesView.findViewById(R.id.activity_comment_image_add);
        mImageAddView.setOnClickListener(v -> addImage());

        mMaxImages = builder.getMaxImages(1);

        return view;
    }

    void close() {
        mActivity.setResult(Activity.RESULT_CANCELED);
        mActivity.finish();
    }

    private void submit() {
        CommentActivity.Result result = new CommentActivity.Result()
                .setText(mEditView.getText());

        if (mImages != null) {
            Uri[] uris = new Uri[mImages.size()];
            mImages.toArray(uris);
            result.setImages(uris);
        }

        mActivity.setResult(RESULT_OK, result.build());
        mActivity.finish();
    }


    private static final int CODE_REQUEST_PICK_PICTURE = 10;

    private void addImage() {
        if (mImages != null && mImages.size() >= mMaxImages) {
            ToastUtils.getInstance(mActivity).show("您最多选择" + mMaxImages + "张图片");
            return;
        }

        mActivity.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                (perm, resultCode) -> {
            if (resultCode != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.getInstance(mActivity).show("没有存储权限，无法插入图片");
            }
            else {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.
                        EXTERNAL_CONTENT_URI, "image/*");
                mActivity.startActivityForResult(intent, CODE_REQUEST_PICK_PICTURE);
            }
        });
    }


    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != CODE_REQUEST_PICK_PICTURE) {
            return;
        }
        if (resultCode != RESULT_OK || data == null) {
            ToastUtils.getInstance(mActivity).show("请求失败");
            return;
        }
        addImage(data.getData());
    }

    private void addImage(Uri uri) {
        // 1. 添加到 View 中
        ImageView iv = makeImageView(mActivity);
        setImageViewUri(iv, uri);
        iv.setTag(uri);

        mImagesView.addView(iv, mImagesView.getChildCount() - 2);
        mImageInfoView.setVisibility(View.GONE);

        // 2. 添加到数据集
        if (mImages == null) {
            mImages = new ArrayList<>(mMaxImages);
        }
        mImages.add(uri);
    }

    private void removeImageView(View iv) {
        mImagesView.removeView(iv);
        mImages.remove((Uri) iv.getTag());
        if (mImages.size() == 0) {
            mImageInfoView.setVisibility(View.VISIBLE);
        }
    }

    private ImageView makeImageView(Context context) {
        LinearLayout.LayoutParams oldLP = (LinearLayout.LayoutParams)
                mImageAddView.getLayoutParams();

        ImageView iv = new ImageView(context);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setLayoutParams(new LinearLayout.LayoutParams(oldLP));
        iv.setOnClickListener(this::removeImageView);
        return iv;
    }

    private void setImageViewUri(ImageView iv, Uri uri) {
        iv.setImageURI(uri);

        // todo: 使用 BitmapFactory.Options 优化大图加载
    }
}
