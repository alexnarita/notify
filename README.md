Forked from [mashlol/notify](https://github.com/mashlol/notify)

----

(:warning: **BEWARE!** :warning: My changes _may_ not follow any guideline, good practices or common sense - Honestly...)

Due to my recent studies with the Android Open Source Project kernel ([AOSP](https://source.android.com/)), I found that I was using the [mashlol's notify](https://github.com/mashlol/notify) more often than I care to admit, so I'm forking it with the excuse of learning cloud messaging and to have the peace of mind that I'm not overloading someone else's cloud.

----

# Installation - NPM
The notify CLI is written with node, so you can install it with npm:

```sh
npm install -g notify-cli
```

Alternatively, you can download & use the shell script equivalent from [here](https://github.com/alexnarita/notify/blob/master/sh/notify.sh), with basic support for adding a single key and sending a notification to it.

You'll also need the app on your phone to actually receive the notifications.

# Usage
Using notify is simple. When you download the app to your phone, it will give you a registration key. This key is how your phone is identified by notify. Before you can begin using notify, you should first register your key.

```sh
notify -r myKey
```

After registering, you can use notify as follows:

```sh
someLongRunningCommand ; notify
```

You will receive a push notification to your phone when the command has completed, regardless of whether or not it was successful.

You can also specify what the notification will say, like this:

```
notify --text "My message"
notify -t "Some cool message"
```

--------

# Tips and Tricks
For those who are too lazy to install node and just want to use `.sh` file:

Add this to your `.bashrc`:

```sh
notify-mobile () {
  echo "-----------"
  echo "Sending notification to: "
  cat ~/.notifyreg
  /path/to/your/git/notify/sh/notify.sh --text $1
  echo "-----------"
}
```

Then the fun begins...

```sh
make -j44 | tee make.log ; test ${PIPESTATUS[0]} -eq 0 && notify-mobile "MAKE OK" || notify-mobile "MAKE NOK"
```

---------

# Bottom notes
## Android App
Android App code has been updated to work with the following versions of firebase libs:

- `com.google.firebase:firebase-core:16.0.4`;
- `com.google.firebase:firebase-firestore:17.1.1`;
- `com.google.firebase:firebase-auth:16.0.4`;
- `com.google.firebase:firebase-messaging:17.3.4`.

Fixed 'identifier' `TextView` placement when app is in landscape orientation.

Added one notification channel to allow customization for newer Android SDKs.

## Cloud
Cloud functions were also updated.

- `firebase-admin` from `5.4.2` to `6.1.0`;
- `firebase-functions` from `0.7.1` to `2.1.0`;
- `firebase` from `N/A` to `5.5.5`.


## Firebase Access Key / Tokens / API / URLs etc
Remember to add your **own** customizations to repo files!!

- Download your own `google-services.json` and place it in `android/Notify/app/`;
- Change some info in `cloud/.firebaserc`;
- Change your access url in `sh/notify.sh`;
- Change your config data in `cloud/functions/index.js`;

obs.: Trying to execute/compiling these files **as they are** in the repository will generate **errors**.


## Creating firebase for running your own push notification system
These are some quick steps I took note when trying to configure my own firebase in GCM. (probably not complete... but can be used as a reference)

1) goto https://firebase.google.com/

2) click login
2.1) login with your google account

3) click "GO TO CONSOLE". This will start a firebase instance for ya

4) Create your project (give it a name)
4.1) accept to sell your soul to google (read and agree with EULA and Usage terms)

5) Add your app to firebase (just follow step-by-step instruction)
5.1) for notify app:
	app name is: `com.kevinbedi.notify` (or another, if you modified the package name)
	App nickname: (whatever name you want)
5.2) Click next and download your `google-services.json`
5.3) place it at `android/Notify/app/google-services.json`
5.4) recompile app
5.5) Launch app in your mobile
5.6) click next and check if app pinged your firebase instance

6) adding functions to firebase

See full doc here https://firebase.google.com/docs/functions/get-started

TLDR (may not be in correct order... It may not even be correct at all):
```sh
$ sudo apt install nodejs npm
$ sudo npm install -g firebase-tools
$ cd cloud/functions
$ npm install firebase-functions@latest firebase-admin@latest --save
$ firebase login
 (put your login info in your browser)
$ vim .firebaserc
  (and add your project id)
$ cd ./functions
$ npm install
$ firebase deploy --only functions
```

7) Create a firestore database in your firebase
7.1) When app stores tokens in firestore, it will look like this:
```
tokens:
	$(Token):
		{gcmToken: "QuhasOk1asjdp1230981283aoskd"}
```

and

```
users:
	$(gcmToken):
		{Token: "Qijasl123aO"}
```

obs.: I should really look into a better way to show database schemas...

-----------

# TODOs

- [ ] Add License file as per [mashlol/notify issue #26](https://github.com/mashlol/notify/issues/26).

- [ ] Looking forward to add support for authentication. For each new anonymous user (app) that connects to the firebase, 2 user entries are created. This could use some more work to keep user list tidy and clean (quick note: firebase doesn't provide a way to batch delete users, operation must be done by hand, one user at a time).

------

# License

As of [mashlol/notify issue #26](https://github.com/mashlol/notify/issues/26), license is MIT.

------

# KUDOS

To my sister, [@lnarita](https://github.com/lnarita), for helping me sorting Java things out!

