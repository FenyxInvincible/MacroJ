package local.autohotkey.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import local.autohotkey.Application;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroDefinition;
import local.autohotkey.data.MacroDefinitionAction;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private final KeyManager keyManager;
    private final Gson gson;

    private Map<Integer, MacroPair> macros;
    private final Map<Integer, MacroThreads> macroThreadMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() throws IOException {

        String macroConfigFile = "mapping-" + Application.PROFILE + ".json";

        Type collectionType = TypeToken.getParameterized(Map.class, String.class, MacroDefinition.class)
                .getType();
        Map<String, MacroDefinition> ms = gson.fromJson(
                new InputStreamReader(Files.loadResource(macroConfigFile)),
                collectionType
        );

        macros = ms.entrySet().stream()
                .map(e -> Pair.of(
                        keyManager.findKeyCodeByText(e.getKey()),
                        createListOfMacroPair(
                                keyManager.findKeyByText(e.getKey()),
                                e.getValue()
                        )
                    )
                ).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    private MacroPair createListOfMacroPair(Key key, MacroDefinition definition) {
        return new MacroPair(findMacroClass(key, definition.getOnPress()), findMacroClass(key, definition.getOnRelease()), key);
    }

    public boolean execute(Key key, KeyEventReceiver.PressState pressState) {
        boolean isExecuted = false;
        MacroPair macroByKey = macros.get(key.getKeyCode());

        if(macroByKey != null){
            Macro macro = macroByKey.getMacroByEventType(pressState);
            if (!macroThreadMap.containsKey(key.getKeyCode())) {
                macroThreadMap.put(key.getKeyCode(), new MacroThreads());
            }

            log.debug("Macro found {}", macro.getClass());

            MacroThreads macroThreads = macroThreadMap.get(key.getKeyCode());

            macroThreads.run(macro, pressState);
            isExecuted = true;
        }
        return isExecuted;
    }

    private Macro findMacroClass(Key key, MacroDefinitionAction definition) {
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

    public boolean hasMacro(Key key) {
        return macros.containsKey(key.getKeyCode());
    }

    @Data
    private static class MacroPair {
        private final Macro onPressMacro;
        private final Macro onReleaseMacro;
        private final Key triggerKey;

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
            return triggerKey == null || triggerKey.isPressed();
        }
    }
}
