export interface DocumentScannerConfiguration
{
    /**
    * The minimum score in percent (0 - 100) of the perspective distortion to accept a detected document.
    * Default is 75.0.
    */
    acceptedAngleScore?: number;
    /**
    * The minimum document width or height in percent (0 - 100) of the screen size to accept a detected document.
    * Default is 80.0.
    */
    acceptedSizeScore?: number;
    /**
    * Controls whether the auto-snapping toggle button is hidden or not.
    */
    autoSnappingButtonHidden?: boolean;
    /**
    * Title of the auto-snapping toggle button.
    */
    autoSnappingButtonTitle?: string;
    /**
    * When auto-snapping is enabled the document scanner will take a photo automatically
    * when a document is detected, conditions are good and the auto-snapping time-out elapses. In this
    * mode the user can still tap the shutter button to snap a document.
    */
    autoSnappingEnabled?: boolean;
    /**
    * Controls the auto-snapping speed. Sensitivity must be within the 0..1 range.
    * A value of 1.0 triggers automatic capturing immediately, a value of 0.0 delays the automatic by 3 seconds.
    * The default value is 0.66 (2 seconds)
    */
    autoSnappingSensitivity?: number;
    /**
    * The background color of the bottom shutter-bar.
    */
    bottomBarBackgroundColor?: string;
    /**
    * The color of the title of all buttons in the bottom shutter-bar (Cancel button, etc.),
    * as well as the camera permission prompt button.
    */
    bottomBarButtonsColor?: string;
    /**
    * The color of the camera background (relevant only when the camera preview mode is CameraPreviewMode.FIT_IN).
    */
    cameraBackgroundColor?: string;
    /**
    * Preview mode of the camera: Fit-In or Fill-In.
    * Optional, default is Fit-In.
    */
    cameraPreviewMode?: CameraPreviewMode;
    /**
    * Title of the cancel button.
    */
    cancelButtonTitle?: string;
    /**
    * Title of the button that opens the screen where the user can allow
    * the usage of the camera by the app.
    */
    enableCameraButtonTitle?: string;
    /**
    * Text that will be displayed when the app
    * is not allowed to use the camera, prompting the user
    * to enable the usage of the camera.
    */
    enableCameraExplanationText?: string;
    /**
    * Controls whether the flash toggle button is hidden or not.
    */
    flashButtonHidden?: boolean;
    /**
    * Title of the flash toggle button.
    */
    flashButtonTitle?: string;
    /**
    * Controls whether the flash should be initially enabled.
    * The default value is FALSE.
    */
    flashEnabled?: boolean;
    flashImageButtonHidden?: boolean;
    /**
    * Sets whether to ignore the net.doo.snap.lib.detector.DetectionResult.OK_BUT_BAD_ASPECT_RATIO detection status.
    * By default BadAspectRatio is not ignored.
    */
    ignoreBadAspectRatio?: boolean;
    /**
    * The image scaling factor. The factor must be within the 0..1 range.
    * A factor of 1 means that the resulting images retain their original size.
    * When the factor is less than 1, resulting images will be made smaller by that factor.
    * By default the scale is 1.
    */
    imageScale?: number;
    /**
    * Controls whether the multi-page toggle button is hidden or not.
    */
    multiPageButtonHidden?: boolean;
    /**
    * Title of the multi-page mode toggle button.
    */
    multiPageButtonTitle?: string;
    /**
    * Controls multi-page mode. When enabled, the user can take multiple document photos before
    * closing the screen by tapping the page counter button. When disabled, the screen will be
    * closed immediately after the first document photo is made.
    * The default value is FALSE.
    */
    multiPageEnabled?: boolean;
    /**
    * Orientation lock mode of the camera: PORTRAIT or LANDSCAPE.
    * By default the camera orientation is not locked.
    */
    orientationLockMode?: CameraOrientationMode;
    /**
    * Title suffix of the button that finishes the document scanning when multi-page scanning is enabled.
    * The button's title has the format "# Pages", where # shows the number of images captured up to now and the
    * suffix "Pages" is set using this method.
    */
    pageCounterButtonTitle?: string;
    /**
    * The background color of the detected document outline when the document's angle, size or aspect ratio
    * is not yet sufficiently good.
    * (All net.doo.snap.lib.detector.DetectionResult with OK_BUT_XXX).
    */
    polygonBackgroundColor?: string;
    /**
    * The background color of the detected document outline when we are ready to snap net.doo.snap.lib.detector.DetectionResult.OK.
    */
    polygonBackgroundColorOK?: string;
    /**
    * The color of the detected document outline when the document's angle, size or aspect ratio
    * is not yet sufficiently good.
    * (All detection statuses in net.doo.snap.lib.detector.DetectionResult that have the OK_BUT_XXX prefix).
    */
    polygonColor?: string;
    /**
    * The color of the detected document outline when we are ready to snap net.doo.snap.lib.detector.DetectionResult.OK.
    */
    polygonColorOK?: string;
    /**
    * Width of the detected document outline.
    */
    polygonLineWidth?: number;
    /**
    * The foreground color of the shutter button in auto-snapping mode.
    */
    shutterButtonAutoInnerColor?: string;
    /**
    * The background color of the shutter button in auto-snapping mode.
    */
    shutterButtonAutoOuterColor?: string;
    shutterButtonIndicatorColor?: string;
    /**
    * The foreground color of the shutter button in manual mode.
    */
    shutterButtonManualInnerColor?: string;
    /**
    * The background color of the shutter button in manual mode.
    */
    shutterButtonManualOuterColor?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.OK_BUT_BAD_ANGLES
    */
    textHintBadAngles?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.OK_BUT_BAD_ASPECT_RATIO
    */
    textHintBadAspectRatio?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.ERROR_NOTHING_DETECTED
    */
    textHintNothingDetected?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.OK
    */
    textHintOK?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.ERROR_TOO_DARK
    */
    textHintTooDark?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.ERROR_TOO_NOISY
    */
    textHintTooNoisy?: string;
    /**
    * Text hint that will be shown when the current detection status is net.doo.snap.lib.detector.DetectionResult.OK_BUT_TOO_SMALL
    */
    textHintTooSmall?: string;
    /**
    * The background color of the top toolbar.
    */
    topBarBackgroundColor?: string;
    /**
    * The color of all active toggle buttons in the toolbar.
    */
    topBarButtonsActiveColor?: string;
    /**
    * The color of all inactive toggle buttons in the toolbar.
    */
    topBarButtonsInactiveColor?: string;
    /**
    * The background color of the user guidance hints.
    */
    userGuidanceBackgroundColor?: string;
    /**
    * The text color of the user guidance hints.
    */
    userGuidanceTextColor?: string;
    /**
     * Limits the maximum size of the document image. If width or height are zero, this property is effectively ignored.
     */
    documentImageSizeLimit?: Size;
    /**
     * Hides the shutter button if set to TRUE. Shows it otherwise. Defaults to FALSE.
     * If set to TRUE, auto-snapping is enabled and the property autoSnappingEnabled of the behaviour configuration will
     * have no effect.
     * Also the auto-snapping button will be hidden.
     */
    shutterButtonHidden?: boolean;
    /** 
     * The text being displayed on the user-guidance label, when the scanners energy saver is activated. 
     * iOS only.
     */
    textHintEnergySavingActive?: string;
    /**
     * Maximum number of pages to scan. Ignored when set to null, or when `multiPageEnabled` is FALSE. Default value is null.
     */
    maxNumberOfPages?: number;
}

