/*
    Scanbot SDK Cordova Plugin

    Copyright (c) 2017 doo GmbH. All rights reserved.
 */
package io.scanbot.sdk.plugin.cordova;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;

import io.scanbot.sdk.ScanbotSDK;
import io.scanbot.sdk.ocr.OpticalCharacterRecognizer;
import io.scanbot.sdk.persistence.Page;
import io.scanbot.sdk.persistence.PageFileStorage;
import io.scanbot.sdk.plugin.cordova.utils.StorageUtils;
import io.scanbot.sdk.plugin.cordova.utils.LogUtils;
import io.scanbot.sdk.process.ImageFilterType;
import io.scanbot.sdk.process.PDFPageSize;
import io.scanbot.sdk.process.PDFRenderer;
import io.scanbot.sdk.process.PageProcessor;
import io.scanbot.tiffwriter.TIFFWriter;

import net.doo.snap.blob.BlobFactory;
import net.doo.snap.blob.BlobManager;
import net.doo.snap.entity.Language;
import net.doo.snap.lib.detector.ContourDetector;
import net.doo.snap.lib.detector.DetectionResult;
import net.doo.snap.persistence.PageFactory;
import net.doo.snap.persistence.cleanup.Cleaner;
import net.doo.snap.process.DocumentProcessor;
import net.doo.snap.process.draft.DocumentDraftExtractor;
import net.doo.snap.process.OcrResult;
import net.doo.snap.util.FileChooserUtils;
import net.doo.snap.util.bitmap.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Scanbot SDK Wrapper for the Cordova Plugin
 */
public class ScanbotSdkWrapper {

    private static final String LOG_TAG = ScanbotSdkWrapper.class.getSimpleName();

    private final Context context;

    public ScanbotSdkWrapper(final Context context) {
        this.context = context;
    }


    public PageFileStorage pageFileStorage() {
        return new ScanbotSDK((Activity) this.context).getPageFileStorage();
    }

    public PageProcessor pageProcessor() {
        return new ScanbotSDK((Activity) this.context).pageProcessor();
    }

    public Bitmap loadImage(final String imageFilePath) throws IOException {
        final Bitmap bitmap = BitmapUtils.decodeQuietly(imageFilePath, null);
        if (bitmap == null) {
            throw new IOException("Could not load image. Bitmap is null.");
        }
        return bitmap;
    }


    public Bitmap loadImage(final Uri imageUri) throws IOException {
        return this.loadImage(FileChooserUtils.getPath(this.context, imageUri));
    }


    public File storeImageAsFile(final Bitmap image, final int quality) throws IOException {
        final File pictureFile = StorageUtils.generateRandomPluginStorageFile("jpg", this.context);
        final FileOutputStream fos = new FileOutputStream(pictureFile);
        image.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        fos.close();
        return pictureFile;
    }


    public Uri storeImage(final Bitmap image, final int quality) throws IOException {
        return Uri.fromFile(storeImageAsFile(image, quality));
    }


    public DocumentDetectionResult documentDetection(final Bitmap bitmap, final boolean releaseBitmap) {
        debugLog("Applying document detection on bitmap...");

        final ContourDetector detector = new ContourDetector();
        final DetectionResult sdkDetectionResult = detector.detect(bitmap);
        final List<PointF> polygon = detector.getPolygonF();

        final Bitmap documentImage;
        if (releaseBitmap) {
            /*
             * This operation crops original bitmap and creates a new one. Old bitmap is recycled
             * and can't be used anymore!!
             */
            documentImage = detector.processImageAndRelease(bitmap, polygon, ContourDetector.IMAGE_FILTER_NONE);
        }
        else {
            documentImage = detector.processImageF(bitmap, polygon, ContourDetector.IMAGE_FILTER_NONE);
        }

        return new DocumentDetectionResult(sdkDetectionResult, polygon, documentImage);
    }

    public List<String> getInstalledOcrLanguages(final PdfSdk pdfSdk) throws IOException {
        debugLog("Detecting installed OCR languages...");

        final List<String> installedLanguages = new ArrayList<String>();
        final Set<Language> allLanguagesWithAvailableOcrBlobs = pdfSdk.blobManager.getAllLanguagesWithAvailableOcrBlobs();
        for (final Language language : allLanguagesWithAvailableOcrBlobs) {
            installedLanguages.add(language.getIso1Code());
        }
        return installedLanguages;
    }


    public OcrResult performOcr(final List<String> languagesIsoCodes,
                                OpticalCharacterRecognizer ocrRecognizer,
                                final List<Uri> images,
                                String outputFormat) throws IOException {
        debugLog("Performing OCR...");

        final Set<Language> languages = new HashSet<Language>();
        for (final String languageIsoCode : languagesIsoCodes) {
            languages.add(Language.languageByIso(languageIsoCode));
        }

        final Set<Language> installedLanguages = ocrRecognizer.getInstalledLanguages();

        if (!installedLanguages.containsAll(languages)) {
            throw new IOException("OCR blobs for selected languages were not found.");
        }

        if (outputFormat.equals("PLAIN_TEXT")) {
            return ocrRecognizer.recognizeTextFromUris(images, languages);
        }
        else {
            return ocrRecognizer.recognizeTextWithPdfFromUris(images, PDFPageSize.FROM_IMAGE, languages);
        }
    }


