const express = require("express"),
    router = express.Router();

router.get("/",(req,res)=>{
    console.log("watch");

});

router.get("/a",(req,res)=>{
    console.log("ASdasda");
    console.log("wasda");
});

module.exports = router;
