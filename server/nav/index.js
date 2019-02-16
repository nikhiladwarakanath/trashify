const express = require('express');

const router = express.Router();

module.exports = () => {
    router.get('/', ( _req, res, next) => {
        console.log("start of server");
        return res.render('home', {
            page: 'Home'
        });

    });

    return router;
};