#  Forza — AI Discord Bot

> A production-ready Discord bot built with **Spring Boot + JDA**, powered by **OpenRouter AI**. Supports AI chat, weather, dictionary, jokes, conversation memory, and rate limiting — all with clean architecture.

![Java](https://img.shields.io/badge/Java-21+-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen?style=flat-square)
![JDA](https://img.shields.io/badge/JDA-5.0.0--beta.21-blue?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

---

##  Features

| Command | Description |
|---------|-------------|
| `!hello` | Bot greets you personally |
| `!ask <question>` | Ask the AI anything (powered by OpenRouter) |
| `!joke` | Get a random programming joke |
| `!define <word>` | Get dictionary definition of any word |
| `!weather <city>` | Get current weather for any city |
| `!forget` | Clear your conversation memory |
| `!help` | Show all available commands |

---

##  Architecture

```
Discord Gateway
      ↓
   JDA Library
      ↓
MessageListener      ← catches events, parses commands
      ↓
CommandService       ← routes to correct service (async)
      ↓
┌─────────────────────────────────────┐
│  HelloService  │  AskService        │
│  JokeService   │  DefineService     │
│  WeatherService│  ForgetService     │
│  HelpService   │                    │
└─────────────────────────────────────┘
      ↓
ConversationMemoryService   ← per-user chat history
RateLimitService            ← anti-spam (5s cooldown)
```

---

##  Project Structure

```
src/main/java/com/example/MyBot/
├── MyBotApplication.java
├── config/
│   ├── BotConfig.java           # JDA setup, async config
│   └── BotLifecycle.java        # Startup/shutdown hooks
├── listener/
│   └── MessageListener.java     # Receives Discord events
├── service/
│   ├── CommandService.java      # Routes commands (async)
│   ├── HelloService.java        # !hello
│   ├── AskService.java          # !ask (AI powered)
│   ├── JokeService.java         # !joke
│   ├── DefineService.java       # !define
│   ├── WeatherService.java      # !weather
│   ├── HelpService.java         # !help
│   ├── ForgetService.java       # !forget
│   ├── ConversationMemoryService.java
│   └── RateLimitService.java
├── model/
│   ├── CommandContext.java      # Parsed command data
│   └── BotCommand.java          # Enum of all commands
└── exception/
    ├── BotException.java
    └── UnknownCommandException.java
```

---

##  Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- A Discord bot token
- An OpenRouter API key
- A OpenWeatherMap API key (free)

### 1. Clone the repo
```bash
git clone https://github.com/yourusername/discord-bot.git
cd discord-bot
```

### 2. Set environment variables
```bash
export DISCORD_BOT_TOKEN=your_discord_bot_token
export OPENROUTER_API_KEY=your_openrouter_key
export WEATHER_API_KEY=your_openweather_key
```

### 3. Run the bot
```bash
mvn spring-boot:run
```

---



##  Discord Developer Portal Setup

1. Go to [discord.com/developers/applications](https://discord.com/developers/applications)
2. Create New Application → Bot

---

##  Design Principles

- **Clean Architecture** — Listener → Service → Model, each layer has one job
- **Event-Driven** — JDA events drive all bot actions
- **Async Processing** — `@Async` keeps JDA event thread free
- **Dependency Injection** — Constructor injection everywhere
- **Graceful Shutdown** — Bot disconnects cleanly on app stop
- **Externalized Config** — No secrets in code, all via env variables

---

##  Tech Stack

- [Spring Boot 3.2.3](https://spring.io/projects/spring-boot)
- [JDA 5.0.0-beta.21](https://github.com/discord-jda/JDA)
- [OpenRouter API](https://openrouter.ai) — AI responses
- [Free Dictionary API](https://dictionaryapi.dev) — Word definitions
- [OpenWeatherMap API](https://openweathermap.org/api) — Weather data
- SLF4J + Logback — Logging

---
