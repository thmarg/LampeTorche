package tm.android.lampetorche;

import java.util.Hashtable;

public class Morse {
    private final static int pointDelay=250;

    private final static int betweenPointOrTraitDelay =pointDelay;
    private final static int traitDelay=3*pointDelay;
    public final static int betweenLetterDelay=3*pointDelay;
    private final static int betweenWordDelay=7*pointDelay;

    private final static int[] masks={1,2,4,8,16,32,64,128};
    private final static Hashtable<String, Byte> alphabet = new Hashtable<String,Byte>();
    // code morse représenté par 0 pour point et 1 pour trait plus 1 extra bit à 1 marquant le debut de la sequence.
    // S ...    1000 soit 8 en décimal.
    // O _ _ _  1111 soit 15 en décimal ...
    // L ._..   10100 soit 20 en décimal
    // Seul S et O sont utilisés pour le moment.
    static {
        alphabet.put("S",Byte.valueOf("8"));
        alphabet.put("O",Byte.valueOf("15"));

    }
    private static boolean stop = false;// stop propage message si pas de renderer.
    private static boolean firstPass=true; // SILENCE forcé pendant 500ms au démarrage sinon premier point ou trait non visible

    public static void stop(){
        stop=true;
        firstPass=true;
    }
    public static void unstop(){
        stop=false;
    }
    private   static void doLetter(String s,MorseAction morseAction) throws InterruptedException {
        Byte b= alphabet.get(s);

        if (b!=null){
            int h=0,bv=b,i=8;
            while(h==0){
                 i--;
                h=bv&masks[i];
            }
            i--;
            while(i>-1) {
               if (stop)
                   return;
                h=bv&masks[i];
                morseAction.doAction(MorseAction.morseAction.LOUD);
                if (h==0)
                    Thread.sleep(pointDelay);
                else
                    Thread.sleep(traitDelay);
                morseAction.doAction(MorseAction.morseAction.SILENCE);
                Thread.sleep(betweenPointOrTraitDelay);

                i--;
            }
        }
    }

    private static void doLetter(byte b,MorseAction action) throws  InterruptedException{
        doLetter(new String(new byte[]{b}),action);
    }

    public static void doWord(String s,MorseAction action) throws InterruptedException{
        if (firstPass){
                action.doAction(MorseAction.morseAction.SILENCE);
                Thread.sleep(500);
                firstPass=false;
        }
       for (byte b: s.getBytes()){
           if (stop)
               return;
           doLetter(b,action);
           Thread.sleep(betweenLetterDelay);
       }
        Thread.sleep(betweenWordDelay);
    }
}
