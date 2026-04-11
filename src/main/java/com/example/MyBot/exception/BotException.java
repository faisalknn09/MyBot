package com.example.MyBot.exception;

/**
 * BotException - Base exception for all bot-related errors.
 *
 * WHY a base exception?
 * - Catch all bot errors with one catch clause
 * - Distinguish bot errors from Java system errors
 * - Subclasses can add specific fields (error codes, command name)
 *
 * Extends RuntimeException (unchecked) — we don't want
 * to force callers to declare "throws BotException" everywhere.
 */
public class BotException extends RuntimeException {

    public BotException(String message) {
        super(message);
    }

    public BotException(String message, Throwable cause) {
        super(message, cause); // Preserves the original stack trace
    }
}
