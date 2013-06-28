package com.leo.soundmixer;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	boolean isRecording = false;
	static final String TAG="AudioMixer";
	static final int freq = 44100;
	@SuppressWarnings("deprecation")
	static final int channelConfigureation = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int recBuffer, playBuffer;
	AudioRecord ar;
	AudioTrack at;
	
	private void startRecord() {
		isRecording = true;
		
		Thread t = new Thread() {
			public void run() {
				try {
					byte[] buffer = new byte[recBuffer];
					ar.startRecording();
					at.play();
					
					while(isRecording) {
						int bufferresult = ar.read(buffer, 0, recBuffer);
						
						byte[] tmpBuffer = new byte[bufferresult];
						System.arraycopy(buffer, 0, tmpBuffer, 0, bufferresult);
						at.write(tmpBuffer, 0, tmpBuffer.length);
					}
					at.stop();
					ar.stop();
				} catch (Throwable t) {
					Log.i(TAG, "Error -1");
				}
			}
		};
		t.start();
	}
	
	private void stopRecord() {
		isRecording = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String title = getString(R.string.title);
		
		setTitle(title);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		recBuffer = AudioRecord.getMinBufferSize(freq, channelConfigureation, audioEncoding);
		playBuffer = AudioTrack.getMinBufferSize(freq, channelConfigureation, audioEncoding);
		
		ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 
				freq, channelConfigureation, audioEncoding, recBuffer);
		
		at = new AudioTrack(AudioManager.STREAM_MUSIC, 
				freq, channelConfigureation, audioEncoding, playBuffer, AudioTrack.MODE_STREAM);
		
		Button record = (Button) findViewById(R.id.button1);
		record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startRecord();
				findViewById(R.id.button1).setEnabled(false);
				findViewById(R.id.button2).setEnabled(true);
			}
		});
		
		Button stop = (Button) findViewById(R.id.button2);
		stop.setEnabled(false);
		stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopRecord();
				findViewById(R.id.button1).setEnabled(true);
				findViewById(R.id.button2).setEnabled(false);
			}
		});
		
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/

}
