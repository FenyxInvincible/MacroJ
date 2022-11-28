package local.macroj.data.macro;

import com.google.gson.reflect.TypeToken;
import local.macroj.ApplicationConfig;
import local.macroj.data.MacroKey;
import local.macroj.data.UseKeyData;
import local.macroj.sender.Sender;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class SpamSequence implements Macro {
    private final Sender sender;
    private static volatile AtomicBoolean keepSpammingWhileNoPressAgain = new AtomicBoolean(false);
    private SpamSequence.SpamData data;
    private MacroKey selfKey;
    private ExecutorService th;

    @Override
    public Type getParamsType() {
        return TypeToken.get(SpamSequence.SpamData.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        data = (SpamSequence.SpamData) param;
        this.selfKey = self;
    }

    @SneakyThrows
    @Override
    public void run() {

        if(data.type == SpamType.PressStartReleaseEnd) {
           loop();
           return;
        }

        if (!keepSpammingWhileNoPressAgain.get()){
            if(th != null) {
                th.shutdownNow();
            }
            keepSpammingWhileNoPressAgain.set(true);
            th = Executors.newSingleThreadExecutor();
            th.submit(() -> {
                loop();
                th.shutdownNow();
            });
        } else {
            keepSpammingWhileNoPressAgain.set(false);
            //th.shutdownNow();
            if(!th.isTerminated() && !th.awaitTermination(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Can not stop spam thread");
            }
        }
    }

    private void loop(){
        while (keepSpaming()) {
            data.keys.stream()
                    .takeWhile(n -> needToExecute())
                    .forEach(k -> {
                        sender.send(k, ApplicationConfig.DEFAULT_SEND_DELAY, selfKey);
                    });
        }
    }

    private boolean needToExecute() {
        log.info("{}", data.behaviour);
        if(data.behaviour == SpamBehaviour.StopImmediately) {
            return keepSpaming();
        } else {
            return true;
        }
    }

    private boolean keepSpaming() {
        if(data.type == SpamType.PressStartReleaseEnd) {
            return selfKey.getKey().isPressed();
        } else {
            return keepSpammingWhileNoPressAgain.get();//stop loop
        }
    }

    public enum SpamType {
        PressStartReleaseEnd,
        PressStartPressEnd
    }

    public enum SpamBehaviour {
        StopImmediately,
        CompleteCycle
    }

    @Data
    @NoArgsConstructor
    public static class SpamData {
        private SpamType type = SpamType.PressStartReleaseEnd;
        private SpamBehaviour behaviour = SpamBehaviour.StopImmediately;
        @NonNull
        private List<UseKeyData> keys;
    }
}
