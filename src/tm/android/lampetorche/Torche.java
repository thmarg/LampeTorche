/**
 *  Torche
 *
 *  Copyright (c) 2014 Thierry Margenstern under MIT license
 *  http://opensource.org/licenses/MIT
 */


package tm.android.lampetorche;
import android.view.*;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.widget.*;

/**
 *  This is the main activity of the application Lampe Torche (flashlight).
 */
public class Torche extends Activity {
	private Camera camera;
	private boolean flashEnabled=false;

	private static boolean modeSOS=false;
	private static boolean flashOn=false;
	private MorseRenderer morseRenderer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainactivity);


		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		initCamera();

		if (camera != null && camera.getParameters().getFlashMode() != null) {

			try { // getFlashMode return FLASH_MODE_OFF instead of null on some device without flash.
				 // force use to get exception if no flash; until api doc is correctly implemented.
				Parameters parms = camera.getParameters();
				parms.setFlashMode(Parameters.FLASH_MODE_ON);
				camera.setParameters(parms);

				parms.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(parms);

				flashEnabled = true;
			} catch (Exception e) {
				flashEnabled = false;
			}

		}
		/* if no flash remove the flash toggle button so the other component stay in the middle off the screen */
		if (!flashEnabled){
			LinearLayout layout = (LinearLayout)findViewById(R.id.layoutWrap);
			layout.removeView(findViewById(R.id.tgl_btn_flash));
		}

		morseRenderer=new MorseRd();

		// start the sosWorker.
		SosWorker.Instance().execute();
	}


    protected void onStart() {
        super.onStart();
		// must we init the camera again ?
         if (flashEnabled && camera==null)
			 initCamera();

    }

//	@Override
//    protected void onResume() {
//        super.onResume();
//        // The activity has become visible (it is now "resumed").
//
//    }
//
//
//	@Override
//	protected void onPause() {
//		// Another activity is taking focus (this activity is about to be "paused").
//		super.onPause();
//
//	}
	
	@Override
    protected void onStop() {
		super.onStop();
       if (flashEnabled && !flashOn){
		   // we must release the camera
		   camera.release();
		   camera=null;
	   }
    }

	@Override
	protected void onDestroy() {
		// if needed turn flash of and release camera
		if (flashEnabled && flashOn) {
			turnFlashOff();
			camera.release();
		}
		// stop the sosworker.
		SosWorker.Instance().stop();

		super.onDestroy();
	}


	private void initCamera(){
		try {
			camera = Camera.open(0);
		} catch (RuntimeException e) {
			Toast t = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
			t.show();

		}
	}

	public void onClick(View view){
		switch (view.getId()){
			case R.id.tgl_btn_flash :
				tgl_btn_flash_onClick(view);
				break;
			case R.id.btn_screen :
				btn_screen_onClick();
				break;
			case R.id.chk_box_sos :
				chk_box_sos_onClick(view);

		}
	}
	
	public void tgl_btn_flash_onClick(View view){
		ToggleButton btnFlash= (ToggleButton)view;
		if (btnFlash.isChecked()){
			if (isModeSOS()){
				startFlashSos();
			} else {
			turnFlashOn();
			}
		} else {
			if (isModeSOS())
				stopFlashSos();
			turnFlashOff();
		}
		
		
	}

	private void turnFlashOff(){
		Parameters cmParams = camera.getParameters();
		cmParams.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(cmParams);
		if (!modeSOS)
		flashOn=false;
	}

	private void turnFlashOn(){
			Parameters cmParams = camera.getParameters();
			cmParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(cmParams);
			flashOn=true;
	}
	
	private void startFlashSos(){
		SosWorker.Instance().addRenderer(morseRenderer);
	}
	private void stopFlashSos(){
		SosWorker.Instance().removeRenderer(morseRenderer);
	}
	
	
	public void btn_screen_onClick(){
		Intent intent = new Intent(getApplicationContext(), ScreenActivity.class);
		startActivity(intent);
		
	}

	

	public void chk_box_sos_onClick(View view){
		CheckBox ck = (CheckBox)view;
		setModeSOS(ck.isChecked());
		ToggleButton flash = (ToggleButton)findViewById(R.id.tgl_btn_flash);
		if (flashEnabled && flash.isChecked()) {
			if (!isModeSOS()){
				stopFlashSos();
				turnFlashOn();
			} else {
				startFlashSos();
			}
		}
	}
	


	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK)
			finish();
		return true;
	}


	public static boolean isModeSOS() {
		return modeSOS;
	}


	public static void setModeSOS(boolean modeSOS) {
		Torche.modeSOS = modeSOS;
	}


	/**
	 *  render morse action, this could have been call from a simple thread
	 *  because the flash is not part of the UI and is then thread safe.
	 *  But for uniformity behavior with ScreenActivity rendering it fallow the
	 *  same pattern.
	 */
	private class MorseRd implements MorseRenderer {

		@Override
		public void render(MorseAction.morseAction morseAction) {
			switch (morseAction) {
				case LOUD:
					turnFlashOn();
					break;
				case SILENCE:
					turnFlashOff();
					break;
			}
		}
	}


}
