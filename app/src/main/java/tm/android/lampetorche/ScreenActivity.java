/*
 *  ScreenActivity
 *
 *  Copyright (c) 2014-2018 Thierry Margenstern under MIT license
 *  http://opensource.org/licenses/MIT
 */
package tm.android.lampetorche;


import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.os.Bundle;

/**
 * This activity make a flash light with the screen:<br>
 *    white and max brightness.
 */
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

	@Override
    protected void onResume() {
        super.onResume();
		getWindow().getAttributes().screenBrightness=1;
		if (Torche.isModeSOS())
			SosWorker.Instance().addRenderer(morseRenderer);
		 else
			findViewById(R.id.llayout).setBackgroundColor(Color.WHITE);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Torche.isModeSOS())
			SosWorker.Instance().removeRenderer(morseRenderer);
	}

	/**
	 * render morse action. Easy here to update the UI.
	 * The render void must be call from onProgressUpdate(...) in an AsyncTask
	 * because this o,Progress... void run on the UI thread, and Android UI is not thread safe.
	 */
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
