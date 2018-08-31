# LampeTorche
===========

#### The goal of this Android application is to provide a simple flashlight for Android devices.
<ul>

  <li>If the device has a camera with a flash you can use it as a flashlight . Simply push the toggle button "Flash off".
  Once activated the flash will remain on until you push again the toggle button "Flash on". So you can navigate to other 
  applications with the flash on.</li><br>
  
  <li>If the device as no flash you can use the screen button, a white screen at max brightness will be displayed. Less light
  than camera flash but still useful at short distance.</li><br>
  
  <li>An original extra feature is implemented : a light morse displaying SOS.
  If both flash and screen are activated and mode SOS is on, flash and screen are synchronized.</li>
</ul>  
  
### Support i18n
  The application is available in the following languages
  <ul>
    <li> English, default</li>
    <li>Chinese (simplified)</li>
    <li> French</li>
    <li>Russian</li>
  </ul>
  
### Android version
<ul>
  <li> master branch:  Android API 14 up to 22 (Lollipop)<li>
  <li> androidM branch:  Android API 23 (Marshmallow) and above, use camera2<li>
  </ul>

### Installation

####System requirement
<ul>
<li>Minimum API level : 14 (platform version 4.0, 4.0.1 and 4.0.2, version code ICE_CREAM_SANDWICH).</li>
<li>Permission : Camera, not mandatory, only for Android Lollipop and below</li>
<li>Feature : Flash, not mandatory.</li>
</ul>

This permission and this feature are mandatory to access the camera flash, but only the flash is used, neither photos nor 
movies are taken.

It seems to work on the latest Lollipop version code despite the fact that the camera api used is deprecated at this
Lollipop api level (21) and replaced by camera2 api. Only tested on emulator.


#### Install on device
Google play : [Lampe Torche](https://play.google.com/store/apps/details?id=tm.android.lampetorche)<br>
The preferred installation location is SD card.

Enjoy !




  
  
    
  
