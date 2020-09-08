package cn.nlifew.xdmzj.bean.comic;

import android.os.Parcel;
import android.os.Parcelable;

import cn.nlifew.xdmzj.bean.BeanSupport;

public class ComiclBean extends BeanSupport {

    public String author_notice;
    public AuthorType[] authors;
    public ChapterType[] chapters;
    public String comic_notice;
    public String comic_py;
    public CommentType comment;
    public int copyright;
    public String cover;
    public String description;
    public DHType[] dh_url_links;
    public int direction;
    public String first_letter;
    public int hidden;
    public int hit_num;
    public int hot_num;
    public int id;
    public int is_dmzj;
    public int is_dot;
    public int is_lock;
    public int isHiddenChapter;
    public int islong;
    public int last_update_chapter_id;
    public String last_update_chapter_name;
    public long last_updatetime;
    public StatusType[] status;
    public int subscribe_num;
    public String title;
    public TypeType[] types;
    public int uid;
    public Object url_links;    // todo: null

    public static final class AuthorType {
        public int tag_id;
        public String tag_name;
    }

    public static final class ChapterType implements Parcelable {
        public ChapterDataType[] data;
        public String title;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(this.data, flags);
            dest.writeString(this.title);
        }

        public ChapterType() {
        }

        private ChapterType(Parcel in) {
            this.data = in.createTypedArray(ChapterDataType.CREATOR);
            this.title = in.readString();
        }

        public static final Creator<ChapterType> CREATOR = new Creator<ChapterType>() {
            @Override
            public ChapterType createFromParcel(Parcel source) {
                return new ChapterType(source);
            }

            @Override
            public ChapterType[] newArray(int size) {
                return new ChapterType[size];
            }
        };
    }

    public static final class ChapterDataType implements Parcelable {
        public int chapter_id;
        public int chapter_order;
        public String chapter_title;
        public int filesize;
        public long updatetime;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.chapter_id);
            dest.writeInt(this.chapter_order);
            dest.writeString(this.chapter_title);
            dest.writeInt(this.filesize);
            dest.writeLong(this.updatetime);
        }

        public ChapterDataType() {
        }

        private ChapterDataType(Parcel in) {
            this.chapter_id = in.readInt();
            this.chapter_order = in.readInt();
            this.chapter_title = in.readString();
            this.filesize = in.readInt();
            this.updatetime = in.readLong();
        }

        public static final Parcelable.Creator<ChapterDataType> CREATOR = new Parcelable.Creator<ChapterDataType>() {
            @Override
            public ChapterDataType createFromParcel(Parcel source) {
                return new ChapterDataType(source);
            }

            @Override
            public ChapterDataType[] newArray(int size) {
                return new ChapterDataType[size];
            }
        };
    }

    public static final class CommentType {
        public int comment_count;
        public CommentDataType[] latest_comment;
    }

    public static final class CommentDataType {
        public String avatar;
        public int comment_id;
        public String content;
        public long createtime;
        public String nickname;
        public int uid;
    }

    public static final class DHType {
        public Object list; // todo: null
        public String title;
    }

    public static final class StatusType {
        public int tag_id;
        public String tag_name;
    }

    public static final class TypeType {
        public int tag_id;
        public String tag_name;
    }
}
