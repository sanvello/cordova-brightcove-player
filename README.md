# Brightcove video player Cordova plugin

## Work

- [x] Rename plugin reference to Cordova.videoPlayer

### Android

- [x] Basic video playback

- [x] CC support

- [x] Migrate to gradle for dependencies

- [x] Full screen on start

- [x] Exit on end

- [x] Playback callback

- [x] Fix white line at the bottom

- [x] Add play by url

- [ ] Landscape mode

### iOS

- [x] Make it work

- [x] CC support

- [x] Migrate to cocoapods (migrated on main, look into updatedPodsFile)

- [x] Full screen on start

- [x] Exit on end

- [x] Playback callback

- [x] Fix white line at the bottom

- [x] Landscape mode

- [x] Add play by url

## Usage

```Javascript
    cordova.plugins.videoPlayer.playById(accountId, policyKey, videoId, success, error)
```

```Javascript
    cordova.plugins.videoPlayer.playByUrl(videoUrl, success, error)
```
