package com.example.MyBot.model;

import com.example.MyBot.exception.UnknownCommandException;

/**
 * BotCommand - All commands the bot knows about.
 *
 * Why an enum? Strings are dangerous — typos cause runtime bugs.
 * Using an enum for commands makes invalid command names a compile-time error.
 * Adding a new command? Add it to the enum and the switch in CommandService.
 *
 * The single source of truth for supported commands.
 * Adding a new command = add a case here + handle it in CommandService.
 *
 * This is the "Enum Pattern" for command routing — much safer than
 * comparing raw strings like if (command.equals("hello")).
 */
public enum BotCommand {

    HELLO("hello"),
    ASK("ask"),
    FORGET("forget"),
    WEATHER("weather"),
    JOKE("joke"),
    DEFINE("define"),
    HELP("help");

    // The string the user types (after the prefix)
    private final String commandName;

    BotCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() { return commandName; }

    /**
     * Convert a raw string to BotCommand enum.
     * "hello" -> BotCommand.HELLO
     * "unknown" -> throws UnknownCommandException
     *
     * @throws UnknownCommandException if no matching command found
     */
    public static BotCommand fromString(String input) {
        for (BotCommand cmd : values()) {
            if (cmd.commandName.equalsIgnoreCase(input)) {
                return cmd;
            }
        }
        throw new UnknownCommandException(input);
    }
}

//Enum of commands