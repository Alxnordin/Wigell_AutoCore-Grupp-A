package com.wac.autocore.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private ObjectProperty<Locale> locale;
    private ResourceBundle resourceBundle;

    private static final LanguageManager instance = new LanguageManager();

    private LanguageManager() {
        locale = new SimpleObjectProperty<>(Locale.forLanguageTag("en"));
        resourceBundle = ResourceBundle.getBundle("language", locale.get());
    }

    public static LanguageManager getInstance() {
        return instance;
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }


    public void changeLanguage() {
        Locale newLocale;

        if (locale.get().equals(Locale.forLanguageTag("en"))) {
            newLocale = Locale.forLanguageTag("sv");
        } else {
            newLocale = Locale.forLanguageTag("en");
        }

        resourceBundle = ResourceBundle.getBundle("language", newLocale);
        locale.set(newLocale);
    }
}
