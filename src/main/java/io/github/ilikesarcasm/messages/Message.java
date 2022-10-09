package io.github.ilikesarcasm.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Message {

    private static LanguageManager languageManager;

    private final ArrayList<HashMap<String, String>> message;

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
     */
    private Message(Object message, Object... params) {
        this.message = new ArrayList<>();

        if (message instanceof String) {
            this.message.add(new HashMap<>());
            this.message.get(0).put("text", MessageFormat.format((String) message, params));
        } else {
            System.out.println(message);
        }
    }

    /**
     * Creates a Message representing the message associated with the given key in the LanguageManager.
     * @param key the key to get the message associated with
     * @return a new Message instance or null if key is not a final path
     */
    public static Message fromKey(String key, Object... params) {
        Object message = Message.languageManager.getFromKey(key);

        return message != null ? new Message(message, params) : null;
    }

    /**
     * Creates a Message representing the given message.
     * @param message the message to represent
     * @return a new Message instance
     */
    public static Message fromMessage(String message, Object... params) {
        return new Message(message, params);
    }

    /**
     * Sends the message to a player.
     * @param target the player to send the message to
     */
    public void sendTo(Player target) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " " + this.message);
    }

    /**
     * Returns the raw parametrized message
     * @return the message
     */
    public String toRaw() {
        StringBuilder message = new StringBuilder();
        for (HashMap<String, String> line : this.message) {
            message.append(line.get("text")).append("\n");
        }

        return message.toString();
    }

    /**
     * Returns the parametrized message with optional hover and click event
     * as a JSON string to use with the tellraw command
     * @return JSON string of the message
     */
    public ArrayList<String> toJson() {
        ArrayList<String> message = new ArrayList<>();
        for (HashMap<String, String> line : this.message) {
            message.add(new JSONObject(line).toJSONString());
        }

        return message;
    }

    /**
     * Sends the message to a CommandSender. Redirects to sendTo(Player) if target is an instance of Player.
     * @param target the user to send the message to
     */
    public void sendTo(CommandSender target) {
        if (target instanceof Player) {
            ArrayList<String> message = this.toJson();
            for (String line : message) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " " + line);
            }
        } else {
            target.sendMessage(this.toRaw());
        }
    }

}
