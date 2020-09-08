package cn.nlifew.xdmzj.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.nlifew.xdmzj.entity.Comment;
import cn.nlifew.xdmzj.entity.ReadingRecord;
import cn.nlifew.xdmzj.utils.Singleton;
import cn.nlifew.xdmzj.xDmzj;
import cn.nlifew.xdmzj.entity.Account;

@Database(entities =
        {Account.class, Comment.class, ReadingRecord.class},
        version = 1, exportSchema = false)
public abstract class CommonDatabase extends RoomDatabase {
    private static final String TAG = "CommonDatabase";

    private static final Singleton<CommonDatabase> sInstance = new Singleton<CommonDatabase>() {
        @Override
        protected CommonDatabase create() {
            return Room
                    .databaseBuilder(xDmzj.sInstance, CommonDatabase.class, "common")
                    .allowMainThreadQueries()
                    .build();
        }
    };

    public static CommonDatabase getInstance() {
        return sInstance.get();
    }

    public abstract Account.Helper getAccountHelper();

    public abstract Comment.Helper getCommentHelper();

    public abstract ReadingRecord.Helper getReadingRecordHelper();
}
