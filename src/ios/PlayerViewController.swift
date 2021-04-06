import BrightcovePlayerSDK

// MARK: Enum

public enum PlaybackModes {
    case Brightcove, URL
}

// MARK: Class

class PlayerViewController: UIViewController, BCOVPlaybackControllerDelegate, BCOVPUIPlayerViewDelegate {

    //MARK: Properties

    private var playbackService: BCOVPlaybackService?
    private var playbackController: BCOVPlaybackController?
    private var videoView: BCOVPUIPlayerView?
    
    private var kViewControllerPlaybackServicePolicyKey: String?
    private var kViewControllerAccountID: String?
    private var kViewControllerVideoID: String?
    
    private var kViewControllerVideoUrl: String?
    
    private var kViewControllerPlaybackMode: PlaybackModes!

    @IBOutlet weak var videoContainer: UIView!
    @IBOutlet weak var closeButton: UIButton!


    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.createPlaybackController()
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        self.setupVideoView()
        self.requestContentFromPlaybackService()
    }

    override func viewWillDisappear(_ animated: Bool) {
        //Force switching to portrait mode to fix a UI bug
        let value = UIInterfaceOrientation.portrait.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
    }

    override var prefersStatusBarHidden : Bool {
        return true
    }

    //MARK: Internal Methods
    
    internal func setVideoUrl(_ videoUrl: String) {
        self.kViewControllerVideoUrl = videoUrl
        self.kViewControllerPlaybackMode = PlaybackModes.URL
    }

    internal func setAccountIds(_ policyKey: String, accountId: String) {
        self.kViewControllerPlaybackServicePolicyKey = policyKey
        self.kViewControllerAccountID = accountId
    }

    internal func setVideoId(_ videoId: String) {
        self.kViewControllerVideoID = videoId
        self.kViewControllerPlaybackMode = PlaybackModes.Brightcove
    }

    internal func playFromExistingView() {
        self.createPlaybackController()
        self.setupVideoView()
        
        if (self.kViewControllerPlaybackMode == PlaybackModes.Brightcove) {
            self.requestContentFromPlaybackService()
        } else {
            self.requestContentFromExternalUrl();
        }
    }

    //MARK: Private Methods

    private func createPlaybackController() {
        let sharedSDKManager: BCOVPlayerSDKManager = BCOVPlayerSDKManager.shared()
        self.playbackController = sharedSDKManager.createPlaybackController()
        self.playbackController?.delegate = self
        self.playbackController?.isAutoAdvance = true
        self.playbackController?.isAutoPlay = true
    }
    

    private func requestContentFromPlaybackService() {
        self.playbackService?.findVideo(withVideoID: self.kViewControllerVideoID!, parameters: nil) { (video: BCOVVideo?, jsonResponse: [AnyHashable: Any]?, error: Error?) -> Void in

            if let video = video {
                self.playbackController?.setVideos([video] as NSArray)
            }
        }
    }
    
    private func requestContentFromExternalUrl() {
        let video: BCOVVideo = BCOVVideo.init(url: URL(string: self.kViewControllerVideoUrl ?? "")!)
         self.playbackController?.setVideos([video] as NSArray)
    }

    private func setupVideoView() {
        self.videoView = BCOVPUIPlayerView(playbackController: self.playbackController, options: nil, controlsView: BCOVPUIBasicControlView.withVODLayout())
        self.videoView?.delegate = self
        self.videoView?.frame = self.videoContainer.bounds
        self.videoView?.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        self.videoContainer.addSubview(videoView!)
        self.videoView?.playbackController = self.playbackController
        self.customizeUI()
        
        if (self.kViewControllerPlaybackMode == PlaybackModes.Brightcove) {
            self.playbackService = BCOVPlaybackService(accountId: self.kViewControllerAccountID, policyKey: self.kViewControllerPlaybackServicePolicyKey)
        }
    }

    private func customizeUI() {
        // Hide fullscreen button
        let fullscreenButton: BCOVPUIButton? = self.videoView?.controlsView.screenModeButton
        fullscreenButton?.isHidden = true
    }

    private func clear() {
        self.playbackController = nil
        self.playbackService = nil
        self.kViewControllerVideoID = nil
    }

    //MARK: Delegate Methods
    // Check to docs of BCOVPlaybackControllerDelegate to add other delegate methods

    internal func playbackController(_ controller: BCOVPlaybackController!, session: BCOVPlaybackSession!) {
    }
    
    // Delegate for close on finish
    internal func playbackController(_ controller: BCOVPlaybackController!, didCompletePlaylist playlist: NSFastEnumeration!) {
        self.dismissPlayerView(AnyClass.self)
    }
    // Delegate for showing close button
    internal func playerView(_ playerView: BCOVPUIPlayerView!, controlsFadingViewDidFadeOut controlsFadingView: UIView!) {
        self.closeButton.isHidden = true;
    }
    // Delegate for hiding close button
    internal func playerView(_ playerView: BCOVPUIPlayerView!, controlsFadingViewDidFadeIn controlsFadingView: UIView!) {
        self.closeButton.isHidden = false;
    }
    
    //MARK: Actions

    @IBAction func dismissPlayerView(_ sender: Any) {
        self.dismiss(animated: true, completion: {(_: Void) -> Void in
            self.playbackController?.pause()
            self.clear()
        })
    }
}
