/*
  Scanbot SDK Cordova Plugin

  Copyright (c) 2017 doo GmbH. All rights reserved.
 */

function cordova_exec(actionName, module) {
  var func = function(successCallback, errorCallback, options) {
    cordova.exec(successCallback, errorCallback, module, actionName, (options ? [options] : []));
  };

  func.__is_cordova_wrapper__ = true;

  return func;
}

function makeCaller(actionName) {
  return cordova_exec(actionName, "ScanbotSdk");
}

function makeUiCaller(actionName) {
  return cordova_exec(actionName, "ScanbotSdkUi");
}

var API = {

  UI: {
    // Scanbot SDK UI functions:

    startDocumentScanner: makeUiCaller("startDocumentScanner"),
    startCroppingScreen: makeUiCaller("startCroppingScreen"),
    startBarcodeScanner: makeUiCaller("startBarcodeScanner"),
    startMrzScanner: makeUiCaller("startMrzScanner"),
  },

  // ------------------------------------------------
  // Scanbot SDK functions:

  initializeSdk: makeCaller("initializeSdk"),
  isLicenseValid: makeCaller("isLicenseValid"),
  detectDocument: makeCaller("detectDocument"),
  applyImageFilter: makeCaller("applyImageFilter"),
  rotateImage: makeCaller("rotateImage"),
  createPdf: makeCaller("createPdf"),
  writeTiff: makeCaller("writeTiff"),
  performOcr: makeCaller("performOcr"),
  getOcrConfigs: makeCaller("getOcrConfigs"),
  createPage: makeCaller("createPage"),
  setDocumentImage: makeCaller("setDocumentImage"),
  detectDocumentOnPage: makeCaller("detectDocumentOnPage"),
  applyImageFilterOnPage: makeCaller("applyImageFilterOnPage"),
  getFilteredDocumentPreviewUri: makeCaller("getFilteredDocumentPreviewUri"),
  rotatePage: makeCaller("rotatePage"),
  removePage: makeCaller("removePage"),
  cleanup: makeCaller("cleanup")
};

(function() {
  if (typeof Promise === "function") {
    function promisify(func) {
      return function(options) {
        return new Promise(function (resolve, reject) {
          func(resolve, reject, options);
        });
      }
    }

    function promisifyObject(obj) {
      var wrapper = {};
      for (var propName in obj) {
        var prop = obj[propName];
        if (typeof prop === "function" && prop.__is_cordova_wrapper__) {
          wrapper[propName] = promisify(prop);
        }
      }
      return wrapper;
    }

    var promises = null;
    API.promisify = function() {
      if (!promises) {
        promises = promisifyObject(API);
        promises.UI = promisifyObject(API.UI);
      }
      return promises;
    }
  }  
})();

module.exports = API;
