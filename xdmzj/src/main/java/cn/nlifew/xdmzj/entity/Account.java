package cn.nlifew.xdmzj.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import cn.nlifew.xdmzj.database.CommonDatabase;
import cn.nlifew.xdmzj.utils.Singleton;

@Entity(tableName = "account")
public class Account {

    private static final Singleton<Account> sAccount = new Singleton<Account>() {
        @Override
        protected Account create() {
            CommonDatabase db = CommonDatabase.getInstance();
            Helper helper = db.getAccountHelper();
            return helper.get();
        }
    };

    public static Account getInstance() {
        return sAccount.get();
    }

    public static void setInstance(Account account) {
        CommonDatabase db = CommonDatabase.getInstance();
        Helper helper = db.getAccountHelper();
        helper.deleteAll();

        if (account != null) {
            helper.save(account);
        }
        sAccount.set(account);
    }

    @PrimaryKey
    public int id;
    public String name;
    public String token;
    public String photo;
    public String phone;
    public String email;
    public String password;


    @Dao
    public interface Helper {
        @Query("DELETE FROM account")
        void deleteAll();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void save(Account account);

        @Query("SELECT * FROM account LIMIT 1")
        Account get();
    }
}
