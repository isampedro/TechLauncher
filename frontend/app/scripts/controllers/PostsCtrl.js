'use strict';
define(['frontend','services/postService','services/sessionService'], function(frontend) {

  frontend.controller('PostsCtrl', function($scope, $location, sessionService, $window, $routeParams, $sessionStorage,postService, $rootScope) {
    $('.modal-backdrop').hide();
    $scope.isPresent = false;
    $scope.$parent.$watch('username',function () {
      var user = sessionService.getStorageUser();
      if (user !== undefined) {
        sessionService.getCurrentUser(user.location).then(function (response) {
          $scope.username = response.data.username;
          $scope.isMod = response.data.verify;
          $scope.isAdmin = response.data.admin;
          $scope.isEnable = response.data.enabled;
          $scope.isPresent = true;
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

    $scope.getPosts = function() {
      postService.getPosts().then(function (posts) {
        $scope.posts = posts.data;
        $scope.pagingLinks = posts.headers('link');
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };
    $scope.setData = function(response) {
        $scope.posts = response.data;
        $scope.pagingLinks = response.headers('link');
    };
    $scope.getPosts();
    $scope.setDel = function (url) {
      $scope.toDel = url;
    };
    $scope.cleanDel = function () {
      $scope.toDel = undefined;
    };
    $scope.deletePost = function () {
      postService.deletePost($scope.toDel).then(function() {
        $scope.getPosts();
        $scope.cleanDel();
        $('#deletePostModal').modal('hide');
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };
    $('#deletePostModal').on('hide.bs.modal',function () {
      $scope.cleanDel();
    });
    $scope.upVote = function(location) {
      postService.upVote(location).then(function () {
        $scope.getPosts();
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };

    $scope.downVote = function(location) {
      postService.downVote(location).then(function() {
        $scope.getPosts();
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
    };



    $scope.getTags = function () {
       postService.getTags().then((tags => {
         $scope.names = tags.data.names;
         $scope.categories = tags.data.categories;
         $scope.types = tags.data.types;
       })).catch((error) => {
         if(error.status === 404) {
           $location.path('/404');
         }
         else {
           $location.path('/500');
         }
       });
    }
    $scope.getTags();
    $scope.namesChosen = [];
    $scope.categoriesChosen = [];
    $scope.typesChosen = [];

    $scope.tagsEmpty = () => {
      return _.isEmpty($scope.namesChosen) && _.isEmpty($scope.categoriesChosen) && _.isEmpty($scope.typesChosen)
    }

    $scope.redirectToExplore = function(tag, type) {

      $rootScope.tagToSearch = tag;
      $rootScope.tagType = type;
      $window.location.href = '#/explore';
    };

    $scope.addName = function (name) {
      if( !$scope.namesChosen.includes(name) ) {
        $scope.namesChosen.push(name);
      } else {
        _.remove($scope.namesChosen, (value) => {
          return value === name
        })
      }
    }

    $scope.addCategory = function (category) {
      if( !$scope.categoriesChosen.includes(category) ) {
        $scope.categoriesChosen.push(category);
      } else {
        _.remove($scope.categoriesChosen, (value) => {
          return value === category;
        })
      }
    }

    $scope.addType = function (type) {
      if( !$scope.typesChosen.includes(type)) {
        $scope.typesChosen.push(type);
      } else {
        _.remove($scope.typesChosen,(value) => {
          return value === type;
        });
      }
    }

    $scope.addPost = () => {
      let post = {
        'title' : $scope.postTitleInput,
        'description' : $scope.postDescriptionInput,
        'names' : $scope.namesChosen,
        'categories' : $scope.categoriesChosen,
        'types' : $scope.typesChosen
      };
      postService.addPost(post, 'posts').then(response => {
        let startIndex = response.headers('location').indexOf("api/");
        let location = response.headers('location').substring(startIndex + 4);
        $location.path(location);
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
});
