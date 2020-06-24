package com.example.calenderlist4;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class EditScheduleActivity2 extends AppCompatActivity implements View.OnClickListener{

    private String schedule;
    private String events;
    private Button editBtn,deleteBtn;
    private EditText scheduleInput;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase myDatabase;      //本例用的数据库

    private EditText inputInfo;     //新的变量
    private Button save;
    private Button reset;
    private TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule2);        //跳转到第二个页面

        Intent intent = getIntent();            // 首先获取到意图对象

        schedule = intent.getStringExtra("schedule");// 获取到传递过来的姓名
        events = intent.getStringExtra("events");   //获取传递过来的备忘录

        initView();
    }

    private void initView() {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        editBtn = findViewById(R.id.change);
        editBtn.setOnClickListener(this);
        deleteBtn = findViewById(R.id.deleteSchedule);
        deleteBtn.setOnClickListener(this);
        scheduleInput = findViewById(R.id.scheduleInput);
        scheduleInput.setText(schedule);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);      //新添加的备忘录
        setFullScreen();
        hideBar();

        inputInfo = (EditText) findViewById(R.id.editText3);
        inputInfo.setText(events);      //设置备忘录显示内容

        save = (Button) findViewById(R.id.button4);
        reset = (Button) findViewById(R.id.button5);
        count = (TextView)findViewById(R.id.textView4);
        inputInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                count.setText(inputInfo.getText().length()+"个字");
            }
        });

        onload();

        inputInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputInfo.setCursorVisible(true);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {        //修改备忘录内容
            @Override
            public void onClick(View view) {
                FileOutputStream fos = null;
                try{
                    /*fos = openFileOutput("txt", Context.MODE_PRIVATE);
                    String text = inputInfo.getText().toString();
                    //Cursor cursor = myDatabase.query("schedules",null,"time=?",new String[]{date},null,null,null);
                    //String text = cursor.getString(cursor.getColumnIndex("events"));
                    fos.write(text.getBytes());
                    ContentValues values = new ContentValues();
                    values.put("events",inputInfo.getText().toString());
                    myDatabase.update("schedules",values,"events=?",new String[]{schedule});
                    Intent intent = new Intent(EditScheduleActivity2.this, MainActivity.class);
                    startActivity(intent);*/
                    editEvent();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try{
                        if(fos!=null){
                            fos.flush();
                            Toast.makeText(EditScheduleActivity2.this,"保存成功！",Toast.LENGTH_SHORT).show();
                            fos.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream fos = null;
                inputInfo.setText("");
                try{
                    fos = openFileOutput("txt", Context.MODE_PRIVATE);
                    String text = "";
                    fos.write(text.getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try{
                        if(fos!=null){
                            fos.flush();
                            Toast.makeText(EditScheduleActivity2.this,"清空成功！",Toast.LENGTH_SHORT).show();
                            fos.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.deleteSchedule:
                deleteMySchedule();
                break;
            case R.id.change:
                editSchedule();
                break;
        }
    }

    private void editSchedule() {       //修改数据库中事件的名称
        ContentValues values = new ContentValues();
        values.put("scheduleDetail",scheduleInput.getText().toString());

        myDatabase.update("schedules",values,"scheduleDetail=?",new String[]{schedule});

        Intent intent = new Intent(EditScheduleActivity2.this, MainActivity.class);
        startActivity(intent);
    }

    private void editEvent() {       //修改备忘录的函数
        ContentValues values = new ContentValues();
        String Btext = inputInfo.getText().toString();
        values.put("events", Btext);
        myDatabase.update("schedules",values,"scheduleDetail=?",new String[]{schedule});
        Toast.makeText(EditScheduleActivity2.this,"保存成功",Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(EditScheduleActivity2.this, MainActivity.class);
        startActivity(intent);*/
    }

    private void deleteMySchedule() {
        myDatabase.delete("schedules","scheduleDetail=?",new String[]{schedule});

        Intent intent = new Intent(EditScheduleActivity2.this, MainActivity.class);
        startActivity(intent);
    }

    public void onload(){
        FileInputStream fis = null;
        try{
            fis = openFileInput("txt");
            if(fis.available()==0){
                return;
            }else{
                byte[] con = new byte[fis.available()];
                while(fis.read(con)!=-1){

                }
                inputInfo.setText(new String(con));
                inputInfo.setSelection(inputInfo.getText().length());
                inputInfo.setCursorVisible(false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    long time;

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if(System.currentTimeMillis()-time>2000){
                Toast.makeText(EditScheduleActivity2.this,"再次点击返回键，程序退出",Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            }else{
                EditScheduleActivity2.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void hideBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    private void setFullScreen(){
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