export interface CroppingScreenConfiguration
{
    /**
    * Background color of the main screen.
    */
    backgroundColor?: string;
    /**
    * Background color of the bottom toolbar.
    */
    bottomBarBackgroundColor?: string;
    /**
    * Color of the titles of all buttons in the bottom toolbar (Rotate button).
    */
    bottomBarButtonsColor?: string;
    /**
    * Title of the cancel button.
    */
    cancelButtonTitle?: string;
    /**
    * Title of the Done button.
    */
    doneButtonTitle?: string;
    /**
    * Default color of the cropping outline.
    */
    polygonColor?: string;
    /**
    * Outline color of magnetically snapped edges.
    */
    polygonColorMagnetic?: string;
    /**
    * Width of the cropping outline.
    */
    polygonLineWidth?: number;
    /**
    * Title of the Rotate button.
    */
    rotateButtonTitle?: string;
    /**
    * Background color of the top toolbar.
    */
    topBarBackgroundColor?: string;
    /**
    * Color of the titles of all buttons in the top toolbar (Cancel and Done buttons).
    */
    topBarButtonsColor?: string;
    /**
     * Title in the top bar (iOS only).
     */
    topBarTitle?: string;
    /**
     * Color of the title in the top bar (iOS only).
     */
    titleColor?: string;
    /**
     * Title of the Detect button.
     */
    detectButtonTitle?: string;
    /**
     * Title of the Reset button.
     */
    resetButtonTitle?: string;
    /**
     * Controls whether the Rotate button is hidden or not.
     */
    rotateButtonHidden?: boolean;
    /**
     * Controls whether the Detect/Reset button is hidden or not.
     */
    detectResetButtonHidden?: boolean;
    /**
     * UI orientation lock mode: PORTRAIT, LANDSCAPE, etc.
     * By default the UI is not locked.
     */
    orientationLockMode?: UIOrientationMode;
}

