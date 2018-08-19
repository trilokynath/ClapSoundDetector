package com.trinfosoft.clapsounddetector;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {
    public String FILE_PATH = Environment.getExternalStorageDirectory() + "/audio.3gp";

    TextView tvResult;
    MediaPlayer mp;
    SwitchCompat mswitch;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mswitch = (SwitchCompat) findViewById(R.id.timerSwitch);

        SharedPreferences prefs = getSharedPreferences("status", MODE_PRIVATE);
        if(prefs!=null)
            status = prefs.getInt("st", 0);

        if (status != 0) {
            mswitch.setChecked(true);
            new RecordAmplitudeTask().execute(new SingleDetectClap(SingleDetectClap.DEFAULT_AMPLITUDE_DIFF));
        }
        mswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    // do something when checked is selected
                    SharedPreferences.Editor editor = getSharedPreferences("status", MODE_PRIVATE).edit();
                    editor.putInt("st", 1);
                    editor.apply();
                    new RecordAmplitudeTask().execute(new SingleDetectClap(SingleDetectClap.DEFAULT_AMPLITUDE_DIFF));

                } else {
                    //do something when unchecked
                    SharedPreferences.Editor editor = getSharedPreferences("status", MODE_PRIVATE).edit();
                    editor.putInt("st", 0);
                    editor.apply();
                }
            }
        });
    }



    private void addControls() {
        tvResult = (TextView) findViewById(R.id.tvResult);
        File file = new File(FILE_PATH);
        if(file.exists()){
            file.delete();
        }
    }




    public class RecordAmplitudeTask extends AsyncTask<AmplitudeClipListener, Void, Boolean> {




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvResult.setText("Detecting clap .....");
//            mp.stop();
        }

        @Override
        protected Boolean doInBackground(AmplitudeClipListener... listeners) {
            if(listeners.length == 0){
                return false;
            }
            AmplitudeClipListener listener = listeners[0];

            MaxAmplitudeRecoder recorder = new MaxAmplitudeRecoder(MaxAmplitudeRecoder.DEFAULT_CLIP_TIME, FILE_PATH, listener, this, getApplicationContext());

            boolean heard = false;
            try{

                heard = recorder.startRecording();

            } catch (IllegalStateException is){
                Log.e("IS", is + "");
                heard = false;
            }catch (RuntimeException re){
                Log.e("RE", re + "");
                heard = false;
            }



            return heard;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.d("result", result+"");
            if(result){
                tvResult.setText("Heard Clap");
                notification();
                mp.start();
                sleep(11000);
                cancelNotification();
                if(status!=0)
                new RecordAmplitudeTask().execute(new SingleDetectClap(SingleDetectClap.DEFAULT_AMPLITUDE_DIFF));
            }
        }


        @Override
        protected void onCancelled() {
            setDoneMessage();
            super.onCancelled();
        }

        private void setDoneMessage() {
            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
        }
    }

    public void notification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Clap Heard");
        builder.setContentText("You have clapped");
//        builder.setSubText("Tap to view the detail.");
        builder.setOngoing(true);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }
    public void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mp.pause();
    }
}
