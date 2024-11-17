package TUI.Menus;

import MusicPlayer.MusicPlayer;
import MusicPlayer.Types.Song;

import TUI.Terminal.TerminalColor;
import TUI.Terminal.TerminalLock;
import TUI.Terminal.TerminalHelper;
import TUI.Terminal.TerminalPosition;
import TUI.Terminal.TerminalInput;

import java.util.Random;

public class MenuManager {
    MusicPlayer musicPlayer;

    SongMenu songMenu;
    HomeMenu homeMenu;

    public MenuManager(TerminalPosition startPos, MusicPlayer musicPlayer, TerminalLock termLock) {
        this.musicPlayer = musicPlayer;

        TerminalHelper terminalHelper = new TerminalHelper(termLock);
        TerminalInput inFunc = new TerminalInput(termLock);

        songMenu = new SongMenu(startPos, musicPlayer, termLock, terminalHelper, inFunc);
        homeMenu = new HomeMenu(startPos, musicPlayer, termLock, terminalHelper, inFunc, this, songMenu);
    }

    // TODO: wired position in code(think about it and maybe move)
    public boolean startSong(int SongId) {
        System.out.println("Loading Song ID " + SongId + " ...");

        // des hier kann normal nicht gesehen werden
        // ist dafür da fals was schif geht
        int loadResold = musicPlayer.loadSong(SongId);
        if (loadResold == -1) {
            System.out.println( TerminalColor.RED +  "cannot load Song  :(" + TerminalColor.RESET);
            return false;
        }

        System.out.println("Song loaded");
        System.out.println("Start playing Song ...");

        if (!musicPlayer.playSong(SongId)) {
            System.out.println("cannot play Song  :(");
            return false;
        }

        return true;
    }

    // TODO: wired position in code(think about it and maybe move)
    public void startMixPlay() {
        Song[] songs = musicPlayer.getSongs();
        int status;

        do {
            Random rng = new Random();
            startSong(rng.nextInt(0, songs.length));
            songMenu.clear();
            status = songMenu.start();
        } while(status != 0);
    }

    public void start() {
        homeMenu.start();
    }
}
