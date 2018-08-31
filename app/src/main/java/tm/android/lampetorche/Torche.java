/*
 *  Torche
 *
 *  Copyright (c) 2014-2018 Thierry Margenstern under MIT license
 *  http://opensource.org/licenses/MIT
 */


package tm.android.lampetorche;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is the main activity of the application Lampe Torche (flashlight).
 */
public class Torche extends Activity {
    private CameraManager cameraManager;

    private static boolean modeSOS = false;
    private MorseRenderer morseRenderer;
    private List<String> camsWithFlash;
    private HashMap<String, Boolean> flashAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainactivity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initCamera();
    }

    @Override
    protected void onDestroy() {
        // if needed turn flash of
        try {
            turnFlashOn(false);
        } catch (CameraAccessException e) {
            // nothing to do
        }
        // stop the sosworker.
        SosWorker.Instance().stop();

        super.onDestroy();
    }


    private void initCamera() {
        camsWithFlash = new ArrayList<>(2);
        flashAvailable = new HashMap<>();
        try {
            cameraManager = getBaseContext().getSystemService(CameraManager.class);
            String[] camIds = cameraManager.getCameraIdList();
            for (String id : camIds)
                if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                        && cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    camsWithFlash.add(id);
                    flashAvailable.put(id, true);
                    cameraManager.registerTorchCallback(new TorcheCallBack(), null);
                }

            morseRenderer = new MorseRd();

            // start the sosWorker.
            SosWorker.Instance().execute();
        } catch (Exception e) {
            Toast t = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
            t.show();

        }
    }

    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.tgl_btn_flash:
                    tgl_btn_flash_onClick(view);
                    break;
                case R.id.btn_screen:
                    btn_screen_onClick();
                    break;
                case R.id.chk_box_sos:
                    chk_box_sos_onClick(view);
            }
        } catch (CameraAccessException e) {
            Toast t = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void tgl_btn_flash_onClick(View view) throws CameraAccessException {
        ToggleButton btnFlash = (ToggleButton) view;
        if (!hasFlashAvailable()) {
            Toast t = Toast.makeText(getApplicationContext(), R.string.no_flash, Toast.LENGTH_LONG);
            t.show();
            btnFlash.setChecked(false);
            return;
        }

        if (btnFlash.isChecked()) {
            if (isModeSOS()) {
                startFlashSos();
            } else {
                turnFlashOn(true);
            }
        } else {
            if (isModeSOS())
                stopFlashSos();
            turnFlashOn(false);
        }
    }

    private void turnFlashOn(boolean flashOn) throws CameraAccessException {
        for (String id : camsWithFlash)
            if (flashAvailable.get(id))
                cameraManager.setTorchMode(id, flashOn);
    }

    private void startFlashSos() {
        SosWorker.Instance().addRenderer(morseRenderer);
    }

    private void stopFlashSos() {
        SosWorker.Instance().removeRenderer(morseRenderer);
    }


    private void btn_screen_onClick() {
        Intent intent = new Intent(getApplicationContext(), ScreenActivity.class);
        startActivity(intent);

    }

    private boolean hasFlashAvailable() {
        boolean result = true;
        for (Boolean b : flashAvailable.values())
            result &= b;
        return result;
    }

    private void chk_box_sos_onClick(View view) {
        CheckBox ck = (CheckBox) view;
        setModeSOS(ck.isChecked());
        if (isModeSOS())
            startFlashSos();
        else
            stopFlashSos();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        return true;
    }


    static boolean isModeSOS() {
        return modeSOS;
    }


    private static void setModeSOS(boolean modeSOS) {
        Torche.modeSOS = modeSOS;
    }


    /**
     * render morse action, this could have been call from a simple thread
     * because the flash is not part of the UI and is then thread safe.
     * But for uniformity behavior with ScreenActivity rendering it fallow the
     * same pattern.
     */
    private class MorseRd implements MorseRenderer {

        @Override
        public void render(MorseAction.morseAction morseAction) {
            try {
                switch (morseAction) {
                    case LOUD:
                        turnFlashOn(true);
                        break;
                    case SILENCE:
                        turnFlashOn(false);
                        break;
                }
            } catch (CameraAccessException e) {
                Toast t = Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
                t.show();
            }
        }
    }

    private class TorcheCallBack extends CameraManager.TorchCallback {
        @Override
        public void onTorchModeUnavailable(@NonNull String cameraId) {
            super.onTorchModeUnavailable(cameraId);
            flashAvailable.put(cameraId, false);
        }

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            super.onTorchModeChanged(cameraId, enabled);
            ((ToggleButton) findViewById(R.id.tgl_btn_flash)).setChecked(enabled);
        }
    }
}
