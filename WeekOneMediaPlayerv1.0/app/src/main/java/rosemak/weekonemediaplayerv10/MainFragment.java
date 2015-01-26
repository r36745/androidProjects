package rosemak.weekonemediaplayerv10;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by stevierose on 1/25/15.
 */
public class MainFragment extends Fragment implements ServiceConnection{
    public static final String TAG = "MainFragment.TAG";
    private ImageButton reButton, ffButton, pauseButton, playButton, loopButton, shuffleButton, stopButton;
    private SeekBar seekBar;
    private TextView songNameTextView;
    private MusicService mService;
    private Handler mHandler = new Handler();
    boolean mBound = true;
    private Intent intent;
    public static final String BROADCAST_ACTION = "rosemak.weekonemediaplayer";
    Intent seekIntent;
    int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reButton = (ImageButton) getActivity().findViewById(R.id.reButton);
        ffButton = (ImageButton) getActivity().findViewById(R.id.ffButton);
        pauseButton = (ImageButton) getActivity().findViewById(R.id.pauseButton);
        playButton = (ImageButton) getActivity().findViewById(R.id.playButton);
        loopButton = (ImageButton) getActivity().findViewById(R.id.loopButton);
        shuffleButton = (ImageButton) getActivity().findViewById(R.id.shuffleButton);
        stopButton = (ImageButton) getActivity().findViewById(R.id.stopButton);
        intent = new Intent(getActivity(), MusicService.class);
        songNameTextView = (TextView) getActivity().findViewById(R.id.songNameTextView);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().bindService(intent, MainFragment.this, Context.BIND_AUTO_CREATE);
                getActivity().startService(intent);

                    //set default artist name
                    songNameTextView.setText("K. Michelle");



            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(intent);
                getActivity().unbindService(MainFragment.this);
                mService.onStop();
            }
        });

        reButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onRE();
            }
        });

        ffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onFF();
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mService.onLoop();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.onPause();
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        MusicService.BoundServiceBinder binder = (MusicService.BoundServiceBinder)service;
        mService = binder.getService();
        mBound = true;

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        mService = null;
        mBound = false;
    }
}
