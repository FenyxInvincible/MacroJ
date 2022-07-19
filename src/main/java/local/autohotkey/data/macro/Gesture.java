package local.autohotkey.data.macro;

import com.google.gson.reflect.TypeToken;
import local.autohotkey.data.Key;
import local.autohotkey.data.MacroKey;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For RobotSender implementation better do not use recursive call when macro is bind on key and resend it
 */
@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class Gesture implements Macro {

    private final Sender sender;
    private Map<Direction, Key> keyData;
    private MacroKey keyInitiator;
    //if was bind to onPress need to wait release or max delay, if onRelease then max delay only
    private boolean initialState;


    private enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    @Override
    public Type getParamsType() {
        return TypeToken.getParameterized(Map.class, Direction.class, Key.class).getType();
    }

    @Override
    public void setParams(Object param, MacroKey self) {
        keyData = (Map<Direction, Key>) param;
        keyInitiator = self;
    }

    @Override
    public void run() {
        initialState = keyInitiator.getKey().isPressed();
        try {
            PointerInfo pointer = MouseInfo.getPointerInfo();
            Point point = pointer.getLocation();
            Direction direction = null;

            ArrayList<Direction> catchedDirections = new ArrayList<>();

            for (int i = 0; i < 50; i++) {
                Thread.sleep(10);
                Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
                log.debug("Point {} Pointer location {}", point, pointerLocation);
                if(!point.equals(pointerLocation)) {
                    catchedDirections.add(findDirection(pointerLocation, point));
                }

                //release happened before max delay
                if(initialState && !keyInitiator.getKey().isPressed()) {
                    break;
                }
            }

            direction = getSingleDirection(catchedDirections);

            if(direction == null) {
                //prevent recursive macro call
                log.info("Gesture: pointer was not moved. Sending key {} by {}", keyInitiator.getKey(), sender.getClass().getSimpleName());
                sender.sendKey(keyInitiator.getKey(), 16, false);

            } else {
                Key key = keyData.get(direction);
                if (key != null) {
                    sender.sendKey(key, 16, true);
                    log.info("Gesture: Key sent {}", key);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Direction getSingleDirection(ArrayList<Direction> catchedDirections) {
        if(catchedDirections.isEmpty()) {
            return null;
        };
        HashMap<Direction, Integer> directions = new HashMap<Direction, Integer>();
        catchedDirections.forEach(d -> directions.compute(d, (direction, count) -> count == null ? 1 : count + 1));
        log.info("Found directions {}", directions);

        return directions.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
    }

    private Direction findDirection(Point newPoint, Point point) {
        double verticalDirection = newPoint.getY() - point.getY();
        double horizontalDirection = newPoint.getX() - point.getX();

        if (Math.abs(verticalDirection) < Math.abs(horizontalDirection)) {
            return horizontalDirection < 0 ? Direction.LEFT : Direction.RIGHT;
        } else {
            return verticalDirection < 0 ? Direction.UP : Direction.DOWN;
        }
    }
}
