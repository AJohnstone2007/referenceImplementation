package uk.ac.rhul.cs.csle.art.old.util.audio;

import java.io.IOException;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

public class MusicPlayer {
  private Synthesizer synthesizer;
  private MidiChannel[] channels;
  private int defaultOctave = 5;
  private int defaultVelocity = 50;
  private int bpm;
  private double bps;
  private double beatPeriod;
  private double beatRatio = 0.9;
  private int beatSoundDelay = (int) (1000.0 * beatRatio / bps);
  private int beatSilenceDelay = (int) (1000.0 * (1.0 - beatRatio) / bps);

  public MusicPlayer() {
    try {
      System.out.print(MidiSystem.getMidiDeviceInfo());
      synthesizer = MidiSystem.getSynthesizer();
      synthesizer.open();
      channels = synthesizer.getChannels();
    } catch (Exception e) {
      System.err.println("miniMusicPlayer exception: " + e.getMessage());
      System.exit(1);
    }

    setBeatRatio(0.9);
    setBpm(100);
    setDefaultVelocity(50);
    rest(2);
  }

  public int getDefaultOctave() {
    return defaultOctave;
  }

  public void setDefaultOctave(int defaultOctave) {
    this.defaultOctave = defaultOctave;
  }

  public int getDefaultVelocity() {
    return defaultVelocity;
  }

  public void setDefaultVelocity(int defaultVelocity) {
    this.defaultVelocity = defaultVelocity;
  }

  public int getBpm() {
    return bpm;
  }

  public void setBpm(int bpm) {
    this.bpm = bpm;
    bps = bpm / 60.0;
    beatPeriod = 1000.0 / bps;
    beatSoundDelay = (int) (beatRatio * beatPeriod);
    beatSilenceDelay = (int) ((1.0 - beatRatio) * beatPeriod);
  }

  public void setBeatRatio(double beatRatio) {
    this.beatRatio = beatRatio;
    beatSoundDelay = (int) (beatRatio * beatPeriod);
    beatSilenceDelay = (int) ((1.0 - beatRatio) * beatPeriod);
  }

  public int noteNameToMidiKey(String n, int octave) {
 // @formatter:off
 int key = octave * 12 +
        ( n.equals("C") ? 0
        : n.equals("C#") ? 1
        : n.equals("Db") ? 1
        : n.equals("D") ? 2
        : n.equals("D#") ? 3
        : n.equals("Eb") ? 3
        : n.equals("E") ? 4
        : n.equals("F") ? 5
        : n.equals("F#") ? 6
        : n.equals("Gb") ? 6
        : n.equals("G") ? 7
        : n.equals("G#") ? 8
        : n.equals("Ab") ? 8
        : n.equals("A") ? 9
        : n.equals("A#") ? 10
        : n.equals("Bb") ? 10
        : n.equals("B") ? 11
        : -1);
 // @formatter:on

    if (key < 0 || key > 127) {
      System.err.println("miniMusicPlayer exception: attempt to access out of range MIDI key " + n + octave);
      System.exit(1);
    }
    return key;
  }

