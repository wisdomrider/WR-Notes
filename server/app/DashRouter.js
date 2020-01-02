const express = require("express"),
    utils = require("./Utils").data,
    CryptoJS = require("crypto-js"),
    router = express.Router();

router.use("/notes", require("./NoteRouter"));

router.get("/", (req, res, next) => {
    var id = req.cookies["id"];
    utils.models.note.find({'user': req.cookies["id"]})
        .then((notes, err) => {
            if (err) next(err);
            else {
                notes.forEach(x => {
                    x.desc = CryptoJS.AES.decrypt(x.desc, id.toString()).toString(CryptoJS.enc.Utf8)
                });
                notes.reverse();
                utils.render(res, 'dash', {title: "Dashboard ", data: notes});

            }

        }).catch(e => next(e));
});

router.get("/logout", (req, res, next) => {
    res.cookie("token", null)
        .cookie(utils.constants.successMessage, "Logged out !")
        .redirect("/")
});

module.exports = router;