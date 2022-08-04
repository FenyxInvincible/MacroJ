package local.macroj.service;

import local.macroj.data.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyManager {

    @Value("${app.keys.all.file}")
    private String keyConfig;

    private Map<Integer, Key> keys;

    //0x3A-40(58-64) 0x88-8F(136-143) 0x97-0x9F(151-159) these ranges are unspecified at Aug. 2022
    //We will use them to create virtual keys;
    private static final Integer[] unspecifiedVkCodes = new Integer[]{
            58, 59, 60, 61, 62, 63, 64,
            136, 137, 138, 139, 140, 141, 142, 143,
            151, 152, 153, 154, 155, 156, 157, 158, 159
    };

    //Although macroJ is multi-thread application, it's not expected that virtual keys would be added in several threads
    //So linked list is good choice here on current moment
    private LinkedList<Integer> availableUnspecifiedCodes = new LinkedList<>();

    @PostConstruct
    private void init() throws IOException {
        try {
            keys = new JsonReader(keyConfig).parse().entrySet()
                    .stream().map(s -> Pair.of(Integer.valueOf(s.getKey()), Key.create(s.getValue().getAsJsonObject())))
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

            availableUnspecifiedCodes.addAll(Arrays.asList(unspecifiedVkCodes));

        } catch (Exception e) {
            log.error("File keys.json is corrupted. {}", keyConfig);
            throw new IllegalArgumentException("File keys.json is corrupted. See logs", e);
        }
    }

    public boolean hasKey(int keyCode) {
        return keys.containsKey(keyCode);
    }

    public Integer findKeyCodeByText(String key) {
        return keys.entrySet().stream()
                .filter(e -> e.getValue().getKeyText().equals(key))
                .map(Map.Entry::getValue)
                .findAny().orElseThrow(() -> new IllegalArgumentException("Could not find key " + key))
                .getKeyCode();
    }

    /**
     * Tries to find key, unless throws exception
     *
     * @param key
     * @return
     */
    public Key findKeyByText(String key) {
        return keys.get(findKeyCodeByText(key.toUpperCase()));
    }

    /**
     * Tries to find key, unless returns null
     *
     * @param key
     * @return
     */
    public Key findKeyByTextSafe(String key) {
        try {
            return findKeyByText(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Key findKeyByKeyCode(int keyCode) {
        return keys.getOrDefault(keyCode, keys.get(0));
    }

    public void cleanVirtualKeys() {
        List<Integer> vkCodes = Arrays.asList(unspecifiedVkCodes);
        availableUnspecifiedCodes.clear();
        availableUnspecifiedCodes.addAll(vkCodes);

        vkCodes.forEach(i -> {
            keys.remove(i);
        });
    }

    public Key createVirtualKey(String virtualKey) {
        Integer virtualKeyVkCode = availableUnspecifiedCodes.pollFirst();

        if(virtualKeyVkCode != null) {
            Key key = new Key(virtualKeyVkCode, virtualKey, 0, false, false);
            if(!keys.containsKey(virtualKeyVkCode)) {
                keys.put(virtualKeyVkCode, key);
            } else {
                throw new IllegalArgumentException(
                        String.format(
                                "Seems like vcode %d is already defined in keys.json by %s. Check unspecifiedVkCodes list",
                                virtualKeyVkCode,
                                keys.get(virtualKeyVkCode)
                        )
                );
            }
            return key;
        } else {
            throw new IllegalArgumentException(
                    String.format(
                        "Not possible to create virtual key %s. No available codes. " +
                        "Perhaps more than %d virtual keys was created for single profile",
                        virtualKey,
                        unspecifiedVkCodes.length
                    )
            );
        }
    }
}
