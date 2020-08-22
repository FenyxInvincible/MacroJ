package local.autohotkey.data.macro.eso;

import local.autohotkey.data.macro.Macro;
import local.autohotkey.utils.eso.Locks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class SettingsMacro implements Macro {

    private final Locks locks;

    @Override
    public void setParams(List<String> params) {
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nUse potions[n]?");
        String setting = scanner.next().equals("y") ? "on" : "off";
        locks.getSettings().put("potion", setting);

        System.out.print("\nUse synergy[y]?");
        setting = scanner.next().equals("y") ? "on" : "off";
        locks.getSettings().put("synergy", setting);
    }
}