export interface MrzScannerConfiguration
{
    bottomButtonsActiveColor?: string;
    bottomButtonsInactiveColor?: string;
    /**
    * Background color outside of the finder window.
    */
    cameraOverlayColor?: string;
    /**
    * Title of the cancel button.
    */
    cancelButtonTitle?: string;
    /**
    * Title of the button that opens the screen where the user can allow
    * the usage of the camera by the app.
    */
    enableCameraButtonTitle?: string;
    /**
    * Text that will be displayed when the app
    * is not allowed to use the camera, prompting the user
    * to enable the usage of the camera.
    */
    enableCameraExplanationText?: string;
    /**
    * Height of the finder window in pixels.
    */
    finderHeight?: number;
    /**
    * Color of the finder window's outline.
    */
    finderLineColor?: string;
    /**
    * Thickness of the finder window's outline.
    */
    finderLineWidth?: number;
    /**
    * Text hint shown under the finder window.
    */
    finderTextHint?: string;
    /**
    * Color of the text hint under the finder window.
    */
    finderTextHintColor?: string;
    /**
    * Width of the finder window in pixels.
    */
    finderWidth?: number;
    flashButtonTitle?: string;
    /**
    * Controls whether the flash should be initially enabled.
    * The default value is FALSE.
    */
    flashEnabled?: boolean;
    /**
    * Orientation lock mode of the camera: PORTRAIT or LANDSCAPE.
    * By default the camera orientation is not locked.
    */
    orientationLockMode?: CameraOrientationMode;
    /**
    * Controls whether to play a beep sound after a successful detection.
    * Default value is TRUE.
    */
    successBeepEnabled?: boolean;
    /**
    * Background color of the top toolbar.
    */
    topBarBackgroundColor?: string;
    /**
    * Color of the titles of all buttons in the top toolbar.
    */
    topBarButtonsColor?: string;
}

