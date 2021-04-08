import BrightcovePlayerSDK

@objc(BrightcovePlayer) class BrightcovePlayer : CDVPlugin, BCOVPlayerCallbackDelegate {    
    
    // MARK: Private properties
    
    private var playerView: PlayerViewController?
    private var storyboard: UIStoryboard?
    private var brightcovePolicyKey: String?
    private var brightcoveAccountId: String?
    private var callbackId: String = ""
    
    
    
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
        self.callbackId = command.callbackId;
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
        self.callbackId = command.callbackId;
        self.playByUrl(videoUrl)
    }
    
    // MARK: Private methods
    
    private func playById(_ videoId: String) {
        self.initPlayerView(videoId, mode: PlaybackModes.Brightcove)
        self.playerView?.delegate = self;
        self.viewController.present(self.playerView!, animated: true)
    }
    
    private func playByUrl(_ videoUrl: String) {
        self.initPlayerView(videoUrl: videoUrl, mode: PlaybackModes.URL)
        self.playerView?.delegate = self;
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
    
    // MARK: Delegate methods
    internal func callBackMessage(callbackMessage: [AnyHashable : Any]) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: callbackMessage)
        commandDelegate.send(pluginResult, callbackId: self.callbackId)
    }
}