/*
package com.example.MyBot.service;

import com.example.MyBot.model.CommandContext;
import org.springframework.stereotype.Service;

@Service
public class ForgetService {

    private final ConversationMemoryService memoryService;

    public ForgetService(ConversationMemoryService memoryService) {
        this.memoryService = memoryService;
    }

    public void execute(CommandContext context) {
        memoryService.clearHistory(context.getAuthorId());
        context.getChannel().sendMessage(
                "🧹 Memory cleared, **" + context.getAuthorName()
                        + "**! I've forgotten our conversation."
        ).queue();
    }
}*/
