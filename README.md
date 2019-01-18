# SmartGWT, SmartClient & GWT JsInterop

The goal of the tutorial project is to show how to use JsInterop in combination with SmartGWT and SmartClient. It's mainly to play with the JsInterop features and use them to get hold of a SmartGWT widget from SmartClient code.

The main (master) branch only contains the initial setup. *If you are only interested in the solution, checkout the solution branch*

```
git checkout solution
``` 

The example project comes from the Isomorphic SmartGWT samples projects (and is published here with permission of Isomorphic Software).

# Introduction

SmartClient and SmartGWT are products by [Isomorphic Software][1]. SmartClient is a UI widget library which can (optionally) be used with a Java-based server allowing to use a single Datasource for both the client and server as well as some built in _Direct Method Invocation_.

SmartGWT is a [GWT][2] wrapper around the SmartClient technology allowing you to also write the frontend in Java and have (Smart)GWT compile this to JavaScript.

## About GWT

- May 2006: GWT 1.0 by Google
- ...
- November 2011: Dart programming language @ Google (competes with GWT internally)
- 2012: Google announces to open source GWT
- March 2013: Last release by Google: GWT 2.5.1
- July 2013: Google announces GWT is now fully open source
- ...
- October 2016: GWT 2.8.0
- April 2017: GWT 2.8.1
- October 2017: GWT 2.8.2
- ???: GWT 3.0
- Google has moved on (Dart, [J2CL][3], ...)

# SmartGWT/SmartClient

Example

**SmartGWT**

~~~~~{.java}
                                                                               
HLayout someLayout = new HLayout();
someLayout.setOverflow(Overflow.HIDDEN);
someLayout.setDefaultLayoutAlign(VerticalAlignment.CENTER);
...
someLayout.addMember(createSubLayout());
~~~~~

**SmartClient** (version 1)

~~~~~{.javascript}
                                                                               
val someLayout = isc.HLayout.create();
someLayout.setOverflow("hidden");
someLayout.setDefaultLayoutAlign("center");
...
someLayout.addMember(createSubLayout());
~~~~~

**SmartClient** (version 2)

~~~~~{.javascript}
                                                                               
val someLayout = isc.HLayout.create({
  overflow: "hidden", 
  defaultLayoutAlign: "center", 
  members: [createSubLayout()], 
});
~~~~~

# JsInterop

- **J**ava**S**cript **Interop**erability [GWT JsInterop][4]
- Goal?
    - Call Java from JavaScript
    - Call JavaScript from Java
- Part of GWT
- Needs an extra flag: `-generateJsInteropExports`

# Tutorial

Assuming you have checked out the repository import the project in your favorite IDE (Intellij, Eclipse, Emacs, vim, ...)

Have a quick look at the following files: 

- `BuiltInDS.java` (sample SmartGWT project)
    - GWT entrypoint
    - Build a Layout with grids, buttons, ...
- `FantasyWorld.java`: Simple class for demo purposes
- BuiltInDS.gwt.xml

**(!) Important (!)**

`BuiltInDS.gwt.xml` has the following optimization to speedup GWT compilation

```
    <!-- Limit permutations -->
    <set-property name="user.agent" value="safari" />
```

Remove/change/tweak this accordingly to your configuration:

- ie8
- gecko1_8
- safari
- ie9
- ie10
(See [UserAgent.gwt.xml][5])

Startup the server and ensure everything is running fine (meaning: you see the main `ListGrid` and you can interact with it):

## Part 1. Basic JsInterop: Expose Java types to JavaScript

Annotate the FantasyWorld object

`FantasyWorld.java`

~~~~~{.java}
                                                                               
@JsType
public class FantasyWorld {
  ...
}

~~~~~

Redeploy the application (*) and refresh in the browser

\(*\) rely only your IDE toolset to do this as quick / efficient as possible. E.g. IntelliJ Ultimate has support for GWT projects using the GWT Run Configuration or using an application server (like Jetty or Tomcat) where you can quickly redeploy without having to restart the server all the time...

With the application open in the browser, open a JavaScript Console and type

~~~~~{.javascript}

