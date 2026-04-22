/*
package com.example.MyBot.service;

import com.example.MyBot.exception.BotException;
import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class AskService {

    private static final Logger log = LoggerFactory.getLogger(AskService.class);

    @Value("${openrouter.api.key}")
    private String apiKey;

    private static final String OPENROUTER_URL =
            "https://openrouter.ai/api/v1/chat/completions";

    // Inject our new services
    private final ConversationMemoryService memoryService;
    private final RateLimitService rateLimitService;

    public AskService(ConversationMemoryService memoryService,
                      RateLimitService rateLimitService) {
        this.memoryService   = memoryService;
        this.rateLimitService = rateLimitService;
    }

    public void execute(CommandContext context) {
        String question = context.getArgs().trim();
        String userId   = context.getAuthorId();

        // Empty question check
        if (question.isEmpty()) {
            context.getChannel().sendMessage(
                    "❓ Usage: `!ask <your question>`"
            ).queue();
            return;
        }

        // Rate limit check
        if (!rateLimitService.isAllowed(userId)) {
            long wait = rateLimitService.getWaitTime(userId);
            context.getChannel().sendMessage(
                    "⏳ Slow down **" + context.getAuthorName()
                            + "**! Please wait " + wait + " more second(s)."
            ).queue();
            return;
        }

        log.info("🤔 Question from [{}]: {}", context.getAuthorName(), question);
        context.getChannel().sendTyping().queue();

        try {
            // Save user's question to memory
            memoryService.addMessage(userId, "user", question);

            // Get history for context-aware response
            List<Map<String, String>> history = memoryService.getHistory(userId);

            // Call AI with full history
            String answer = callOpenRouter(history);

            // Save AI response to memory
            memoryService.addMessage(userId, "assistant", answer);

            String response = String.format(
                    "**%s** asked: *%s*\n\n🤖 **AI:** %s",
                    context.getAuthorName(), question, answer
            );

            if (response.length() > 2000) {
                response = response.substring(0, 1997) + "...";
            }

            context.getChannel().sendMessage(response).queue(
                    ok  -> log.debug("✅ Response sent"),
                    err -> log.error("❌ Failed to send", err)
            );

        } catch (Exception e) {
            log.error("Error calling OpenRouter", e);
            throw new BotException("Failed to get AI response", e);
        }
    }

    private String callOpenRouter(List<Map<String, String>> history) throws Exception {

        // Build messages array from history
        StringBuilder messagesJson = new StringBuilder();
        messagesJson.append("[");
        messagesJson.append("{\"role\":\"system\",\"content\":\"You are a helpful Discord bot. Keep answers concise and friendly.\"},");

        for (int i = 0; i < history.size(); i++) {
            Map<String, String> msg = history.get(i);
            String content = msg.get("content")
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            messagesJson.append("{\"role\":\"")
                    .append(msg.get("role"))
                    .append("\",\"content\":\"")
                    .append(content)
                    .append("\"}");

            if (i < history.size() - 1) messagesJson.append(",");
        }
        messagesJson.append("]");

        String requestBody = "{\"model\":\"mistralai/mistral-7b-instruct:free\","
                + "\"messages\":" + messagesJson + "}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENROUTER_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "https://github.com/mybot")
                .header("X-Title", "MyDiscordBot")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            log.error("OpenRouter error: {}", response.body());
            throw new BotException("OpenRouter error: " + response.statusCode());
        }

        return parseResponse(response.body());
    }

    private String parseResponse(String json) {
        try {
            int contentIndex = json.indexOf("\"content\":");
            if (contentIndex == -1) return "Sorry, no response received.";

            int start = json.indexOf("\"", contentIndex + 10) + 1;
            int end   = json.indexOf("\"", start);

            while (end > 0 && json.charAt(end - 1) == '\\') {
                end = json.indexOf("\"", end + 1);
            }

            return json.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

        } catch (Exception e) {
            log.error("Failed to parse response", e);
            return "Sorry, I had trouble reading the response!";
        }
    }
}
*/
package com.example.MyBot.service;

import com.example.MyBot.exception.BotException;
import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AskService {

    private static final Logger log = LoggerFactory.getLogger(AskService.class);

    @Value("${openrouter.api.key}")
    private String apiKey;

    // OpenRouter endpoint — OpenAI compatible
    private static final String OPENROUTER_URL =
            "https://openrouter.ai/api/v1/chat/completions";

    public void execute(CommandContext context) {
        String question = context.getArgs().trim();

        if (question.isEmpty()) {
            context.getChannel().sendMessage(
                    "❓ Please ask something! Usage: `!ask <your question>`"
            ).queue();
            return;
        }

        log.info("🤔 Question from [{}]: {}", context.getAuthorName(), question);

        // Show typing while waiting
        context.getChannel().sendTyping().queue();

        try {
            String answer = callOpenRouter(question);

            String response = String.format(
                    "**%s** asked: *%s*\n\n🤖 **AI:** %s",
                    context.getAuthorName(),
                    question,
                    answer
            );

            // Discord 2000 char limit
            if (response.length() > 2000) {
                response = response.substring(0, 1997) + "...";
            }

            context.getChannel().sendMessage(response).queue(
                    ok  -> log.debug("✅ Response sent"),
                    err -> log.error("❌ Failed to send", err)
            );

        } catch (Exception e) {
            log.error("Error calling OpenRouter", e);
            throw new BotException("Failed to get AI response", e);
        }
    }

    private String callOpenRouter(String question) throws Exception {

        // Escape the question properly
        String escapedQuestion = question
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        // Build request body
        String requestBody = "{"
                + "\"model\": \"google/gemma-3-4b-it:free\","
                + "\"messages\": ["
                + "{"
                + "\"role\": \"user\","
                + "\"content\": \"" + escapedQuestion + "\""
                + "}"
                + "]"
                + "}";

        log.info("Request body: {}", requestBody); // See exact request in logs

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENROUTER_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "https://github.com/mybot")
                .header("X-Title", "MyDiscordBot")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        log.info("Status: {}", response.statusCode());
        log.info("Response: {}", response.body()); // See full response

        if (response.statusCode() != 200) {
            throw new BotException("OpenRouter error " + response.statusCode()
                    + ": " + response.body());
        }

        return parseResponse(response.body());
    }

    private String parseResponse(String json) {
        try {
            // Extract content from:
            // {"choices":[{"message":{"content":"answer here"}}]}
            int contentIndex = json.indexOf("\"content\":");
            if (contentIndex == -1) return "Sorry, no response received.";

            int start = json.indexOf("\"", contentIndex + 10) + 1;
            int end   = json.indexOf("\"", start);

            // Handle multi-line responses
            while (end > 0 && json.charAt(end - 1) == '\\') {
                end = json.indexOf("\"", end + 1);
            }

            return json.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

        } catch (Exception e) {
            log.error("Failed to parse response", e);
            return "Sorry, I had trouble reading the response!";
        }
    }
}

/*
package com.example.MyBot.service;

import com.example.MyBot.exception.BotException;
import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

*/
/**
 * AskService - Handles the !ask <question> command.
 *
 * Currently: returns dummy responses from a pre-defined list.
 * Future: replace getAiResponse() with an OpenAI / Anthropic API call.
 *
 * The rest of the code (listener, config, command service) stays exactly the same.
 * That's the power of clean architecture!
 *//*

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

    */
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
     *//*

    private String getAiResponse(String question) {
        // Simulate thinking time (remove when using real AI)
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        return DUMMY_RESPONSES.get(random.nextInt(DUMMY_RESPONSES.size()));
    }
}

//!ask logic (AI-ready)
*/
