var mongoose = require('mongoose');
var schemas = require('./schemas');

mongoose.connect("mongodb://localhost:27017/mydb");

var UserModel = mongoose.model('user', schemas.userSchema);

var myobj = [
  { phone_id: "b1ce82d48fcfa9d5",
    contacts: [
      // { name: 'John', number: 'Highway 71'},
      // { name: 'Peter', number: 'Lowstreet 4'},
      // { name: 'Amy', number: 'Apple st 652'}
    ] 
  },

  // { name: 'Hannah', number: 'Mountain 21'},
  // { name: 'Michael', number: 'Valley 345'},
  // { name: 'Sandy', number: 'Ocean blvd 2'},
  // { name: 'Betty', number: 'Green Grass 1'},
  // { name: 'Richard', number: 'Sky st 331'},
  // { name: 'Susan', number: 'One way 98'},
  // { name: 'Vicky', number: 'Yellow Garden 2'},
  // { name: 'Ben', number: 'Park Lane 38'},
  // { name: 'William', number: 'Central st 954'},
  // { name: 'Chuck', number: 'Main Road 989'},
  // { name: 'Viola', number: 'Sideway 1633'}
];

UserModel.collection.insert(myobj, function(err, res) {
  if (err) throw err;

  console.log("Number of records inserted: " + res.insertedCount);
  mongoose.disconnect();
});