// :)                                                                               
var world1 = new com.smartgwt.sample.client.FantasyWorld();
world1.showName();

world1.changeName("Gaia");
world1.showName();

// For the curious ones
world1;

// :(
world1.com_smartgwt_sample_client_FantasyWorld_name
 
~~~~~

Let's add some namespace and also annotate the class properties

`FantasyWorld.java`

~~~~~{.java}
                                                                               
package com.smartgwt.sample.client;                                            
/**
 * Custom namespace
 */
@JsType(namespace = "fantasy")
public class FantasyWorld {

    @JsProperty(name = "name")
    private String name = "Planet";

    @JsProperty(name = "cities")
    private Set<String> cities = new HashSet<>();

...
}
 
~~~~~

Redeploy and check

~~~~~{.javascript}
                                                                               
var world2 = new fantasy.FantasyWorld();
world2.showName();

world2.changeName("Gaia");

world2.name;
 
~~~~~

# Part 2: Native methods

Add the following code to `BuiltInDS.java`

`BuiltInDS.java`

~~~~~{.java}
@JsType
public class BuiltInDS implements EntryPoint {
                                                                               
    public void onModuleLoad() {
        // ...
        initializeWorld();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // JavaScript native functions
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void initializeWorld();

}

~~~~~

- GWT modules have a so called `EntryPoint`
- Let's call the `initializeWorld` at the end of `onModuleLoad`
- Java only knows that `initializeWorld` _will exist_ at runtime
- `initializeWorld` is JavaScript function which lives in the `Global` namespace

Define the `initializeWorld` function in JavaScript to do whatever you want

*war/js/fantasy.js*

~~~~~{.javascript}
                                                                               
// Add a custom label once the GWT module is initialized
function initializeWorld() {
  var helloLabel = isc.Label.create({
    contents: "Hello from JavaScript"
  });
  helloLabel.draw();
}
 
~~~~~

. . .

*BuiltInDS.html*

~~~~~{.html}
<html>
  <head>
    ...
    <script src="js/fantasy.js"></script>
  </head>
  <body>...</body>
</html>
~~~~~

This should draw a simple label on top your SmartGWT application.

Now, just for the sake of the example, we can add methods dynamically...

*war/js/fantasy.js*

~~~~~{.javascript}
                                                                               
// Add a new function `addCity` to an existing object (type) `FantasyWorld`
function initializeWorld() {
  // `prototype` => modify the FantasyWorld type
  fantasy.FantasyWorld.prototype.addCity = function (cityName) {
    this.cities.add(cityName);
  };
}
 
~~~~~

... and you can reference them back from Java

*FantasyWorld.java*

~~~~~{.java}
                                                                               
public String addAllCities() {
    String[] allCities = new String[]{
        "Midgar", "Kalm", "Fort Condor", "Junon", 
        "Costa del Sol", "North Corel", "Gongaga", 
        "Cosmo Canyon"
    };
    for (String city : allCities) {
        // This is the native method
        addCity(city);
    }
    return showCities();
}

public native void addCity(String city);
 
~~~~~

(note the `addCity` part)

## Part 3: SmartGWT - SmartClient

*Goal:* Can we add our `cities` as a new entry in the `ListGrid` (currently Animals, Office Supplies, Employees)

*war/js/cities.data.js* (file already exists in the codebase)

~~~~~{.javascript}
                                                                               
[
  { cityName: "Midgar" },
  { cityName: "Kalm" },
  { cityName: "Fort Condor" },
  { cityName: "Junon" },
  { cityName: "Costa del Sol" },
  { cityName: "North Corel" },
  { cityName: "Gongaga" },
  { cityName: "Cosmo Canyon" }
]
~~~~~

Create a `DataSource` and a `Record`

~~~~~{.javascript}
                                                                               
// Create the cities DataSource and bind to the data in cities.data.js
var citiesDs = isc.DataSource.create({
  ID: "citiesDS",
  dataFormat: "json",
  dataURL: "js/cities.data.js",
  fields: 
      [ { name: "cityName", title: "Name", type: "text" } ]
});

var citiesRecord = {
  dsName: "citiesDS",
  dsTitle: "Cities"
};
 
~~~~~


How do we get hold of the `ListGrid`?

~~~~~{.javascript}
                                                                               
function addCitiesToGrid() {
  var mainModule = ???
  ...
}
                                                                               
~~~~~

. . . 

Add an inner class (for the sake of this example) called `Registry` to *BuiltInDS.java* 

~~~~~{.java}
                                                                               
@JsType(namespace = "j2js")
public static class Registry {
    private static Map<String, Object> shared = new HashMap<>();

