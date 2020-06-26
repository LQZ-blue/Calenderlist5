package com.example.calenderlist4;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private String date;
    private SQLiteDatabase myDatabase;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private TextView otherSchedule[] = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        otherSchedule[0] = findViewById(R.id.thing1);
        otherSchedule[1] = findViewById(R.id.thing2);
        otherSchedule[2] = findViewById(R.id.thing3);
        otherSchedule[3] = findViewById(R.id.thing4);
        otherSchedule[4] = findViewById(R.id.thing5);

        writeOut();
    }
    private void writeOut(){
        Cursor cursor = myDatabase.query("schedules",null,"time=?",new String[]{date},null,null,null);
        for(TextView v:otherSchedule){
            v.setText("");
            v.setVisibility(View.GONE);
        }
        if(cursor.moveToFirst()){
            int scheduleCount = 0;
            do{
                String aScheduleDetail = cursor.getString(cursor.getColumnIndex("scheduleDetail"));
                otherSchedule[scheduleCount].setText("日程"+(scheduleCount+1)+"："+aScheduleDetail);
                otherSchedule[scheduleCount].setVisibility(View.VISIBLE);
                scheduleCount++;
                //一定要有这句 不然TextView不够多要数组溢出了
                if(scheduleCount >= 5)
                    break;
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
}
