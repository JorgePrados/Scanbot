/*
    Scanbot SDK Cordova Plugin

    Copyright (c) 2017 doo GmbH. All rights reserved.
 */
package io.scanbot.sdk.plugin.cordova;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.scanbot.mrzscanner.model.MRZField;
import io.scanbot.mrzscanner.model.MRZRecognitionResult;
import io.scanbot.sdk.barcode.entity.BarcodeFormat;
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult;
import io.scanbot.sdk.persistence.Page;
import io.scanbot.sdk.persistence.PageFileStorage;
import io.scanbot.sdk.plugin.cordova.dto.JsonPage;
import io.scanbot.sdk.plugin.cordova.utils.JsonArgs;
import io.scanbot.sdk.plugin.cordova.utils.JsonUtils;
import io.scanbot.sdk.plugin.cordova.utils.ObjectMapper;
import io.scanbot.sdk.ui.view.barcode.BarcodeScannerActivity;
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration;
import io.scanbot.sdk.ui.view.base.configuration.CameraOrientationMode;
import io.scanbot.sdk.ui.view.base.configuration.OrientationMode;
import io.scanbot.sdk.ui.view.camera.DocumentScannerActivity;
import io.scanbot.sdk.ui.view.camera.configuration.DocumentScannerConfiguration;
import io.scanbot.sdk.ui.view.edit.CroppingActivity;
import io.scanbot.sdk.ui.view.edit.configuration.CroppingConfiguration;
import io.scanbot.sdk.ui.view.mrz.MRZScannerActivity;
import io.scanbot.sdk.ui.view.mrz.configuration.MRZScannerConfiguration;

import static io.scanbot.sdk.plugin.cordova.ScanbotConstants.REQUEST_SB_BARCODE_SCANNER;
import static io.scanbot.sdk.plugin.cordova.ScanbotConstants.REQUEST_SB_CROPPING;
import static io.scanbot.sdk.plugin.cordova.ScanbotConstants.REQUEST_SB_DOCUMENT_SCANNER;
import static io.scanbot.sdk.plugin.cordova.ScanbotConstants.REQUEST_SB_MRZ_SCANNER;


/**
 * Scanbot SDK UI Cordova Plugin
 */
public class ScanbotSdkUiPlugin extends ScanbotCordovaPluginBase {

    private static final String LOG_TAG = ScanbotSdkUiPlugin.class.getSimpleName();

    private static final String EXTRAS_JSON_ARGS = "EXTRAS_JSON_ARGS";


