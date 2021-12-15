'use strict';
define(['frontend', 'services/sessionService'], function(frontend) {


    frontend.controller('RecoverPasswordCtrl', function($scope, sessionService, $location) {
      $('.modal-backdrop').hide();
      $scope.emailSent = false;

      $scope.sendEmail = function(mail) {
        sessionService.setPasswordToken(mail).then(function (response) {
          $scope.emailSent = true;
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      };

    });

});
