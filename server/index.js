const express = require('express')
const app = express();
const path = require('path');
const nav = require('./nav');



app.use('/',nav());  
app.use(express.static('public'));
app.get('/favicon.ico',(req,res,next) => {
  return res.sendStatus(204);
});

app.listen(7676, () => {
  console.log('Example app listening on port 7676!')
});

module.export = app;