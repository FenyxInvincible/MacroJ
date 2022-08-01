package local.macroj.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import local.macroj.data.RuntimeConfig;
import local.macroj.log.MemoryLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemoryLogging {

    private final LoggingSystem loggingSystem;
    private final LoggerGroups loggerGroups;
    private final RuntimeConfig config;
    private MemoryLog appender;

    @PostConstruct
    public void resetLogger() {
        getLogger().resize(config.getSettings().getMaxLogLines());
    }

    public String getLogs() {
        return getLogger().getAsString();
    }

    @WriteOperation
    public void configureLogLevel(@Selector String name, @Nullable LogLevel configuredLevel) {

        LoggerGroup group = this.loggerGroups.get(name);
        if (group != null && group.hasMembers()) {
            group.configureLogLevel(configuredLevel, this.loggingSystem::setLogLevel);
            return;
        }
        this.loggingSystem.setLogLevel(name, configuredLevel);
    }

    public void enableMemoryLogging(boolean state) {
        getLogger().setEnabled(state);
    }

    //Workaround to get exact instance of appender which was created
    private MemoryLog getLogger(){
        if (appender == null) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            appender = (MemoryLog) context.getLogger("local.macroj").getAppender("Memory");
        }
        return appender;
    }
}
