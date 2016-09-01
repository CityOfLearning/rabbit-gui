# rabbit-gui [![Build Status](https://travis-ci.org/CityOfLearning/rabbit-gui.svg?branch=DYN-CityServer)](https://travis-ci.org/CityOfLearning/rabbit-gui)
Graphical framework built on top of Forge Mod Loader and designed to facilitate the creation of graphical user interfaces

##Getting started
We are using [JitPack.io](http://jitpack.io) for build deployment. So you must add this repository to your build script
```
 repositories {
        maven { 
          url "https://jitpack.io" 
        }
 }
```
After that in the dependencies section add:
```
compile 'com.github.FRedEnergy:rabbit-gui:v1.3.0:shaded'
```
You also can use 10 first chars from commit hash as a version

##Cloning
1. Clone repository using `git clone https://github.com/FRedEnergy/rabbit-gui.git`
2. Navigate to the new folder called "rabbit-gui"
3. Install Forge development workspace using `gradlew setupDecompWorkspace`
4. Depending on your IDE:
  * If you are using IDEA run `gradlew idea` command and import project as Gradle project
  * If you are using Eclipse run `gradlew eclipse` and open workspace at `/rabbit-gui/eclipse`
5. In order to build project use command `gradlew build` and navigate to `/rabbit-gui/build/libs` for jar file
