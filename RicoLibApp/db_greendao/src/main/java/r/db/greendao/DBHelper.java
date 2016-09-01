package r.db.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;


/**
 * Created by Rico on 2015/8/14.
 */
public class DBHelper {

    private static boolean finishing;
    private static HashMap<String, RDBEntity> dbEntityMap = new HashMap<>();


    public static <T extends RDaoSession> void initDB(Context context,  String dbName, Class<? extends SQLiteOpenHelper> sqliteHelperClz, Class<T> daoSessionClz, Class<? extends RDaoMaster> daoMasterClz) {
        if (dbEntityMap.get(dbName) == null)
            try {
                SQLiteOpenHelper sqliteHelper = sqliteHelperClz.getConstructor(Context.class, String.class, SQLiteDatabase.CursorFactory.class).newInstance(context, dbName, null);
                RDBEntity<T> dbEntity = new RDBEntity<T>(sqliteHelper, daoSessionClz, daoMasterClz);
                dbEntityMap.put(dbName, dbEntity);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

    }

    public static void clear() {
        finishing = true;
        for (RDBEntity dbEntity :
                dbEntityMap.values()) {
//            dbEntity.getDaoSession().clear();
//            dbEntity.getDb().close();
            dbEntity.finish();
        }
        dbEntityMap.clear();
        finishing = false;
    }

    public static SQLiteDatabase getDB(String dbName) throws DBNotInitException, DBNotWritableException {
        if (finishing)
            throw new DBNotInitException();
        RDBEntity dbEntity = dbEntityMap.get(dbName);
        if (dbEntity == null)
            throw new DBNotInitException();
        else {
            if (dbEntity.getDb().isReadOnly())
                throw new DBNotWritableException();
            else
                return dbEntity.getDb();
        }
    }


    public static <T extends RDaoSession> T getDaoSession(String dbName,Class<T> clz) throws DBNotInitException {
        if (finishing)
            throw new DBNotInitException();
        RDBEntity dbEntity = dbEntityMap.get(dbName);
        if (dbEntity == null)
            throw new DBNotInitException();
        else {
            return (T) dbEntity.getDaoSession();
        }
    }

    public static class RDBEntity<T extends RDaoSession> {
        private SQLiteOpenHelper helper;
        private SQLiteDatabase db;
        private T daoSession;
//        private Class<?> daoSessionClz;
//        private Class<?> daoMasterClz;
//        private int version;

        public RDBEntity(SQLiteOpenHelper sqLiteOpenHelper, T daoSession, SQLiteDatabase db) {
            this.helper = sqLiteOpenHelper;
            this.daoSession = daoSession;
            this.db = db;
        }

        public RDBEntity( SQLiteOpenHelper sqLiteOpenHelper, Class<? extends RDaoSession> daoSessionClz, Class<? extends RDaoMaster> daoMasterClz) {
            this.helper = sqLiteOpenHelper;
//            this.daoSessionClz = daoSessionClz;
//            this.daoMasterClz = daoMasterClz;
            this.db = helper.getReadableDatabase();

            try {
                this.daoSession = (T) daoMasterClz.getConstructor(SQLiteDatabase.class).newInstance(db).newSession();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        public SQLiteDatabase getDb() {
            return db;
        }

        public T getDaoSession() {
            return daoSession;
        }

        public void finish() {
            this.daoSession.clear();
//            this.db.close();
        }
    }
}
