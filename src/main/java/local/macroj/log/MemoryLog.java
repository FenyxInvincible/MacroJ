package local.macroj.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MemoryLog extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private EvictingQueue<String> queue = EvictingQueue.create(50);
    /**
     * Disabled by default
     */
    @Getter
    private transient boolean isEnabled = false;

    synchronized public void resize(int size) {
        queue = EvictingQueue.create(size);
    }

    public void setEnabled(boolean state) {
        isEnabled = state;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if(isEnabled) {
            queue.offer(eventObject.toString());
        }
    }

    public String getAsString() {
        return queue.stream().parallel().collect(Collectors.joining("\n"));
    }
}
