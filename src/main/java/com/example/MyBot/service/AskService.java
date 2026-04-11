package com.example.MyBot.service;

import com.example.MyBot.exception.BotException;
import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * AskService - Handles the !ask <question> command.
 *
 * Currently: returns dummy responses from a pre-defined list.
 * Future: replace getAiResponse() with an OpenAI / Anthropic API call.
 *
 * The rest of the code (listener, config, command service) stays exactly the same.
 * That's the power of clean architecture!
 */
@Service
public class AskService {

    private static final Logger log = LoggerFactory.getLogger(AskService.class);
    private static final Random random = new Random();

    // Dummy responses for now — replace with real AI later
    private static final List<String> DUMMY_RESPONSES = List.of(
            "Great question! I'm still learning, but I'll get smarter soon! 🧠",
            "Interesting! The answer might be 42. 🌌",
            "I'm currently just a demo, but soon I'll be powered by real AI! 🤖",
            "That's a deep question. Let me think... just kidding, I'm a dummy response! 😄",
            "I don't know yet, but connect me to an AI API and I will!"
    );

    public void execute(CommandContext context) {
        String question = context.getArgs().trim();

        // Validate input — user must provide a question
        if (question.isEmpty()) {
            context.getChannel().sendMessage(
                    "❓ Please provide a question! Usage: `!ask <your question>`"
            ).queue();
            return;
        }

        log.info("🤔 Processing question from [{}]: {}", context.getAuthorName(), question);

        // Show typing indicator while "processing" — good UX!
        context.getChannel().sendTyping().queue();

        try {
            String answer = getAiResponse(question);

            String response = String.format(
                    "**%s** asked: *%s*\n\n🤖 **Answer:** %s",
                    context.getAuthorName(),
                    question,
                    answer
            );

            context.getChannel().sendMessage(response).queue(
                    ok  -> log.debug("✅ Ask response sent"),
                    err -> log.error("❌ Failed to send ask response", err)
            );

        } catch (Exception e) {
            log.error("Error getting AI response", e);
            throw new BotException("Failed to get AI response", e);
        }
    }

    /**
     * This is where you plug in your AI provider.
     *
     * TO ADD OPENAI: Replace this with:
     *   return openAiClient.chat(question);
     *
     * TO ADD ANTHROPIC: Replace this with:
     *   return anthropicClient.messages(question);
     *
     * Everything above this method stays the same!
     */
    private String getAiResponse(String question) {
        // Simulate thinking time (remove when using real AI)
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        return DUMMY_RESPONSES.get(random.nextInt(DUMMY_RESPONSES.size()));
    }
}

//!ask logic (AI-ready)