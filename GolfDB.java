/**
 * Created by adam on 6/25/15.
 */
public class GolfDB extends SQLiteOpenHelper {
    private static GolfDB golfDB;
    private static final String DATABASE_NAME = "golf.db";

    //round table
    public static final String ROUND_COLUMN_COURSENAME = "courseName";
    public static final String ROUND_COLUMN_GOLFERNAME = "golferName";
    public static final String ROUND_COLUMN_HOLENUMBER = "holeNumber";
    public static final String ROUND_COLUMN_SCORE = "score";
    public static final String ROUND_COLUMN_PAR = "par";
    public static final String ROUND_COLUMN_HANDICAP = "handicap";
    public static final String ROUND_COLUMN_PUTTS = "putts";
    public static final String ROUND_COLUMN_GREENSHIT = "greensHit";
    public static final String ROUND_COLUMN_FAIRWAYHIT = "fairwayHit";

    //profile table
    public static final String PROFILE_TABLE_NAME = "Profiles";
    public static final String PROFILE_COLUMN_NAME = "name";
    public static final String PROFILE_COLUMN_PIC = "pic";
    public static final String PROFILE_COLUMN_HANDICAP = "handicap";

    //strokes table
    public static final String STROKES_TABLE_NAME = "strokes";
    public static final String STROKES_COLUMN_HOLE = "holeNumber";
    public static final String STROKES_COLUMN_CLUB = "clubName";
    public static final String STROKES_COLUMN_LATITUDE = "latitude";
    public static final String STROKES_COLUMN_LONGITUDE = "longitude";

    private String roundTableName;
    private String strokesTableName;

    public static GolfDB getGolfDatabase(Context context) {
        if (golfDB == null) {
            golfDB = new GolfDB(context);
        }
        return golfDB;
    }

    private GolfDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void createNewRound(String name) {
        //check if trying to create new round
        if (!name.equalsIgnoreCase("")) {
            SQLiteDatabase db = golfDB.getWritableDatabase();

            //get todays date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh", Locale.US);
            Date date = new Date();
            String time = sdf.format(date);

            //create strokes table for SQLITE
            strokesTableName = STROKES_TABLE_NAME + name + time;


            db.execSQL("DROP TABLE IF EXISTS " + strokesTableName);
            db.execSQL(
                    "CREATE TABLE " + strokesTableName +
                            " (id INTEGER PRIMARY KEY, holeNumber TEXT, clubName TEXT, " +
                            "latitude TEXT, longitude TEXT)"
            );

            //create round table for SQLITE
            roundTableName = name + time;

            db.execSQL("DROP TABLE IF EXISTS " + roundTableName);
            db.execSQL(
                    "CREATE TABLE " + roundTableName +
                            " (id INTEGER PRIMARY KEY, courseName TEXT, golferName TEXT, holeNumber TEXT, par TEXT, handicap TEXT, score TEXT, putts TEXT, greensHit TEXT, fairwayHit TEXT);"
            );
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
       try {
            Cursor res = db.rawQuery("SELECT * FROM " + PROFILE_TABLE_NAME, null);
        } catch (android.database.sqlite.SQLiteException exception) {
            db.execSQL(
                    "CREATE TABLE " + PROFILE_TABLE_NAME + " (id INTEGER PRIMARY KEY, name TEXT, pic TEXT, handicap TEXT);"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + strokesTableName);
        db.execSQL("DROP TABLE IF EXISTS " + roundTableName);
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
        onCreate(db);
    }

    public int numberOfProfiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PROFILE_TABLE_NAME);
        return numRows;
    }

    public boolean insertHole(String course, String golfer, String hole, String par, String handicap, String score, String putts,
                              String greensHit, String fairwayHit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("courseName", course);
        contentValues.put("golferName", golfer);
        contentValues.put("holeNumber", hole);
        contentValues.put("par", par);
        contentValues.put("handicap", handicap);
        contentValues.put("score", score);
        contentValues.put("putts", putts);
        contentValues.put("greensHit", greensHit);
        contentValues.put("fairwayHit", fairwayHit);

        db.insert(roundTableName, null, contentValues);
        return true;
    }

