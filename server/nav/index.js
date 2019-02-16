const express = require('express');
const geolib = require('geolib');
const fs = require('fs');

const router = express.Router();

module.exports = () => {
    router.get('/', ( _req, res, next) => {
        console.log("start of server");
        return res.render('home', {
            page: 'Home'
        });

    });

  router.get('/closeBins', (req,res,next) => {
    let rawData = fs.readFileSync('./data/data.json')
    var allBins = JSON.parse(rawData)
    var curLocLong =73.944158
    var curLocLat = 40.678178
    var distance = [];
    var fullResponse = {};
    for(i=0;i<allBins.length;i++){
      var curValue = allBins[i]
      var lat = curValue["Latitude"]
      var long = curValue["Longitude"]
      distance[i] = geolib.getDistance(
                    {"latitude": curLocLat, "longitude":curLocLong},
                    {"latitude": lat, "longitude": long}
                    );
      var k = distance[i]
      fullResponse[k]={"Site type":curValue["Site type"],"latitude":lat,"longitude":long}
    }
    var top5 = Object.keys(fullResponse).sort()
    top5 = top5.slice(0,5)
    var resp=[]
    for(i=0;i<5;i++){
      resp[i] = fullResponse[top5[i]]
      console.log("response: "+fullResponse[top5[i]])
    }
    res.send(resp)
  });

  router.get('/AllBins', (req,res,next) => {
    let rawData = fs.readFileSync('./data/data.json')
    var allBins = JSON.parse(rawData)
    console.log(allBins)
    res.send(allBins)
  } );



    return router;
};
