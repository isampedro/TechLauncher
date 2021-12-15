'use strict';
define(['angular'], function(angular) {
  var mod = angular.module('components',['restangular']);
  mod.controller('PagingCtrl', function($scope,Restangular) {
    $scope.data = $scope.paging;
    $scope.refreshData = function () {
        $scope.prev = undefined;
        $scope.next = undefined;
        var parts = $scope.data.split(',').reduce((acc, link) => {
        var match = link.match(/<(.*)>; rel="(\w*)"/)
        var url = match[1]
        var rel = match[2]
        acc[rel] = url
        return acc;
      }, {});

      if (parts.next !== undefined) {
        $scope.next = parts.next;
      }
      if (parts.prev !== undefined) {
        $scope.prev = parts.prev;
      }
    };
    $scope.refreshData();

    $scope.$watch('paging',function () {
      $scope.data = $scope.paging;
      $scope.refreshData();
    });

    $scope.getPrev = function () {
      Restangular.oneUrl('routeName',$scope.prev).get().then( function (response) {
        if($scope.id === undefined) {
          $scope.$parent.setData(response);
        } else {
          $scope.$parent.setData(response,$scope.id);
        }
        $scope.data = response.headers('link');
        $scope.refreshData();
      });
    };
    $scope.getNext = function () {
      Restangular.oneUrl('routeName',$scope.next).get().then( function (response) {
        if($scope.id === undefined) {
          $scope.$parent.setData(response);
        } else {
          $scope.$parent.setData(response,$scope.id);
        }
        $scope.data = response.headers('link');
        $scope.refreshData();
      });
    };
  });
	mod.directive('myNavbar', function() {
    return {
      restrict: 'E',
      controller: 'IndexCtrl',
      templateUrl: 'views/components/navbar.html'
    };
  });
  mod.directive('pagination', function() {
    return {
      restrict: 'E',
      scope: {
        paging: '@',
        id: '@'
      },
      controller: 'PagingCtrl',
      templateUrl: 'views/components/pagination.html'
    };
  });
  mod.directive('modalDeletePost', function() {
    return {
      restrict: 'E',
      scope: {
        del: '=',
        clean: '='
      },
      templateUrl: 'views/components/post_modal.html'
    };
  });

  mod.directive('modalLogin', function() {
    return {
      restrict: 'E',
      templateUrl: 'views/components/login_modal.html'
    };
  });

  mod.directive('mailModal', function() {
    return {
      restrict: 'E',
      templateUrl: 'views/components/confirm_mail_modal.html'
    };
  });

  mod.directive('deleteCommentModal', function() {
    return {
      restrict: 'E',
      scope: {
        del: '=',
        clean: '='
      },
      templateUrl: 'views/components/delete_comment_modal.html'
    };
  });

  mod.directive('deleteContentModal', function() {
    return {
      restrict: 'E',
      scope: {
        del: '=',
        clean: '='
      },
      templateUrl: 'views/components/delete_content_modal.html'
    };
  });

  mod.directive('deleteTechModal', function() {
    return {
      restrict: 'E',
      scope: {
        del: '=',
        clean: '='
      },
      templateUrl: 'views/components/delete_tech_modal.html'
    };
  });

  mod.directive('addContentModal', function() {
    return {
      restrict: 'E',
      scope: {
        add: '=',
        error: '=',
        errorDetails: '='
      },
      templateUrl: 'views/components/add_content_modal.html'
    };
  });

  mod.directive('reportContentModal', function() {
    return {
      restrict: 'E',
      scope: {
        report: '=',
        clean: '='
      },
      templateUrl: 'views/components/report_content_modal.html'
    };
  });

  mod.directive('reportCommentModal', function() {
    return {
      restrict: 'E',
      scope: {
        report: '=',
        clean: '='
      },
      templateUrl: 'views/components/report_comment_modal.html'
    };
  });

});
