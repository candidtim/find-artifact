window.onload = function () {
  'use strict';

  var defaultBuildToolCookie = 'default-build-tool';

  var showPopover = function (buttonId) {
    $('#'+buttonId).popover('show');
    setTimeout(function() {
        $('#'+buttonId).popover('hide');
    }, 1000);
  };

  var setCookie = function (cname, value) {
    var today = new Date();
    var expire = new Date(); expire.setTime(today.getTime() + 3600000*24*365);
    document.cookie = cname+'='+value+';expires='+expire.toGMTString();
  };

  var getCookie = function (cname) {
    var name = cname + '=';
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
      var c = ca[i];
      while (c.charAt(0) === ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) === 0) {
        return c.substring(name.length,c.length);
      }
    }
    return '';
  };

  var copy = function (buttonId, containerId) {
    var node = document.getElementById( containerId );
    if ( document.selection ) {
      var range = document.body.createTextRange();
      range.moveToElementText( node );
      range.select();
    } else if ( window.getSelection ) {
      var range = document.createRange();
      range.selectNodeContents( node );
      window.getSelection().removeAllRanges();
      window.getSelection().addRange( range );
    }
    var successful = document.execCommand('copy');
    if (successful) {
      showPopover(buttonId);
    }
  };

  var setDefaultBuildTool = function (buttonId, buildTool) {
    setCookie(defaultBuildToolCookie, buildTool);
    showPopover(buttonId);
  };

  var activateBuildToolTab = function (defaultBuildTool) {
    var buildTool = defaultBuildTool || 'Maven';
    $('#tab-title-'+buildTool).addClass('active');
    $('#tab-'+buildTool).addClass('active');
  };


  //
  // actions on load
  //
  activateBuildToolTab(getCookie(defaultBuildToolCookie));


  window['findartifact'] = window['findartifact'] || {};
  window['findartifact'].copy = copy;
  window['findartifact'].setDefaultBuildTool = setDefaultBuildTool;

};
