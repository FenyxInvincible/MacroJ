package local.macroj.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import local.macroj.data.*;
import local.macroj.data.macro.DummyMacro;
import local.macroj.data.macro.Macro;
import local.macroj.macro.MacroThreads;
import local.macroj.utils.Files;
import local.macroj.utils.ScreenPicker;
import local.macroj.utils.YamlJsonConverter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static local.macroj.data.Key.KEY_NONE;

@Component
@Slf4j
@RequiredArgsConstructor
@Lazy
public class MacroFactory {

    private final ApplicationContext context;
    private final Gson gson;
    private final RuntimeConfig config;
    private final YamlJsonConverter converter;

    @Value("${app.defaultPackage}")
    private final String defaultPackage;

    @Value("${app.profilesPath}")
    private final String profilesPath;

    private List<MacroPair> macros;
    private final Map<MacroKey, MacroThreads> macroThreadMap = new ConcurrentHashMap<>();

    public void init() throws IOException {
        macroThreadMap.clear();

        String macroConfigFile = getMacroConfigFilePath(profilesPath, config.getCurrentProfile());

        Type collectionType = TypeToken.getParameterized(Map.class, MacroKey.class, MacroDefinition.class)
                .getType();

        Reader reader = (macroConfigFile.endsWith(".yaml")) ?
                new StringReader(converter.toJson(Files.readFile(macroConfigFile))) :
                new FileReader(macroConfigFile);

        Map<MacroKey, MacroDefinition> ms = gson.fromJson(
                reader,
                collectionType
        );

        macros = ms.entrySet().stream()
                .map(e -> createListOfMacroPair(e.getKey(), e.getValue())
                ).collect(Collectors.toList());
    }

    public static String getMacroConfigFilePath(String profilesFolder, String profileName) {
        return profilesFolder + "/mapping-" + profileName;
    }

    private MacroPair createListOfMacroPair(MacroKey key, MacroDefinition definition) {
        return new MacroPair(
                Optional.ofNullable(definition.getApplication()).orElse(ApplicationDefinition.DUMMY),
                findMacroClass(key, definition.getOnPress()),
                findMacroClass(key, definition.getOnRelease()),
                key
        );
    }

    public boolean execute(MacroKey key, MacroListener.EventState pressState) {
        boolean isExecuted = false;

        MacroPair macroByKey = macros.stream()
        .filter(p -> key.equals(p.getMacroKey()))
        .findAny()
        .orElse(null);

        if(macroByKey != null &&
                macroByKey.applicationDefinition.isValidApplication(
                        ScreenPicker.getForegroundWindowTitle(),
                        ScreenPicker.getForegroundWindowPath()
                )
        ){
            Macro macro = macroByKey.getMacroByEventType(pressState);
            if (!macroThreadMap.containsKey(key)) {
                macroThreadMap.put(key, new MacroThreads());
            }

            log.info(
                    "Macro for key {} [modifier: {}, state: {}]  is found {}",
                    key.getKey().getKeyText(),
                    Optional.ofNullable(key.getModifier()).orElse(KEY_NONE).getKeyText(),
                    pressState,
                    macro.getClass()
            );

            MacroThreads macroThreads = macroThreadMap.get(key);

            macroThreads.run(macro, pressState);
            isExecuted = true;
        }
        return isExecuted;
    }

    private Macro findMacroClass(MacroKey key, MacroDefinitionAction definition) {
        Macro bean = null;

        if (definition == null) {
            bean = new DummyMacro();
        } else {
            try {
                Class<?> beanClass;

                try {
                    beanClass = Class.forName(defaultPackage + definition.getMacroClass());
                } catch (ClassNotFoundException e) {
                    beanClass = Class.forName(definition.getMacroClass());
                }

                bean = (Macro) context.getBean(beanClass);

                if (bean.getParamsType() == null) {
                    bean.setParams(definition.getParams(), key);
                } else {
                    Type collectionType = bean.getParamsType();
                    String tempParamJson = gson.toJson(definition.getParams());
                    try{
                        Object params = gson.fromJson(tempParamJson, collectionType);
                        bean.setParams(params, key);
                    } catch (Exception e) {
                        log.error("{}", tempParamJson);
                        throw new IllegalArgumentException(e);
                    }
                }
            } catch (ClassCastException | ClassNotFoundException e) {
                log.error("Can not find macro class in definition {}", definition);
                throw new IllegalArgumentException("Can not find macro class " + definition.getMacroClass(), e);
            }
        }

        return bean;
    }

    @Nullable
    public MacroKey getMacro(Key key) {
        if(macros == null) {
            return null;
        }

        return macros.stream()
                .filter(p -> p.getMacroKey().getKey().equals(key) && p.isTriggered())
                .map(p -> p.getMacroKey())
                .findAny()
                .orElse(null);
    }

    public void stop() {
        macroThreadMap.entrySet().stream().forEach(mt -> mt.getValue());
        macroThreadMap.clear();
        macros = null;
        config.setCurrentProfile(null);
    }

    @Data
    private static class MacroPair {
        private final ApplicationDefinition applicationDefinition;
        private final Macro onPressMacro;
        private final Macro onReleaseMacro;
        private final MacroKey macroKey;

        public Macro getMacroByEventType(MacroListener.EventState eventType) {
            switch (eventType) {
                case DOWN:
                    return getOnPressMacro();
                case UP:
                    return getOnReleaseMacro();
                default:
                    throw new IllegalArgumentException("Unknown event type " + eventType);
            }
        }

        public boolean isTriggered() {
            return macroKey.getModifier() == null || macroKey.getModifier().isPressed();
        }
    }
}
