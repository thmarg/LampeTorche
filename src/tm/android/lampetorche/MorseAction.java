/**
 *  MorseAction
 *
 *  Copyright (c) 2014 Thierry Margenstern under MIT license
 *  http://opensource.org/licenses/MIT
 */
package tm.android.lampetorche;

/**
 * To be notified of morse action (dot or dash)
 * @see tm.android.lampetorche.Morse
 * @see tm.android.lampetorche.SosWorker
 */
public interface MorseAction {
    enum morseAction {LOUD,SILENCE}
    void doAction(morseAction morseAction) ;



}
