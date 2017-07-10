var mongoose = require('mongoose');
var schemas = require('./schemas');
var bodyParser = require('body-parser');
var express = require('express');

var app = express();
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());

mongoose.connect("mongodb://localhost:27017/mydb");

var UserModel = mongoose.model('user', schemas.userSchema);


app.get('/', (req, res) => {
	res.send("GET");
});


app.post('/add', (req, res) => {

	var id = req.body.id;
	var mode = req.body.mode;
	var newName = req.body.newName;
	var newNumber = req.body.newNumber;

	if (mode == 1) {
		var oldName = req.body.oldName;
		var oldNumber = req.body.oldNumber;
		var oldItem = {name: oldName, number: oldNumber};
	}

	console.log(id + " " + newName + " " + newNumber + " " + mode);

	var condition = {phone_id: id};
	var contactItem = {name: newName, number: newNumber};

	UserModel.find(condition, function (err, data) {
		if (err) throw err;
		if (data.length > 0) {
			console.log("PHONE ID EXISTS");

			if (mode == 0) {
				UserModel.collection.update(condition, {"$push": {"contacts": contactItem }}, function(err, res) {
					if (err) throw err;
					console.log("inserted!");
				});
			} else if (mode == 1) {
				UserModel.collection.update(condition, {"$pull": {"contacts": oldItem }}, function(err, res) {
					if (err) throw err;
					console.log("updated!");
				});
				UserModel.collection.update(condition, {"$push": {"contacts": contactItem }}, function(err, res) {
					if (err) throw err;
					console.log("inserted!");
				});
			} else {
				console.log("something went wrong");
			}

			res.send(true);	
		} else {
			console.log("PHONE ID DOESNT EXIST");
			res.send("");
		}
	});
});


app.post('/validate', (req, res) => {
	var id = req.body.id;

	UserModel.find({phone_id: id}, function(err, data) {
		if (err) throw err;
		if (data.length > 0) {
			console.log("Validating: phone exists");
			res.send(true);
		} else {
			console.log("Validating: phone doesn't exist");
			res.send(false);
		} 
	});
});


app.post('/syncTo', (req, res) => {
	var id = req.body.id;
	var contacts = req.body.contacts;
	var json = JSON.parse(contacts);
	console.log(json);

	var item = {"phone_id": id, contacts: json};

	UserModel.collection.insert(item, function(err, res) {
		if (err) throw err;
		console.log("Number of records inserted: " + res.insertedCount);
	});

	res.send(true);
});


app.post('/syncFrom', (req, res) => {
	var id = req.body.id;

	UserModel.find({phone_id: id}, function(err, data) {
		if (err) throw err;
		if (data.length > 0) {
			res.send(data[0]);
			console.log("sent " + data[0]);
		} else {
			console.log("syncFrom Error");
			res.send("");
		} 
	});
});

app.post('/delete', (req, res) => {

	var id = req.body.id;
	var name = req.body.name;
	var number = req.body.number;

	var condition = {phone_id: id};
	var contactItem = {name: name, number: number};

	UserModel.find(condition, function (err, data) {
		if (err) throw err;
		if (data.length > 0) {
			console.log("PHONE ID EXISTS");

			UserModel.collection.update(condition, {"$pull": {"contacts": contactItem }}, function(err, res) {
				if (err) throw err;
				console.log("deleted!");
			});

			res.send(true);	
		} else {
			console.log("PHONE ID DOESNT EXIST");
			res.send("");
		}
	});
});

app.post('/syncGallery', (req, res) => {
	var id = req.body.id;

	UserModel.find({phone_id: id}, function(err, data) {
		if (err) throw err;

		if (data.length > 0) {
			res.send(data[0]["photos"]);
		} else {
			console.log("syncFrom Error");
			res.send("");
		}
	});
});

app.post('/addGallery', (req, res) => {
	var id = req.body.id;
	var photo_id = req.body.photo_id;
	var bitmap = req.body.bitmap;
	console.log("ADD " + id + " " + photo_id);
	var photoItem = {bitmap: bitmap, photo_id: photo_id};
	var condition = {phone_id: id};

	UserModel.collection.update(condition, {"$push": {"photos": photoItem }}, function(err, res) {
		if (err) throw err;
		console.log("inserted!");
	});

	res.send("");
});

app.post('/deleteGallery', (req, res) => {
	var id = req.body.id;
	var photo_id = req.body.photo_id;
	var bitmap = req.body.bitmap;
	console.log("DELETE " + id + " " + photo_id);
	var photoItem = {photo_id: photo_id, bitmap: bitmap};
	var condition = {phone_id: id};

	UserModel.collection.update(condition, {"$pull": {"photos": photoItem }}, function(err, result) {
		if (err) throw err;
		console.log("deleted ");
	});

	res.send("");
});


app.listen(3000, () => console.log('Server running on port 3000'));