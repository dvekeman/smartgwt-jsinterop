// --------------------------------------------------------------------------------------------------------- //
// Remove all between these lines ->
// --------------------------------------------------------------------------------------------------------- //
// Add a custom label once the GWT module is initialized
// Add a new function `addCity` to an existing object (type) `FantasyWorld`
function initializeWorld() {
  // `prototype` => modify the FantasyWorld type
  fantasy.FantasyWorld.prototype.addCity = function (cityName) {
    this.cities.add(cityName);
  };
  addCitiesToGrid();
}

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

// Doesn't work :-( (don't know why)
function addCitiesToGrid1() {
  var mainGrid = j2js.Registry.lookup("MainListGrid");
  mainGrid.addData(citiesRecord);
}

// Our new function
function addCitiesToGrid() {
  // Lookup in the registry
  var mainModule = j2js.Registry.lookup("BuiltInDS");

  // Add our custom JS `ListGridRecord`
  mainModule.addJSData(citiesRecord);
}// --------------------------------------------------------------------------------------------------------- //
// <- Remove all between these lines
// --------------------------------------------------------------------------------------------------------- //