    public boolean updateHole(Integer id, String course, String golfer, String hole, String par, String handicap, String score, String putts,
                              String greensHit, String fairwayHit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("courseName", course);
        contentValues.put("golferName", golfer);
        contentValues.put("holeNumber", hole);
        contentValues.put("par", par);
        contentValues.put("handicap", handicap);
        contentValues.put("score", score);
        contentValues.put("putts", putts);
        contentValues.put("greensHit", greensHit);
        contentValues.put("fairwayHit", fairwayHit);

        db.update(roundTableName, contentValues, "id = ?", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteHole(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(roundTableName,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String[]> getAllEntriesForCurrentRound() {
        ArrayList<String[]> array_list = new ArrayList<String[]>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor holeCursor = db.rawQuery("SELECT * FROM " + roundTableName, null);
        holeCursor.moveToFirst();

        while (!holeCursor.isAfterLast()) {
            String[] entry = new String[9];

            entry[0] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_COURSENAME));
            entry[1] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_GOLFERNAME));
            entry[2] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_HOLENUMBER));
            entry[3] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_PAR));
            entry[4] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_HANDICAP));
            entry[5] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_SCORE));
            entry[6] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_PUTTS));
            entry[7] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_GREENSHIT));
            entry[8] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_FAIRWAYHIT));

            array_list.add(entry);
            holeCursor.moveToNext();
        }

        holeCursor.close();

        return array_list;
    }

    public ArrayList<String[]> getAllEntriesForRound(String name) {
        ArrayList<String[]> array_list = new ArrayList<String[]>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        /*try {
            Cursor holeCursor =  db.rawQuery( "SELECT * FROM " + roundName, null );
        }catch (android.database.sqlite.SQLiteException exception){
            db.execSQL(
                    "CREATE TABLE " + roundName +
                            " (id INTEGER PRIMARY KEY, courseName TEXT, golferName TEXT, holeNumber TEXT, par TEXT, score TEXT);"
            );
        }*/
        Cursor holeCursor = db.rawQuery("SELECT * FROM " + name, null);
        holeCursor.moveToFirst();

        while (!holeCursor.isAfterLast()) {
            String[] entry = new String[9];

            entry[0] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_COURSENAME));
            entry[1] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_GOLFERNAME));
            entry[2] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_HOLENUMBER));
            entry[3] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_PAR));
            entry[4] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_HANDICAP));
            entry[5] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_SCORE));
            entry[6] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_PUTTS));
            entry[7] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_GREENSHIT));
            entry[8] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_FAIRWAYHIT));

            array_list.add(entry);
            holeCursor.moveToNext();
        }

        holeCursor.close();

        return array_list;
    }

    public ArrayList<ArrayList<String[]>> getAllRounds() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ArrayList<String[]>> allRounds = new ArrayList<ArrayList<String[]>>();
        Cursor roundCursor = db.rawQuery("SELECT * FROM " + "sqlite_master WHERE type='table'", null);
        roundCursor.moveToFirst();

        while (!roundCursor.isAfterLast()) {
            String tableName = roundCursor.getString(roundCursor.getColumnIndex("tbl_name"));
            if (tableName.equalsIgnoreCase("android_metadata") ||
                    tableName.equalsIgnoreCase("Profiles")) {
                roundCursor.moveToNext();
                continue;
            }

            Cursor holeCursor = db.rawQuery("SELECT * FROM " + tableName, null);
            holeCursor.moveToFirst();

            ArrayList<String[]> round = new ArrayList<String[]>();

            while (!holeCursor.isAfterLast()) {
                String[] entry = new String[10];

                entry[0] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_COURSENAME));
                entry[1] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_GOLFERNAME));
                entry[2] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_HOLENUMBER));
                entry[3] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_PAR));
                entry[4] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_HANDICAP));
                entry[5] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_SCORE));
                entry[6] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_PUTTS));
                entry[7] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_GREENSHIT));
                entry[8] = holeCursor.getString(holeCursor.getColumnIndex(ROUND_COLUMN_FAIRWAYHIT));
                entry[9] = tableName;

                round.add(entry);
                holeCursor.moveToNext();
            }
            holeCursor.close();

            if (round.size() > 3) {
                allRounds.add(0, round);
            } else {
                db.execSQL("DROP TABLE IF EXISTS " + tableName);
            }
            roundCursor.moveToNext();
        }
        roundCursor.close();

        return allRounds;
    }

    public void deleteRound(String roundName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + roundName);
    }

    // Profile Database
    public boolean insertProfile(String name, String pic, String handicap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COLUMN_NAME, name);
        contentValues.put(PROFILE_COLUMN_PIC, pic);
        contentValues.put(PROFILE_COLUMN_HANDICAP, handicap);


        db.insert(PROFILE_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateProfile(Integer id, String name, String pic, String handicap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COLUMN_NAME, name);
        contentValues.put(PROFILE_COLUMN_PIC, pic);
        contentValues.put(PROFILE_COLUMN_HANDICAP, handicap);
        db.update(PROFILE_TABLE_NAME, contentValues, "id = ?", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteProfile(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROFILE_TABLE_NAME,
                "name = ? ",
                new String[]{name});
    }

    public ArrayList<String[]> getAllProfiles() {
        ArrayList<String[]> array_list = new ArrayList<String[]>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + PROFILE_TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            String[] entry = new String[3];

            entry[0] = res.getString(res.getColumnIndex(PROFILE_COLUMN_NAME));
            entry[1] = res.getString(res.getColumnIndex(PROFILE_COLUMN_PIC));
            entry[2] = res.getString(res.getColumnIndex(PROFILE_COLUMN_HANDICAP));

            array_list.add(entry);
            res.moveToNext();
        }

        res.close();

        return array_list;
    }

    //strokes database
    public boolean insertStroke(String hole, String club, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(STROKES_COLUMN_HOLE, hole);
        contentValues.put(STROKES_COLUMN_CLUB, club);
        contentValues.put(STROKES_COLUMN_LATITUDE, latitude);
        contentValues.put(STROKES_COLUMN_LONGITUDE, longitude);

        db.insert(strokesTableName, null, contentValues);
        return true;
    }

    public Integer deleteStroke(String lat) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(strokesTableName,
                STROKES_COLUMN_LATITUDE + " = ? ",
                new String[]{lat});
    }

    public ArrayList<String[]> getAllStrokes() {
        ArrayList<String[]> array_list = new ArrayList<String[]>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + strokesTableName, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            String[] entry = new String[3];
            entry[0] = res.getString(res.getColumnIndex(STROKES_COLUMN_HOLE));
            entry[1] = res.getString(res.getColumnIndex(STROKES_COLUMN_LATITUDE));
            entry[2] = res.getString(res.getColumnIndex(STROKES_COLUMN_LONGITUDE));

            array_list.add(entry);
            res.moveToNext();
        }

        return array_list;
    }
}