    private CallbackContext callbackContext;
    private JSONObject jsonArgs;


    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }


    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        LOG.d(LOG_TAG, "execute: action=" + action + "; callbackId=" + callbackContext.getCallbackId());

        this.jsonArgs = (args.length() > 0 ? args.getJSONObject(0) : new JSONObject());
        debugLog("JSON args: " + jsonArgs.toString());

        // First check if SDK is initialized:
        if (!ScanbotSdkPlugin.isSdkInitialized()) {
            final String errorMsg = "Scanbot SDK is not initialized. Please call the ScanbotSdk.initializeSdk() function first.";
            errorLog(errorMsg);
            callbackContext.error(errorMsg);
            return true;
        }

        this.callbackContext = callbackContext;

        if (action.equals("startDocumentScanner")) {
            startDocumentScannerActivity();
            return true;
        }


        if (action.equals("startCroppingScreen")) {
            startCroppingActivity();
            return true;
        }

        if (action.equals("startBarcodeScanner")) {
            startBarcodeScannerActivity();
            return true;
        }

        if (action.equals("startMrzScanner")) {
            startMrzScannerActivity();
            return true;
        }

        return false;
    }


    /**
     * Called when the Activity is being destroyed (e.g. if a plugin calls out to an
     * external Activity and the OS kills the CordovaActivity in the background).
     * The plugin should save its state in this method only if it is awaiting the
     * result of an external Activity and needs to preserve some information so as
     * to handle that result; onRestoreStateForActivityResult() will only be called
     * if the plugin is the recipient of an Activity result
     *
     * @return  Bundle containing the state of the plugin or null if state does not
     *          need to be saved
     */
    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        if (this.jsonArgs != null) {
            state.putString(EXTRAS_JSON_ARGS, this.jsonArgs.toString());
        }
        return state;
    }


    /**
     * Called when a plugin is the recipient of an Activity result after the
     * CordovaActivity has been destroyed. The Bundle will be the same as the one
     * the plugin returned in onSaveInstanceState()
     *
     * @param state             Bundle containing the state of the plugin
     * @param callbackContext   Replacement Context to return the plugin result to
     */
    @Override
    public void onRestoreStateForActivityResult(final Bundle state, final CallbackContext callbackContext) {
        super.onRestoreStateForActivityResult(state, callbackContext);

        this.callbackContext = callbackContext;

        if (state.containsKey(EXTRAS_JSON_ARGS)) {
            try {
                this.jsonArgs = new JSONObject(state.getString(EXTRAS_JSON_ARGS));
            } catch (final JSONException e) {
                errorLog("Could not restore JSON args", e);
            }
        }
    }


    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode   The request code originally supplied to startActivityForResult(),
     *                      allowing you to identify who this result came from.
     * @param resultCode    The integer result code returned by the child activity through its setResult().
     * @param intent        An Intent, which can return result data to the caller (various data can be
     *                      attached to Intent "extras").
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        debugLog("onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + ", intent=" + intent);

        switch (requestCode) {
            case REQUEST_SB_DOCUMENT_SCANNER:
                this.handleDocumentScannerResult(intent, (resultCode != Activity.RESULT_OK));
                return;
            case REQUEST_SB_CROPPING:
                this.handleCroppingResult(intent, (resultCode != Activity.RESULT_OK));
                return;
            case REQUEST_SB_BARCODE_SCANNER:
                this.handleBarcodeScannerResult(intent, (resultCode != Activity.RESULT_OK));
                return;
            case REQUEST_SB_MRZ_SCANNER:
                this.handleMrzScannerResult(intent, (resultCode != Activity.RESULT_OK));
                return;
            default:
                return;
        }
    }


    private JSONObject getUiConfigs() throws JSONException {
        // "uiConfigs" is an optional JSON object. If not provided, return an empty JSON object (means use default ui configs)
        return JsonUtils.getJsonObject(jsonArgs, "uiConfigs", new JSONObject());
    }


    /**
     * Starts Scanbot DocumentScanner UI.
     */
    private void startDocumentScannerActivity() {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final DocumentScannerConfiguration configuration = new DocumentScannerConfiguration();
                    final JSONObject uiConfigs = getUiConfigs();
                    if (uiConfigs.has("orientationLockMode")) {
                        final String orientation = uiConfigs.getString("orientationLockMode");

                        if ("PORTRAIT".equals(orientation)) {
                            configuration.setOrientationLockMode(CameraOrientationMode.PORTRAIT);
                        } else if ("PORTRAIT_UPSIDE_DOWN".equals(orientation)) {
                            configuration.setOrientationLockMode(CameraOrientationMode.PORTRAIT);
                        } else if ("LANDSCAPE_LEFT".equals(orientation)) {
                            configuration.setOrientationLockMode(CameraOrientationMode.LANDSCAPE);
                        } else if ("LANDSCAPE_RIGHT".equals(orientation)) {
                            configuration.setOrientationLockMode(CameraOrientationMode.LANDSCAPE);
                        } else if ("LANDSCAPE".equals(orientation)) {
                            configuration.setOrientationLockMode(CameraOrientationMode.LANDSCAPE);
                        }
                    }

                    ObjectMapper.map(uiConfigs, configuration);
                    final Intent intent = DocumentScannerActivity.newIntent(getApplicationContext(), configuration);
                    startScanbotActivityForResult(intent, REQUEST_SB_DOCUMENT_SCANNER);
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error starting DocumentScanner UI.", e);
                }
            }
        });
    }


    /**
     * Starts Scanbot Cropping UI.
     *
     * @throws JSONException
     */
    private void startCroppingActivity() {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    final Page page = JsonPage.fromJson(getPageJsonArg(jsonArgs, true)).asSdkPage();
                    final CroppingConfiguration configuration = new CroppingConfiguration();

                    final JSONObject uiConfigs = getUiConfigs();
                    if (uiConfigs.has("orientationLockMode")) {
                        final String orientation = uiConfigs.getString("orientationLockMode");
                        uiConfigs.remove("orientationLockMode");

                        if ("PORTRAIT".equals(orientation)) {
                            configuration.setOrientationLockMode(OrientationMode.PORTRAIT);
                        } else if ("PORTRAIT_UPSIDE_DOWN".equals(orientation)) {
                            configuration.setOrientationLockMode(OrientationMode.PORTRAIT);
                        } else if ("LANDSCAPE_LEFT".equals(orientation)) {
                            configuration.setOrientationLockMode(OrientationMode.LANDSCAPE);
                        } else if ("LANDSCAPE_RIGHT".equals(orientation)) {
                            configuration.setOrientationLockMode(OrientationMode.LANDSCAPE);
                        } else if ("LANDSCAPE".equals(orientation)) {
                            configuration.setOrientationLockMode(OrientationMode.LANDSCAPE);
                        }
                    }

                    ObjectMapper.map(uiConfigs, configuration);
                    configuration.setPage(page);
                    final Intent intent = CroppingActivity.newIntent(getApplicationContext(), configuration);
                    startScanbotActivityForResult(intent, REQUEST_SB_CROPPING);
                }
                catch (final Exception e) {
                    errorCallback(callbackContext, "Error starting Cropping UI.", e);
                }
            }
        });
    }


    /**
     * Starts Scanbot Barcode Scanner UI.
     *
     * @throws JSONException
     */
    private void startBarcodeScannerActivity() {
        try {
            final BarcodeScannerConfiguration configuration = new BarcodeScannerConfiguration();
            final JSONObject uiConfigs = getUiConfigs();
            ObjectMapper.map(uiConfigs, configuration);
            if (uiConfigs.has("barcodeFormats")) {
                JSONArray barcodeFormats = uiConfigs.getJSONArray("barcodeFormats");
                final ArrayList<BarcodeFormat> nativeBarcodeFormats = new ArrayList<BarcodeFormat>();

                for (int i = 0; i < barcodeFormats.length(); ++i) {
                    nativeBarcodeFormats.add(BarcodeFormat.valueOf(barcodeFormats.getString(i)));
                }
                configuration.setBarcodeFormatsFilter(nativeBarcodeFormats);
            }

            final Intent intent = BarcodeScannerActivity.newIntent(this.cordova.getActivity().getApplicationContext(), configuration);

            this.cordova.startActivityForResult(this, intent, REQUEST_SB_BARCODE_SCANNER);
        }
        catch (final Exception e) {
            errorCallback(callbackContext, "Error starting barcode scanner.", e);
        }
    }

    private void startMrzScannerActivity() {
        try {
            final MRZScannerConfiguration configuration = new MRZScannerConfiguration();
            ObjectMapper.map(getUiConfigs(), configuration);
            final Intent intent = MRZScannerActivity.newIntent(this.cordova.getActivity().getApplicationContext(), configuration);

            this.cordova.startActivityForResult(this, intent, REQUEST_SB_MRZ_SCANNER);
        }
        catch (final Exception e) {
            errorCallback(callbackContext, "Error starting barcode scanner.", e);
        }
    }

    private void handleDocumentScannerResult(final Intent intent, final boolean canceled) {
        final JSONArray pagesJsonArray = new JSONArray();

        try {
            if (!canceled) {
                final PageFileStorage pageFileStorage = this.sdkWrapper.pageFileStorage();
                final Parcelable[] parcelablePages = intent.getParcelableArrayExtra(DocumentScannerActivity.SNAPPED_PAGE_EXTRA);
                for (final Parcelable p: parcelablePages) {
                    final JsonPage jsonPage = JsonPage.fromSdkPage((Page) p, pageFileStorage);
                    pagesJsonArray.put(jsonPage.asJsonObj());
                }
            }

            successCallback(callbackContext, canceled, new JsonArgs()
                    .put("pages", pagesJsonArray)
            );
        }
        catch (final Exception e) {
            errorCallback(callbackContext, "Could not transform result from DocumentScannerActivity to JSON.", e);
        }
    }


    private void handleCroppingResult(final Intent intent, final boolean canceled) {
        final JSONObject pageJsonObj;
        if (!canceled) {
            final Page page = intent.getParcelableExtra(CroppingActivity.EDITED_PAGE_EXTRA);
            pageJsonObj = JsonPage.fromSdkPage(page, this.sdkWrapper.pageFileStorage()).asJsonObj();
        }
        else {
            pageJsonObj = null;
        }

        successCallback(this.callbackContext, canceled, "page", pageJsonObj);
    }


    private void handleBarcodeScannerResult(final Intent intent, final boolean canceled) {
        final JSONObject barcodeResultJsonObj;
        if (!canceled) {
            final BarcodeScanningResult barcodeResult = intent.getParcelableExtra(BarcodeScannerActivity.SCANNED_BARCODE_EXTRA);
            barcodeResultJsonObj = new JsonArgs()
                    .put("barcodeFormat", barcodeResult.getBarcodeFormat().name())
                    .put("textValue", barcodeResult.getText())
                    .jsonObj();
        }
        else {
            barcodeResultJsonObj = null;
        }

        successCallback(this.callbackContext, canceled, "barcodeResult", barcodeResultJsonObj);
    }


    private void handleMrzScannerResult(final Intent intent, final boolean canceled) {
        final JSONArray fieldsArray = new JSONArray();

        try {
            final JSONObject mrzResult;

            if (!canceled) {
                final MRZRecognitionResult mrzRecognitionResult = intent.getParcelableExtra(MRZScannerActivity.EXTRACTED_FIELDS_EXTRA);
                for (final MRZField field : mrzRecognitionResult.fields) {
                    final JSONObject jsonField = new JsonArgs()
                            .put("name", field.name.name())
                            .put("value", field.value)
                            .put("confidence", field.averageRecognitionConfidence)
                            .jsonObj();
                    fieldsArray.put(jsonField);
                }

                mrzResult = new JsonArgs()
                        .put("checkDigitsCount", mrzRecognitionResult.checkDigitsCount)
                        .put("validCheckDigitsCount", mrzRecognitionResult.validCheckDigitsCount)
                        .put("fields", fieldsArray)
                        .jsonObj();
            }
            else {
                mrzResult = null;
            }

            successCallback(callbackContext, canceled, new JsonArgs()
                    .put("mrzResult", mrzResult)
            );
        }
        catch (final Exception e) {
            errorCallback(callbackContext, "Could not transform MRZRecognitionResult to JSON.", e);
        }
    }
}
