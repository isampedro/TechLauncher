'use strict';
define(['frontend','ng-file-upload'], function(frontend) {

  frontend.controller('SuccessCtrl', function($scope, $filter, $routeParams,$localStorage, $location) {
    switch ($routeParams.success_type) {
      case 'pending_validation' : $scope.title = $filter('translate')('REGISTER_SUCCESS_ACCOUNT_CREATED');
                                  $scope.message = $filter('translate')('REGISTER_SUCCESS_EMAIL_SENT');
                                  break;
      case 'email_validated'    : $scope.title = $filter('translate')('REGISTER_SUCCESS_EMAIL_VALIDATED');
                                  $scope.message = $filter('translate')('REGISTER_SUCCESS_ACCOUNT_VALIDATED');
                                  break;
      case 'email_resent'       : $scope.title = $filter('translate')('REGISTER_SUCCESS_ACCOUNT_RESENT');
                                  $scope.message = $filter('translate')('REGISTER_SUCCESS_EMAIL_RESENT');
                                  break;
      case 'recover_password'   : $scope.title = $filter('translate')('REGISTER_CHANGE_PASSWORD_TITLE');
                                  $scope.message = $filter('translate')('REGISTER_CHANGE_PASSWORD_MESSAGE');
                                  break;
    }

    $scope.goHome = () => {
      $location.path('/');
    }
  });
  });
