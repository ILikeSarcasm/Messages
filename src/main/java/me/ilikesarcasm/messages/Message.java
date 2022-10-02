package me.ilikesarcasm.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    private static LanguageManager languageManager;

    private final String message;

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
    private Message(String message) {
        this.message = message;
    }

    /**
     * Creates a Message representing the message associated with the given key in the LanguageManager.
     * @param key the key to get the message associated with
     * @return a new Message instance or null if key is not a final path
     */
    public static Message fromKey(String key) {
        String message = Message.languageManager.getFromKey(key);

        return message != null ? new Message(message) : null;
    }

    /**
     * Creates a Message representing the given message.
     * @param message the message to represent
     * @return a new Message instance
     */
    public static Message fromMessage(String message) {
        return new Message(message);
    }

    /**
     * Sends the message to a player.
     * @param target the player to send the message to
     */
    public void sendTo(Player target) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " " + this.message);
    }

    /**
     * Sends the message to a CommandSender. Redirects to sendTo(Player) if target is an instance of Player.
     * @param target the user to send the message to
     */
    public void sendTo(CommandSender target) {
        if (target instanceof Player) {
            this.sendTo((Player) target);
        } else {
            target.sendMessage(this.message);
        }
    }

}
