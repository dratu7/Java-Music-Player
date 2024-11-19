package TUI;

import MusicPlayer.MusicPlayer;

import MusicPlayer.Types.Song;

import TUI.Menus.MenuManager;
import TUI.Menus.SongMenu;

import TUI.Terminal.TerminalColor;
import TUI.Terminal.TerminalControl;
import TUI.Terminal.TerminalLock;
import TUI.Terminal.TerminalPosition;

import java.util.Random;

public class TUI {
    volatile MusicPlayer musicPlayer;
    TerminalLock termLock;
    MenuManager menuMgr;

    public TUI(int maxLoadSongs) {
        musicPlayer = new MusicPlayer(maxLoadSongs);
        termLock = new TerminalLock();
    }

    public void start(String dirPath) {

        if (!setDir(dirPath)) return;

        // prepare Terminal
        TerminalControl.clear();

        // Create the Menu Manager
        // The Menu Manager will create the Menus(Home and Song)
        menuMgr = new MenuManager(
                new TerminalPosition(1, 3),
                musicPlayer,
                termLock,
                this
        );

        // create the song info UI
        TUISongDisplay songUiUpdater = new TUISongDisplay(
                Thread.currentThread(),
                musicPlayer,
                termLock
        );

        // start the Menus and the song Info ui
        songUiUpdater.start();
        menuMgr.start();

        // Menu Manager returning mens that the Home Menu is closed
        // therefor we can now end the program

        songUiUpdater.interrupt(); // exit the Program
    }

    public boolean setDir(String dirPath) {
        if (musicPlayer.useDir(dirPath)) {
            System.out.println(dirPath + "Directory Loaded!");
            return true;
        } else {
            System.out.println("cant load Directory: " + dirPath);
            return false;
        }
    }

    public boolean playSong(int SongId) {
        System.out.println("Loading Song ID " + SongId + " ...");

        // des hier kann normal nicht gesehen werden
        // ist dafür da fals was schif geht
        int loadResold = musicPlayer.loadSong(SongId);
        if (loadResold == -1) {
            System.out.println(TerminalColor.RED + "cannot load Song  :(" + TerminalColor.RESET);
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

    public void startMixPlay() {
        Song[] songs = musicPlayer.getSongs();
        SongMenu songMenu = menuMgr.getSongMenu();

        Random rng = new Random();
        int status;
        do {
            playSong(rng.nextInt(0, songs.length));

            songMenu.clear();
            status = songMenu.start();
        } while(status != 0);
    }
}
