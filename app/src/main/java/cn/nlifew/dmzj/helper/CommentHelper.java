package cn.nlifew.dmzj.helper;

import android.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.net.IDN;
import java.util.Map;

import cn.nlifew.xdmzj.bean.SimpleBean;
import cn.nlifew.xdmzj.bean.space.CommentBean;
import cn.nlifew.xdmzj.database.CommonDatabase;
import cn.nlifew.xdmzj.entity.Account;
import cn.nlifew.xdmzj.entity.Comment;
import cn.nlifew.xdmzj.request.IComic;
import cn.nlifew.xdmzj.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class CommentHelper {
    private static final String TAG = "CommentHelper";

    public interface Callback {
        int ERRNO_OK            = 0;
        int ERRNO_NEED_LOGIN    = 1;
        int ERRNO_NETWORK_ERR   = 2;
        int ERRNO_REMOTE_DENIED = 3;
        int ERRNO_REPEAT_OPTION = 4;
        int ERRNO_UNKNOWN       = -1;

        void onSucceed();
        void onFailed(int errno, String msg);
    }


    /**
     * 大妈之家的点赞是这样的，同时保存到本地和服务器
     * 但并没有向服务器查询，同一账号换个设备就没了
     * 当前的版本中使用了
     * {@link cn.nlifew.xdmzj.bean.comic.CommentBean.CommentType#is_goods}
     * 这个保留的标志位，为 1 表示点赞过
     * @param comic_id comic_id
     * @param comment_id comment_id
     * @param callback callback
     */
    public static void star(int comic_id, int comment_id, Callback callback) {
        Account account = Account.getInstance();
        if (account == null) {
            callback.onFailed(Callback.ERRNO_NEED_LOGIN, "您需要先登录");
            return;
        }
        IComic comic = NetworkUtils.create(IComic.class);
        Call<SimpleBean> call = comic.starComment(comment_id, comic_id, comment_id);
        call.enqueue(new retrofit2.Callback<SimpleBean>() {
            @Override
            public void onResponse(Call<SimpleBean> call, Response<SimpleBean> response) {
                SimpleBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                if (bean.code != 0) {
                    callback.onFailed(Callback.ERRNO_REMOTE_DENIED, "失败：" + bean.code + " " + bean.msg);
                    return;
                }

                Comment comment = toComment();

                // 保存到数据库
                try {
                    CommonDatabase db = CommonDatabase.getInstance();
                    Comment.Helper helper = db.getCommentHelper();
                    helper.insert(comment);
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: ", e);
                    callback.onFailed(Callback.ERRNO_UNKNOWN, e.toString());
                    return;
                }
                callback.onSucceed();
            }

            Comment toComment() {
                Comment comment = new Comment();
                comment.id = comment_id;
                comment.comic_id = comic_id;
                comment.create_time = System.currentTimeMillis();
                return comment;
            }

            @Override
            public void onFailure(Call<SimpleBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                callback.onFailed(Callback.ERRNO_NETWORK_ERR, t.toString());
            }
        });
    }

    public static void reply(int comic_id, int to_comment_id, int to_uid,
                             int origin_comment_id, String text, Callback callback) {
        Account account = Account.getInstance();
        if (account == null) {
            callback.onFailed(Callback.ERRNO_NEED_LOGIN, "您需要先登录");
            return;
        }

        Map<String, String> map = new ArrayMap<>(8);
        map.put("obj_id", Integer.toString(comic_id));
        map.put("to_comment_id", Integer.toString(to_comment_id));
        map.put("to_uid", Integer.toString(to_uid));
        map.put("sender_terminal", "1");
        map.put("dmzj_token", account.token);
        map.put("content", text);
        map.put("origin_comment_id", Integer.toString(origin_comment_id));

        IComic comic = NetworkUtils.create(IComic.class);
        Call<SimpleBean> call = comic.sendComment(map);
        call.enqueue(new retrofit2.Callback<SimpleBean>() {
            @Override
            public void onResponse(Call<SimpleBean> call, Response<SimpleBean> response) {
                SimpleBean bean;
                if (! response.isSuccessful() || (bean = response.body()) == null) {
                    onFailure(call, new IOException(response.code() + " " + response.message()));
                    return;
                }
                if (bean.code != 0) {
                    callback.onFailed(Callback.ERRNO_REMOTE_DENIED, "失败：" + bean.code + " " + bean.msg);
                    return;
                }
                callback.onSucceed();
            }


            @Override
            public void onFailure(Call<SimpleBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request(), t);
                callback.onFailed(Callback.ERRNO_NETWORK_ERR, t.toString());
            }
        });
    }
}
