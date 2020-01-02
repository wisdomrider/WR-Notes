const express = require("express"),
    utils = require("./Utils").data,
    CryptoJS = require("crypto-js"),
    router = express.Router();

router.post("/add", (req, res, next) => {
    if (req.body.key === "") {
        req.body.user = req.cookies["id"];
        req.body.desc = CryptoJS.AES.encrypt(req.body.desc, req.body.user.toString()).toString();
        utils.models.note.create(req.body)
            .then((note, err) => {
                if (err) {
                    res.json({success: false});
                } else {
                    res.json({success: true, id: note._id});
                }
            }).catch(e => res.json({success: false}));

    } else {
        if (req.body.title === "DEL" || req.body.title === "DELETE") {
            utils.models.note.findOneAndDelete({_id: req.body.key}, (err) => {
                if (err) res.json({success: false});
                else res.json({success: true})
            })//aa
        } else {
            req.body.user = req.cookies["id"];
            req.body.desc = CryptoJS.AES.encrypt(req.body.desc, req.body.user.toString()).toString();
            utils.models.note.findOneAndUpdate({_id: req.body.key}, req.body, (err, doc) => {
                if (err) {
                    res.json({success: false})
                } else {
                    res.json({success: true})
                }
            }).catch(e => next(e));
        }
    }
});


module.exports = router;