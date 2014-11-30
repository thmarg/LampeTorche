package tm.android.lampetorche;


public interface MorseAction {
    enum morseAction {LOUD,SILENCE}
    void doAction(morseAction morseAction) ;



}
