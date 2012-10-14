/*
 * JAC Radio online radio streamer (jacradio.com.au)
 * Copyright (C) 2012 Christopher Jenkins
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jinkle.jac.radio;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class JACRadio extends Activity implements OnClickListener {
	
	private Button playButton;
	private Button stopButton;
	private MediaPlayer mPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // prevent multiple audio streams from being launched when reopening app
        if (!isTaskRoot()) {
        	  final Intent intent = getIntent();
        	  final String intentAction = intent.getAction(); 
        	  if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
        	      finish();
        	      return;       
        	  }
        }
        
        setContentView(R.layout.activity_main);
        
        jacPlayerUI();
        jacPlayerInitialize();
        startPlaying();
    }

    public void jacPlayerUI() {
    	playButton = (Button) findViewById(R.id.playButton);
    	playButton.setOnClickListener(this);
    	
    	stopButton = (Button) findViewById(R.id.stopButton);
    	stopButton.setEnabled(false);
    	stopButton.setOnClickListener(this);
    }
    
    public void onClick(View view) {
    	if (view == playButton) {
    		startPlaying();
    	} else if (view == stopButton) {
    		stopPlaying();
    	}
    }
    
    private void startPlaying() {
    	stopButton.setEnabled(true);
    	playButton.setEnabled(false);
    	
    	mPlayer.prepareAsync();
    	
    	mPlayer.setOnPreparedListener(new OnPreparedListener() {
    		public void onPrepared(MediaPlayer mp) {
    			mPlayer.start();
    		}
    	});
    }
    
    private void stopPlaying() {
    	if (mPlayer.isPlaying()) {
    		mPlayer.stop();
    		mPlayer.release();
    		jacPlayerInitialize();
    	}
    	
    	stopButton.setEnabled(false);
    	playButton.setEnabled(true);
    }
    
    private void jacPlayerInitialize() {
    	mPlayer = new MediaPlayer();
    	
    	try {
    		mPlayer.setDataSource("http://sbs-stream.sbs.uq.edu.au:8000/jacradio.mp3");
    	} catch (IllegalArgumentException e) {
    			e.printStackTrace();
        } catch (IllegalStateException e) {
            	e.printStackTrace();
        } catch (IOException e) {
            	e.printStackTrace();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_exit:
            	System.exit(0);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onBackPressed() {
    	// exit on back button until I figure out running in notification bar
    	System.exit(0);
    }
}