'use strict';
define(['frontend', 'services/techsService', 'services/sessionService'], function(frontend) {

  frontend.controller('EditTechCtrl', function($scope, $location, $window, $routeParams, techsService, $sessionStorage,Restangular, sessionService, $localStorage) {

    $scope.isPresent = false;
    $scope.isVerify = false;
    $scope.isOwner = false;
    $scope.errorDetails = undefined;
    $scope.error = false;

    var user = sessionService.getStorageUser();

    if (user !== undefined) {
      sessionService.getCurrentUser(user.location).then(function (response) {
        $scope.username = response.data.username;
        $scope.isMod = response.data.verify;
        $scope.isAdmin = response.data.admin;
        $scope.isEnable = response.data.enabled;
        $scope.isPresent = true;
        $scope.userVerifications = response.data.verifications;
      }).catch((error) => {
        if(response.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    }

    $scope.getTech = function() {
      techsService.getTech($routeParams.id).then(function (tech) {
        $scope.tech = tech.data;
        $scope.tech.picture = undefined;
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };

    techsService.getCategories().then(function (cats) {
      $scope.categories = cats.data;
    }).catch((error) => {
      if(error.status === 404) {
        $location.path('/404');
      }
      else {
        $location.path('/500');
      }
    });

    techsService.getTypes().then(function (cats) {
      $scope.types = cats.data;
    }).catch((error) => {
      if(error.status === 404) {
        $location.path('/404');
      }
      else {
        $location.path('/500');
      }
    });

    $scope.getTech();

    $scope.setPic = function(file) {
      $scope.tech.picture = file;
    };

    $scope.editTech = function () {
      $scope.error = false;
      $scope.errorDetails = undefined;
      techsService.editTech($routeParams.id, $scope.tech).then(function (response) {
        if (response.status === 200) {
          $window.location.href = '#/techs/' + $routeParams.id;
        }
      }).catch(function (error) {
        if( error.status === 400 ) {
          $scope.error = true;
          $scope.errorDetails = error.data;
        } else if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };

    $scope.addTech = function () {
      $scope.error = false;
      $scope.errorDetails = undefined;
      techsService.addTech($scope.add).then(function (response) {
        if (response.status === 201) {
          let startIndex = response.headers('location').indexOf("api/");
          let location = response.headers('location').substring(startIndex + 4);
          $location.path(location);
        }
      }).catch(function (error) {
        if( error.status === 400 ) {
          $scope.error = true;
          $scope.errorDetails = error.data;
        } else if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };

    // Form Validations
    $scope.techNameValidator = {
      minLen: 1,
      maxLen: 50,
      pattern: /[a-zA-Z0-9 -+#*]+/
    };

    $scope.techIntroValidator = {
      minLen: 1,
      maxLen: 5000,
    };

    $scope.techDescriptionValidator = {
      minLen: 1,
      maxLen: 500,
    };
  });
});
