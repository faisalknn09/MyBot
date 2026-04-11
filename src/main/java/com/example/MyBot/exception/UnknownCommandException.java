package com.example.MyBot.exception;

/**
 * UnknownCommandException - User typed "!xyz" which doesn't exist.
 *
 * Having a specific exception lets CommandService handle this
 * differently from, say, a network failure.
 * Unknown command -> show "try !hello or !ask" message
 * Network failure -> show "something went wrong" message
 */
public class UnknownCommandException extends BotException {

    private final String attemptedCommand;

    public UnknownCommandException(String command) {
        super("Unknown command: " + command);
        this.attemptedCommand = command;
    }

    public String getAttemptedCommand() { return attemptedCommand; }
}