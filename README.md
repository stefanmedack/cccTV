[![Get it on Google Play](https://upload.wikimedia.org/wikipedia/commons/c/cd/Get_it_on_Google_play.svg)](https://play.google.com/store/apps/details?id=de.stefanmedack.ccctv)
[![Apache License](http://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](http://choosealicense.com/licenses/apache-2.0/)

# cccTV

An Android TV App for the media API of the CCC written in Kotlin.

* https://api.media.ccc.de

This App uses [a fork][c3media-base-fork] of [c3media-base][c3media-base-orig] by [Tobias Preuss][tobias-preuss].

The Logo ["Voctocat"][voctocat] is kindly provided by [Blinry][blinry] under CC BY-NC-SA 4.0 License.

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
[c3media-base-fork]: https://github.com/stefanmedack/c3media-base
[tobias-preuss]: https://github.com/johnjohndoe
[blinry]: https://github.com/blinry
[stefan]: https://github.com/stefanmedack
[voctocat]: https://morr.cc/voctocat/
