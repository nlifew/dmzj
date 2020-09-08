package cn.nlifew.xdmzj.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

@Entity(tableName = "reading_record")
public class ReadingRecord {


    @PrimaryKey
    public int id;

    public int chapterId;
    public long timestamp;
    public String title;
    public int page;

    @Dao
    public interface Helper {

        @Query("SELECT * FROM reading_record WHERE id = :id")
        ReadingRecord findRecordById(int id);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void save(ReadingRecord record);

        @Query("DELETE FROM reading_record")
        void deleteAll();

        @Delete
        void delete(ReadingRecord record);
    }
}
