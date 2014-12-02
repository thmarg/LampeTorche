/**
 *  MorseRenderer
 *
 *  Copyright (c) 2014 Thierry Margenstern under MIT license
 *  http://opensource.org/licenses/MIT
 */
package tm.android.lampetorche;

/**
 * Render a morse action ( a dot or a dash)
 * @see tm.android.lampetorche.Torche
 * @see tm.android.lampetorche.ScreenActivity
 */
public interface MorseRenderer {
    void render(MorseAction.morseAction morseAction);
}
