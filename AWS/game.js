var mongoose = require('mongoose');
var schemas = require('./schemas');
var uri = "mongodb://localhost:27017/mydb";

mongoose.connect(uri);

var GameModel = mongoose.model('game', schemas.gameSchema);

GameModel.find({}, function (err, data) {
	if (err) throw err;
	console.log(JSON.stringify(data));
//	console.log(data);
	mongoose.disconnect();
});