[![Apache License](http://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](http://choosealicense.com/licenses/apache-2.0/)

[<img src="https://upload.wikimedia.org/wikipedia/commons/c/cd/Get_it_on_Google_play.svg" alt="Get it on Google Play" width="230">][play] &nbsp;&nbsp;&nbsp; **OR** [get an APK][releases]

# cccTV

An Android TV App for the media API of the Chaos Computer Club e.V. (CCC) written in Kotlin.

* https://media.ccc.de

This App uses [c3media-base][c3media-base-orig] by [Tobias Preuss][tobias-preuss].
In case Gradle can not find this dependency, check out the sources and deploy them locally.

The Logo ["Voctocat"][voctocat] is kindly provided by [Blinry][blinry] under CC BY-NC-SA 4.0 License.

## Screenshots

<img src="https://lh3.googleusercontent.com/VWveX4oHmYityFLHKI3Dsogmt4bEbSYthDWv-8sbgSWZDB452HhEmfni1Sczey-33w=h900-rw" alt="Screenshot #1"
width="45%"> <img src="https://lh3.googleusercontent.com/onhizPzjofBqFLTlVLX65kTWfJXK-hZMIbnLBcD70P_Rk4InQkdFpjXKU6JK6ms8BCs=h900-rw" 
alt="Screenshot #2" width="45%">

<img src="https://lh3.googleusercontent.com/NFzZtTw73dvGmq54zgTtfNEziEj-c2JAneLjgEh1rQRWeloErCe0gJPunAb5mLFhxw=h900-rw" alt="Screenshot #3" 
width="45%"> <img src="https://lh3.googleusercontent.com/3HPO32nkCoSLMHfKRhDoshhpTOpafTnl40SYSysqD3ouSku9eao2C8pQFslvkDrKgA=h900-rw" 
alt="Screenshot #4" width="45%">

## Setup

In the root project you can find `gradle.properties` defining the signing configuration for the `release`-Build.
If you want to build a `release` version, it is important to replace the placeholders defined there by correct signing credentials.

## Author

* [Stefan Medack][stefan]

## License

    Copyright 2017 Stefan Medack

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[c3media-base-orig]: https://github.com/johnjohndoe/c3media-base
[tobias-preuss]: https://github.com/johnjohndoe
[blinry]: https://github.com/blinry
[stefan]: https://twitter.com/Zonic03
[voctocat]: https://morr.cc/voctocat/

[play]: https://play.google.com/store/apps/details?id=de.stefanmedack.ccctv
[releases]: https://github.com/stefanmedack/cccTV/releases