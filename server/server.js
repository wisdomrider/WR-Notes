const express = require('express')
    , app = express(),
    config = require("./Config"),
    cookie = require("cookie-parser"),
    createError = require('http-errors'),
    path = require("path"),
    md5 = require("md5"),
    mongoose = require("mongoose");

require("dotenv").config();


app.use(function (req, res, next) {
    next(createError(404));
});

app.use(function (err, req, res, next) {
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};
    res.status(err.status || 500);
    res.render('error', {
        error: err
    });
    //asdad
});

app.listen(process.env.PORT || 8280, () => {
    console.log("listerning");
});


