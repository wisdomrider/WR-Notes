var mongoose = require('mongoose');
var Schema = mongoose.Schema;


var User = new Schema({
    username: {
        type: String,
        required: true,
        unique: true

    },
    name: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true
    },
    cookie: {
        type: String
    },
    key: {
        type: String,
        required: false
    },
    mkey: {
        type: String,
        required: false
    }
});
module.exports = mongoose.model('User', User);