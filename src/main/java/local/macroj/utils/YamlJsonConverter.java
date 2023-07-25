package local.macroj.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class YamlJsonConverter {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final DumperOptions options;
    {
        options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
    }
    private final Yaml yamlMapper = new Yaml(options);

    public String toJson(String yamlString) throws JsonProcessingException {
        Object parsedYaml = yamlMapper.load(yamlString);

        if (parsedYaml instanceof Map) {
            parsedYaml = excludeTopLevelAnchors((Map<String, Object>)parsedYaml);
        }

        return jsonMapper.writeValueAsString(parsedYaml);
    }

    public String toYaml(String json) throws JsonProcessingException {

        Object obj = jsonMapper.readValue(json, Object.class);
        return yamlMapper.dump(obj);
    }

    /**
     * It is not possible in YAML 1.2 (or any former version) to hide anchors.
     * The reasoning behind this is that YAML has been designed to be a serialization language, not a configuration language.
     * Workaround: use same approach as Docker 3.4 and remove top level anchors started with 'x-'
     * @param parsedYaml Map<String, Object> of shakeYaml default implementation
     */
    private Map<String, Object> excludeTopLevelAnchors(Map<String, Object> parsedYaml) {
        return parsedYaml.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("x-"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
