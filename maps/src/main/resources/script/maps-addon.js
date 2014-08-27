// namespace for addon
var com = com || {};
com.ponysdk = com.ponysdk || {};
com.ponysdk.addon = com.ponysdk.addon || {};

com.ponysdk.addon.Map = function(pony, params) {

	this.id = params.id;
	this.widgetID = params.widgetID;
	this.widgetElement = params.widgetElement;
	this.pony = pony;

	this.geocoder = new google.maps.Geocoder();

	var latlng = new google.maps.LatLng(-34.397, 150.644);
	var mapOptions = {
		zoom : 8,
		center : latlng,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	}

	this.map = new google.maps.Map(this.widgetElement, mapOptions);
};

// called when widget attached to a parent
com.ponysdk.addon.Map.prototype.onAttach = function(attached) {
};

// update method (server to native)
com.ponysdk.addon.Map.prototype.update = function(data) {
	if (data.find) {
		this.find(data.find);
	} else if (data.center) {
		this.map.setCenter(new google.maps.LatLng(data.center.lat, data.center.lng));
	}
};

com.ponysdk.addon.Map.prototype.find = function(address) {
	var that = this;
	that.geocoder.geocode({
		'address' : address
	}, function(results, status) {
		var r = [];
		for(var i=0; i < results.length; i++) {
			var result = results[i];
			r.push( { 'address': result.formatted_address, 'lat': result.geometry.location.lat(), 'lng': result.geometry.location.lng() } );
		}
		that.pony.sendDataToServer(that.id, {	'geocoder' : r });
	});
};