export interface BarcodeScannerConfiguration
{
    bottomButtonsActiveColor?: string;
    bottomButtonsInactiveColor?: string;
    /**
    * Background color outside of the finder window.
    */
    cameraOverlayColor?: string;
    /**
    * Title of the cancel button.
    */
    cancelButtonTitle?: string;
    /**
    * Title of the button that opens the screen where the user can allow
    * the usage of the camera by the app.
    */
    enableCameraButtonTitle?: string;
    /**
    * Text that will be displayed when the app
    * is not allowed to use the camera, prompting the user
    * to enable the usage of the camera.
    */
    enableCameraExplanationText?: string;
    /**
    * Height of the finder window in pixels.
    */
    finderHeight?: number;
    /**
    * Color of the finder window's outline.
    */
    finderLineColor?: string;
    /**
    * Thickness of the finder window's outline.
    */
    finderLineWidth?: number;
    /**
    * Text hint shown under the finder window.
    */
    finderTextHint?: string;
    /**
    * Color of the text hint under the finder window.
    */
    finderTextHintColor?: string;
    /**
    * Width of the finder window in pixels.
    */
    finderWidth?: number;
    flashButtonTitle?: string;
    /**
    * Controls whether the flash should be initially enabled.
    * The default value is FALSE.
    */
    flashEnabled?: boolean;
    /**
    * Orientation lock mode of the camera: PORTRAIT or LANDSCAPE.
    * By default the camera orientation is not locked.
    */
    orientationLockMode?: CameraOrientationMode;
    /**
    * Controls whether to play a beep sound after a successful detection.
    * Default value is TRUE.
    */
    successBeepEnabled?: boolean;
    /**
    * Background color of the top toolbar.
    */
    topBarBackgroundColor?: string;
    /**
    * Color of the titles of all buttons in the top toolbar.
    */
    topBarButtonsColor?: string;
}

export type BarcodeFormat =
    "AZTEC"
    | "CODABAR"
    | "CODE_128"
    | "CODE_39"
    | "CODE_93"
    | "DATA_MATRIX"
    | "EAN_13"
    | "EAN_8"
    | "ITF"
    | "MAXICODE"
    | "PDF_417"
    | "QR_CODE"
    | "RSS_14"
    | "RSS_EXPANDED"
    | "UNKNOWN"
    | "UPC_A"
    | "UPC_E"
    | "UPC_EAN_EXTENSION"
;

export type CameraPreviewMode =
    "FILL_IN"
    | "FIT_IN"
;

export type CameraImageFormat =
    "JPG"
    | "PNG"
;

export type CameraOrientationMode =
    "NONE"
    | "PORTRAIT"
    | "PORTRAIT_UPSIDE_DOWN"
    | "LANDSCAPE_LEFT"
    | "LANDSCAPE_RIGHT"
    | "LANDSCAPE"
;

export type UIOrientationMode =
    "NONE"
    | "PORTRAIT"
    | "PORTRAIT_UPSIDE_DOWN"
    | "LANDSCAPE_LEFT"
    | "LANDSCAPE_RIGHT"
    | "LANDSCAPE"
;

export type DetectionStatus = 
    "OK"
    | "OK_BUT_TOO_SMALL"
    | "OK_BUT_BAD_ANGLES"
    | "OK_BUT_BAD_ASPECT_RATIO"
    | "OK_BARCODE"
    | "OK_OFF_CENTER"
    | "ERROR_NOTHING_DETECTED"
    | "ERROR_TOO_DARK"
    | "ERROR_TOO_NOISY";

export type Status = "OK" | "CANCELED";

export type ImageFilter =
    "NONE"
    | "COLOR_ENHANCED"
    | "GRAYSCALE"
    | "BINARIZED"
    | "COLOR_DOCUMENT"
    | "PURE_BINARIZED"
    | "BACKGROUND_CLEAN"
    | "BLACK_AND_WHITE"
    | "OTSU_BINARIZATION"
    | "DEEP_BINARIZATION"
    | "LOW_LIGHT_BINARIZATION"
    | "LOW_LIGHT_BINARIZATION_2"
    | "EDGE_HIGHLIGHT";

export type OCROutputFormat =
    "PLAIN_TEXT"
    | "RESULT_JSON"
    | "PDF_FILE"
    | "FULL_OCR_RESULT";

export type PDFPageSize =
    "FROM_IMAGE"
    | "A4"
    | "FIXED_A4"
    | "US_LETTER"
    | "FIXED_US_LETTER"
    | "AUTO_LOCALE"
    | "AUTO"

export interface DocumentDetectionResult {
    detectionResult: DetectionStatus;
    polygon?: Point[];
    documentImageFileUri?: string;
}

export interface Point {
    x: number;
    y: number;
}

export interface Size {
    width: number;
    height: number;
}

