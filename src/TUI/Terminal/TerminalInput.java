package TUI.Terminal;

import TUI.TimeStamp;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class TerminalInput {
    Scanner scanner;
    TerminalHelper terminalHelper;

    public TerminalInput(TerminalLock termLock) {
        scanner = new Scanner(System.in).useLocale(Locale.ENGLISH);
        terminalHelper = new TerminalHelper(termLock);
    }

    public String getString(String prompt) throws IOException, InterruptedException {
        terminalHelper.savePrint(prompt);
        waitForStuff();
        return scanner.next();
    }

    public int getInt(String prompt) throws IOException, InterruptedException {
        terminalHelper.savePrint(prompt);
        waitForStuff();
        return scanner.nextInt();
    }

    public TimeStamp getTimeStamp() throws IOException, InterruptedException {
        int minute = getInt("minute? ");
        int second = getInt("second? ");

        return new TimeStamp(minute, second);
    }

    private void waitForStuff() throws IOException, InterruptedException {
        while (System.in.available() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException _) {
                throw new InterruptedException("Interrupted while reading input");
            }
        }
    }
}
