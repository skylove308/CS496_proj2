var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var tmpSchema = new Schema ({
	name: String,
	number: String
});

var userSchema = new Schema ({
	phone_id: {type: String, require: true},
	contacts: [
		{name: String, number: String}
	],
	photos: [
		{photo_id: String, bitmap: String}
	]
});

exports.tmpSchema = tmpSchema;
exports.userSchema = userSchema;