/*
    Scanbot SDK Cordova Plugin

    Copyright (c) 2017 doo GmbH. All rights reserved.
 */
package io.scanbot.sdk.plugin.cordova;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.preference.PreferenceManager;

import net.doo.snap.lib.detector.DetectionResult;
import net.doo.snap.persistence.DocumentStoreStrategy;
import net.doo.snap.process.OcrResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.scanbot.sdk.ScanbotSDK;
import io.scanbot.sdk.ScanbotSDKInitializer;
import io.scanbot.sdk.ocr.OpticalCharacterRecognizer;
import io.scanbot.sdk.persistence.Page;
import io.scanbot.sdk.persistence.PageFileStorage;
import io.scanbot.sdk.persistence.PageStorageSettings;
import io.scanbot.sdk.plugin.cordova.dto.JsonPage;
import io.scanbot.sdk.plugin.cordova.utils.StorageUtils;
import io.scanbot.sdk.plugin.cordova.utils.ImageUtils;
import io.scanbot.sdk.plugin.cordova.utils.JsonArgs;
import io.scanbot.sdk.plugin.cordova.utils.JsonUtils;
import io.scanbot.sdk.plugin.cordova.utils.LogUtils;
import io.scanbot.sdk.plugin.cordova.utils.ObjectMapper;
import io.scanbot.sdk.process.ImageFilterType;
import io.scanbot.sdk.process.PDFPageSize;

import static io.scanbot.sdk.plugin.cordova.utils.JsonUtils.resultToJson;


/**
 * Scanbot SDK Cordova Plugin
 */
public class ScanbotSdkPlugin extends ScanbotCordovaPluginBase {

    private static final String LOG_TAG = ScanbotSdkPlugin.class.getSimpleName();