export interface Page {
    pageId: string;
    polygon: Point[];
    detectionResult: DetectionStatus;
    filter: ImageFilter;
    documentImageSizeLimit?: Size;

    originalImageFileUri: string;
    documentImageFileUri?: string;
    originalPreviewImageFileUri: string;
    documentPreviewImageFileUri?: string;
}

export  interface DocumentScannerResult {
    status: Status;
    pages: Page[];
}

export  interface CroppingResult {
    status: Status;
    page?: Page;
}

export interface MrzResult {
    status: Status;
    mrzResult?: {
        fields: {
            name: string;
            value: string;
            confidence: number;
        }[];
        checkDigitsCount: number;
        validCheckDigitsCount: number;
    }
}

export interface BarcodeResult {
    status: Status;
    barcodeResult?: {
        barcodeFormat?: BarcodeFormat;
        textValue?: string;
    }
}

export interface GenericResult {
    status: Status;
    message?: string;
}

///// Promisified Scanbot API

export interface ScanbotSDKConfiguration {
    licenseKey: string;
    loggingEnabled?: boolean;
    storageImageQuality?: number;
    storageImageFormat?: CameraImageFormat;
    storageBaseDirectory?: string;
}

export interface ScanbotSDKUI {
    startDocumentScanner(args?: {uiConfigs?: DocumentScannerConfiguration}): Promise<DocumentScannerResult>;
    startCroppingScreen(args: {page: Page; uiConfigs?: CroppingScreenConfiguration}): Promise<CroppingResult>;
    startMrzScanner(args?: {uiConfigs?: MrzScannerConfiguration}): Promise<MrzResult>;
    startBarcodeScanner(args?: {uiConfigs?: BarcodeScannerConfiguration}): Promise<BarcodeResult>;
}

export interface ScanbotSDK {
    UI: ScanbotSDKUI;

    initializeSdk(config: ScanbotSDKConfiguration): Promise<GenericResult>;
    isLicenseValid(): Promise<GenericResult & {isLicenseValid: boolean}>;

    detectDocument(args: {
        imageFileUri: string,
        quality?: number
    }): Promise<GenericResult & DocumentDetectionResult>;
    applyImageFilter(args: {
        imageFileUri: string,
        imageFilter: ImageFilter,
        quality?: number
    }): Promise<GenericResult & {imageFileUri: string}>;
    rotateImage(args: {
        imageFileUri: string,
        degrees: number,
        quality?: number
    }): Promise<GenericResult & {imageFileUri: string}>;

    getOcrConfigs(): Promise<GenericResult & {
        languageDataPath: string;
        installedLanguages: string[];
    }>;
    performOcr(args: {
        images: string[];
        languages: string[];
        outputFormat?: OCROutputFormat;
    }): Promise<GenericResult & {
        plainText?: string;
        pdfFileUri?: string;
        jsonFileUri?: string;
    }>;

    createPdf(args: {images: string[], pageSize: PDFPageSize}): Promise<GenericResult & {pdfFileUri: string;}>;

    writeTiff(args: {
        images: string[];
        oneBitEncoded?: boolean;
    }): Promise<GenericResult & {tiffFileUri: string;}>;

    createPage(args: {originalImageFileUri: string}): Promise<GenericResult & {page: Page}>;
    setDocumentImage(args: {page: Page; documentImageFileUri: string}): Promise<GenericResult & {page: Page}>;
    detectDocumentOnPage(args: {page: Page}): Promise<GenericResult & {page: Page}>;
    applyImageFilterOnPage(args: {page: Page, imageFilter: ImageFilter}): Promise<GenericResult & {page: Page}>;
    rotatePage(args: {page: Page, times: number}): Promise<GenericResult & {page: Page}>;
    getFilteredDocumentPreviewUri(args: {page: Page, imageFilter: ImageFilter}): Promise<GenericResult & {imageFileUri: string}>;
    removePage(args: {page: Page}): Promise<GenericResult>;

    cleanup(): Promise<GenericResult>;
}

///// Base Cordova API

