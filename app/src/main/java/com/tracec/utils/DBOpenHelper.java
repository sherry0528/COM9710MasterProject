package com.tracec.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import com.tracec.data.Clinician;
import com.tracec.data.ExcelData;
import com.tracec.data.Patient;
import com.tracec.data.Record;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class DBOpenHelper extends SQLiteOpenHelper{
    /**
     * AndroidSDK bring database db
     */
    private SQLiteDatabase db;

    /**
     * This purpose for context for clinican, patient
     */
    public DBOpenHelper(Context context){
        super(context,"db_trace",null,1);
        db = getReadableDatabase();
    }

    /**
     * Rewrite because class DBOpenHelper extends SQLiteOpenHelper
     * This two pathways are abstract in abstract-SQLiteOpenHelper
     * So it should rewrite abstract in DBOpenHelper
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS clinicians(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "clinicianid TEXT," +
                "clinicianname TEXT," +
                "clinicname TEXT," +
                "contactnumber TEXT," +
                "email TEXT," +
                "password TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS patients(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patientid TEXT," +
                "surname TEXT," +
                "givenname TEXT," +
                "password TEXT," +
                "birth TEXT," +
                "guardianname TEXT," +
                "guardianphone TEXT," +
                "clinicianid TEXT," +
                "clinicianname TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS casinos(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "clinicianid TEXT," +
                "casinoname TEXT,"+
                "log TEXT,"+
                "lat TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS records(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patientid TEXT," +
                "starttime TEXT," +
                "endtime TEXT)");

        initDataBase(db);
    }

    private void initDataBase (SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("clinicianid","C0001");
        values.put("clinicianname","Sherry Chen");
        values.put("clinicname","Flinders Medical Clinic");
        values.put("contactnumber","0433721897");
        values.put("email","chan1616@flinders.edu.au");
        values.put("password","123456");
        db.insert("clinicians", null, values);

        ContentValues values1 = new ContentValues();
        values1.put("patientid","P0001");
        values1.put("surname","Roy");
        values1.put("givenname","Lee");
        values1.put("password","123456");
        values1.put("birth","04/05/1992");
        values1.put("guardianname","Roy");
        values1.put("guardianphone","123456789");
        values1.put("clinicianid","C0001");
        values1.put("clinicianname","Sherry Chen");
        db.insert("patients", null, values1);

        ContentValues values2 = new ContentValues();
        values2.put("clinicianid","C0001");
        values2.put("casinoname","Flinders university Tonsley");
        values2.put("log","-35.008");
        values2.put("lat","138.572");
        db.insert("casinos",null,values2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }

    /**
     * This is way for add or delete info
     */
    //添加新用户，即注册
    public boolean addPatient(Patient patient){
        ContentValues values = new ContentValues();
        values.put("patientid", patient.getPatientid());
        values.put("password", patient.getPassword());
        values.put("surname",patient.getSurname());
        values.put("givenname",patient.getGivenname());
        values.put("birth",patient.getBirth());
        values.put("clinicianid",patient.getClinicianid());
        values.put("clinicianname",patient.getClinicianname());
        values.put("guardianname",patient.getGuardianname());
        values.put("guardianphone",patient.getGuardianphone());
        return db.insert("patients","_id",values)>0;
    }

    //Based on user for checking register existing
    public int findPatient(String patientid){
        int result=0;
        Cursor cursor = db.query("patients",null,"patientid='"+patientid+"'",null,null,null,null);
        if(cursor!=null){
            result=cursor.getCount();
            cursor.close();
        }
        return result;
    }

    public boolean findClinicianByNameAndPwd(String clinicianid,String password){
        String sql="select * from clinicians where clinicianid=? and password=?";
        Cursor cursor=db.rawQuery(sql, new String[]{clinicianid,password});
        if(cursor.moveToFirst()==true){
            cursor.close();
            return true;
        }
        return false;
    }

    public Clinician getClinician(String clinicianid){
        Clinician clinician = null;
        String sql="select * from clinicians where clinicianid=?";
        Cursor cursor=db.rawQuery(sql, new String[]{clinicianid});
        if (cursor.moveToNext()) {
            clinician = new Clinician();
            clinician.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            clinician.setClinicianid(cursor.getString(cursor.getColumnIndex("clinicianid")));
            clinician.setClinicianname(cursor.getString(cursor.getColumnIndex("clinicianname")));
            clinician.setContactnumber(cursor.getString(cursor.getColumnIndex("contactnumber")));
            clinician.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            clinician.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        }
        return clinician;
    }

    public List<Patient> getAllPatient(String clinicianid){
        List<Patient> list = new ArrayList<>();
        Cursor cursor = db.query("patients",null,"clinicianid='"+clinicianid+"'",null,null,null,null);
        while (cursor.moveToNext()){
            Patient patient = new Patient();
            patient.setPatientid(cursor.getString(cursor.getColumnIndex("patientid")));
            patient.setSurname(cursor.getString(cursor.getColumnIndex("surname")));
            patient.setGivenname(cursor.getString(cursor.getColumnIndex("givenname")));
            patient.setClinicianid(cursor.getString(cursor.getColumnIndex("clinicianid")));
            patient.setClinicianname(cursor.getString(cursor.getColumnIndex("clinicianname")));
            patient.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            patient.setBirth(cursor.getString(cursor.getColumnIndex("birth")));
            patient.setGuardianname(cursor.getString(cursor.getColumnIndex("guardianname")));
            patient.setGuardianphone(cursor.getString(cursor.getColumnIndex("guardianphone")));
            list.add(patient);
        }
        return list;
    }

    public Patient getLast(){
        Patient patient = new Patient();
        Cursor cursor = db.query("patients", null, null, null, null, null, "_id desc", "1");
        if (cursor.moveToNext()) {
            patient.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            patient.setPatientid(cursor.getString(cursor.getColumnIndex("patientid")));
            patient.setSurname(cursor.getString(cursor.getColumnIndex("surname")));
            patient.setGivenname(cursor.getString(cursor.getColumnIndex("givenname")));
            patient.setClinicianid(cursor.getString(cursor.getColumnIndex("clinicianid")));
            patient.setClinicianname(cursor.getString(cursor.getColumnIndex("clinicianname")));
            patient.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            patient.setBirth(cursor.getString(cursor.getColumnIndex("birth")));
            patient.setGuardianname(cursor.getString(cursor.getColumnIndex("guardianname")));
            patient.setGuardianphone(cursor.getString(cursor.getColumnIndex("guardianphone")));
        }
        return patient;
    }

    public List<Patient> queryPatient(String surname,String givenname,String birth){
        List<Patient> list = new ArrayList<>();
        String where="";
        if(!surname.equals("")){
            where="surname like '%"+surname+"%'";
        }
        if(!givenname.equals("")){
            if(where.equals("")){
                where = "givenname like '%"+givenname+"%'";
            } else {
                where=where +" and givenname like '%"+givenname+"%'";
            }
        }
        if(!birth.equals("")){
            if(where.equals("")){
                where="birth = '"+birth+"'";
            } else {
                where=where +" and birth='"+birth+"'";
            }
        }
        Cursor cursor = db.query("patients",null,where,null,null,null,null);
        while (cursor.moveToNext()){
            Patient patient = new Patient();
            patient.setPatientid(cursor.getString(cursor.getColumnIndex("patientid")));
            patient.setSurname(cursor.getString(cursor.getColumnIndex("surname")));
            patient.setGivenname(cursor.getString(cursor.getColumnIndex("givenname")));
            patient.setClinicianid(cursor.getString(cursor.getColumnIndex("clinicianid")));
            patient.setClinicianname(cursor.getString(cursor.getColumnIndex("clinicianname")));
            patient.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            patient.setBirth(cursor.getString(cursor.getColumnIndex("birth")));
            patient.setGuardianname(cursor.getString(cursor.getColumnIndex("guardianname")));
            patient.setGuardianphone(cursor.getString(cursor.getColumnIndex("guardianphone")));
            list.add(patient);
        }
        return list;
    }

    public Patient getPatient(String patientid){
        Patient patient = null;
        Cursor cursor = db.query("patients", null, "patientid='"+patientid+"'", null, null, null, null);
        if (cursor.moveToNext()) {
            patient = new Patient();
            patient.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            patient.setPatientid(cursor.getString(cursor.getColumnIndex("patientid")));
            patient.setSurname(cursor.getString(cursor.getColumnIndex("surname")));
            patient.setGivenname(cursor.getString(cursor.getColumnIndex("givenname")));
            patient.setClinicianid(cursor.getString(cursor.getColumnIndex("clinicianid")));
            patient.setClinicianname(cursor.getString(cursor.getColumnIndex("clinicianname")));
            patient.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            patient.setBirth(cursor.getString(cursor.getColumnIndex("birth")));
            patient.setGuardianname(cursor.getString(cursor.getColumnIndex("guardianname")));
            patient.setGuardianphone(cursor.getString(cursor.getColumnIndex("guardianphone")));
        }
        return patient;
    }

    public boolean addRecord(String pid,String starttime){
        ContentValues values = new ContentValues();
        values.put("patientid",pid);
        values.put("starttime",starttime);
        values.put("endtime","");
        return db.insert("records","_id",values)>0;
    }

    public int getLastRecord(String pid){
        int id=0;
        Cursor cursor = db.query("records", null, "patientid='"+pid+"'", null, null, null, "_id desc", "1");
        if (cursor.moveToNext()) {
            id=cursor.getInt(cursor.getColumnIndex("_id"));
        }
        return id;
    }

    public boolean updateRecord(String endtime,int id){
        ContentValues values = new ContentValues();
        values.put("endtime",endtime);
        return db.update("records",values,"_id=?",new String[]{id+""}) > 0;
    }

    public List<Record> getAllRecord(String pid){
        List<Record> list = new ArrayList<>();
        Cursor cursor = db.query("records",null,"patientid='"+pid+"'",null,null,null,"_id desc");
        while (cursor.moveToNext()){
            Record data = new Record();
            data.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            data.setPid(cursor.getString(cursor.getColumnIndex("patientid")));
            data.setStarttime(cursor.getString(cursor.getColumnIndex("starttime")));
            data.setEndtime(cursor.getString(cursor.getColumnIndex("endtime")));
            list.add(data);
        }
        return list;
    }

    public List<ExcelData> getExcelData(String pid){
        Patient patient=getPatient(pid);

        List<ExcelData> list = new ArrayList<>();
        Cursor cursor = db.query("records",new String[]{"substr(starttime,0,7) as time","count(_id) as c"},"patientid='"+pid+"'",null,"substr(starttime,0,7)",null,null);
        while (cursor.moveToNext()){
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String newtime=time.substring(5,6)+time.substring(0,4);

            ExcelData data = new ExcelData();
            data.setDate("01/"+newtime+"-31/"+newtime);
            data.setPid(patient.getPatientid());
            data.setPname(patient.getSurname() + " " + patient.getGivenname());
            data.setPdob(patient.getBirth());
            data.setClosingtimes(cursor.getInt(cursor.getColumnIndex("c"))+" times");

            List<Record> rlist = getRecordByTime(pid,time);
            int sumtime = 0;
            for (int j = 0; j < rlist.size(); j++) {
                Record record = rlist.get(j);
                int minutes = getGapMinutes(record.getStarttime(), record.getEndtime());
                sumtime = sumtime + minutes;
            }
            data.setStaytime(sumtime + " minutes");
            data.setWarningtimes(cursor.getInt(cursor.getColumnIndex("c"))+" times");
            list.add(data);
        }
        return list;
    }

    public List<Record> getRecordByTime(String pid,String time){
        List<Record> list = new ArrayList<>();
        Cursor cursor = db.query("records",null,"patientid='"+pid+"' and starttime like '"+time+"%'",null,null,null,null);
        while (cursor.moveToNext()){
            Record data = new Record();
            data.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            data.setPid(cursor.getString(cursor.getColumnIndex("patientid")));
            data.setStarttime(cursor.getString(cursor.getColumnIndex("starttime")));
            data.setEndtime(cursor.getString(cursor.getColumnIndex("endtime")));
            list.add(data);
        }
        return list;
    }

    private static int getGapMinutes(String startDate, String endDate) {
        long start = 0;
        long end = 0;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            start = df.parse(startDate).getTime();
            end = df.parse(endDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int minutes = (int) ((end - start) / (1000 * 60));
        return minutes;
    }
}
