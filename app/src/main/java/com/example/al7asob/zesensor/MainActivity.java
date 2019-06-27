package com.example.al7asob.zesensor;

import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Sensor accelerometer;
    SensorManager sm;
    TextView acceleration,Result;
    float start;
    boolean f=false;
    List<Float[]> list=new ArrayList<Float[]>();
    Interpreter tflite;
    String label []={"left","Right","Up","Down","Clockwise circle","Anticlockwise circle","Clockwise box","AntiClockwise box","left up Edge","Right up Edge","Right Down Edge","Left Down Edge","left up V","Right up V","left down V","Right down V","Right Down S","Left Down S","Left up S","Right up S"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm=(SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        acceleration=(TextView) findViewById(R.id.acceleration);
        Result=(TextView) findViewById(R.id.TV);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            tflite=new Interpreter(LoadModelFile());
        }

    }
    private MappedByteBuffer LoadModelFile()throws IOException{
        AssetFileDescriptor fileDescriptor =this.getAssets().openFd("dataset.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startofset= fileDescriptor.getStartOffset();
        long declaredlength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startofset,declaredlength);
    }
    @Override
    public void onSensorChanged(final SensorEvent se) {


        ToggleButton toggle = (ToggleButton) findViewById(R.id.tb);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    acceleration.setText("record started");
                    Float[] points={(float)se.timestamp,se.values[0],se.values[1],se.values[2]};
                    list.add(points);
                } else {
                    float [][] p=new float[1][1];
                    acceleration.setText("record stopped");
                    tflite.run(list,p);

                    Result.setText(label[(int) p[0][0]]);
                }
            }
        });

//        if(f==false)
//        {
//            f=true;
//            start=se.values[0];
//        }else {
//            if(start>se.values[0])
//            {
//                acceleration.setText("device moved right");
//            }else{
//                acceleration.setText("device moved left");
//            }
//
//        }


        acceleration.setText("X: "+se.values[0]+
                             "\n Y: "+se.values[1]+
                             "\n Z: "+se.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
