package com.example.MyBot.model;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class CommandContext {

    private final String commandName;
    private final String args;
    private final String authorName;
    private final String authorId;
    private final TextChannel channel;
    private final Guild guild;

    private CommandContext(Builder builder) {
        this.commandName = builder.commandName;
        this.args        = builder.args;
        this.authorName  = builder.authorName;
        this.authorId    = builder.authorId;
        this.channel     = builder.channel;
        this.guild       = builder.guild;
    }

    public String getCommandName()  { return commandName; }
    public String getArgs()         { return args; }
    public String getAuthorName()   { return authorName; }
    public String getAuthorId()     { return authorId; }
    public TextChannel getChannel() { return channel; }
    public Guild getGuild()         { return guild; }
    public boolean isFromGuild()    { return guild != null; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String commandName;
        private String args;
        private String authorName;
        private String authorId;
        private TextChannel channel;
        private Guild guild;

        public Builder commandName(String v) { this.commandName = v; return this; }
        public Builder args(String v)        { this.args = v;        return this; }
        public Builder authorName(String v)  { this.authorName = v;  return this; }
        public Builder authorId(String v)    { this.authorId = v;    return this; }
        public Builder channel(TextChannel v){ this.channel = v;     return this; }
        public Builder guild(Guild v)        { this.guild = v;       return this; }

        public CommandContext build() {
            return new CommandContext(this);
        }
    }
}