type SuccessCallback<TResult> = (result: TResult) => void;
type ErrorCallback = (error: {
    status: "ERROR";
    message: string;
}) => void;

export interface ScanbotSDKUICordova {
    startDocumentScanner(success: SuccessCallback<DocumentScannerResult>, error: ErrorCallback, args?: {uiConfigs?: DocumentScannerConfiguration});
    startCroppingScreen(success: SuccessCallback<CroppingResult>, error: ErrorCallback, args: {page: Page; uiConfigs?: CroppingScreenConfiguration});
    startMrzScanner(success: SuccessCallback<MrzResult>, error: ErrorCallback, args?: {uiConfigs?: MrzScannerConfiguration});
    startBarcodeScanner(success: SuccessCallback<BarcodeResult>, error: ErrorCallback, args?: {uiConfigs?: BarcodeScannerConfiguration});
}

export interface ScanbotSDKCordova {
    /**
     * Returns a promisified version of the Scanbot SDK API. Available only if there is also an available Promise global function.
     */
    promisify?(): ScanbotSDK;

    UI: ScanbotSDKUICordova;

    initializeSdk(success: SuccessCallback<GenericResult>, error: ErrorCallback, config: ScanbotSDKConfiguration): void;
    isLicenseValid(success: SuccessCallback<GenericResult & {isLicenseValid: boolean}>, error: ErrorCallback): void;

    detectDocument(success: SuccessCallback<GenericResult & DocumentDetectionResult>, error: ErrorCallback, args: {
        imageFileUri: string,
        quality?: number
    }): void;
    applyImageFilter(success: SuccessCallback<GenericResult & {imageFileUri: string}>, error: ErrorCallback, args: {
        imageFileUri: string,
        imageFilter: ImageFilter,
        quality?: number
    }): void;
    rotateImage(success: SuccessCallback<GenericResult & {imageFileUri: string}>, error: ErrorCallback, args: {
        imageFileUri: string,
        degrees: number,
        quality?: number
    }): void;

    getOcrConfigs(success: SuccessCallback<GenericResult & {
        languageDataPath: string;
        installedLanguages: string[];
    }>, error: ErrorCallback): void;
    performOcr(success: SuccessCallback<GenericResult & {
        plainText?: string;
        pdfFileUri?: string;
    }>, error: ErrorCallback, args: {
        images: string[];
        languages: string[];
        outputFormat?: OCROutputFormat;
    }): void;

    createPdf(success: SuccessCallback<GenericResult & {pdfFileUri: string}>, error: ErrorCallback, args: {images: string[], pageSize: PDFPageSize}): void;

    writeTiff(success: SuccessCallback<GenericResult & {tiffFileUri: string}>, error: ErrorCallback, args: {
        images: string[];
        oneBitEncoded?: boolean;
    }): void;

    createPage(success: SuccessCallback<GenericResult & {page: Page}>, error: ErrorCallback, args: {originalImageFileUri: string}): void;
    setDocumentImage(success: SuccessCallback<GenericResult & {page: Page}>, error: ErrorCallback, args: {page: Page, imageUri: string}): void;
    detectDocumentOnPage(success: SuccessCallback<GenericResult & {page: Page}>, error: ErrorCallback, args: {page: Page}): void;
    applyImageFilterOnPage(success: SuccessCallback<GenericResult & {page: Page}>, error: ErrorCallback, args: {page: Page, imageFilter: ImageFilter}): void;
    rotatePage(success: SuccessCallback<GenericResult & {page: Page}>, error: ErrorCallback, args: {page: Page, times: number}): void;
    getFilteredDocumentPreviewUri(success: SuccessCallback<GenericResult & {imageFileUri: string}>, error: ErrorCallback, args: {page: Page, imageFilter: ImageFilter}): void;
    removePage(success: SuccessCallback<GenericResult>, error: ErrorCallback, page: Page): void;

    cleanup(success: SuccessCallback<GenericResult>, error: ErrorCallback): void;
}

declare let ScanbotSDK: ScanbotSDKCordova;

export default ScanbotSDK;
