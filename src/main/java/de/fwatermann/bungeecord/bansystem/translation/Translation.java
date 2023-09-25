package de.fwatermann.bungeecord.bansystem.translation;

import de.fwatermann.bungeecord.bansystem.BanSystem;
import de.fwatermann.bungeecord.bansystem.util.FileUtils;
import de.fwatermann.bungeecord.bansystem.util.Pair;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translation {

    private static final Logger logger = BanSystem.getInstance().getLogger();
    private static final Pattern attributeRegex =
            Pattern.compile("<(([^<>]+?=[^<>]+?)(;[^<>]+?=[^<>]+?)*)>", Pattern.MULTILINE);

    // <(([^<>]+?=[^<>]+?)(;[^<>]+?=[^<>]+?)*)> - Matches only attribute tags
    // <((.*?=.*?)(;.*?=.*?)*?)>(.*?)(?=<|$) - Matches all tags

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

        Matcher matcher = attributeRegex.matcher(rawText);

        ComponentBuilder builder = new ComponentBuilder();
        List<Pair<Integer, Integer>> attLocs = new ArrayList<>();
        while (matcher.find()) {
            attLocs.add(new Pair<>(matcher.start(), matcher.end()));
        }

        if (attLocs.isEmpty()) return builder.append(rawText).create();

        for (int i = 0; i < attLocs.size(); i++) {
            Pair<Integer, Integer> attrLoc = attLocs.get(i);
            String[] attributes = rawText.substring(attrLoc.a() + 1, attrLoc.b() - 1).split(";");
            String text =
                    rawText.substring(
                            attrLoc.b(),
                            i + 1 < attLocs.size() ? attLocs.get(i + 1).a() : rawText.length());

            builder.append(text);
            for (String s : attributes) {
                String[] parts = s.split("=");
                String name = parts[0];
                String value = parts[1];
                switch (name) {
                    case "color", "c" -> builder.color(ChatColor.of(value));
                    case "bold", "b" -> builder.bold(Boolean.parseBoolean(value));
                    case "italic", "i" -> builder.italic(Boolean.parseBoolean(value));
                    case "underlined", "u" -> builder.underlined(Boolean.parseBoolean(value));
                    case "strikethrough", "s" -> builder.strikethrough(Boolean.parseBoolean(value));
                    case "obfuscated", "o" -> builder.obfuscated(Boolean.parseBoolean(value));
                    case "insertion" -> builder.insertion(value);
                    case "reset" -> {
                        builder.reset();
                        builder.color(ChatColor.WHITE);
                        builder.obfuscated(false);
                        builder.underlined(false);
                        builder.bold(false);
                        builder.italic(false);
                        builder.strikethrough(false);
                    }
                    default -> logger.warning("Unknown attribute \"" + name + "\"!");
                }
            }
        }
        return builder.create();
    }

    private static String getTranslation(String messageId, String langKey) {
        if (!dataDirInitialized) {
            return missingTranslation(
                    messageId, DEFAULT_LANGKEY, "Default file is not initialized!");
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
                return missingTranslation(messageId, langKey);
            }
            return getTranslation(messageId, DEFAULT_LANGKEY);
        }
        return translations
                .get(langKey)
                .getOrDefault(messageId, missingTranslation(messageId, langKey));
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

    private static String missingTranslation(String messageId, String langKey, String... info) {
        String strInfo = info.length > 0 ? " " + Arrays.toString(info) : "";
        return String.format("Missing translation %s/%s %s", messageId, langKey, strInfo);
    }
}
