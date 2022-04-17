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

@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class Gesture implements Macro {

    private final Sender sender;
    private Map<Direction, Key> keyData;

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
    }

    @Override
    public void run() {

        try {
            PointerInfo pointer = MouseInfo.getPointerInfo();
            Point point = pointer.getLocation();

            List<Point> directionList = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                Thread.sleep(60);
                pointer = MouseInfo.getPointerInfo();
                Point newPoint = pointer.getLocation();
                directionList.add(newPoint);
            }

            Point maxPointer = new Point();

            directionList.stream()
                    .forEach(
                            point1 -> {
                                double x = Math.abs(point1.getX() - point.getX());
                                if (Math.abs(maxPointer.getX()) < x) {
                                    maxPointer.setLocation(point1.getX() - point.getX(), maxPointer.getY());
                                }

                                double y = Math.abs(point1.getY() - point.getY());
                                if (Math.abs(maxPointer.getY()) < y) {
                                    maxPointer.setLocation(maxPointer.getX(), point1.getY() - point.getY());
                                }
                            }
                    );

            Direction d = findDirection(maxPointer, new Point());

            Key key = keyData.get(d);
            if(key != null) {
                sender.sendKey(key, 64);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
