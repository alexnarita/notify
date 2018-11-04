const functions = require('firebase-functions');
const admin = require('firebase-admin');
const firebase = require('firebase');

// Input here information from your firebase
// Project overview >> Add app >> </>
// copy data from snippet window
var config = {
    apiKey: "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
    authDomain: "BBBBBBBBBBBBBBBBBB.firebaseapp.com",
    databaseURL: "https://BBBBBBBBBBBBBBBBBB.firebaseio.com",
    projectId: "BBBBBBBBBBBBBBBBBB",
    storageBucket: "BBBBBBBBBBBBBBBBBB.appspot.com",
    messagingSenderId: "000000000000"
  };

admin.initializeApp(config);
firebase.initializeApp(config);

const settings = {timestampsInSnapshots: true};

exports.sendNotification = functions.https.onRequest((request, response) => {
  var params = request.query;

  var to = params.to;
  var text = params.text || 'Your command is complete.';
  var title = params.title || 'Notify';

  if (!to) {
    response.json({success: false, error: 'No "to" param specified.'});
    return;
  }

  var db = firebase.firestore();

  /* The following is line is to avoid warning messages */
  db.settings(settings);

  return db.collection('tokens').doc(to).get().then(function(snap) {

    if (!snap.exists) {
      response.json({success: false, error: 'Invalid "to" param specified.'});
      return;
    }

    var message = {
      notification: {
        title: title,
        body: text,
      },
      token: snap.data()['gcmToken']
    };

    admin.messaging().send(message)
    // admin.messaging().sendToDevice(snap.data()['gcmToken'], payload) //bkp
      .then(() => {
        response.json({success: true});
      })
      .catch((error) => {
        response.json({success: false, error: error});
      });
  });
});
