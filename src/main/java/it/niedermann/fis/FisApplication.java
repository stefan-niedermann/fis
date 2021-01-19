package it.niedermann.fis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

@SpringBootApplication
public class FisApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Console console = System.console();

    public static void main(String[] args) {
        final Collection<String> mutableArgs = new ArrayList<>(Arrays.asList(args));

        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith("--ftp.user="))) {
            System.out.print("FTP username: ");
            mutableArgs.add("--ftp.user=" + scanner.next());
        }

        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith("--ftp.password="))) {
            System.out.print("FTP password: ");
            mutableArgs.add("--ftp.user=" + nextSecret());
        }

        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith("--weather.key="))) {
            System.out.print("Weather API Key: ");
            mutableArgs.add("--weather.key=" + nextSecret());
        }

        SpringApplication.run(FisApplication.class, mutableArgs.toArray(String[]::new));
    }

    /**
     * Tries to hide the entered value from the {@link Console}.
     * In case the {@link System#console()} doesn't support this, we will fall back to a visible value.
     */
    private static String nextSecret() {
        if (console == null) {
            return scanner.next();
        } else {
            return Arrays.toString(console.readPassword());
        }
    }
}
