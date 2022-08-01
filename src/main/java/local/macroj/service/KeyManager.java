package local.macroj.service;

import local.macroj.data.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyManager {

    @Value("${app.keys.all.file}")
    private String keyConfig;

    private Map<Integer, Key> keys;

    @PostConstruct
    private void init() throws IOException {
        try {
            keys = new JsonReader(keyConfig).parse().entrySet()
                    .stream().map(s -> Pair.of(Integer.valueOf(s.getKey()), Key.create(s.getValue().getAsJsonObject())))
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
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

    public Key findKeyByText(String key) {
        return keys.get(findKeyCodeByText(key.toUpperCase()));
    }

    public Key findKeyByKeyCode(int keyCode) {
        return keys.getOrDefault(keyCode, keys.get(0));
    }
}