    public static void register(String name, Object o) {
        shared.put(name, o);
    }

    public static Object lookup(String name) {
        return shared.get(name);
    }
}
~~~~~

Register something into it:

*BuiltInDS.java*

~~~~~{.java}
                                                                               
public class BuiltInDS implements EntryPoint {

    public void onModuleLoad() {
        ...
        
        Registry.register("BuiltInDS", this);
        Registry.register("MainListGrid", grid.getOrCreateJsObj());

        initializeWorld();
    }
~~~~~

. . . 

Now we can look it up from our javascript code 

*fantasy.js*

~~~~~{.javascript}
                                                                               
// Doesn't work :-( (due to the RecordGridClickHandler)
function addCitiesToGrid1() {
  var mainGrid = j2js.Registry.lookup("MainListGrid");
  mainGrid.addData(citiesRecord);
}

function addCitiesToGrid() {
  var mainModule = j2js.Registry.lookup("BuiltInDS");
  // ...
}
~~~~~

Some glue code (which can probably be avoided if there is a workaround for the ClassCastException)

*BuiltInDS.java*

~~~~~{.java}
                                                                               
public void addJSData(JavaScriptObject jsObj){
    // conceptually: 
    // this.grid.addData(new DSRecord(jsObj));
    // ^ but the above line fails in the listgrid click handler

    // so: 
    DSRecord dsRecord = new DSRecord(jsObj);
    String name = dsRecord.getDsName();
    String title = dsRecord.getDsTitle();

    // Workaround: rewrap the DSRecord
    this.grid.addData(new DSRecord(title, name));
}
 
~~~~~

Final version of our `addCitiesToGrid`

*war/js/fantasy.js*

~~~~~{.javascript}
                                                                               
function initializeWorld() {
  // `prototype` => modify the FantasyWorld type
  fantasy.FantasyWorld.prototype.addCity = function (cityName) {
    this.cities.add(cityName);
  };
  
  initializeWorld();
  
}

// Create the cities DataSource and bind to the data in cities.data.js
var citiesDs = isc.DataSource.create({
  ID: "citiesDS",
  dataFormat: "json",
  dataURL: "js/cities.data.js",
  fields:
    [ { name: "cityName", title: "Name", type: "text", primaryKey: "true" } ]
});

var citiesRecord = {
  dsName: "citiesDS",
  dsTitle: "Cities"
};

function addCitiesToGrid() {
    // Lookup in the registry
    var mainModule = j2js.Registry.lookup("BuiltInDS");

    // Add our custom JS `ListGridRecord`
    mainModule.addJSData(citiesRecord);
}
 
~~~~~

# Setup notes for Intellij Ultimate

- File > New > Project (or Module) from existing sources
- Choose 'Create project from existing sources'
- Intellij should recognize the Web and GWT facet

The project will not yet compile

- In the module dependencies, add all jars from your SmartGWT distribution `$SMARTGWTEE_HOME/lib`
- Configure the GWT facet
    - compiler parameters: `-generateJsInteropExports -localWorkers 4`
- Configure the Web facet. 
    - Ensure the Web Module Deployment Descriptor points to the `war/WEB-INF/web.xml`
    - Ensure the Web Module resources point to the `war` folder

[1]: https://www.smartclient.com
[2]: http://www.gwtproject.org
[3]: https://github.com/google/j2cl
[4]: http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsJsInterop.html
[5]: https://gwt.googlesource.com/gwt/+/2.8.2/user/src/com/google/gwt/useragent/UserAgent.gwt.xml