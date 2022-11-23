package io.github.ilikesarcasm.messages;

import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.openmbean.InvalidKeyException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class LanguageManager {

    private static final String LANGUAGE_FOLDER = "lang";

    private static LanguageManager instance;

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
     * Returns the message or the structure associated with the given key.
     * @param key the key to get the message associated with
     * @return the associated message or structure
     */
    public Object getFromKey(String key) {
        return this.messages.get(key);
    }

    /**
     * Load messages from a language file.
     * @param languageFile The file to load the messages from
     */
    public void loadLanguage(File languageFile) {
        YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        this.messages = languageConfig.getValues(true);
    }

}