  // Silence
  public void rest(int beats) {
    try {
      Thread.sleep((long) (beats * beatPeriod));
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  // Single notes
  public void play(int k) {
    try {
      channels[0].noteOn(k, defaultVelocity);
      Thread.sleep(beatSoundDelay);
      channels[0].noteOn(k, 0);
      Thread.sleep(beatSilenceDelay);
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  public void play(String n) {
    play(noteNameToMidiKey(n, defaultOctave));
  }

  public void play(String n, int octave) {
    play(noteNameToMidiKey(n, octave));
  }

  // Arrays of notes
  public void play(int[] k) {
    try {
      for (int i = 0; i < k.length; i++)
        channels[1].noteOn(k[i], defaultVelocity);
      Thread.sleep(beatSoundDelay);
      for (int i = 0; i < k.length; i++)
        channels[1].noteOn(k[i], 0);
      Thread.sleep(beatSilenceDelay);
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  private void playSequentially(int[] k) {
    try {
      for (int i = 0; i < k.length; i++) {
        channels[i].noteOn(k[i], defaultVelocity);
        Thread.sleep(beatSoundDelay);
        channels[i].noteOn(k[i], 0);
        Thread.sleep(beatSilenceDelay);
      }
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  // Scales
  public void playScale(String n, Scale s) {
    playScale(noteNameToMidiKey(n, defaultOctave), s);
  }

  public void playScale(String n, int octave, Scale s) {
    playScale(noteNameToMidiKey(n, octave), s);
  }

  public void playScale(int k, Scale s) {
    int[] keys;
    switch (s) {
    case CHROMATIC:
      keys = new int[] { k, k + 1, k + 2, k + 3, k + 4, k + 5, k + 6, k + 7, k + 8, k + 9, k + 10, k + 11, k + 12 };
      break;

    case MAJOR: // TTSTTTS
      keys = new int[] { k, k + 2, k + 4, k + 5, k + 7, k + 9, k + 11, k + 12 };
      break;

    case MINOR_NATURAL: // TSTTSTT
      keys = new int[] { k, k + 2, k + 3, k + 5, k + 7, k + 8, k + 10, k + 12 };
      break;
    case MINOR_HARMONIC: // TSTTS3S
      keys = new int[] { k, k + 2, k + 3, k + 5, k + 7, k + 8, k + 11, k + 12 };
      break;
    case MINOR_MELODIC_ASCENDING: // TSTTS3S - harmonic with with sixth sharpened
      keys = new int[] { k, k + 2, k + 3, k + 5, k + 7, k + 9, k + 11, k + 12 };
      break;
    case MINOR_MELODIC_DESCENDING: // TSTTS3S - harmonic with seventh flattened making it the same as the natural minor
      keys = new int[] { k + 12, k + 10, k + 8, k + 7, k + 5, k + 3, k + 2, k };
      break;

    default:
      keys = new int[] { k };
      break;
    }
    playSequentially(keys);
  }

  // Programmed chords
  public void playChord(String n, Chord type) {
    playChord(noteNameToMidiKey(n, defaultOctave), type);
  }

  public void playChord(String n, int octave, Chord type) {
    playChord(noteNameToMidiKey(n, octave), type);
  }

  public void playChord(int k, Chord type) {
    int[] keys;
    switch (type) {
    case MAJOR:
      keys = new int[] { k, k + 4, k + 7 };
      break;
    case MAJOR7:
      keys = new int[] { k, k + 4, k + 7, k + 11 };
      break;
    case MINOR:
      keys = new int[] { k, k + 3, k + 7 };
      break;
    case MINOR7:
      keys = new int[] { k, k + 4, k + 7 };
      break;
    default:
      keys = new int[] { k };
      break;
    }
    play(keys);
  }

  public void close() {
    rest(3);
    synthesizer.close();
  }

  private void tune() {
    int base = 47;
    play(base + 14);
    play(base + 12);
    play(base + 11);
    play(base + 7);
    play(base + 5);
    play(base + 7);
    play(base + 2);
    rest(2);
  }

  private void tuneChordMajor() {
    int base = noteNameToMidiKey("C", 5);
    playChord(base + 14, Chord.MAJOR);
    playChord(base + 12, Chord.MAJOR);
    playChord(base + 11, Chord.MAJOR);
    playChord(base + 7, Chord.MAJOR);
    playChord(base + 5, Chord.MAJOR);
    playChord(base + 7, Chord.MAJOR);
    playChord(base + 2, Chord.MAJOR);
  }

  private void tuneChordMinor() {
    int base = noteNameToMidiKey("C", 5);
    playChord(base + 14, Chord.MINOR);
    playChord(base + 12, Chord.MINOR);
    playChord(base + 11, Chord.MINOR);
    playChord(base + 7, Chord.MINOR);
    playChord(base + 5, Chord.MINOR);
    playChord(base + 7, Chord.MINOR);
    playChord(base + 2, Chord.MINOR);
  }

  // Test example
  public static void main(String[] args) throws IOException {
    System.err.println("miniMusicPlayer test routine");
    MusicPlayer mp = new MusicPlayer();
    mp.testExample();
    // mp.guitarTuner();
  }

  int standardGuitarTuning[] = { noteNameToMidiKey("E", 4), noteNameToMidiKey("B", 3), noteNameToMidiKey("G", 3), noteNameToMidiKey("D", 3),
      noteNameToMidiKey("A", 2), noteNameToMidiKey("E", 2), };

  private void guitarTuner() throws IOException {
    int repeatCount = 3;
    setBpm(50);
    for (int i = standardGuitarTuning.length - 1; i >= 0; i--) {
      while (System.in.available() == 0) {
        play(standardGuitarTuning[i]);
      }
      System.in.read(); // Need to read CR and LF on Windows. Unix amd MacOS will presumably need only one (not tested)
      System.in.read();
    }

    setBpm(100);

    for (int j = 0; j < repeatCount; j++)
      for (int i = standardGuitarTuning.length - 1; i >= 0; i--)
        play(standardGuitarTuning[i]);
  }

  void testExample() {
    playScale("C", Scale.CHROMATIC);
    rest(2);
    String note = "C";
    int octave = 6;
    play(note, octave);
    rest(2);
    playScale("C", Scale.MAJOR);
    rest(2);
    playScale("C", Scale.MINOR_NATURAL);
    rest(2);
    playScale("C", Scale.MINOR_HARMONIC);
    rest(2);
    playScale("C", Scale.MINOR_MELODIC_ASCENDING);
    playScale("C", Scale.MINOR_MELODIC_DESCENDING);
    rest(2);
    playChord("C", Chord.MAJOR);
    rest(2);
    playChord("C", Chord.MINOR);
    rest(2);
    tune();
    rest(2);
    tuneChordMajor();
    rest(2);
    tuneChordMinor();
    rest(2);
    close();
  }
}