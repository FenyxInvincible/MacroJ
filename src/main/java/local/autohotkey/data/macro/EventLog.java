package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
@Slf4j
public class EventLog implements Macro {

    @Override
    public void setParams(List<String> params) {
    }

    @Override
    public void run() {
        log.info("Key pressed");
    }
}
