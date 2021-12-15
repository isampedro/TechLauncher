'use strict';
define(['frontend', 'services/sessionService'], function(frontend) {

    frontend.controller('LoginCtrl', function($scope, $window, $http, $location, $localStorage, $sessionStorage, sessionService,Restangular) {
      $('.modal-backdrop').hide();
      $scope.failedLogin = false;
      $scope.rememberMe = false;
      $scope.login = function() {
        sessionService.login($scope.usernameInput, $scope.passwordInput).then(function(response) {
            if (response.data.token) {
              // store username and token in local storage to keep user logged in between page refreshes
              $localStorage.remember = {me: $scope.rememberMe};
              if ($localStorage.remember.me) {
                $localStorage.currentUser = {location: response.data.location, token: response.data.token};
              } else {
                $sessionStorage.currentUser = {location: response.data.location, token: response.data.token};
              }

              $scope.$parent.checkUser();
             $window.location.href = '#/';
            }
        }, function(response) {
          $scope.failedLogin = true;
        });
      };


      // Form Validations
      $scope.usernameValidator = {
        minLen: 3,
        maxLen: 100,
        pattern: /[a-zA-Z0-9]+/
      };

      $scope.passwordValidator = {
        minLen: 6,
        maxLen: 100,
      };


    });

});
