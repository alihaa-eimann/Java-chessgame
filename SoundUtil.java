import java.awt.Toolkit;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * Generates tiny synthesized sound effects on the fly (no external audio
 * files needed) so the project stays self-contained. Falls back to a
 * plain system beep if the audio system is unavailable.
 */
class SoundUtil {

    static void playMove() {
        playTone(660, 90, 0.25);
    }

    static void playCapture() {
        playTone(300, 150, 0.35);
    }

    static void playGameOver() {
        playTone(200, 450, 0.35);
    }

    static void playTimeUp() {
        playTone(150, 600, 0.4);
    }

    private static void playTone(final int freqHz, final int durationMs, final double volume) {
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                int numSamples = (int) (durationMs * sampleRate / 1000);
                byte[] buffer = new byte[numSamples];
                for (int i = 0; i < numSamples; i++) {
                    double angle = 2.0 * Math.PI * i * freqHz / sampleRate;
                    double envelope = Math.exp(-3.0 * i / numSamples); // fade out
                    buffer[i] = (byte) (Math.sin(angle) * 110 * volume * envelope);
                }
                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, true);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format, buffer.length);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();
            } catch (Exception e) {
                Toolkit.getDefaultToolkit().beep();
            }
        }).start();
    }
}
