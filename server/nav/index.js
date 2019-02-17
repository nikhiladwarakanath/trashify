const express = require('express');
const geolib = require('geolib');
const fs = require('fs');

const router = express.Router();


function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1);
  var a =
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ;
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c; // Distance in km
  return d;
}

function deg2rad(deg) {
  return deg * (Math.PI/180)
}

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
    var curLocLat =req.query.latitude
    var curLocLong = req.query.longitude
    var distance = [];
    var fullResponse = {};
    for(i=0;i<allBins.length;i++){
      var curValue = allBins[i]
      var lat = curValue["Latitude"]
      var long = curValue["Longitude"]
      distance[i] = getDistanceFromLatLonInKm(
                    curLocLat,curLocLong,
                    lat, long);
      var k = distance[i]
      fullResponse[k]={"Site type":curValue["Site type"],"latitude":lat,"longitude":long}
    }
    var top5 = Object.keys(fullResponse)
    top5 = top5.sort()
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
