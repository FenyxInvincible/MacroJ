package local.autohotkey.data;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Data
@Slf4j
@Builder

public class ApplicationDefinition {
    public static final ApplicationDefinition DUMMY = ApplicationDefinition.builder().build();
    private final String titleStartsWith;
    private final String titleRegex;
    private final String pathStartsWith;
    private final String pathRegex;



    /**
     *
     * @param applicationName
     * @return Returns true if no specified validations
     */
    public boolean isValidApplication(String applicationName, String applicationPath) {
        boolean rtn = true;
        if(titleStartsWith != null) {
            rtn =  applicationName.startsWith(titleStartsWith);
            log.trace("isValidApplication titleStartsWith applicationName: {} titleStartsWith: {} = {}", applicationName, titleStartsWith, rtn);
        }

        if(titleRegex != null && rtn) {
            Pattern pattern = Pattern.compile(titleRegex);
            rtn =  pattern.matcher(applicationName).find();
            log.trace("isValidApplication titleRegex applicationName: {} titleRegex: {} = {}", applicationName, titleRegex, rtn);
        }

        if(pathStartsWith != null && rtn) {
            rtn =  applicationPath.startsWith(pathStartsWith);
            log.trace("isValidApplication pathStartsWith applicationPath: {} pathStartsWith: {} = {}", applicationPath, pathStartsWith, rtn);
        }

        if(pathRegex != null && rtn) {
            Pattern pattern = Pattern.compile(pathRegex);
            rtn =  pattern.matcher(applicationPath).find();
            log.trace("isValidApplication pathRegex applicationPath: {} pathRegex: {} = {}", applicationPath, pathRegex, rtn);
        }

        return rtn;
    }
}
