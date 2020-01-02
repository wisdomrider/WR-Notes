const express = require("express"),
    utils = require("./Utils").data,
    md5 = require("md5"),
    CryptoJS = require("crypto-js"),
    router = express.Router();


router.post("/:type/login", (req, res, next) => {
    req.params.type = req.params.type.toLowerCase();
    req.body.password = md5(req.body.username + req.body.password);
    utils.models.user.findOne({"username": req.body.username, "password": req.body.password})
        .then((user, err) => {
            if (user) {
                var key = req.params.type === "web" ? "key" : "mkey";
                req.params.type === "web" ? user.key = md5(Date.now()) : user.mkey = md5(Date.now());
                user.save((err) => {
                    if (err) next(err);
                    else
                        res.json({
                            success: true,
                            data: {
                                key: req.params.type === "web" ? user.key : user.mkey
                            },
                            message: "Login successful !"
                        })
                });

            } else {
                res.status(406).json({
                    success: false,
                    message: "Username/Password not matched !"
                })
            }
        })
        .catch(e => next(e));

});

function basicAuth(req, res, next) {
    req.params.type = req.params.type.toLowerCase();
    let auth = req.headers["authorization"];
    if (auth.split("Bearer ").length < 2)
        auth = null;
    if (auth == null) {
        res.status(406).json({success: false, message: "Unauthorized access !"});
        return
    }
    var key1 = req.params.type === "web" ? {key: auth.split("Bearer ")[1].trim()} : {mkey: auth.split("Bearer ")[1].trim()};
    utils.models.user.findOne(key1, (err, user) => {
        if (user) {
            next.user = user;
            next();
        } else
            res.status(406).json({success: false, message: "Unauthorized access !"})
    });
}

router.post("/:type/search/:s", basicAuth, (req, res, next) => {
    utils.models.note.find({user: next.user._id})
        .exec((err, data) => {
            if (err) {
                res.json(err);
                return;
            }
            var selected = [];
            data.forEach(x => {
                x.desc = CryptoJS.AES.decrypt(x.desc, next.user._id.toString()).toString(CryptoJS.enc.Utf8)
                if (x.title.toLowerCase().includes(req.params.s.toLowerCase()) || x.desc.toLowerCase().includes(req.params.s.toLowerCase()))
                    selected.push(x);
            });
            res.json({success: true, data: selected});
        })

});


router.post("/:type/register", (req, res, next) => {
    req.body.password = md5(req.body.username + req.body.password);
    utils.models.user.create(req.body)
        .then((data, err) => {
            if (err) res.json({success: false});
            else
                res.json({success: true})
        })
        .catch(e => res.json({success: false}));
});

router.post("/:type/add/note", basicAuth, (req, res, next) => {
    req.body.user = next.user._id;
    req.body.desc = CryptoJS.AES.encrypt(req.body.desc, next.user._id.toString()).toString();
    utils.models.note.create(req.body)
        .then((note, err) => {
            if (note) {
                res.json({
                    success: true,
                    "message": "Created",
                    data: {
                        "id": note._id
                    }
                })
            } else if (err) {
                res.json({success: false, errors: err.errors})
            }
        }).catch((e) => res.json({success: false, errors: e.errors}))
});
router.post("/:type/edit/note/:id", basicAuth, (req, res, next) => {
    req.body.user = next.user._id;
    req.body.desc = CryptoJS.AES.encrypt(req.body.desc.toString(), next.user._id.toString()).toString();
    utils.models.note.findOneAndUpdate({_id: req.params.id}, req.body, (err) => {
        if (err) next(err);
        else {
            res.json({success: true, "message": "updated !"})
        }
    })
});
router.post("/:type/delete/note/:id", basicAuth, (req, res, next) => {
    req.body.user = next.user._id;
    utils.models.note.findOneAndDelete({_id: req.params.id}, (err) => {
        if (err) res.json({success: false, "error": "unknown id !"});
        else {
            res.json({success: true, "message": "deleted !"})
        }
    })
});
router.get("/:type/notes", basicAuth, (req, res, next) => {
    var id = next.user._id;
    utils.models.note.find({'user': id})
        .then((notes, err) => {
            var id = next.user._id;
            if (err) next(err);
            else {
                notes.forEach(x => {
                    x.desc = CryptoJS.AES.decrypt(x.desc, id.toString()).toString(CryptoJS.enc.Utf8)
                });

                res.json({success: true, data: notes});

            }

        }).catch(e => next(e));

});


module.exports = router;
