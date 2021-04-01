var exec = require("cordova/exec");

exports.playById = function (accountId, policyKey, videoId, success, error) {
  exec(success, error, "BrightcovePlayer", "play", [
    accountId,
    policyKey,
    videoId,
  ]);
};

// exports.init = function(policyKey, accountId, success, error) {
//     exec(success, error, 'BrightcovePlayer', 'initAccount', [policyKey, accountId])
// };

// exports.switchAccountTo = function(policyKey, accountId, success, error) {
//     exec(success, error, 'BrightcovePlayer', 'switchAccount', [policyKey, accountId])
// }
