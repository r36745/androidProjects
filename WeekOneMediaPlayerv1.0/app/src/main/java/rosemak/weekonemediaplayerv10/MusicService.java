package rosemak.weekonemediaplayerv10;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by stevierose on 1/23/15.
 */
public class MusicService extends Service {
    private Handler mHandler = new Handler();
    MediaPlayer mPlayer;
    boolean mPrepared;
    BoundServiceBinder mBinder;
    private NotificationManager mManager;
    NotificationManager notificationManager;
    int notifyID = 4791;
    private boolean isRepeat = false;
    private int currentSongIndex = 0;
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;
    boolean isNotificActive = false;
    String sntSeekPos;
    int intSeekPos;
    int mediaPosition;
    int mediaMax;
    public static int songEnded;
    private Intent seekIntent;
    public static final String BROADCAST_ACTION = "rosemak.weekonemediaplayer";
    NotificationCompat.Builder notificBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        mBinder = new BoundServiceBinder();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         onPlay();
        showNotification("K. Michelle", "Love em All", "K. Michelle-Love em All");
        //setupHandler();
        return Service.START_STICKY;
    }

   /* private void setupHandler() {
        mHandler.removeCallbacks(sendUpdatesToUI);
        mHandler.postDelayed(sendUpdatesToUI, 1000);
    }
    
    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
           logMediaPosition();
        }
    };

    private void logMediaPosition() {
        if (mPlayer.isPlaying()) {
            mediaPosition = mPlayer.getCurrentPosition();
            //seekIntent = new Intent(getApplicationContext(),MainFragment.AlertReceiver.class);
            mediaMax = mPlayer.getDuration();
            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            //sendBroadcast(seekIntent);
        }
    }*/

    public void onPlay() {
        String song1 = "android.resource://" + getPackageName() + "/raw/love_em_all";
        String song2 = "android.resource://" + getPackageName() + "/raw/awesome";
        String song3 = "android.resource://" + getPackageName() + "/raw/aint_that_easy";

        if (mPlayer == null ) {
            //Player being initiated
            Toast.makeText(getApplicationContext(), "Player Starting", Toast.LENGTH_LONG).show();
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //showNotification();
            String [] favSongs = {song1, song2, song3};

            mPlayer.reset();
            Uri uri = Uri.parse(favSongs[currentSongIndex]);
            try {
                mPlayer.setDataSource(getApplicationContext(),uri);
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (mPlayer != null && !mPlayer.isPlaying()){
            //unpause media player
            mPlayer.start();
            Toast.makeText(getApplicationContext(), "Player was paused, starting back", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Music is Playing", Toast.LENGTH_LONG).show();
        }

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    String song1 = "android.resource://" + getPackageName() + "/raw/love_em_all";
                    String song2 = "android.resource://" + getPackageName() + "/raw/awesome";
                    String song3 = "android.resource://" + getPackageName() + "/raw/aint_that_easy";
                    String[] title = {"Ladies","D'Angelo"};
                    String[] text = {"Awesome", "Aint That Easy"};
                    String[] ticker = {"Ladies-Awesome", "D'Angelo-Aint That Easy"};
                    String [] favSongs = {song1, song2, song3};
                    if (isNotificActive) {
                       stopNotification();
                    } else {
                        Toast.makeText(getApplicationContext(), "Not Active", Toast.LENGTH_LONG).show();
                    }


                    currentSongIndex = (currentSongIndex + 1)% favSongs.length;
                    Log.i("FLAG", "Index= " +currentSongIndex);
                    if (currentSongIndex > 0) {
                        try {
                            mPlayer.reset();
                            mPlayer.setDataSource(getApplicationContext(), Uri.parse(favSongs[currentSongIndex]));
                            mPlayer.prepareAsync();




                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("FLAG", "Songs Done");
                        Toast.makeText(getApplicationContext(),"Songs Done", Toast.LENGTH_LONG).show();
                    }
                    for (int i=0; i<title.length;i++) {
                        Bundle args = new Bundle();
                        showNotification(title[i], text[i], ticker[i]);
                        break;
                    }

                }
            });

    }

    public void onStop() {
        mPlayer.stop();
        mPrepared = false;
        mPlayer.reset();
        mPlayer.release();
        stopNotification();
    }


    public void onRE() {
        if (isRepeat) {
            mPlayer.seekTo(0);
            //isRepeat = false;
        } else {
            int currentPosition = mPlayer.getCurrentPosition();
            if (currentPosition - seekBackwardTime >=0) {
                mPlayer.seekTo(currentPosition - seekBackwardTime);
            }else {
                mPlayer.seekTo(0);
            }
        }

    }

    public void onPause() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        } else {
            mPlayer.pause();
        }

    }

    public void onFF() {
        if (isRepeat) {
            mPlayer.seekTo(mPlayer.getDuration());
            isRepeat = false;
        } else {
            int currentPosition = mPlayer.getCurrentPosition();
            if (currentPosition + seekForwardTime <= mPlayer.getDuration()) {
                mPlayer.seekTo(currentPosition + seekForwardTime);
            } else {
                mPlayer.seekTo(mPlayer.getDuration());
            }
        }
    }

    public void onLoop() {
        if (isRepeat) {
            isRepeat = false;
            mPlayer.setLooping(false);
            Toast.makeText(getApplicationContext(), "Loop is Off", Toast.LENGTH_LONG).show();
        } else {
            isRepeat = true;
            Toast.makeText(getApplicationContext(), "Loop is On", Toast.LENGTH_LONG).show();
            mPlayer.setLooping(true);
        }

    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        //mHandler.removeCallbacks(sendUpdatesToUI);
        mPlayer.release();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {


        Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    public class BoundServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Service Bound", Toast.LENGTH_SHORT).show();
        return mBinder;
    }


    public void showNotification(String cTitle, String cText, String ticker) {

        // Builds a notification
        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(cTitle)
                .setContentText(cText)
                .setTicker(ticker)
                .setSmallIcon(R.drawable.music_woman);

        // Define that we have the intention of opening MoreInfoNotification
        Intent moreInfoIntent = new Intent(this, MusicService.class);

        // Used to stack tasks across activites so we go to the proper place when back is clicked
        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(getApplicationContext());

        // Add all parents of this activity to the stack
        tStackBuilder.addParentStack(MainActivity.class);

        // Add our new Intent to the stack
        tStackBuilder.addNextIntent(moreInfoIntent);

        // Define an Intent and an action to perform with it by another application
        // FLAG_UPDATE_CURRENT : If the intent exists keep it but update it if needed
        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Defines the Intent to fire when the notification is clicked
        notificBuilder.setContentIntent(pendingIntent);

        // Gets a NotificationManager which is used to notify the user of the background event
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Post the notification
        notificationManager.notify(notifyID, notificBuilder.build());

        // Used so that we can't stop a notification that has already been stopped
        isNotificActive = true;


    }


    public void stopNotification() {

        // If the notification is still active close it
        if(isNotificActive) {
            notificationManager.cancel(notifyID);
        }

    }
}
