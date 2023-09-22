package de.fwatermann.bungeecord.bansystem.translation;

import de.fwatermann.bungeecord.bansystem.BanSystem;
import de.fwatermann.bungeecord.bansystem.util.FileUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translation {

    private static final Logger logger = BanSystem.getInstance().getLogger();
    private static final Pattern attributeRegex =
            Pattern.compile("<((.*?=.*?)(;.*?=.*?)*?)>(.*?)(?=<|$)", Pattern.MULTILINE);
    private static final String DEFAULT_LANGKEY = "en_US";
    private static final HashMap<String, Map<String, String>> translations = new HashMap<>();
    private static boolean dataDirInitialized = false;
    private static File langDir;

    static {
        File dataDir = BanSystem.getInstance().getDataFolder();
        BanSystem.getInstance().getDataFolder().mkdirs();
        if (dataDir.exists()) {
            langDir = new File(dataDir, "lang");
            langDir.mkdirs();
            if (langDir.exists()) {
                try {
                    FileUtils.inputStreamToFile(
                            BanSystem.getInstance()
                                    .getResourceAsStream("lang/" + DEFAULT_LANGKEY + ".lang"),
                            new File(langDir, DEFAULT_LANGKEY + ".lang"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                loadTranslation(DEFAULT_LANGKEY);
                dataDirInitialized = true;
            } else {
                logger.severe("Could not create language directory!");
            }
        } else {
            logger.severe("Could not create plugin data directory!");
        }
    }

    public static String text(String messageId, Locale locale, Object... args) {
        return String.format(getTranslation(messageId, langKey(locale)), args);
    }

    public static String text(String messageId, CommandSender sender, Object... args) {
        return text(messageId, sender instanceof ProxiedPlayer pp ? pp.getLocale() : null, args);
    }

    public static String text(String messageId, ProxiedPlayer player, Object... args) {
        return text(messageId, player.getLocale(), args);
    }

    public static BaseComponent[] component(
            String messageId, CommandSender sender, Object... args) {
        return component(
                messageId, sender instanceof ProxiedPlayer pp ? pp.getLocale() : null, args);
    }

    public static BaseComponent[] component(String messageId, Locale locale, Object... args) {
        String rawText = text(messageId, locale, args);

        // Escape < and > to prevent them from being interpreted as attributes
        rawText = rawText.replace("\\<", "&lt;").replace("\\>", "&gt;");

        Matcher matcher = attributeRegex.matcher(rawText);

        ComponentBuilder builder = new ComponentBuilder();
        boolean any = false;
        while (matcher.find()) {
            String[] attributes = matcher.group(1).split(";");
            String text = matcher.group(4);

            // Unescape < and > to prevent them from being interpreted as attributes
            text = text.replace("&lt;", "<").replace("&gt;", ">");

            builder.append(text);
            any = true;
            Arrays.stream(attributes)
                    .map(s -> s.split("="))
                    .forEach(
                            s -> {
                                switch (s[0]) {
                                    case "color" -> builder.color(ChatColor.of(s[1]));
                                    case "bold" -> builder.bold(Boolean.parseBoolean(s[1]));
                                    case "italic" -> builder.italic(Boolean.parseBoolean(s[1]));
                                    case "underlined" -> builder.underlined(
                                            Boolean.parseBoolean(s[1]));
                                    case "strikethrough" -> builder.strikethrough(
                                            Boolean.parseBoolean(s[1]));
                                    case "obfuscated" -> builder.obfuscated(
                                            Boolean.parseBoolean(s[1]));
                                    case "insertion" -> builder.insertion(s[1]);
                                    case "reset" -> {
                                        builder.reset();
                                        builder.color(ChatColor.WHITE);
                                        builder.obfuscated(false);
                                        builder.underlined(false);
                                        builder.bold(false);
                                        builder.italic(false);
                                        builder.strikethrough(false);
                                    }
                                    default -> logger.warning("Unknown attribute " + s[0] + "!");
                                }
                            });
        }
        if (!any) {
            builder.append(rawText);
        }
        return builder.create();
    }

    private static String getTranslation(String messageId, String langKey) {
        if (!dataDirInitialized) {
            return "Missing translation <"
                    + messageId
                    + ">-"
                    + DEFAULT_LANGKEY
                    + " (Default file could not be initialized!)";
        }

        if (!translations.containsKey(langKey)) {
            loadTranslation(langKey);
        }

        // Check if translation was loaded, if not get default language (en_US)
        if (!translations.containsKey(langKey)) {
            if (langKey.equals(DEFAULT_LANGKEY)) {
                logger.log(
                        Level.WARNING,
                        "Could not find translation for language key " + langKey + "!");
                return "Missing translation <" + messageId + ">-" + langKey + "!";
            }
            return getTranslation(messageId, DEFAULT_LANGKEY);
        }
        return translations
                .get(langKey)
                .getOrDefault(
                        messageId, "Missing translation <" + messageId + ">-" + langKey + "!");
    }

    /**
     * Load a translation file from the language directory.
     *
     * @param langKey Language key
     */
    private static void loadTranslation(String langKey) {
        HashMap<String, String> translation = new HashMap<>();
        File langFile = new File(langDir, langKey + ".lang");
        if (!langFile.exists()) {
            logger.log(
                    Level.WARNING,
                    "Could not find translation file for language key " + langKey + "!");
            return;
        }
        try {
            String[] lines = FileUtils.readLines(langFile);
            for (String line : lines) {
                if (line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                translation.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        translations.put(langKey, translation);
    }

    private static String langKey(Locale locale) {
        if (locale == null) return DEFAULT_LANGKEY;
        return locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toUpperCase();
    }
}
