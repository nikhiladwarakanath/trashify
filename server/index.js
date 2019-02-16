const express = require('express')
const app = express();
const path = require('path');
const nav = require('./nav');
const bodyParser = require('body-parser');

app.set('view engine','ejs');
if (app.get('env') === 'development'){
    app.locals.pretty = true;
}
app.set('views',path.join(__dirname,'./views'));

app.use(bodyParser.urlencoded({extended:true}));
app.use('/',nav());  
app.use(express.static('public'));
app.get('/favicon.ico',(req,res,next) => {
  return res.sendStatus(204);
});

app.listen(7676, () => {
  console.log('Example app listening on port 7676!')
});

module.export = app;