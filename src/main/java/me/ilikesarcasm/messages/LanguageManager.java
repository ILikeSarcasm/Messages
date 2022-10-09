package me.ilikesarcasm.messages;

import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.openmbean.InvalidKeyException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private static final String LANGUAGE_FOLDER = "lang";
    private static final HashMap<String, String> LANGUAGES_TO_FILE = new HashMap<String, String>() {{
        put("English", "en.yml");
        put("French", "fr.yml");
        put("German", "de.yml");
        put("Spanish", "es.yml");
        put("Italian", "it.yml");
    }};

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
     * @param language    The language name
     * @param classLoader The class loader of the plugin
     * @throws InvalidKeyException   when the language is not handled in LANGUAGES_TO_FILE
     * @throws FileNotFoundException when the language file can not be open
     */
    public void loadLanguage(String language, ClassLoader classLoader) throws InvalidKeyException, FileNotFoundException {
        this.name = language;
        if (!LanguageManager.LANGUAGES_TO_FILE.containsKey(language)) {
            throw new InvalidKeyException("Unknown language: " + language);
        }
        String languageFile = LanguageManager.LANGUAGE_FOLDER + File.separator + LanguageManager.LANGUAGES_TO_FILE.get(language);
        InputStream inputStream = classLoader.getResourceAsStream(languageFile);
        if (inputStream == null) {
            throw new FileNotFoundException("Couldn't find " + languageFile);
        }
        InputStreamReader languageReader = new InputStreamReader(inputStream);
        YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageReader);
        this.messages = languageConfig.getValues(true);
    }

}
