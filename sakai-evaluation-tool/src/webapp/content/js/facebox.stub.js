(function(global){
  var $ = global.jQuery;
  if (!$ || $.facebox) {
    return;
  }

  var noop = function() { return this; };
  var settings = {
    elementToUpdate: null,
    loadingImage: '',
    overlay: false,
    closeImage: null,
    faceboxHtml: ''
  };

  var api = function() { return; };
  api.settings = settings;
  api.loading = function() {};
  api.reveal = function() {};
  api.close = function() {};
  api.setHeader = function() {};
  api.saveOrder = function() {};
  api.clearFacebox = function() {};

  $.facebox = api;
  $.fn.facebox = function(){ return this; };
  $.fn.faceboxGrid = function(){ return this; };
  $.fn.childEdit = function(){ return this; };
})(window);
