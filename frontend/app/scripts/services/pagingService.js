'use strict';
define(['frontend'], function(frontend) {

  frontend.service('pagingService', function(Restangular) {

    this.getPage = function (location) {
      return Restangular.one(location).get();
    };

  });
});
