# AIChat Mod (Fabric 1.20.4)

Chat with AI via OpenRouter directly inside Minecraft.

## Setup

1. Get a free API key at https://openrouter.ai/keys
2. In-game, run `/aiconfig` — paste your key into chat when prompted
3. Run `/ai on` to enable AI mode
4. Type normally — messages go to the AI instead of the server

## Commands

| Command | Description |
|---|---|
| `/aiconfig` | Interactive API key setup (prompts in chat) |
| `/aiconfig key <key>` | Set API key directly |
| `/aiconfig model <model>` | Set model (default: `openai/gpt-4o-mini`) |
| `/aiconfig system <prompt>` | Set system prompt |
| `/aiconfig status` | Show current config |
| `/aiconfig reset` | Clear API key |
| `/ai on` | Enable AI chat mode |
| `/ai off` | Disable AI chat mode |
| `/ai` | Toggle AI on/off |
| `/ai clear` | Clear conversation history |

## Build

```
./gradlew build
```
Output: `build/libs/aichat-1.0.0.jar`

## Requirements
- Minecraft 26.1
- Fabric Loader 0.19.2+
- Fabric API
- Java 25+
