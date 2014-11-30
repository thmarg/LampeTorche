package tm.android.lampetorche;


import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.os.Bundle;

public class ScreenActivity extends Activity  {

	private MorseRenderer morseRenderer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screenlayout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON+WindowManager.LayoutParams.FLAG_FULLSCREEN);
		morseRenderer = new MorseRd();

	}

	
	
	
	/*@Override
    protected void onStart() {
        super.onStart();
   }*/

	@Override
    protected void onResume() {
        super.onResume();
		getWindow().getAttributes().screenBrightness=1;
		if (Torche.isModeSOS())
			SosWorker.Instance().addRenderer(morseRenderer);
		 else
			findViewById(R.id.llayout).setBackgroundColor(Color.WHITE);

    }

	/*
	@Override
	protected void onPause() {
		// Another activity is taking focus (this activity is about to be "paused").
		super.onPause();
	}
	
	@Override
    protected void onStop() {
		super.onStop();
		
			
    }*/


	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Torche.isModeSOS())
			SosWorker.Instance().removeRenderer(morseRenderer);

	}


	private class MorseRd implements MorseRenderer {


		@Override
		public void render(MorseAction.morseAction morseAction) {
			switch (morseAction) {
				case LOUD:
					findViewById(R.id.llayout).setBackgroundColor(Color.WHITE);
					break;
				case SILENCE:
					findViewById(R.id.llayout).setBackgroundColor(Color.BLACK);
			}
		}
	}
}
