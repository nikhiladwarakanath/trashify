const express = require('express');

const router = express.Router();

module.exports = () => {
    router.get('/', (req,res,next) => {
        console.log("start of server");
    });

    return router;
};