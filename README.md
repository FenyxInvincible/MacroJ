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

# **Adding profile**
Adding new profile doesn't require compilation. 

New profile can be easily added. Just need to put `mapping-***.json` where *** is profile name single word. 
-This JSON file has to contain similar configuration for each macro key

```json
"KEYNAME": {
    "onPress": {
        "macroClass": "MACRO CLASS PATH",
        "params": 
            ... depends on macro class
        
    },
    "onRelease": {
        "macroClass": "MACRO CLASS PATH",
        "params": [ //list of string by default if is not specified
            "1", "asd"
        ]
    }
}
```
`KEYNAME` - string value of key (see src/main/resources/keys.json). Modifier + key name is allowed. For example `LCONTROL+1`

`macroClass` - path to macro class eg. `local.autohotkey.data.macro.SendSequence`. See **List of included macros**

`params` - parameters for macro. Depends on macro class and could be checked by Type getParamsType() method. 
If it's not specified, default null is used from interface and pure Gson deserialisation will appear. 
For example following code will be transformed to `List<String>`
```json
"params": [
    "asd", "1", "."
]
```

# **Adding new macro**

Any macro class has to implement `local.autohotkey.data.macro.Macro` interface.
Expected data type from JSON params section can be specified by overriding `Type getParamsType()` method or Gson deserialisation by default.

# **List of included macros**

You could check `mapping-test.json` for example of macro usage

### local.autohotkey.data.macro.Crosshair

Scales and draws image (crosshair for example) at the center of screen; 

Required params - json object with `imagePath`, `scaleX`, `scaleY` keys.
```json
"params": {
    "imagePath": "profiles/crosshair.png",
    "scaleX": "60",
    "scaleY": "60"
}
```

### local.autohotkey.data.macro.Gesture
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

### local.autohotkey.data.macro.RemapKey
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


### local.autohotkey.data.macro.SendSequence
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

### local.autohotkey.data.macro.SpamSequence
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
Other macros are too specific for game, but could be used as example and found in `local.autohotkey.data.macro` package

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
        "macroClass": "local.autohotkey.data.macro.DebugMacro",
        "params": []
    }
}
```
It's part of **test** profile. Just run this profile, press 6 key and check logs.