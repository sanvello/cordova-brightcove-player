var exec = require("cordova/exec");

exports.playById = function (accountId, policyKey, videoId, success, error) {
  exec(success, error, "BrightcovePlayer", "playById", [
    accountId,
    policyKey,
    videoId,
  ]);
};

exports.playByUrl = function (videoUrl, success, error) {
  exec(success, error, "BrightcovePlayer", "playByUrl", [videoUrl]);
};
