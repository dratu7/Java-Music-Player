package main;

import gui.Gui;
import musicPlayer.PlayerStarter;

public class CliOptions {
    boolean directStart = false;
    boolean starterIsSet = false;
    Class<? extends PlayerStarter> playerStarer;

    public CliOptions(String[] args) throws UnknownCliOption {
        for (int i = 0; i <= args.length - 2; i++) {

            if (args[i].equals("-p")) {
                directStart = true;
            }else if (!starterIsSet) {
                if (args[i].equals("-t")) {
                    starterIsSet = true;
                    playerStarer = tui.TUI.class;
                }else if (args[i].equals("-g")) {
                    starterIsSet = true;
                    playerStarer = Gui.class;
                }else {
                    throw new UnknownCliOption(args[i]);
                }
            }else {
                throw new UnknownCliOption(args[i]);
            }
        }

        if (!starterIsSet)
            throw new UnknownCliOption("");
    }
}
