package tm.android.lampetorche;
import android.view.*;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.ToggleButton;



public class Torche extends Activity {
	private Camera camera;
	private boolean flashEnabled=false;

	private static boolean modeSOS=false;

	private MorseRenderer morseRenderer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainactivity);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		try {
			camera = Camera.open(0);
		} catch (RuntimeException e) {
			Toast t = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
			t.show();

		}

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
		findViewById(R.id.tgl_btn_flash).setVisibility(flashEnabled ? View.VISIBLE : View.INVISIBLE);
		morseRenderer=new MorseRd();

		SosWorker.Instance().execute();
	}

	/*
    protected void onStart() {
        super.onStart();
         

    }

	@Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

    }

	
	@Override
	protected void onPause() {
		// Another activity is taking focus (this activity is about to be "paused").
		super.onPause();

	}
	
	@Override
    protected void onStop() {
		super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }*/

	@Override
	protected void onDestroy() {
		if (flashEnabled) {
			turnFlashOff();
			camera.release();
		}
		SosWorker.Instance().stop();

		super.onDestroy();
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
	}

	private void turnFlashOn(){
			Parameters cmParams = camera.getParameters();
			cmParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(cmParams);
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
