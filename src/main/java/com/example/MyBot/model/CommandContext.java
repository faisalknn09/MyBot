package com.example.MyBot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * Why a context object? Instead of passing 5 parameters through every method call, wrap them in one object.
 * Cleaner code, and you can add fields later (like timestamp, permissions) without changing all method signatures.
 * CommandContext - A snapshot of everything about a command invocation.
 *
 * Think of it as a "request object" — all the info a service needs
 * to process a command in one place.
 *
 * @Builder  = Lombok gives us CommandContext.builder().commandName(...).build()
 * @Getter   = Lombok generates all getters
 * @ToString = Lombok generates toString() for logging
 *
 * WHY immutable (no setters)?
 * A command context never changes once parsed. Immutable objects are
 * thread-safe by nature — no synchronization needed in async processing.
 */
@Getter
@Builder
@ToString
public class CommandContext {

    /** The command name, e.g. "hello" or "ask" */
    private final String commandName;

    /** Arguments after the command name, e.g. "what is Java?" */
    private final String args;

    /** The Discord username of the person who sent the command */
    private final String authorName;

    /** The Discord user ID (unique, unlike names which can be duplicated) */
    private final String authorId;

    /** The channel to reply to (never null) */
    private final TextChannel channel;

    /** The server (null if command came from a DM) */
    private final Guild guild;

    /** Convenience method: is this command from a guild or a DM? */
    public boolean isFromGuild() {
        return guild != null;
    }
}
