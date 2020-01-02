const express = require('express')
    , app = express(),
    config = require("./Config"),
    cookie = require("cookie-parser"),
    createError = require('http-errors'),
    path = require("path"),
    md5 = require("md5"),
    utils = require("./app/Utils").data,
    mongoose = require("mongoose");

app.use(cookie());
app.use(express.urlencoded());
app.use(express.json());
app.get('/', utils.checkIfLogged1st, (req, res, next) => {
    utils.render(res, "index", {title: "Welcome !"})
});
app.set("view engine", "ejs");
app.set('views', path.join(__dirname, 'views'));
app.use("/", express.static(path.join(__dirname, "public")));
mongoose.set('useNewUrlParser', true);
mongoose.set('useFindAndModify', false);
mongoose.set('useCreateIndex', true);
var conn = mongoose.connect(config.config, config.mongo);

conn.then((db) => {
    console.log("Connected...")
}, (err) => console.log(err));


//routing


app.use("/dash", utils.checkIfLogged, require("./app/DashRouter"));
app.use("/api", require("./app/ApiRouter"));
app.post("/", (req, res, next) => {
    req.body.password = md5(req.body.username + req.body.password);
    utils.models.user.findOne({"username": req.body.username, "password": req.body.password})
        .then((user, err) => {
            if (user) {
                user.cookie = md5(req.body.username + new Date());
                user.save((err) => {
                    res.cookie("token", user.cookie, true)
                        .cookie("id", user._id, true)
                        .cookie(utils.constants.successMessage, "Logged successfully !")
                        .redirect("/dash");
                });

            } else {
                res.cookie(utils.constants.errorMessage, "Username/Password not matched !")
                    .redirect("/");
            }
        })
        .catch(e => next(e));
});
app.post("/register", (req, res, next) => {
    req.body.password = md5(req.body.username + req.body.password);
    utils.models.user.create(req.body)
        .then((data, err) => {
            if (err) next(err);
            else
                res.cookie(utils.constants.successMessage, "Account created !")
                    .redirect("/");
        })
        .catch(e => next(e));
});


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
});

app.listen(process.env.PORT || 8080, () => {
    console.log("listerning");
});