    private static boolean isSdkInitialized = false;


    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }


    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        LOG.d(LOG_TAG, "execute: action=" + action + "; callbackId=" + callbackContext.getCallbackId());
        final JSONObject jsonArgs = (args.length() > 0 ? args.getJSONObject(0) : new JSONObject());
        debugLog("JSON args: " + jsonArgs.toString());

        if (action.equals("initializeSdk")) {
            initializeSdk(jsonArgs, callbackContext);
            return true;
        }

        if (!isSdkInitialized()) {
            errorCallback(callbackContext, "Scanbot SDK is not initialized. Please use 'initializeSdk(...)' to initialize the Scanbot SDK.");
            return true;
        }

        if (action.equals("isLicenseValid")) {
            try {
                isLicenseValid(callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not perform license validation.", e);
            }
            return true;
        }

        if (action.equals("detectDocument")) {
            try {
                detectDocument(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not perform document detection.", e);
            }
            return true;
        }

        if (action.equals("performOcr")) {
            try {
                performOcr(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not perform OCR.", e);
            }
            return true;
        }

        if (action.equals("getOcrConfigs")) {
            try {
                getOcrConfigs(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not get OCR configs.", e);
            }
            return true;
        }

        if (action.equals("applyImageFilter")) {
            try {
                applyImageFilter(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not apply image filter on image.", e);
            }
            return true;
        }

        if (action.equals("applyImageFilterOnPage")) {
            try {
                applyImageFilterOnPage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not apply image filter on page.", e);
            }
            return true;
        }

        if (action.equals("getFilteredDocumentPreviewUri")) {
            try {
                getFilteredDocumentPreviewUri(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not get filtered document preview image URI.", e);
            }
            return true;
        }

        if (action.equals("rotateImage")) {
            try {
                rotateImage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not apply rotation on image.", e);
            }
            return true;
        }

        if (action.equals("rotatePage")) {
            try {
                rotatePage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not apply rotation on page.", e);
            }
            return true;
        }

        if (action.equals("createPdf")) {
            try {
                createPdf(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not create PDF.", e);
            }
            return true;
        }

        if (action.equals("writeTiff")) {
            try {
                writeTiff(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not create TIFF.", e);
            }
            return true;
        }

        if (action.equals("cleanup")) {
            try {
                cleanup(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not cleanup the temporary directory of Scanbot SDK Plugin.", e);
            }
            return true;
        }

        if (action.equals("createPage")) {
            try {
                createPage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not create pages.", e);
            }
            return true;
        }

        if (action.equals("detectDocumentOnPage")) {
            try {
                detectDocumentOnPage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Error while detecting document on page.", e);
            }
            return true;
        }

        if (action.equals("setDocumentImage")) {
            try {
                setDocumentImage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not set document image.", e);
            }
            return true;
        }

        if (action.equals("removePage")) {
            try {
                removePage(jsonArgs, callbackContext);
            } catch (final Exception e) {
                errorCallback(callbackContext, "Could not remove page.", e);
            }
            return true;
        }


        return false;
    }

    private static synchronized void setSdkInitialized(final boolean flag) {
        isSdkInitialized = flag;
    }

    public static synchronized boolean isSdkInitialized() {
        return isSdkInitialized;
    }


    /**
     * Initializes Scanbot SDK.
     *
     * @param args Optional JSON Args:
     *             loggingEnabled: true,
     *             licenseKey: 'xyz..'
     *             storageImageQuality: 1-100
     *             storageImageFormat: "JPEG" or "PNG"
     * @throws JSONException
     */
    private void initializeSdk(final JSONObject args, final CallbackContext callbackContext) {
        debugLog("Initializing Scanbot SDK ...");
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final ScanbotSdkConfiguration configuration = new ScanbotSdkConfiguration();
                    ObjectMapper.map(args, configuration);

                    final Application app = cordova.getActivity().getApplication();
                    final String callbackMessage;

                    LogUtils.setLoggingEnabled(configuration.isLoggingEnabled());
                    final ScanbotSDKInitializer initializer = new ScanbotSDKInitializer();
                    initializer.withLogging(LogUtils.isLoggingEnabled());

                    if (configuration.getStorageBaseDirectory() != null && !"".equals(configuration.getStorageBaseDirectory())) {
                        final Uri uri = Uri.parse(configuration.getStorageBaseDirectory());
                        final File customStorageBaseDirectory = new File(uri.getPath());
                        customStorageBaseDirectory.mkdirs();
                        if (!customStorageBaseDirectory.isDirectory()) {
                            throw new IOException("Specified storageBaseDirectory is not valid: " + uri);
                        }
                        initializer.sdkFilesDirectory(app, customStorageBaseDirectory);
                        StorageUtils.setCustomStorageBaseDirectory(customStorageBaseDirectory);
                        debugLog("Using custom storage base directory: " + customStorageBaseDirectory.getAbsolutePath());
                    }

                    final String licenseKey = configuration.getLicenseKey();
                    if (licenseKey != null && !"".equals(licenseKey.trim()) && !"null".equals(licenseKey.toLowerCase())) {
                        initializer.license(app, licenseKey);
                        callbackMessage = "Scanbot SDK initialized.";
                    } else {
                        callbackMessage = "Scanbot SDK initialized. Trial mode activated. You can now test all features for 60 seconds.";
                    }

                    initializer.usePageStorageSettings(new PageStorageSettings.Builder()
                            .imageQuality(configuration.getStorageImageQuality())
                            .imageFormat(configuration.getStorageImageFormat())
                            .build());

                    initializer.prepareOCRLanguagesBlobs(true);

                    initializer.prepareMRZBlobs(true);

                    initializer.initialize(app);

                    setSdkInitialized(true);
                    debugLog(callbackMessage);
                    successCallback(callbackContext, false, callbackMessage);
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error initializing Scanbot SDK.", e);
                }
            }
        });
    }

    private void isLicenseValid(final CallbackContext callbackContext) {
        debugLog("Checking Scanbot SDK license...");
        final ScanbotSDK scanbotSDK = new ScanbotSDK(cordova.getActivity());
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    successCallback(callbackContext, false, new JsonArgs()
                            .put("isLicenseValid", scanbotSDK.isLicenseValid())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error checking Scanbot SDK license.", e);
                }
            }
        });
    }


    private void detectDocument(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final String imageFileUri = getImageFileUriArg(args);
        final int quality = getImageQualityArg(args);
        debugLog("Performing document detection on image: " + imageFileUri);
        debugLog("quality: " + quality);

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Bitmap sourceImage = loadImage(imageFileUri);
                    final ScanbotSdkWrapper.DocumentDetectionResult result = sdkWrapper.documentDetection(sourceImage, true);

                    debugLog("Document detection result: " + result.sdkDetectionResult);
                    debugLog("Document detection polygon: " + result.polygon);

                    final Uri resultImgUri;
                    if (result.documentImage != null) {
                        resultImgUri = sdkWrapper.storeImage(result.documentImage, quality);
                        debugLog("Stored document image: " + resultImgUri.toString());
                    } else {
                        resultImgUri = null;
                    }

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("detectionResult", JsonUtils.sdkDetectionResultToJsString(result.sdkDetectionResult))
                            .put("documentImageFileUri", (resultImgUri != null ? resultImgUri.toString() : null))
                            .put("polygon", JsonUtils.sdkPolygonToJson(result.polygon))
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not perform document detection on image: " + imageFileUri, e);
                }
            }
        });
    }


    private void applyImageFilter(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final String imageFileUri = getImageFileUriArg(args);
        final String imageFilter = getImageFilterArg(args);
        final int quality = getImageQualityArg(args);

        debugLog("Applying image filter (" + imageFilter + ") on image: " + imageFileUri);
        debugLog("quality: " + quality);

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Bitmap bitmap = loadImage(imageFileUri);
                    final Bitmap result = sdkWrapper.applyImageFilter(bitmap, JsonUtils.jsImageFilterToSdkFilter(imageFilter));
                    final Uri resultImgUri = sdkWrapper.storeImage(result, quality);
                    debugLog("Stored filtered image: " + resultImgUri.toString());

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("imageFileUri", resultImgUri.toString())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error applying filter on image: " + imageFileUri, e);
                }
            }
        });
    }


    private void getFilteredDocumentPreviewUri(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final Page page = JsonPage.fromJson(getPageJsonArg(args, true)).asSdkPage();
        final String imageFilter = getImageFilterArg(args);

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final ImageFilterType filterType = JsonUtils.jsImageFilterToSdkFilter(imageFilter);
                    final Uri filteredPreviewUri = sdkWrapper.getFilteredDocumentPreviewUri(page, filterType);

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("imageFileUri", StorageUtils.uriWithHash(filteredPreviewUri.toString()))
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error getting filtered preview image URI of page: " + page.getPageId(), e);
                }
            }
        });
    }


    private void applyImageFilterOnPage(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final Page page = JsonPage.fromJson(getPageJsonArg(args, true)).asSdkPage();
        final String imageFilter = getImageFilterArg(args);

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Page updatedPage = sdkWrapper.applyImageFilterOnPage(page, JsonUtils.jsImageFilterToSdkFilter(imageFilter));

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("page", JsonPage.fromSdkPage(updatedPage, sdkWrapper.pageFileStorage()).asJsonObj())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error applying filter on page: " + page.getPageId(), e);
                }
            }
        });
    }


    private void rotateImage(final JSONObject args, final CallbackContext callbackContext) throws JSONException, IOException {
        final String imageFileUri = getImageFileUriArg(args);
        final int degrees = getDegreesArg(args);
        final int quality = getImageQualityArg(args);
        debugLog("Applying image rotation with " + degrees + " degrees on image: " + imageFileUri);
        debugLog("quality: " + quality);

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Bitmap bitmap = loadImage(imageFileUri);
                    final Uri resultImgUri = sdkWrapper.storeImage(ImageUtils.rotateBitmap(bitmap, -degrees), quality);
                    debugLog("Stored rotated image: " + resultImgUri.toString());

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("imageFileUri", resultImgUri.toString())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error applying rotation on image: " + imageFileUri, e);
                }
            }
        });
    }


    private void rotatePage(final JSONObject args, final CallbackContext callbackContext) throws JSONException, IOException {
        final Page page = JsonPage.fromJson(getPageJsonArg(args, true)).asSdkPage();
        final int times = getTimesArg(args);
        debugLog("Rotating page " + page.getPageId() + " " + times + " times...");

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Page updatedPage = sdkWrapper.rotatePage(page, times);

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("page", JsonPage.fromSdkPage(updatedPage, sdkWrapper.pageFileStorage()).asJsonObj())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error applying filter on page.", e);
                }
            }
        });
    }


    private void createPdf(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final List<Uri> images = getImagesArg(args);
        if (images.size() == 0) {
            errorCallback(callbackContext, "At least one image must be present.");
            return;
        }

        final PDFPageSize pageSize = getPDFPageSizeArg(args);

        debugLog("Creating PDF of " + images.size() + " images ...");

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                final ScanbotSdkWrapper.PdfSdk pdfSdk = new ScanbotSdkWrapper.PdfSdk(cordova.getActivity());
                File tempPdfFile = null;
                try {
                    tempPdfFile = sdkWrapper.createPdf(images, pageSize, pdfSdk);
                    debugLog("Got temp PDF file from SDK: " + tempPdfFile);

                    final Uri pdfOutputUri = Uri.fromFile(StorageUtils.generateRandomPluginStorageFile("pdf", cordova.getActivity()));
                    debugLog("Copying SDK temp file to plugin storage dir: " + pdfOutputUri);
                    StorageUtils.copyFile(tempPdfFile, new File(pdfOutputUri.getPath()));

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("pdfFileUri", pdfOutputUri.toString())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error creating PDF file.", e);
                }
                finally {
                    if (tempPdfFile != null && tempPdfFile.exists()) {
                        debugLog("Deleting temp file: " + tempPdfFile);
                        tempPdfFile.delete();
                    }
                }
            }
        });
    }

    private void writeTiff(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final List<Uri> images = getImagesArg(args);
        if (images.size() == 0) {
            errorCallback(callbackContext, "At least one image must be present.");
            return;
        }

        debugLog("Creating TIFF of " + images.size() + " images ...");

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final File tiffFile = sdkWrapper.writeTiff(images, getOneBitEncodedArg(args));

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("tiffFileUri", Uri.fromFile(tiffFile).toString())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error creating TIFF file.", e);
                }
            }
        });
    }

    private void performOcr(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final List<Uri> images = getImagesArg(args);
        final List<String> languages = getLanguagesArg(args);
        final String outputFormat = JsonUtils.getJsonArg(args, "outputFormat", "PDF_FILE");

        if (images.size() == 0) {
            errorCallback(callbackContext, "At least one image must be present.");
            return;
        }

        debugLog("Performing OCR on " + images.size() + " images ...");

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                final ScanbotSdkWrapper.PdfSdk pdfSdk = new ScanbotSdkWrapper.PdfSdk(cordova.getActivity());
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(cordova.getActivity());
                final DocumentStoreStrategy documentStoreStrategy = new DocumentStoreStrategy(cordova.getActivity(), preferences);
                final OpticalCharacterRecognizer ocrRecognizer = pdfSdk.scanbotSDK.ocrRecognizer();

                File tempPdfFile = null;
                try {
                    // first check if requested languages are installed:
                    final List<String> check = new ArrayList<String>(languages);
                    check.removeAll(sdkWrapper.getInstalledOcrLanguages(pdfSdk));
                    if (!check.isEmpty()) {
                        errorCallback(callbackContext, "Missing OCR language files for languages: " + check.toString());
                        return;
                    }

                    final OcrResult result = sdkWrapper.performOcr(languages, ocrRecognizer, images, outputFormat);
                    debugLog("Got OCR result from SDK: " + result);

                    final JsonArgs jsonArgs = new JsonArgs();
                    if (outputFormat.equals("PLAIN_TEXT")) {
                        jsonArgs.put("plainText", result.getRecognizedText());

                    } else if (outputFormat.equals("RESULT_JSON")) {
                        jsonArgs.put("jsonFileUri", saveResult(result).toString());

                    } else if (outputFormat.equals("PDF_FILE") || outputFormat.equals("FULL_OCR_RESULT")) {
                        tempPdfFile = documentStoreStrategy.getDocumentFile(result.sandwichedPdfDocument.getId(), result.sandwichedPdfDocument.getName());
                        debugLog("Got temp PDF file from SDK: " + tempPdfFile);

                        final Uri pdfOutputUri = Uri.fromFile(StorageUtils.generateRandomPluginStorageFile("pdf", cordova.getActivity()));
                        debugLog("Copying SDK temp file to plugin storage dir: " + pdfOutputUri);
                        StorageUtils.copyFile(tempPdfFile, new File(pdfOutputUri.getPath()));

                        jsonArgs.put("pdfFileUri", pdfOutputUri.toString());

                        if (outputFormat.equals("FULL_OCR_RESULT")) {
                            jsonArgs.put("jsonFileUri", saveResult(result).toString());
                        }

                    } else {
                        jsonArgs.put("plainText", result.getRecognizedText());
                    }

                    successCallback(callbackContext, false, jsonArgs);
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not perform OCR on images: " + images.toString(), e);
                }
                finally {
                    if (tempPdfFile != null && tempPdfFile.exists()) {
                        debugLog("Deleting temp file: " + tempPdfFile);
                        tempPdfFile.delete();
                    }
                }
            }
        });

    }

    private Uri saveResult(OcrResult result) throws Exception {
        final Uri jsonFileUri = Uri.fromFile(StorageUtils.generateRandomPluginStorageFile("json", cordova.getActivity()));
        File jsonFile = new File(jsonFileUri.getPath());
        Writer output = new BufferedWriter(new FileWriter(jsonFile));
        output.write(resultToJson(result).toString());
        output.close();
        return jsonFileUri;
    }

    private void getOcrConfigs(final JSONObject args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final ScanbotSdkWrapper.PdfSdk pdfSdk = new ScanbotSdkWrapper.PdfSdk(cordova.getActivity());
                    final List<String> languages = sdkWrapper.getInstalledOcrLanguages(pdfSdk);
                    final File ocrBlobsDir = pdfSdk.blobManager.getOCRBlobsDirectory();

                    final JsonArgs jsonResult = new JsonArgs();
                    jsonResult.put("languageDataPath", Uri.fromFile(ocrBlobsDir).toString());
                    jsonResult.put("installedLanguages", new JSONArray(languages));

                    successCallback(callbackContext, false, jsonResult);
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not get OCR configs.", e);
                }
            }
        });
    }

    private void createPage(final JSONObject args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final String imageFileUri = getImageFileUriArg(args, "original");
                    final PageFileStorage pageFileStorage = sdkWrapper.pageFileStorage();
                    final List<PointF> emptyPolygon = Collections.emptyList();

                    final String pageId = pageFileStorage.add(ImageUtils.loadImage(Uri.parse(imageFileUri), cordova.getActivity()));
                    final Page page = new Page(pageId, emptyPolygon, DetectionResult.OK, ImageFilterType.NONE);

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("page", JsonPage.fromSdkPage(page, pageFileStorage).asJsonObj())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not create page.", e);
                }
            }
        });
    }

    private void detectDocumentOnPage(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final Page page = JsonPage.fromJson(getPageJsonArg(args, true)).asSdkPage();

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Page updatedPage = sdkWrapper.detectDocumentOnPage(page);

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("page", JsonPage.fromSdkPage(updatedPage, sdkWrapper.pageFileStorage()).asJsonObj())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not detect document on page.", e);
                }
            }
        });
    }

    private void setDocumentImage(final JSONObject args, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final JsonPage page = JsonPage.fromJson(getPageJsonArg(args, true));
                    final Uri imageFileUri = Uri.parse(getImageFileUriArg(args, "document"));
                    final PageFileStorage pageFileStorage = sdkWrapper.pageFileStorage();
                    pageFileStorage.setImageForId(ImageUtils.loadImage(imageFileUri, cordova.getActivity()), page.pageId, PageFileStorage.PageFileType.DOCUMENT);

                    successCallback(callbackContext, false, new JsonArgs()
                            .put("page", JsonPage.fromSdkPage(page.asSdkPage(), pageFileStorage).asJsonObj())
                    );
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not set document image on page.", e);
                }
            }
        });
    }

    private void removePage(final JSONObject args, final CallbackContext callbackContext) throws JSONException {
        final JsonPage jsonPage = JsonPage.fromJson(getPageJsonArg(args, true));
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final PageFileStorage pageFileStorage = sdkWrapper.pageFileStorage();

                    if (pageFileStorage.remove(jsonPage.pageId)) {
                        successCallback(callbackContext, false, "Page removed.");
                    }
                    else {
                        errorCallback(callbackContext, "Could not remove page.");
                    }
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not remove page.", e);
                }
            }
        });
    }

    private void cleanup(final JSONObject args, final CallbackContext callbackContext) {
        debugLog("Cleaning storage directories of the Scanbot SDK and Plugin ...");
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    StorageUtils.cleanupPluginStorageDirectory(cordova.getActivity());
                    sdkWrapper.pageFileStorage().removeAll();

                    successCallback(callbackContext, false, "Cleanup successfully done.");
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Could not cleanup all or some of the storage directories.", e);
                }
            }
        });
    }

}
