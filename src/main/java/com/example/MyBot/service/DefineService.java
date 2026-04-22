package com.example.MyBot.service;

import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class DefineService {

    private static final Logger log = LoggerFactory.getLogger(DefineService.class);

    // Free Dictionary API — no key needed!
    private static final String DICT_URL =
            "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public void execute(CommandContext context) {
        String word = context.getArgs().trim().toLowerCase();

        if (word.isEmpty()) {
            context.getChannel().sendMessage(
                    "📖 Please provide a word! Usage: `!define <word>`"
            ).queue();
            return;
        }

        // Only single words allowed
        if (word.contains(" ")) {
            context.getChannel().sendMessage(
                    "📖 Please provide a single word! Example: `!define ephemeral`"
            ).queue();
            return;
        }

        log.info("📖 Define requested for word: {}", word);
        context.getChannel().sendTyping().queue();

        try {
            String definition = fetchDefinition(word);
            context.getChannel().sendMessage(definition).queue(
                    ok  -> log.debug("✅ Definition sent"),
                    err -> log.error("❌ Failed to send definition", err)
            );
        } catch (Exception e) {
            log.error("Error fetching definition for: {}", word, e);
            context.getChannel().sendMessage(
                    "❌ Could not find definition for **" + word + "**"
            ).queue();
        }
    }

    private String fetchDefinition(String word) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DICT_URL + word))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 404) {
            return "❌ No definition found for **" + word + "**";
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: " + response.statusCode());
        }

        return parseDefinition(word, response.body());
    }

    private String parseDefinition(String word, String json) {
        try {
            // Extract phonetic
            String phonetic = "";
            int phoneticIdx = json.indexOf("\"phonetic\":\"");
            if (phoneticIdx != -1) {
                int start = phoneticIdx + 12;
                int end   = json.indexOf("\"", start);
                phonetic  = json.substring(start, end);
            }

            // Extract part of speech
            String partOfSpeech = "";
            int posIdx = json.indexOf("\"partOfSpeech\":\"");
            if (posIdx != -1) {
                int start  = posIdx + 16;
                int end    = json.indexOf("\"", start);
                partOfSpeech = json.substring(start, end);
            }

            // Extract definition
            String definition = "";
            int defIdx = json.indexOf("\"definition\":\"");
            if (defIdx != -1) {
                int start  = defIdx + 14;
                int end    = json.indexOf("\"", start);
                definition = json.substring(start, end);
            }

            // Extract example if available
            String example = "";
            int exIdx = json.indexOf("\"example\":\"");
            if (exIdx != -1) {
                int start = exIdx + 11;
                int end   = json.indexOf("\"", start);
                example   = json.substring(start, end);
            }

            // Build response
            StringBuilder sb = new StringBuilder();
            sb.append("📖 **").append(word.toUpperCase()).append("**");
            if (!phonetic.isEmpty())    sb.append(" `").append(phonetic).append("`");
            sb.append("\n");
            if (!partOfSpeech.isEmpty()) sb.append("*").append(partOfSpeech).append("*\n");
            sb.append("\n**Definition:** ").append(definition);
            if (!example.isEmpty())     sb.append("\n**Example:** *").append(example).append("*");

            return sb.toString();

        } catch (Exception e) {
            log.error("Failed to parse definition", e);
            return "❌ Could not parse definition for **" + word + "**";
        }
    }
}