# **About**
This tool is designed to provide macro creation functionality (like AutoHotkey) using Java as main language.

It was designed for Windows and JNA is default key sender. It's possible to use Java Robot instead.
Need to change Sender bean to following in `ApplicationConfig.java`

```java
@Bean
public Sender sender(){
    return new RobotSender();
}
```
In that case MacroJ could be used on other OS (haven't check).

It allows to use all power of Java threads and C-like syntax for macro definitions

# **Requirement**
Java 8+ installed

# **How to use**
There are several default included macro definitions which can be used out of the box.

Download [release](https://github.com/FenyxInvincible/MacroJ/releases) and unpack. 

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
"params": {
    "UP": "5",
    "DOWN": "6",
    "LEFT": "7",
    "RIGHT": "8"
}
```

### RemapKey
It's used for remapping one key to other. 

Required params - List of {"key": "KEYNAME", "delay": millis, "action": "Press or Release"}
```json
"params": {
    "key": "4", //key name for keys.json
    "delay": 500 // delay between press and release
}
```
or
```json
"params": {
    "key": "4", //key name for keys.json
    "action": "Press" // Press or Release action, delay will be ignored
}
```


### SendSequence
Send sequence of keys with specified delay between

Required params - List of {"key": "KEYNAME", "delay": millis, "action": "Press or Release"}
```json
"params": [
    //delay here is time between keys. Delay between press and release is constant 64 ms
    {"key": "4", "delay": 500},
    {"key": "E", "delay": 30, "action": "Press"},
    {"key": "RMB", "delay": 30},
    {"key": "E", "delay": 30, "action": "Release"},
    {"key": "RMB", "delay": 3000},
    {"key": "4", "delay": 10}
]
```

### SpamSequence
Send sequence of keys while key is pressed

Required params - List of {"key": "KEYNAME", "delay": millis, "action": "Press or Release"}
```json
"params": [
    //delay here is time between keys. Delay between press and release is constant 64 ms
    {"key": "4", "delay": 500}, 
    {"key": "E", "delay": 30, "action": "Press"},
    {"key": "RMB", "delay": 30},
    {"key": "E", "delay": 30, "action": "Release"},
    {"key": "RMB", "delay": 3000},
    {"key": "4", "delay": 10}
]
```

### DoubleAction
Choosing between 2 options depends on action (key press or release).
This macro sends press and release of specified key.
If action is changed before specified time: shortKey will be emulated, longKey otherwise.
If chosen section is **onPress**, shortKey will be sent in case **release** action is happens before **longPressMs**
If chosen section is **onRelease**, same but vice versa.

```json
"params": {
    "longPressMs": 200,  
    "shortKey": "A",
    "longKey": "B", 
    "propagateCall": false,
    "pressReleaseDelayMs": 16
}
```
By default, all sendings from sender are not propagated, so this sending will not trigger next macro
When propagateCall = true, if there is macro that is bound to sent key, it will be executed.
**Note:** _propagateCall_ may lead to infinite loop and OutOfMemory exception.

Required params - List of {"key": "KEYNAME", "delay": millis, "action": "Press or Release"}
```json
"params": [
    //delay here is time between keys. Delay between press and release is constant 64 ms
    {"key": "4", "delay": 500}, 
    {"key": "E", "delay": 30, "action": "Press"},
    {"key": "RMB", "delay": 30},
    {"key": "E", "delay": 30, "action": "Release"},
    {"key": "RMB", "delay": 3000},
    {"key": "4", "delay": 10}
]
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