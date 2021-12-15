'use strict';
define(['frontend', 'services/sessionService'], function(frontend) {


  frontend.controller('RegisterConfirmCtrl', function($scope, $routeParams, sessionService, $filter, $location) {
    $scope.registerConfirmed = false;
    sessionService.confirmRegister($routeParams.token).then(function(response) {
      if (response.status === 200) {
        $scope.registerConfirmed = true;
        $scope.title = $filter('translate')('REGISTER_SUCCESS_EMAIL_VALIDATED');
        $scope.message = $filter('translate')('REGISTER_SUCCESS_ACCOUNT_VALIDATED');
        $scope.button = $filter('translate')('BUTTON_GO_HOME')
        $scope.status = response.status;
      }
    }).catch((error) => {
        if(error.status === 410 ){
          $scope.title = $filter('translate')('REGISTER_REQUEST_NEW_TOKEN_TITLE');
          $scope.message = $filter('translate')('REGISTER_REQUEST_NEW_TOKEN_DESCRIPTION');
          $scope.button = $filter('translate')('BUTTON_REQUEST_NEW_TOKEN')
          $scope.status = error.status;
        }
        else if( error.status === 404 ) {
          $location.path('/404');
        } else {
          $location.path('/500');
        }
    });
    $scope.goHome = () => {
      if( $scope.status === 200 ) {
        $location.path('/');
      } else if( $scope.status === 410 ) {
        sessionService.requestNewToken().then((response) => {
          $scope.title = $filter('translate')('REGISTER_SUCCESS_ACCOUNT_RESENT');
          $scope.message = $filter('translate')('REGISTER_SUCCESS_EMAIL_RESENT');
          $scope.button = $filter('translate')('BUTTON_GO_HOME')
          $scope.status = response.status;
        })
      }
    }
  });
});
