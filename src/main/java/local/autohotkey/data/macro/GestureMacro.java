package local.autohotkey.data.macro;

import local.autohotkey.data.Key;
import local.autohotkey.sender.Sender;
import local.autohotkey.service.KeyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.List;

@Component
@Slf4j
@Scope("prototype")
@RequiredArgsConstructor
public class GestureMacro implements Macro {

    private final KeyManager keys;
    private final Sender sender;

    private Map<Direction, String> rawSequenceMap = new HashMap<>();

    private enum Direction {
        UP, DOWN, RIGHT, LEFT
    }

    @Override
    public void setParams(Object param, Key self) {
        List<String> params = (List<String>) param;
        rawSequenceMap.put(Direction.UP, params.get(0));
        rawSequenceMap.put(Direction.DOWN, params.get(1));
        rawSequenceMap.put(Direction.LEFT, params.get(2));
        rawSequenceMap.put(Direction.RIGHT, params.get(3));
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

            String rawSequence = rawSequenceMap.get(d);
            executeSequence(rawSequence);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeSequence(String rawSequence) {
        String[] sequenceArray = rawSequence.split("\\|");
        for (String entry : sequenceArray) {
            if (entry.startsWith("{")) {
                try {
                    Thread.sleep(Integer.parseInt(entry.replaceAll("[^\\d.]", "")));
                } catch (Exception e) {
                    log.error("{} can not be parsed as int", entry, e);
                }

            } else {
                Key key = keys.findKeyByText(entry);
                sender.sendKey(key, 32);
            }
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
