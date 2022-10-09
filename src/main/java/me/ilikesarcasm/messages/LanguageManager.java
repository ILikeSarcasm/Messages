package me.ilikesarcasm.messages;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.Reader;
import java.util.Map;

public class LanguageManager {

    private static LanguageManager instance;

    private String name;
    private Map<String, Object> messages;

    /**
     * Returns the running instance of the LanguageManager or a new one if it doesn't already exists
     * @return the LanguageManager instance
     */
    public static LanguageManager getInstance() {
        if (LanguageManager.instance == null) {
            LanguageManager.instance = new LanguageManager();
        }

        return LanguageManager.instance;
    }

    /**
     * Returns the loaded language name.
     * @return the language name
     */
    public String getLanguage() {
        return this.name;
    }

    /**
     * Returns the message or the structure associated with the given key.
     * @param key the key to get the message associated with
     * @return the associated message or structure
     */
    public Object getFromKey(String key) {
        return this.messages.get(key);
    }

    /**
     * Load messages from a language file. The language file must exists.
     * @param language     The language name
     * @param languageFile The file to load the messages from
     */
    public void loadLanguage(String language, File languageFile) {
        this.name = language;
        YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        this.messages = languageConfig.getValues(true);
    }

    /**
     * Load messages from a language file. The language file must exists.
     * @param language       The language name
     * @param languageReader The reader to load the messages from
     */
    public void loadLanguage(String language, Reader languageReader) {
        this.name = language;
        YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageReader);
        this.messages = languageConfig.getValues(true);
    }

}
