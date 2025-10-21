package com.mat.mindpet;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "mindpet.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_USER_TABLE = "CREATE TABLE user (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT," +
                "last_name TEXT," +
                "email TEXT," +
                "password TEXT," +
                "join_date TEXT" +
                ");";


        String CREATE_PET_TABLE = "CREATE TABLE pet (" +
                "pet_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "pet_name TEXT," +
                "level INTEGER," +
                "mood TEXT," +
                "FOREIGN KEY(user_id) REFERENCES user(user_id)" +
                ");";


        String CREATE_TASK_TABLE = "CREATE TABLE task (" +
                "task_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "title TEXT," +
                "deadline TEXT," +
                "is_completed INTEGER," +
                "reward_points INTEGER," +
                "created_at TEXT," +
                "FOREIGN KEY(user_id) REFERENCES user(user_id)" +
                ");";


        String CREATE_SCREENTIME_TABLE = "CREATE TABLE screentime (" +
                "entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "app_name TEXT," +
                "date TEXT," +
                "minutes_used INTEGER," +
                "goal_minutes INTEGER," +
                "exceeded_goal INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES user(user_id)" +
                ");";


        String CREATE_PROGRESS_TABLE = "CREATE TABLE progress (" +
                "progress_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "date TEXT," +
                "tasks_completed INTEGER," +
                "screen_goals_met INTEGER," +
                "daily_score INTEGER," +
                "streak_count INTEGER," +
                "FOREIGN KEY(user_id) REFERENCES user(user_id)" +
                ");";


        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PET_TABLE);
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_SCREENTIME_TABLE);
        db.execSQL(CREATE_PROGRESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS pet");
        db.execSQL("DROP TABLE IF EXISTS task");
        db.execSQL("DROP TABLE IF EXISTS screentime");
        db.execSQL("DROP TABLE IF EXISTS progress");
        onCreate(db);
    }
}
