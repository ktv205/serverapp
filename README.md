# serverapp

This is a app that is used to mimic the functionality of a photo gallery app.

This app has two activites.
1. MainActivity
2.FileSharingActivity

1.MainActivity: In this activity I am copying images from the photo app and saving it to the private images folder
of this app

2.FileSharingActivity is exposed to the apps which sends an implicit intent for image files. So whenever an app
sends intent for image files to android system the android system serches for apps that can share images.In our case
we defined in the android manifest file that this app can share images to other apps. So user is presented with 
selection of apps( one of the app will be the server app). When the user selects this app he will be presented with 
gallery of images to pick from. When the user picks an image we will be grating him the permission to access the image 
as shown here http://developer.android.com/training/secure-file-sharing/share-file.html

Go through the doumentation here http://developer.android.com/training/secure-file-sharing/index.html
