/*  Morse
 *
 *  Copyright (c) 2014 Thierry Margenstern under MIT license
 *  http://opensource.org/licenses/MIT
 */

package tm.android.lampetorche;

import java.util.Hashtable;

/**  <h6> This is class implement the morse protocole</h6>
 *      <ol>
 *      <li>The dot (.) is the base time duration</li>
 *      <li>A dash (_) has three times the dot duration</li>
 *      <li>The time delay between parts of the same letter is the dot duration</li>
 *      <li>The time delay between letters is three times the dot duration</li>
 *      <li>The time delay between words is seven times the dot duration</li>
 *      </ol>
 *      <br>
 *      <p>
 *      This class is responsible to send dot and dash with the good timing to represent the message send by the caller through the
 *      <code>doWords</code> method. In fact this class tells the caller what action is done (dot or dash) and the caller will then tell its
 *      registered renderer to render the action. A dot or dash can be render by sound, visually with light, by writing on a console,
 *      by a real machine ...</p>
 *      <p>
 *      In this project the "caller" describes above is the class <code>SosWorker</code>. It's a thread (<code>AsyncTask</code>)
 *      implementing the <code>MorseAction</code> interface. It waits for renderer to be added to it then if any he continuously call the <code>doWords("SOS",..)</code> method<br>
 *      This <code>SosWorker</code> is the input, it says witch  message to morse.<br>
 *      We have two renderer, the <code>Torche</code> class rendering with the flash, and the <code>ScreenActivity</code> class rendering on the screen.
 *      </p>
 *      There is little work to do, to do more things with all this.<br>
 *          <ul>
 *              <li> Stroboscope<br>
 *          Add a public void that will do a single point, make a public accessible pointdelay property, add in <code>Torche</code>
 *          a strob mode and that's it, add a check box and a slider on the UI and your done.</li>
 *          <li>Morse complete<br>
 *              Add all the letters and digits, add a void  to decode morse.
 *              Replace <code>SosWorker</code> by any class that will input from keyboard, voice or any thing else.
 *              Make the renderer you want and you can communicate with the morse code.<br>
 *              To go further you can create your on protocol based on dot and dash...
 *              </li>
 *          </ul>
 *
 *
 *
 *
 */
class Morse {
    private final static int pointDelay=250;

    private final static int betweenPointOrTraitDelay =pointDelay;
    private final static int traitDelay=3*pointDelay;
    private final static int betweenLetterDelay=3*pointDelay;
    private final static int betweenWordDelay=7*pointDelay;

    private final static int[] masks={1,2,4,8,16,32,64,128};
    private final static Hashtable<String, Byte> alphabet = new Hashtable<>();
    // code morse représenté par 0 pour point et 1 pour trait plus 1 extra bit à 1 marquant le debut de la sequence.
    // S ...    1000 soit 8 en décimal.
    // O _ _ _  1111 soit 15 en décimal ...
    // L ._..   10100 soit 20 en décimal
    // Seul S et O sont utilisés pour le moment.
    static {
        alphabet.put("S",Byte.valueOf("8"));
        alphabet.put("O",Byte.valueOf("15"));

    }
    private static boolean stop = false;//to  stop  message sending.
    private static boolean firstPass=true; // SILENCE forced for 500ms at start up otherwise first dot or dash may be non visible.
    private static byte blanc = " ".getBytes()[0];
    /**
     * Call this void if no more renderer are available.
     */
    static void stop(){
        stop=true;
        firstPass=true;
    }

    /**
     * Call this void if some renderer is available.
     */
    static void unstop(){
        stop=false;
    }

    /**
     * Transform the letter into its code morse.
     *
     * @param letter
     * @param morseAction
     * @throws InterruptedException
     */
    private   static void doLetter(byte letter,MorseAction morseAction) throws InterruptedException {
        Byte b= alphabet.get(new String(new byte[]{letter}));

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


    /**
     * Tranforme words en code morse.
     * @param words
     * @param action
     * @throws InterruptedException
     */
     static void doWords(String words, MorseAction action) throws InterruptedException{
        if (firstPass){
                action.doAction(MorseAction.morseAction.SILENCE);
                Thread.sleep(500);
                firstPass=false;
        }
       for (byte b: words.getBytes()){
           if (stop)
               return;
           if (b==blanc) {// word separator
               Thread.sleep(betweenWordDelay);
               continue;
           }
           doLetter(b, action);
           Thread.sleep(betweenLetterDelay);
       }
        Thread.sleep(betweenWordDelay);
    }
}
