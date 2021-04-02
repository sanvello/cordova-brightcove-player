import BrightcovePlayerSDK

@objc(BrightcovePlayer) class BrightcovePlayer : CDVPlugin {

    private var playerView: PlayerViewController?
    private var storyboard: UIStoryboard?
    private var brightcovePolicyKey: String?
    private var brightcoveAccountId: String?

    @objc(playById:)
    func play(_ command: CDVInvokedUrlCommand) {
        
        self.brightcoveAccountId = (command.arguments[0] as? NSNumber)?.stringValue ?? ""
        self.brightcovePolicyKey = (command.arguments[1] as? String) ?? ""

        let videoId = (command.arguments[2] as? NSNumber)?.stringValue ?? ""
        if (videoId == "") || (self.brightcoveAccountId == "") ||  (self.brightcovePolicyKey == "") {
            let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Wrong input parameters")
            commandDelegate.send(pluginResult, callbackId: "01")
        }

        self.playById(videoId)
    }

    private func initPlayerView(_ videoId: String) {
            self.storyboard = UIStoryboard(name: "BrightcovePlayer", bundle: nil)
            self.playerView = self.storyboard?.instantiateInitialViewController() as? PlayerViewController
            self.playerView?.setAccountIds(self.brightcovePolicyKey!, accountId: self.brightcoveAccountId!)
            self.playerView?.setVideoId(videoId)
    }

    private func playById(_ videoId: String) {
        self.initPlayerView(videoId)
        self.viewController.present(self.playerView!, animated: true)
    }
}
