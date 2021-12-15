'use strict';
define(['frontend','services/sessionService'], function(frontend) {


    frontend.controller('RegisterCtrl', function($location, $scope, sessionService) {
      $('.modal-backdrop').hide();
      $scope.registered = false;
      $scope.error = false;
      $scope.errorDetails = undefined;

      $scope.register = function(email, username, password) {
        sessionService.createUser(email, username, password).then(function(response) {
          $scope.registered = true;
          $scope.error = false;
          $scope.errorDetails = undefined;
        }).catch((error) => {
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
