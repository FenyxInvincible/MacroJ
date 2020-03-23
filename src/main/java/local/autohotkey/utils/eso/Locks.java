package local.autohotkey.utils.eso;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Data
public class Locks {
    private ReentrantLock switchBarLock = new ReentrantLock();
    private ReentrantLock castLock = new ReentrantLock();
    private AtomicInteger numberOfCasting = new AtomicInteger(0);
}
