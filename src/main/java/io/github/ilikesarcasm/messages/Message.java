package io.github.ilikesarcasm.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Message {

    private static LanguageManager languageManager;

    private final JsonObject message;

    /**
     * Sets the used LanguageManager.
     * @param languageManager instance of LanguageManager
     */
    public static void setLanguageManager(LanguageManager languageManager) {
        Message.languageManager = languageManager;
    }

    /**
     * Constructor.
     * @param message the message this Message instance represents
     * @param params  the values to parametrize the message
     */
    private Message(Object message, Object... params) {
        JsonObjectBuilder messageBuilder = Json.createObjectBuilder().add("text", "");
        JsonArrayBuilder extraBuilder = Json.createArrayBuilder();
        
        switch (message.getClass().getSimpleName()) {
            case "ArrayList":
                ArrayList<String> lines = (ArrayList<String>)message;
                for (int i = 0; i < lines.size(); i++) {
                    boolean newLine = i > 0;
                    this.processLine(lines.get(i), params, extraBuilder, newLine);
                }
                break;

            case "MemorySection":
                Set<String> keys = ((MemorySection)message).getKeys(false);
                if (keys.contains("text")) {
                    this.processLine(message, params, extraBuilder);
                } else {
                    boolean newLine = false;
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (key.startsWith("line")) {
                            Object line = ((MemorySection)message).get(key);
                            this.processLine(line, params, extraBuilder, newLine);
                            newLine = true;
                        }
                    }
                    this.processOptions((MemorySection)message, params, messageBuilder);
                }
                break;

            case "String":
                this.processLine(message, params, extraBuilder);
                break;
        }
            
        this.message = messageBuilder.add("extra", extraBuilder.build()).build();
    }

    /**
     * Add line to a JsonArrayBuilder
     * @param line    the YAML representation of the line
     * @param params  the values to parametrize the line
     * @param newLine truth value for adding a new line at the start of this line
     */
    private void processLine(Object line, Object[] params, JsonArrayBuilder builder, boolean newLine) {
        JsonObjectBuilder lineBuilder = Json.createObjectBuilder();
        
        switch (line.getClass().getSimpleName()) {
            case "String":
            lineBuilder.add("text", (newLine ? "\n" : "") + new MessageFormat((String)line).format(params));
                break;

            case "MemorySection":
                this.processOptions((MemorySection)line, params, lineBuilder, newLine);
                break;
        }

        builder.add(lineBuilder.build());
    }

    /**
     * Add line to a JsonArrayBuilder
     * @param line    the YAML representation of the line
     * @param params  the values to parametrize the line
     */
    private void processLine(Object line, Object[] params, JsonArrayBuilder builder) {
        this.processLine(line, params, builder, false);
    }

    /**
     * Add options to a JsonObjectBuilder.
     * @param options the options to add to the builder
     * @param params  the values to parametrize the options
     * @param builder the builder to add the options to
     * @param newLine truth value for adding a new line at the start of this line
     */
    private void processOptions(MemorySection options, Object[] params, JsonObjectBuilder builder, boolean newLine) {
        Set<String> keys = options.getKeys(false);

        for (String key: keys) {
            if (!(options.get(key) instanceof String)) {
                continue;
            }

            String value = new MessageFormat((String)(options.get(key))).format(params);

            switch (key) {
                case "text":
                    builder.add("text", (newLine ? "\n" : "") + value);
                    break;

                case "hover":
                    builder.add("hoverEvent", Json.createObjectBuilder()
                                                  .add("action", "show_text")
                                                  .add("value", value)
                                                  .build());
                    break;

                case "click":
                    builder.add("clickEvent", Json.createObjectBuilder()
                                                  .add("action", "run_command")
                                                  .add("value", value)
                                                  .build());
                    break;
            }
        }
    }

    /**
     * Add options to a JsonObjectBuilder.
     * @param options the options to add to the builder
     * @param params  the values to parametrize the options
     * @param builder the builder to add the options to
     */
    private void processOptions(MemorySection options, Object[] params, JsonObjectBuilder builder) {
        this.processOptions(options, params, builder, false);
    }

    /**
     * Creates a Message representing the message associated with the given key in the LanguageManager.
     * @param key     the key to get the message associated with
     * @param params  the values to parametrize the message
     * @return a new Message instance or null if key is not found
     */
    public static Message fromKey(String key, Object... params) {
        Object message = Message.languageManager.getFromKey(key);

        return message != null ? new Message(message, params) : null;
    }

    /**
     * Creates a Message representing the given message.
     * @param message the message to represent
     * @param params  the values to parametrize the message
     * @return a new Message instance
     */
    public static Message fromMessage(String message, Object... params) {
        return new Message(message, params);
    }

    /**
     * Extract the text part of the message
     * @return the text part of the message
     */
    public String toRaw() {
        return this.message.getJsonArray("extra").stream().map(
            line -> line.asJsonObject().getString("text")
        ).toString();
    }

    /**
     * Sends the message to a player.
     * @param target the player to send the message to
     */
    public void sendTo(Player target) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " " + this.message.toString());
    }

    /**
     * Sends the message to a CommandSender. Redirects to sendTo(Player) if target is an instance of Player.
     * @param target the user to send the message to
     */
    public void sendTo(CommandSender target) {
        if (target instanceof Player) {
            this.sendTo((Player)target);
        } else {
            target.sendMessage(this.toRaw());
        }
    }

    /**
     * Sends the message to every players.
     */
    public void sendToAll() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a " + this.message);
    }

}
