# Adobe Color Tool Library for Android
[![Download](https://api.bintray.com/packages/marcauberer/adobecolortool/com.chillibits%3Aadobecolortool/images/download.svg)](https://bintray.com/marcauberer/adobecolortool/com.chillibits%3Aadobecolortool/_latestVersion)
![Android CI](https://github.com/marcauberer/adobe-color-tool/workflows/Android%20CI/badge.svg)
[![API](https://img.shields.io/badge/API-19%2B-red.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

Adobe Color Tool is a library for transforming lists of colors to binary strings / byte arrays in Adobe ACO / ASE format.

## Try it
If you want to test the library, please visit the sample app on [Google Play](https://play.google.com/store/apps/details?id=com.chillibits.adobecolortoolsample)!

## Usage
The first step for using this library is, to add it to the dependency section in your project:
```gradle
implementation 'com.chillibits:adobecolortool:1.0.0-alpha02'
```
Also you have to declare a file provider in your manifest, which should look similar to this one:
```xml
<application>
    <!-- ... -->
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="com.chillibits.adobecolorsample"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
</application>
```
Remember to replace `com.chillibits.adobecolorsample` with your package name.

Furthermore, please create  the following file in your `res/xml/file_paths.xml` directory to specify the file path rules of the file provider:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <files-path path="/" name="allfiles" />
</paths>
```

## Export colors
The colors have to be provided as variable of the type `List<AdobeColor>`. <br>
This list of colors can then be exported in ACO format like this:
```kotlin
val colors = listOf(
    AdobeColor(Color.rgb(173, 13, 52), "ad0d34"),
    AdobeColor(Color.rgb(199, 122, 49), "c77a31"),
    AdobeColor(Color.rgb(241, 15, 107), "f10f6b")
)
AdobeColorTool(this).exportColorListAsACO(colors)
```
*Note: Before you call the `exportColorListAsACO` function, you have to request the `WRITE_EXTERNAL_STORAGE` permission. Otherwise, the app will crash with an error.*

And in the ASE format like this:
```kotlin
AdobeColorTool(this).exportColorListAsASE(colors)
```
The ASE format is also capable of saving the palette name (to be exact the group name). This can be done like this:
```kotlin
AdobeColorTool(this).exportColorListAsASE(colors, "My palette")
```

## Import colors
You can call the `importColorList` method to bring up a dialog for picking a single ACO or ASE file.
```kotlin
AdobeColorTool(this).importColorList(this, object: AdobeColorTool.AdobeImportListener {
    override fun onComplete(colors: List<AdobeColor>) {
        /* Your code */
    }

    override fun onCancel() {
        /* Your code */
    }
})
```
The selected file will automatically be analyzed, whether it is a ACO or ASE file and you get back a object of type `List<AdobeColor>` in the `onComplete` callback method.

## More in-depth technical information
The following remarks are based on the following data:
- Palette name: `AdobeColorTool`
- Colors:
  - Name: `ad0d34`, Color: `#AD0D34 (RGB: 173, 13, 52)`
  - Name: `c77a31`, Color: `#C77A31 (RGB: 199, 122, 49)`
  - Name: `f10f6b`, Color: `#F10F6B (RGB: 241, 15, 107)`

### Structure of the ACO color format
This is the resulting ACO file in byte-wise hex encoding, using the example data from above:
```
[ 0x00 0x01 0x00 0x03 0x00 0x00 0xAD 0xAD 0x0D 0x0D 0x34 0x34 0x00 0x00 0x00 0x00
  0xC7 0xC7 0x7A 0x7A 0x31 0x31 0x00 0x00 0x00 0x00 0xF1 0xF1 0x0F 0x0F 0x6B 0x6B
  0x00 0x00 0x00 0x02 0x00 0x03 0x00 0x00 0xAD 0xAD 0x0D 0x0D 0x34 0x34 0x00 0x00
  0x00 0x00 0x00 0x06 0x00 0x61 0x00 0x64 0x00 0x30 0x00 0x64 0x00 0x33 0x00 0x34
  0x00 0x00 0xC7 0xC7 0x7A 0x7A 0x31 0x31 0x00 0x00 0x00 0x00 0x00 0x06 0x00 0x63
  0x00 0x37 0x00 0x37 0x00 0x61 0x00 0x33 0x00 0x31 0x00 0x00 0xF1 0xF1 0x0F 0x0F
  0x6B 0x6B 0x00 0x00 0x00 0x00 0x00 0x06 0x00 0x66 0x00 0x31 0x00 0x30 0x00 0x66
  0x00 0x36 0x00 0x62 ]
```

This binary data can be split up like this:
![ACO format byte visualization](https://github.com/marcauberer/adobe-color-tool/raw/main/media/aco_structure.png)


**More information / Sources:**
- [http://www.nomodes.com/aco.html](http://www.nomodes.com/aco.html)
- [https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577411_pgfId-1055819](https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577411_pgfId-1055819)

### Structure of the ASE color format
This is the resulting ASE file in byte-wise hex encoding, using the example data from above:
```
[ 0x41 0x53 0x45 0x46 0x00 0x01 0x00 0x00 0x00 0x00 0x00 0x05 0xC0 0x01 0x00 0x00
  0x00 0x16 0x00 0x0A 0x00 0x49 0x00 0x6D 0x00 0x61 0x00 0x67 0x00 0x69 0x00 0x6E
  0x00 0x61 0x00 0x72 0x00 0x79 0x00 0x00 0x00 0x01 0x00 0x00 0x00 0x22 0x00 0x07
  0x00 0x61 0x00 0x64 0x00 0x30 0x00 0x64 0x00 0x33 0x00 0x34 0x00 0x00 0x52 0x47
  0x42 0x20 0x3F 0x2D 0xAD 0xAE 0x3D 0x50 0xD0 0xD1 0x3E 0x50 0xD0 0xD1 0x00 0x02
  0x00 0x01 0x00 0x00 0x00 0x22 0x00 0x07 0x00 0x63 0x00 0x37 0x00 0x37 0x00 0x61
  0x00 0x33 0x00 0x31 0x00 0x00 0x52 0x47 0x42 0x20 0x3F 0x47 0xC7 0xC8 0x3E 0xF4
  0xF4 0xF5 0x3E 0x44 0xC4 0xC5 0x00 0x02 0x00 0x01 0x00 0x00 0x00 0x22 0x00 0x07
  0x00 0x66 0x00 0x31 0x00 0x30 0x00 0x66 0x00 0x36 0x00 0x62 0x00 0x00 0x52 0x47
  0x42 0x20 0x3F 0x71 0xF1 0xF2 0x3D 0x70 0xF0 0xF1 0x3E 0xD6 0xD6 0xD7 0x00 0x02
  0xC0 0x02 0x00 0x00 0x00 0x00 ]
```

This binary data can be split up like this:
![ASE format byte visualization](https://github.com/marcauberer/adobe-color-tool/raw/main/media/ase_structure.png)

**More information / Sources:**
- [https://www.cyotek.com/blog/reading-adobe-swatch-exchange-ase-files-using-csharp](https://www.cyotek.com/blog/reading-adobe-swatch-exchange-ase-files-using-csharp)
- [https://www.cyotek.com/blog/writing-adobe-swatch-exchange-ase-files-using-csharp](https://www.cyotek.com/blog/writing-adobe-swatch-exchange-ase-files-using-csharp)
- [http://www.selapa.net/swatches/colors/fileformats.php#adobe_ase](http://www.selapa.net/swatches/colors/fileformats.php#adobe_ase)

## Supported languages
Here are the currently supported languages for this library.

-   English
-   German
-   French

New translations are highly appreciated. If you want to translate the lib, please open a pr.

## Contribute to the project
If you want to contribute to this project, please feel free to send us a pull request.

## Credits
Thanks to all contributors and translators!

© Marc Auberer 2020-2021