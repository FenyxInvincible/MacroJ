# **About**
MacroJ is a free, open-source tool for Windows.
Designed to create extensive key bindings, macros and hotkeys for various Windows applications (e.g. video games, image-, video- and 3D editors).
It allows users to:
- Create hotkeys for mouse and keyboard
- Remap keys
- Bind several hotkeys on one physical button
- Create complex (even multi-threading) scenarios using power of Java concurrency

This tool could be useful in following scenarios:
- Gaming (remap keys, if it's not supported by game, create auto heal, triple shot etc)
- Shortcut creator (see Examples below)

It allows to use all power of Java threads and C-like syntax for macro definitions

# **Requirement**
Java 11+ installed. It can be download here https://www.oracle.com/java/technologies/downloads/#java11-windows

# **Implementation:**

It was designed for Windows and JNA is default key sender. It's possible to use Java Robot instead.
Need to change Sender bean to following in `ApplicationConfig.java`

```java
@Bean
public Sender sender(){
    return new RobotSender();
}
```

# **How to use**
It could be used without Java knowledge, but functionality will be limited by existing macros. 
There are several default included macro definitions which can be used out of the box (see List of included macros).

**Download [release](https://github.com/FenyxInvincible/MacroJ/releases) and unpack.** 

Alternatively, clone repo and execute `./gradlew build`. All necessary files will be in `build/libs` folder (macroj-1.0-SNAPSHOT.jar, profiles)
Run `java -jar macroj-1.0-SNAPSHOT.jar` or create .bat file
```
start /REALTIME java -jar "macroj-1.0-SNAPSHOT.jar"
```

# **Profile types**
MacroJ supports **JSON** and **YAML** profiles. JSON is main type and used by default. 

YAML is **converted** to JSON before execution. 

Top level YAML sections which are started with **'x-'** (e.g _x-Template_) will be **removed** from result JSON.
It allows to use YAML anchors like template variables. For example:

```yaml
x-SpellChangeKey: &TemplateVar "UP"
params:
 key: *TemplateVar
```

will be converted to

```json
{
  "params": {
    "key": "UP"
  }
}
```

# **Adding profile**
Adding new profile doesn't require compilation. 

New profile can be easily added. Just need to put `mapping-***.json` where *** is profile name single word.
Alternatively use File -> Create Profile in UI.
-This JSON file has to contain similar configuration for each macro key

```json
"KEYNAME": {//all available keys can be found in src/main/resources/keys.json
    "onPress": {
        "macroClass": "MACRO CLASS",//e.g. SendSequence
        "params": 
            ... depends on macro class
        
    },
    "onRelease": {
        "macroClass": "MACRO CLASS PATH",//e.g. SendSequence
        "params": [ //list of string by default if is not specified
            "1", "asd"
        ]
    }
}
```
`KEYNAME` - string value of key (see src/main/resources/keys.json). Modifier + key name is allowed. For example `LCONTROL+1`

`macroClass` - path to macro class eg. `local.macroj.data.macro.SendSequence`. See **List of included macros**

`params` - parameters for macro. Depends on macro class and could be checked by Type getParamsType() method. 
If it's not specified, default null is used from interface and pure Gson deserialisation will appear. 
For example following code will be transformed to `List<String>`
```json
"params": [
    "asd", "1", "."
]
```

# **Adding new macro**

Any macro class has to implement `local.macroj.data.macro.Macro` interface.
Expected data type from JSON params section can be specified by overriding `Type getParamsType()` method or Gson deserialisation by default.

# **List of included macros**

You could check `mapping-test.json` for example of macro usage

### Crosshair

Scales and draws image (crosshair for example) at the center of screen; 

Required params - json object with `imagePath`, `scaleX`, `scaleY` keys.
```json
"params": {
    "imagePath": "profiles/crosshair.png",
    "scaleX": "60",
    "scaleY": "60"
}
```

### Gesture
Allows to bind four actions for one key. When key is pressed and mouse direction is UP/DOWN/LEFT/RIGHT specific key is mapped.

Required params - json object with direction as key and key name as value.
Excess direction can be skipped.
```json
"CAPSLOCK": {
    "onPress": {
        "macroClass": "Gesture",
        "params": {
            "UP": "5",
            "DOWN": "6",
            "LEFT": "7",
            "RIGHT": "8"
    }
}
```

### RemapKey
It's used for remapping one key to other. 

```json
"CAPSLOCK": {
    "onPress": {
        "macroClass": "RemapKey",
        "params": {
            "key": "4", //key name for keys.json
            "delay": 500 // delay between press and release
        }
    }
}

```
or
```json
"CAPSLOCK": {
    "onPress": {
        "macroClass": "RemapKey",
        "params": {
            "key": "4", //key name for keys.json
            "action": "Press" // Press or Release action, delay will be ignored
        }
    },
    "onRelease": {
        "macroClass": "RemapKey",
        "params": {
            "key": "4", //key name for keys.json
            "action": "Release" // Press or Release action, delay will be ignored
        }
    }
}
```


### SendSequence
Send sequence of keys with specified delay between

```json
"CAPSLOCK": {
    "onPress": {
        "macroClass": "SendSequence",
        "params": [
            //delay here is time between keys. Delay between press and release is constant 64 ms
            {"key": "4", "delay": 500},
            {"key": "E", "delay": 30, "action": "Press"},
            {"key": "RMB", "delay": 30},
            {"key": "E", "delay": 30, "action": "Release"},
            {"key": "RMB", "delay": 3000},
            {"key": "4", "delay": 10}
        ]
    }
}
```

### SpamSequence
Send sequence of keys while key is pressed

```json
"CAPSLOCK": {
    "onPress": {
        "macroClass": "SpamSequence",
        "params": [
            //delay here is time between keys. Delay between press and release is constant 64 ms
            {"key": "4", "delay": 500},
            {"key": "E", "delay": 30, "action": "Press"},
            {"key": "RMB", "delay": 30},
            {"key": "E", "delay": 30, "action": "Release"},
            {"key": "RMB", "delay": 3000},
            {"key": "4", "delay": 10}
        ]
    }
}
```

### DoubleAction
Choosing between 2 options depends on action (key press or release).
This macro sends press and release of specified key.
If action is changed before specified time: shortKey will be emulated, longKey otherwise.
If chosen section is **onPress**, shortKey will be sent in case **release** action is happens before **longPressMs**
If chosen section is **onRelease**, same but vice versa.

```json
"CAPSLOCK": {
    "onPress": {
        "macroClass": "DoubleAction",
        "params": {
            "shortKey": "A", //required
            "longKey": "B", //required
            "longPressMs": 200, //optional. default 500
            "propagateCall": false, //optional. default false
            "pressReleaseDelayMs": 16 //optional. default 16
        }
    }
}
```
By default, all sendings from sender are not propagated, so this sending will not trigger next macro
When propagateCall = true, if there is macro that is bound to sent key, it will be executed.
**Note:** _propagateCall_ may lead to infinite loop and OutOfMemory exception.

### PixelChecker
Starts listening pixel color change and perform actions if color is (or not) changed.
Pressing macro key starts listening. Pressing it again, will stop listening

```json
"CAPSLOCK": {//key which starts listening
    "onPress": {
        "macroClass": "PixelChecker",
            "params": {
                "x": 158, //required, pixel x coordinate
                "y": 1378, //required, pixel y coordinate
                "equality": false, //if true will execute actions from keys section when pixel is equal, if false - not equals
                "keys": [ //action which will be executed when condition is happened
                  {"key": "1", "delay": 1500}
                ],
                "color": { //Whole section is optional. If specified macro will check pixel equality to this color                          
                  "r": 0,  // If not specified color will be picked at startup, when key (CAPSLOCK in current case) is pressed
                  "g": 0,  
                  "b": 0
                },
                "icon": {//draw icon to show that current listening in progress
                    "imagePath": "images/hp.png",
                    "x": 100,
                    "y": 150,
                    "width": 25,
                    "height": 25
                },
                "refreshTimeMs": 100 //optional, default 200 ms
        }
    }
}
```

### PixelColor
Similar to **PixelChecker** validates pixel color, but unlike of it validates only specific amount of times

```json
"CAPSLOCK": {//key which starts listening
    "onPress": {
        "macroClass": "PixelChecker",
            "params": {
                "x": 158, //required, pixel x coordinate
                "y": 1378, //required, pixel y coordinate
                "equality": false, //if true will execute actions from keys section when pixel is equal, if false - not equals
                "keys": [ //action which will be executed when condition is happened
                  {"key": "1", "delay": 1500}
                ],
                "color": { //Whole section is optional. If specified macro will check pixel equality to this color                          
                  "r": 0,  // If not specified color will be picked at startup, when key (CAPSLOCK in current case) is pressed
                  "g": 0,  
                  "b": 0
                },
                "maxAttempts": 3, //optional, default 3
                "attemptsDelayMs": 100 //optional, default 50 ms
        }
    }
}
```

Other macros are too specific for game, but could be used as example and found in `local.macroj.data.macro` package

# Limiting of macro execution by application
By default, all macro will be executed for any active appliaction. 
There is a way to limit this behavior. For example: 
```json
"1": {
    "application": {
        "pathRegex": ".*chrome.*"
    },
    "onPress": ...
    "onRelease": ...
}
```
There are 4 checks which can be used: titleStartsWith, titleRegex, pathStartsWith, pathRegex.
You can specify one check or any combination of them.
First 2 checks window title, other path to executable. 
They are implemented in chain, so if anyone returns false, other will not be executed.
**Note:** _regex_ operations are usually more costly compared to _startWith_

Window title and path to application in foreground could be checked by DebugMacro:
```json
"6": {
    "onPress": {
        "macroClass": "DebugMacro",
        "params": []
    }
}
```
It's part of **test** profile. Just run this profile, press 6 key and check logs.

# Virtual macro keys
Sometimes may be required to call on macro from another. 
Virtual keys don't exist outside MacroJ, so can not be accidentally called by pressing key.
In this case virtual keys could be useful.
For example Gesture macro could store 4 key directions, which could be executed later.
Virtual keys have to start with **macro** word prefix and any other words letters (_e.g. macro1, macro_1, macroMyCoolMacro_). 

```json
"1": {
    "onPress": {
        "macroClass": "Gesture",
        "params": {
            "UP": "macro1", //link to virtual key
            "DOWN": "macro2", //link to virtual key
            "LEFT": "macro3", //link to virtual key
            "RIGHT": "macro4" //link to virtual key
        }
    }
},
"macro1": {
    "onPress": {
    .......
    }
},
"macro2": ... ,
"macro3": ... ,
"macro4": ... ,
```
Here 4 virtual keys will be created. 
When Gesture macro send keys by direction, specific macro will be executed.
Virtual keys live only in profile execution lifecicle and will be cleaned when profile stops.

# **Examples:**
Using 2 usless keys Capslock and right control we could have 6 useful shotrcuts in photoshop:

```json
    //short press will select brush, long(more that 200 ms) - pen
    "CAPSLOCK": {
        "onPress": {
            "macroClass": "DoubleAction",
            "params": {
                "shortKey": "B",//brush
                "longKey": "P",//pen
                "longPressMs": 200,
                "propagateCall": false,
                "pressReleaseDelayMs": 16
            }
        }
    },
    "RControl": {//photoshop example. Press right control, move mouse up/down/right/left, release right control
        "onPress": {
            "macroClass": "Gesture",
            "params": {
                "UP": "M",//selection
                "DOWN": "L",//lasso
                "LEFT": "T", //text
                "RIGHT": "E" //Erase
            }
        }
    }
}
```
