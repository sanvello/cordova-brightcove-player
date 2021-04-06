import BrightcovePlayerSDK

@objc(BrightcovePlayer) class BrightcovePlayer : CDVPlugin {
    
    // MARK: Private properties

    private var playerView: PlayerViewController?
    private var storyboard: UIStoryboard?
    private var brightcovePolicyKey: String?
    private var brightcoveAccountId: String?
    
    // MARK: Cordova links

    @objc(playById:)
    func playById(_ command: CDVInvokedUrlCommand) {
        if (command.arguments.count != 3) {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_INVALID_ACTION, messageAs: "Wrong input parameters")
            commandDelegate.send(pluginResult, callbackId: "01")
        }
        self.brightcoveAccountId = (command.arguments[0] as? NSNumber)?.stringValue ?? ""
        self.brightcovePolicyKey = (command.arguments[1] as? String) ?? ""

        let videoId = (command.arguments[2] as? NSNumber)?.stringValue ?? ""
        if (videoId == "") || (self.brightcoveAccountId == "") ||  (self.brightcovePolicyKey == "") {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Wrong input parameters")
            commandDelegate.send(pluginResult, callbackId: "01")
            
        }

        self.playById(videoId)
    }
    
    @objc(playByUrl:)
    func playByUrl(_ command: CDVInvokedUrlCommand) {
        if (command.arguments.count != 1) {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_INVALID_ACTION, messageAs: "Wrong input parameters")
            commandDelegate.send(pluginResult, callbackId: "01")
        }
        let videoUrl = (command.arguments[0] as? String) ?? ""
        
        if (videoUrl == "") {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Wrong input parameters")
            commandDelegate.send(pluginResult, callbackId: "01")
            
        }

        self.playByUrl(videoUrl)
    }
    
    // MARK: Private methods
    
    private func playById(_ videoId: String) {
        self.initPlayerView(videoId, mode: PlaybackModes.Brightcove)
        self.viewController.present(self.playerView!, animated: true)
    }
    
    private func playByUrl(_ videoUrl: String) {
        self.initPlayerView(videoUrl: videoUrl, mode: PlaybackModes.URL)
        self.viewController.present(self.playerView!, animated: true)
    }

    private func initPlayerView(_ videoId: String = "", videoUrl: String = "", mode: PlaybackModes) {
            self.storyboard = UIStoryboard(name: "BrightcovePlayer", bundle: nil)
            self.playerView = self.storyboard?.instantiateInitialViewController() as? PlayerViewController
        if (mode == PlaybackModes.Brightcove) {
            self.playerView?.setAccountIds(self.brightcovePolicyKey!, accountId: self.brightcoveAccountId!)
            self.playerView?.setVideoId(videoId)
        } else {
            self.playerView?.setVideoUrl(videoUrl)
        }
    }
}