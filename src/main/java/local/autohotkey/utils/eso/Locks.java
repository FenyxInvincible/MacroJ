package local.autohotkey.utils.eso;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Data
public class Locks {
    volatile private ReentrantLock switchBarLock = new ReentrantLock(true);
    volatile private ReentrantLock castLock = new ReentrantLock();
    volatile private AtomicInteger numberOfCasting = new AtomicInteger(0);
    volatile private AtomicLong lcmPress = new AtomicLong(0);
    volatile private Map<String, String> settings = new ConcurrentHashMap<>();
}
