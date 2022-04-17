package local.autohotkey.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import local.autohotkey.Application;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroDefinition;
import local.autohotkey.data.MacroDefinitionAction;
import local.autohotkey.data.MacroKey;
import local.autohotkey.data.macro.DummyMacro;
import local.autohotkey.data.macro.Macro;
import local.autohotkey.macro.MacroThreads;
import local.autohotkey.utils.Files;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import local.autohotkey.jna.hook.key.KeyEventReceiver;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MacroFactory {

    public static final int KEY_PRESS = 2401;
    public static final int KEY_RELEASE = 2402;

    private final ApplicationContext context;
    private final Gson gson;

    private List<MacroPair> macros;
    private final Map<MacroKey, MacroThreads> macroThreadMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() throws IOException {

        String macroConfigFile = "./profiles/mapping-" + Application.PROFILE + ".json";

        Type collectionType = TypeToken.getParameterized(Map.class, MacroKey.class, MacroDefinition.class)
                .getType();
        Map<MacroKey, MacroDefinition> ms = gson.fromJson(
                new FileReader(macroConfigFile),
                collectionType
        );

        macros = ms.entrySet().stream()
                .map(e -> createListOfMacroPair(e.getKey(), e.getValue())
                ).collect(Collectors.toList());
    }

    private MacroPair createListOfMacroPair(MacroKey key, MacroDefinition definition) {
        return new MacroPair(findMacroClass(key, definition.getOnPress()), findMacroClass(key, definition.getOnRelease()), key);
    }

    public boolean execute(MacroKey key, KeyEventReceiver.PressState pressState) {
        boolean isExecuted = false;

        MacroPair macroByKey = macros.stream()
        .filter(p -> key.equals(p.getMacroKey()))
        .findAny()
        .orElse(null);

        if(macroByKey != null){
            Macro macro = macroByKey.getMacroByEventType(pressState);
            if (!macroThreadMap.containsKey(key)) {
                macroThreadMap.put(key, new MacroThreads());
            }

            log.debug("Macro found {}", macro.getClass());

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
                bean = (Macro) context.getBean(Class.forName(definition.getMacroClass()));

                if (bean.getParamsType() == null) {
                    bean.setParams(definition.getParams(), key);
                } else {
                    Type collectionType = bean.getParamsType();
                    String tempParamJson = gson.toJson(definition.getParams());
                    Object params = gson.fromJson(tempParamJson, collectionType);
                    bean.setParams(params, key);
                }
            } catch (ClassCastException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Can not find macro class" + definition.getMacroClass(), e);
            }
        }

        return bean;
    }

    @Nullable
    public MacroKey getMacro(Key key) {
        return macros.stream()
                .filter(p -> p.getMacroKey().getKey().equals(key) && p.isTriggered())
                .map(p -> p.getMacroKey())
                .findAny()
                .orElse(null);
    }

    @Data
    private static class MacroPair {
        private final Macro onPressMacro;
        private final Macro onReleaseMacro;
        private final MacroKey macroKey;

        public Macro getMacroByEventType(KeyEventReceiver.PressState eventType) {
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
