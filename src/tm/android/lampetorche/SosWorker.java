package tm.android.lampetorche;


import android.os.AsyncTask;

import java.util.ArrayList;

public class SosWorker extends AsyncTask<Void,MorseRenderer,Void> implements MorseAction
{
    private static  SosWorker _instance;
    private boolean run =true;
    private ArrayList<MorseRenderer> renderers;
    private morseAction currentAction;

private SosWorker(){
    renderers = new ArrayList<MorseRenderer>(2);
}

    public static SosWorker Instance(){
        if (_instance==null)
            _instance = new SosWorker();

        return _instance;
    }
    @Override
    protected Void doInBackground(Void... params) {

            try {
                while(run) {
                    if (renderers.size() == 0) {
                        Thread.sleep(100);
                        continue;
                    }
                    Morse.doWord("SOS", this);
                }
            } catch (InterruptedException e) {
               // e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onProgressUpdate(MorseRenderer... values) {
            for(MorseRenderer renderer : renderers)
                renderer.render(currentAction);

    }


    public void addRenderer(MorseRenderer renderer){

        renderers.add(renderer);
        Morse.unstop();
    }

    public void removeRenderer(MorseRenderer renderer){
        renderers.remove(renderer);
        if (renderers.size()==0)
            Morse.stop();
    }

    public void stop(){
        run=false;
        cancel(true);
        _instance=null;
    }

    @Override
    public void doAction(morseAction at) {
        currentAction=at;
        publishProgress();
    }
}
