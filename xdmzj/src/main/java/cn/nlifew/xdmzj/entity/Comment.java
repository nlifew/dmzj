package cn.nlifew.xdmzj.entity;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import cn.nlifew.xdmzj.bean.comic.CommentBean;

@Entity(tableName = "comment")
public class Comment {


    @PrimaryKey
    public int id;
    public String content;
    public long create_time;
    public String upload_images;

    public int comic_id;
    public String comic_name;

    public int to_uid;
    public String to_name;
    public int to_comment_id;
    public String to_comment_content;

    public int sender_uid;
    public String sender_name;
    public String sender_image;


    @Dao
    public interface Helper {

        @Query("SELECT * FROM comment WHERE id = :id")
        Comment findCommentById(int id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void saveAll(Comment[] comments);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Comment comment);

        @Update
        void update(Comment comment);
    }
}
