var mongoose = require('mongoose');
var Schema = mongoose.Schema;


var Note = new Schema({
    title: {
        type: String,
        required: true

    },
    desc: {
        type: String,
        required: true
    },

    user: {
        type: String,
        required: true
    }
}, {timestamps: true});
Note.index({title: 'text', desc: 'text'});
module.exports = mongoose.model('Note', Note);