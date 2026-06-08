package ukma.fourgirls.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static ResourceBundle uiBundle;
    private static final List<Runnable> listeners = new ArrayList<>();

    static {
        setLanguage(Locale.of("uk"));
    }

    public static void setLanguage(Locale locale) {
        uiBundle = ResourceBundle.getBundle("lang/ui", locale);
        notifyListeners();
    }

    public static String getString(String key) {
        try {
            return uiBundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static void addLanguageChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}