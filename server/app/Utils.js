f = {};
f.checkIfLogged1st = (req, res, next) => {
    let cookie = req.cookies["token"];
    if (cookie == null) {
        next();
        return
    }
    f.models.user.findOne({"cookie": cookie})
        .then((user, err) => {
            if (err) next(err);
            else if (user) {
                res.redirect("/dash");
            } else next();
        })
        .catch(e => next(e));
};


f.checkIfLogged = (req, res, next) => {
    let cookie = req.cookies["token"];
    if (cookie == null) res.redirect("/");
    else
        f.models.user.findOne({"cookie": cookie})
            .then((user, err) => {
                if (err) next(err);
                else if (user) {
                    res.cookie("id", user._id, true);
                    next();
                } else res.redirect("/");
            })
            .catch(e => next(e));
};

f.models = {
    user: require("../models/User"),
    note: require("../models/Notes")
};

f.constants = {
    successMessage: "successMessage",
    errorMessage: "errorMessage",
    warningMessage: "warningMessage"
};

f.render = (res, page, vars = {}) => {
    var successMessage = res.req.cookies["successMessage"];
    var errorMessage = res.req.cookies[f.constants.errorMessage];
    var warningMessage = res.req.cookies[f.constants.warningMessage];
    if (errorMessage !== undefined)
        vars[f.constants.errorMessage] = errorMessage;
    if (successMessage !== undefined)
        vars[f.constants.successMessage] = successMessage;
    if (warningMessage !== undefined)
        vars[f.constants.warningMessage] = warningMessage;
    res.clearCookie(f.constants.successMessage);
    res.clearCookie(f.constants.warningMessage);
    res.clearCookie(f.constants.errorMessage);
    res.render(page, vars);
};


exports.data = f;