package fxmediaplayer.control;
import fxmediaplayer.FXMediaPlayerControlInterface;
import fxmediaplayer.FXMediaPlayerInterface;
import javafx.scene.control.TabPane;
import javafx.scene.media.MediaPlayer;
public class MediaPlayerTabControl implements FXMediaPlayerControlInterface {
private FXMediaPlayerInterface FXMediaPlayer = null;
private TabPane tabControl = null;
private MediaPlayerControlTab controlTab = null;
private MediaPlayerSpectrumTab spectrumTab = null;
private MediaPlayerEqualizerTab equalizerTab = null;
private MediaPlayerEffectsTab effectsTab = null;
private MediaPlayerMarkersTab markersTab = null;
private MediaPlayerPlayListTab playListTab = null;
public MediaPlayerTabControl(FXMediaPlayerInterface FXMediaPlayer) {
this.FXMediaPlayer = FXMediaPlayer;
}
public TabPane getTabControl() {
if (tabControl == null) {
tabControl = new TabPane();
tabControl.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
tabControl.setPrefHeight(200);
controlTab = new MediaPlayerControlTab(FXMediaPlayer);
spectrumTab = new MediaPlayerSpectrumTab(FXMediaPlayer);
equalizerTab = new MediaPlayerEqualizerTab(FXMediaPlayer);
effectsTab = new MediaPlayerEffectsTab(FXMediaPlayer);
markersTab = new MediaPlayerMarkersTab(FXMediaPlayer);
playListTab = new MediaPlayerPlayListTab(FXMediaPlayer);
tabControl.getTabs().add(controlTab.getControlTab());
tabControl.getTabs().add(spectrumTab.getSpectrumTab());
tabControl.getTabs().add(equalizerTab.getEqualizerTab());
tabControl.getTabs().add(effectsTab.getColorAdjustTab());
tabControl.getTabs().add(markersTab.getMarkersTab());
tabControl.getTabs().add(playListTab.getPlayListTab());
}
return tabControl;
}
@Override
public void onMediaPlayerChanged(MediaPlayer oldMediaPlayer) {
controlTab.onMediaPlayerChanged(oldMediaPlayer);
spectrumTab.onMediaPlayerChanged(oldMediaPlayer);
equalizerTab.onMediaPlayerChanged(oldMediaPlayer);
effectsTab.onMediaPlayerChanged(oldMediaPlayer);
markersTab.onMediaPlayerChanged(oldMediaPlayer);
}
}