    public Bitmap cropAndWarpImage(final Bitmap bitmap, final List<PointF> polygon, final boolean releaseBitmap) {
        final ContourDetector detector = new ContourDetector();
        if (releaseBitmap) {
            /*
             * This operation crops original bitmap and creates a new one. Old bitmap is recycled
             * and can't be used anymore!!
             */
            return detector.processImageAndRelease(bitmap, polygon, ContourDetector.IMAGE_FILTER_NONE);
        }
        else {
            return detector.processImageF(bitmap, polygon, ContourDetector.IMAGE_FILTER_NONE);
        }
    }


    public Bitmap applyImageFilter(final Bitmap bitmap, final ImageFilterType imageFilter) {
        debugLog("Applying image filter on bitmap...");

        final ContourDetector detector = new ContourDetector();
        final List<PointF> polygon = new ArrayList<PointF>();

        return detector.processImageAndRelease(bitmap, polygon, imageFilter.getCode());
    }


    public File createPdf(final List<Uri> images, final PDFPageSize pageSize, final PdfSdk pdfSdk) {
        PDFRenderer pdfRenderer = pdfSdk.scanbotSDK.pdfRenderer();
        return pdfRenderer.renderDocumentFromImages(images, pageSize);
    }

    public File writeTiff(final List<Uri> images, boolean binarized) throws IOException {
        final TIFFWriter writer = new TIFFWriter();
        final List<File> files = new ArrayList<File>();
        for (final Uri uri : images) {
            files.add(new File(uri.getPath()));
        }

        final File tiffTargetFile = StorageUtils.generateRandomPluginStorageFile("tiff", this.context);
        if (binarized) {
            writer.writeBinarizedMultiPageTIFFFromFileList(files, tiffTargetFile);
        } else {
            writer.writeMultiPageTIFFFromFileList(files, tiffTargetFile);
        }
        return tiffTargetFile;
    }

    public Uri getFilteredDocumentPreviewUri(final Page page, final ImageFilterType filterType) throws IOException {
        final Uri filteredPreviewUri = pageFileStorage().getFilteredPreviewImageURI(page.getPageId(), filterType);
        if (!new File(filteredPreviewUri.getPath()).exists()) {
            pageProcessor().generateFilteredPreview(page, filterType);
        }
        return filteredPreviewUri;
    }

    public Page applyImageFilterOnPage(final Page page, final ImageFilterType filterType) throws IOException {
        return new ScanbotSDK((Activity) this.context).pageProcessor().applyFilter(page, filterType);
    }

    public Page rotatePage(final Page page, final int times) throws IOException {
        return new ScanbotSDK((Activity) this.context).pageProcessor().rotate(page, times);
    }

    public Page detectDocumentOnPage(final Page page) throws IOException {
        return new ScanbotSDK((Activity) this.context).pageProcessor().detectDocument(page);
    }


    private void debugLog(final String msg) {
        LogUtils.debugLog(LOG_TAG, msg);
    }

    private void errorLog(final String msg) {
        LogUtils.errorLog(LOG_TAG, msg);
    }


    public static final class DocumentDetectionResult {
        public final DetectionResult sdkDetectionResult;
        public final List<PointF> polygon = new ArrayList<PointF>();
        public final Bitmap documentImage;

        public DocumentDetectionResult(final DetectionResult sdkDetectionResult,
                                       final List<PointF> polygon,
                                       final Bitmap documentImage) {
            this.sdkDetectionResult = sdkDetectionResult;
            if (polygon != null) {
                this.polygon.addAll(polygon);
            }
            this.documentImage = documentImage;
        }
    }


    public static final class PdfSdk {
        public final ScanbotSDK scanbotSDK;
        public final PageFactory pageFactory;
        public final DocumentProcessor documentProcessor;
        // scanbotSDK.documentDraftExtractor() returns default DocumentDraftExctactor which treats every snappingDraft as single document.
        // You can change this behaviour by either implementing your own extractor or using one which is coming with SDK (see MultipleDocumentsDraftExtractor)
        public final DocumentDraftExtractor documentDraftExtractor;
        public final Cleaner sdkCleaner;
        public final BlobManager blobManager;
        public final BlobFactory blobFactory;

        public PdfSdk(final Activity activity) {
            this.scanbotSDK = new ScanbotSDK(activity);
            this.pageFactory = scanbotSDK.pageFactory();
            this.documentProcessor = scanbotSDK.documentProcessor();
            this.documentDraftExtractor = scanbotSDK.documentDraftExtractor();
            this.sdkCleaner = scanbotSDK.cleaner();
            this.blobManager = scanbotSDK.blobManager();
            this.blobFactory = scanbotSDK.blobFactory();
        }
    }


}
