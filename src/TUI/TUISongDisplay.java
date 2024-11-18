package TUI;

import MusicPlayer.MusicPlayer;
import MusicPlayer.Types.LoadedSong;

import TUI.Terminal.TerminalControl;
import TUI.Terminal.TerminalLock;
import TUI.Terminal.TerminalPosition;

public class TUISongDisplay extends Thread {
    final Thread menuThread;
    final MusicPlayer musicPlayer;
    final TerminalLock termLock;
    final TerminalPosition END_POS = new TerminalPosition(500, 2);

    public TUISongDisplay(Thread menuThread, MusicPlayer musicPlayer, TerminalLock termLock) {
        this.menuThread = menuThread;
        this.musicPlayer = musicPlayer;
        this.termLock = termLock;
    }

    @Override
    public void run() {
        // define vars for use in while loop
        LoadedSong currentSong;
        String songName;
        long songLength;
        long currentTime;

        boolean runing = true;
        do {
            currentSong = musicPlayer.getCurrentSong();
            if (currentSong == null) {
                printSongInfo("---", "---", "----");
            } else {
                songName = currentSong.getName();
                songLength = currentSong.getMaxTime();
                currentTime = currentSong.getCurrentTime();

                printSongInfo(
                        TimeStamp.format(currentTime),
                        TimeStamp.format(songLength),
                        songName
                );

                // Song Menu will clos and go back to Home Menu when song is done playing
                if(songLength == currentTime) {
                    menuThread.interrupt();
                }
            }

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException _) {
                runing = false; // the program while now be closed
            }
        } while(runing);
    }

    private void printSongInfo(String currentTime, String songLength, String songName) {
        // save cursor position
        termLock.lockTerminal();
        TerminalControl.saveCursorPos();

        // clear old and set cursor to start
        TerminalControl.setCursorPos(END_POS);
        TerminalControl.clearToStart();
        TerminalControl.setCursorPos(TerminalPosition.START);

        // print the info
        System.out.println("Playing: " + songName);
        System.out.println(currentTime + " / " + songLength);

        // reset cursor to before this function
        TerminalControl.loadCursorPos();
        termLock.unlockTerminal();
    }
}
