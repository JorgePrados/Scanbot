/*
    Scanbot SDK Cordova Plugin

    Copyright (c) 2019 doo GmbH. All rights reserved.
 */
package io.scanbot.sdk.plugin.cordova.utils;

import android.graphics.PointF;
import android.graphics.RectF;

import net.doo.snap.lib.detector.DetectionResult;
import net.doo.snap.process.OcrResult;
import net.doo.snap.process.OcrResultBlock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.scanbot.sdk.persistence.Page;
import io.scanbot.sdk.process.ImageFilterType;

public final class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    // ImageFilter mapping: JS constants => SDK
    private static final Map<String, ImageFilterType> imageFilterMapping = new HashMap<String, ImageFilterType>();
    static {
        imageFilterMapping.put("NONE",                   ImageFilterType.NONE);
        imageFilterMapping.put("COLOR_ENHANCED",         ImageFilterType.COLOR_ENHANCED);
        imageFilterMapping.put("GRAYSCALE",              ImageFilterType.GRAYSCALE);
        imageFilterMapping.put("BINARIZED",              ImageFilterType.BINARIZED);
        imageFilterMapping.put("COLOR_DOCUMENT",         ImageFilterType.COLOR_DOCUMENT);
        imageFilterMapping.put("PURE_BINARIZED",         ImageFilterType.PURE_BINARIZED);
        imageFilterMapping.put("BACKGROUND_CLEAN",       ImageFilterType.BACKGROUND_CLEAN);
        imageFilterMapping.put("BLACK_AND_WHITE",        ImageFilterType.BLACK_AND_WHITE);
        imageFilterMapping.put("OTSU_BINARIZATION",      ImageFilterType.OTSU_BINARIZATION);
        imageFilterMapping.put("DEEP_BINARIZATION",      ImageFilterType.DEEP_BINARIZATION);
        imageFilterMapping.put("EDGE_HIGHLIGHT",         ImageFilterType.EDGE_HIGHLIGHT);
        imageFilterMapping.put("LOW_LIGHT_BINARIZATION", ImageFilterType.LOW_LIGHT_BINARIZATION);
        imageFilterMapping.put("LOW_LIGHT_BINARIZATION_2", ImageFilterType.LOW_LIGHT_BINARIZATION_2);
    }

    // Document Detection Result mapping: SDK => JS constants
    private static final Map<DetectionResult, String> detectionResultMapping = new HashMap<DetectionResult, String>();
    static {
        detectionResultMapping.put(DetectionResult.OK,                       "OK");
        detectionResultMapping.put(DetectionResult.OK_BARCODE,               "OK_BARCODE");
        detectionResultMapping.put(DetectionResult.OK_BUT_BAD_ANGLES,        "OK_BUT_BAD_ANGLES");
        detectionResultMapping.put(DetectionResult.OK_BUT_BAD_ASPECT_RATIO,  "OK_BUT_BAD_ASPECT_RATIO");
        detectionResultMapping.put(DetectionResult.OK_BUT_TOO_SMALL,         "OK_BUT_TOO_SMALL");
        detectionResultMapping.put(DetectionResult.OK_OFF_CENTER,            "OK_OFF_CENTER");
        detectionResultMapping.put(DetectionResult.ERROR_TOO_DARK,           "ERROR_TOO_DARK");
        detectionResultMapping.put(DetectionResult.ERROR_TOO_NOISY,          "ERROR_TOO_NOISY");
        detectionResultMapping.put(DetectionResult.ERROR_NOTHING_DETECTED,   "ERROR_NOTHING_DETECTED");
    }

    private JsonUtils() { }


    public static JSONArray sdkPolygonToJson(final List<PointF> polygon) {
        final JSONArray result = new JSONArray();
        for (final PointF p: polygon) {
            final JsonArgs jsonPoint = new JsonArgs();
            jsonPoint.put("x", p.x).put("y", p.y);
            result.put(jsonPoint.jsonObj());
        }
        return result;
    }

    public static List<PointF> jsonPolygonToSdk(final JSONArray jsonPolygon) throws JSONException {
        final List<PointF> polygon = new ArrayList<PointF>();
        if (jsonPolygon != null && jsonPolygon.length() > 0) {
            if (jsonPolygon.length() == 4) {
                for (int i=0; i<4; i++) {
                    final JSONObject jsonPoint = jsonPolygon.getJSONObject(i);
                    polygon.add(new PointF((float)jsonPoint.getDouble("x"), (float)jsonPoint.getDouble("y")));
                }
            } else {
                debugLog("Invalid polygon (does not contain 4 points): " + jsonPolygon.toString());
            }
        }
        return polygon;
    }

    public static ImageFilterType jsImageFilterToSdkFilter(final String imageFilter) {
        if (imageFilterMapping.containsKey(imageFilter)) {
            return imageFilterMapping.get(imageFilter);
        }
        debugLog("Warning! Unsupported imageFilter: " + imageFilter);
        return ImageFilterType.NONE;
    }

    public static String sdkImageFilterToJsFilter(final ImageFilterType filter) {
        for (Map.Entry<String, ImageFilterType> e: imageFilterMapping.entrySet()) {
            if (e.getValue().equals(filter)) {
                return e.getKey();
            }
        }
        debugLog("Warning! Unsupported ImageFilterType: " + filter.name());
        return filter.name();
    }


    public static String sdkDetectionResultToJsString(final DetectionResult detectionResult) {
        if (detectionResultMapping.containsKey(detectionResult)) {
            return detectionResultMapping.get(detectionResult);
        }
        debugLog("Warning! Unsupported DetectionResult: " + detectionResult);
        return detectionResult.name();
    }

    public static DetectionResult jsDetectionResultToSdkDetectionResult(final String detectionResult) {
        if (!detectionResultMapping.values().contains(detectionResult)) {
            debugLog("Warning! Unsupported DetectionResult string: " + detectionResult);
            return DetectionResult.ERROR_NOTHING_DETECTED;
        }
        return DetectionResult.valueOf(detectionResult);
    }

    public static boolean getJsonArg(final JSONObject args, final String key, final boolean defaultValue) throws JSONException {
        if (args.has(key)) {
            return args.getBoolean(key);
        }
        return defaultValue;
    }

    public static String getJsonArg(final JSONObject args, final String key, final String defaultValue) throws JSONException {
        if (args.has(key)) {
            return args.getString(key);
        }
        return defaultValue;
    }

    public static int getJsonArg(final JSONObject args, final String key, final int defaultValue) throws JSONException {
        if (args.has(key)) {
            return args.getInt(key);
        }
        return defaultValue;
    }

    public static double getJsonArg(final JSONObject args, final String key, final double defaultValue) throws JSONException {
        if (args.has(key)) {
            return args.getDouble(key);
        }
        return defaultValue;
    }

    public static List<String> getJsonArg(final JSONObject args, final String arrayKey) throws JSONException {
        final List<String> result = new ArrayList<String>();
        if (args.has(arrayKey)) {
            final JSONArray jsonArray = args.getJSONArray(arrayKey);
            for (int i=0; i < jsonArray.length(); i++) {
                result.add(jsonArray.getString(i));
            }
        }
        return result;
    }

    public static JSONArray getJsonArray(final JSONObject args, final String arrayKey, final JSONArray defaultValue) throws JSONException {
        if (args.has(arrayKey)) {
            return args.getJSONArray(arrayKey);
        }
        return defaultValue;
    }

    public static JSONObject getJsonObject(final JSONObject args, final String key, final JSONObject defaultValue) throws JSONException {
        if (args.has(key)) {
            return args.getJSONObject(key);
        }
        return defaultValue;
    }

    private static void debugLog(final String msg) {
        LogUtils.debugLog(LOG_TAG, msg);
    }

    private static void errorLog(final String msg) {
        LogUtils.errorLog(LOG_TAG, msg);
    }

    private static void errorLog(final String msg, final Throwable e) {
        LogUtils.errorLog(LOG_TAG, msg, e);
    }


    public static JSONObject resultToJson(OcrResult result) throws JSONException {
        JSONObject resultJson = new JSONObject();
        JSONArray pagesJson = new JSONArray();

        for (OcrResult.OCRPage page: result.ocrPages) {
            pagesJson.put(pageToJson(page));
        }

        resultJson.put("pages", pagesJson);
        return resultJson;
    }

    private static JSONObject pageToJson(OcrResult.OCRPage page) throws JSONException {
        JSONObject pageJson = new JSONObject();

        pageJson.put("text", page.text);

        JSONArray wordsJson = new JSONArray();
        for (OcrResultBlock block: page.words) {
            wordsJson.put(blockToJson(block));
        }
        pageJson.put("words", wordsJson);

        JSONArray linesJson = new JSONArray();
        for (OcrResultBlock block: page.lines) {
            linesJson.put(blockToJson(block));
        }
        pageJson.put("lines", linesJson);

        JSONArray paragraphsJson = new JSONArray();
        for (OcrResultBlock block: page.paragraphs) {
            paragraphsJson.put(blockToJson(block));
        }
        pageJson.put("paragraphs", paragraphsJson);

        return  pageJson;
    }

    private static JSONObject blockToJson(OcrResultBlock block) throws JSONException {
        JSONObject blockJson = new JSONObject();

        blockJson.put("boundingBox", boundingBoxToJson(block.boundingBox));
        blockJson.put("text", block.text);
        blockJson.put("confidence", block.confidenceValue);

        return  blockJson;
    }

    private static JSONObject boundingBoxToJson(RectF box) throws JSONException {
        JSONObject boxJson = new JSONObject();

        boxJson.put("x", box.centerX());
        boxJson.put("y", box.centerY());
        boxJson.put("width", box.width());
        boxJson.put("height", box.height());

        return  boxJson;
    }

    public static Page.Size sizeFromJson(JSONObject obj) throws JSONException {
        return obj != null ? new Page.Size(obj.getInt("width"), obj.getInt("height")) : new Page.Size();
    }

    public static JSONObject jsonFromSize(Page.Size size) {
        if (size.getWidth() == Integer.MAX_VALUE && size.getHeight() == Integer.MAX_VALUE) {
            return null;
        }
        final Map<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.put("width", size.getWidth());
        argsMap.put("height", size.getHeight());
        return new JSONObject(argsMap);
    }
}
