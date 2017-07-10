var mongoose = require('mongoose');
var schemas = require('./schemas');

mongoose.connect("mongodb://localhost:27017/mydb");

var UserModel = mongoose.model('user', schemas.userSchema);

UserModel.collection.drop(function (err) {
	if (err) throw err;
	console.log("dropped all records");
	mongoose.disconnect();
});