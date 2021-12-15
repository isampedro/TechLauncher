'use strict';
define(['frontend','services/userService','services/sessionService','ng-file-upload'], function(frontend) {

  frontend.controller('UserCtrl', function($location, $scope, $routeParams, userService,sessionService,$window,Restangular) {

    $scope.$parent.$watch('username',function () {
      var user = sessionService.getStorageUser();
      if ($scope.$parent.username !== undefined) {
        $scope.username = $scope.$parent.username;
        sessionService.getCurrentUser(user.location).then(function (response) {
          $scope.allowMod = response.data.allowedModeration;
          $scope.modValue = $scope.allowMod;
          $scope.username = response.data.username;
          $scope.id = $routeParams.id;
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      }
    });

    userService.getUser($routeParams.id).then(function (user) {
      $scope.profile = user.data;
      if (user.commentAmount !== 0) {
        userService.getData($scope.profile.comments).then(function (comments) {
          $scope.profile.comments = comments.data;
          $scope.commentPaging = comments.headers('link');
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      }
      if (user.contentAmount !== 0) {
        userService.getData($scope.profile.content).then(function (content) {
          $scope.profile.content = content.data;
          $scope.contentPaging = content.headers('link');
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      }
      if (user.postsAmount !== 0) {
        userService.getData($scope.profile.posts).then(function (posts) {
          $scope.profile.posts = posts.data;
          $scope.postsPaging = posts.headers('link');
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      }
      if (user.techsAmount !== 0) {
        userService.getData($scope.profile.techs).then(function (techs) {
          $scope.profile.techs = techs.data;
          $scope.techsPaging = techs.headers('link');
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      }
      if (user.votesAmount !== 0) {
        userService.getData($scope.profile.votes).then(function (votes) {
          $scope.profile.votes = votes.data;
          $scope.votesPaging = votes.headers('link');

        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      }
    });
    $scope.setData = function(response,id) {
      switch (id) {
        case 'comments':
          $scope.profile.comments = response.data;
          $scope.commentPaging = response.headers('link');
          break;
        case 'content':
          $scope.profile.content = response.data;
          $scope.contentPaging = response.headers('link');
          break;
        case 'techs':
          $scope.profile.techs = response.data;
          $scope.techsPaging = response.headers('link');
          break;
        case 'posts':
          $scope.profile.posts = response.data;
          $scope.postsPaging = response.headers('link');
          break;
        case 'votes':
          $scope.profile.votes = response.data;
          $scope.votesPaging = response.headers('link');
          break;
        default:
          break;
      }
    };
    $scope.updateMod = function(set) {
      userService.setMod($scope.profile.modLocation,set).then(function (response) {
        if (response.status === 200) {
          userService.getUser($routeParams.id).then(function (user) {
            $scope.profile = user.data;
            $scope.allowMod = user.data.allowedModeration;
            $scope.modValue = $scope.allowMod;
            if (set === false) {
              $('#stopBeingAModModal').modal('hide');
            }
          }).catch((error) => {
            if(error.status === 404) {
              $location.path('/404');
            }
            else {
              $location.path('/500');
            }
          });

        }
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };
    $scope.changeMod = function() {
      if ($scope.allowMod === true) {
        $('#stopBeingAModModal').modal('show');
      } else {
        $scope.updateMod(true);
      }
    };

    $('#stopBeingAModModal').on('hide.bs.modal',function () {
      $scope.modValue = $scope.allowMod;
      $('#modCheckbox').prop('checked',$scope.allowMod);
    });
    $('#editProfileModal').on('hide.bs.modal',function () {
      $scope.update = undefined;
    });
    $scope.setPic = function(file) {
      $scope.update.picture = file;
    };

    $scope.updateProfile = function () {
      userService.update($scope.update.picture,$scope.update.description,$routeParams.id).then(function (response) {
        if (response.status === 200) {
          userService.getUser($routeParams.id).then(function (user) {
            $scope.profile.description = user.data.description;
            $scope.profile.image = user.data.image + '?t=' + new Date().getTime();
            $('profilePicture').get();
          }).catch((error) => {
            $('#editProfileModal').modal('hide');
            if(error.status === 404) {
              $location.path('/404');
            }
            else {
              $location.path('/500');
            }
          });
          $('#editProfileModal').modal('hide');
        }
      }).catch((error) => {
        if( error.status === 400 ) {
          $scope.error = true;
          $scope.errorDetails = error.data;
        } else {
          $('#editProfileModal').modal('hide');
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        }
      });
    };
    $scope.changePass = function() {
      $('#editProfileModal').modal('hide');
    };
  });
});
