
<h1 align="center">Android Simple Permission Manager</h1>
<p align="center">
  <a href="https://app.codacy.com/manual/a-anand-91119/SimplePermissionManager?utm_source=github.com&utm_medium=referral&utm_content=a-anand-91119/SimplePermissionManager&utm_campaign=Badge_Grade_Dashboard" rel="nofollow"><img src="https://camo.githubusercontent.com/8bfcb96fada23923d178481242a6574bd3388f1d/68747470733a2f2f6170692e636f646163792e636f6d2f70726f6a6563742f62616467652f47726164652f6134646434613739393266663465393738666465386565363038363137366564" alt="Codacy Badge" data-canonical-src="https://api.codacy.com/project/badge/Grade/e3f3cd9db8a646e8a3bc7f3e59e45706" style="max-width:100%;"></a>
  <a href="https://jitpack.io/#a-anand-91119/SimplePermissionManager"> <img src="https://jitpack.io/v/a-anand-91119/SimplePermissionManager/month.svg" /></a>
  <a href="https://jitpack.io/#a-anand-91119/SimplePermissionManager"> <img src="https://jitpack.io/v/a-anand-91119/SimplePermissionManager.svg" /></a>
  <a href="https://circleci.com/gh/a-anand-91119/SimplePermissionManager/tree/master"> <img src="https://circleci.com/gh/a-anand-91119/SimplePermissionManager/tree/master.svg?style=shield" /></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <br /><br />
  Simple Permission Manager that help you to manage permissions in Android Marshmallow and above.
</p>


# Support Simple Permission Manager

Simple Permission Manager is an independent project with ongoing development and support. If you wish to support my works by donating please consider

  - [One-time donation via PayPal](https://www.paypal.me/notyouraveragedev)
  - [Become a backer or sponsor on Patreon](https://www.patreon.com/not_your_average_dev)

<a href="https://www.patreon.com/join/not_your_average_dev?" alt="Become a Patron"><img src="https://c5.patreon.com/external/logo/become_a_patron_button.png" /></a>

# Simple Permission Manager

Simple Permission Manager is an open-sourced android library to request and manage permissions at runtime on android versions M and above. The library has been created with one thing in mind, <strong> Make Permission Management Easy for Developers</strong>. Use can leave Android Runtime Permission to SimplePermissionManager and focus on creating your application.

Advantages of using SimplePermissionManager are
 
  - Requests a Single permission.
  - Request Group of permissions together.
  - If permissions are denied, a custom Alert Dialog will be shown explaining why ther permission is needed, before requesting a second time.
  - If permissions are permanently denied, automatically open Application Info Page in settings to manually grant requests.
  - Developer need not keep track of permission statuses, SimplePermissionManager will do it for you.
  - No need to override onRequestPermissionsResult() method and check permission status. SimplePermissionManager will provide you with a PermissionResponse that denotes whether a Permission has been granted, denied or permanently denied.
  - Irrespective of the permission status, you can make permission requests and SimplePermissionManager will take care of requesting permission, showing alert dialog, and opening settings.
  - Simple and easy Builder to create PermissionManager

 Responses returned using PermissionResponseListener
  - <strong>PermissionManager.PERMISSION_GRANTED (0)</strong>
  - <strong>PermissonManager.PERMISSION_DENIED (1)</strong>
  - <strong>PermissionManager.PERMISSION_PERMANENTLY_DENIED (-3)</strong>

## How to integrate into your app

Integrating the library into you app is extremely easy. A few changes in the build gradle and your all ready to use Simple Preference Manager. Make the following changes.

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```
Step 2. Add the dependency

```java
dependencies {
        implementation 'com.github.a-anand-91119:SimplePermissionManager:<latest-version>'
}
```

## How to use the library
Once you integrated the library in your project but **how do you use it**? Well its really easy just follow the steps below.

```java

// Create a PermissionManager with all the features
// This PermissionManager will display a toast and open settings page for permanently denied permissions
PermissionManager permissionManager = PermissionManagerBuilder.withContext(this)
                .addPermissionResponseListener(new PermissionResponseListener() {
                    @Override
                    public void singlePermissionResponse(PermissionResponse permissionResponse) {
                    	// Single permission requests responses
                    }

                    @Override
                    public void multiplePermissionResponse(List<PermissionResponse> permissionResponses) {
                    	// Group permission requests responses
                    }
                }).build();

// For showing a SnackBar instead of Toast and an action to open settings, 
// provide the view that the snackbar can use to find parent
PermissionMananger permissionManager = PermissionManagerBuilder.withContext(this)
                .addPermissionResponseListener(new PermissionResponseListener() {
                    @Override
                    public void singlePermissionResponse(PermissionResponse permissionResponse) {
                        // Single permission requests responses
                    }

                    @Override
                    public void multiplePermissionResponse(List<PermissionResponse> permissionResponses) {
                        // Group permission requests responses
                    }

                }).enableSnackbarForSettings(findViewById(R.id.layout_container))
                .build();


// To check whether a permission has been granted or not use hasPermission()
boolean status = permissionManager.hasPermission(Manifest.permission.CALL_PHONE);


// To check the status of multiple permissions, pass the permissions as argument to hasPermissions()
List<PermissionResponse> permissionResponses = permissionManager.hasPermissions(Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS);

// To request for a permission use the requestPermission()
// The user response will be available in the singlePermissionResponse() callback of PermissionResponseListener
// A default message will be displayed before requesting previously denied permissions
permissionManager.requestPermission(Manifest.permission.CAMERA);
// You can use use the overloaded message to display a custom message in alert dialog
permissionManager.requestPermission(Manifest.permission.CAMERA, "Reason Why My App Needs This Permission");


// To request a group of permissions, use the method requestPermissions()
permissionManager.requestPermissions(Manifest.permission.CAMERA, 
                Manifest.permission.RECORD_AUDIO, 
                Manifest.permission.READ_CONTACTS);
// To display a custom message use the requestPermissions() method accepting a message and String[] of permissions
permissionManager.requestPermissions("Reason Why Group Of Permissions Are Needed For My App",
                new String[]{Manifest.permission.CAMERA, 
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_CONTACTS});
```
That's pretty much it. All the users responses will be notified to you using the attached PermissionResponseListener.

## Author
Maintained by A Anand [Not Your Average Dev](https://notyouraveragedev.in)

## Contribution

  - Bug reports and pull requests are welcome.

## Change Log
  - Version 1.0.1
     - minor changes
  - version 1.0
     - initial commit
    
## License
  ```
  MIT License

  Copyright (c) 2020 A Anand

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
  ```
