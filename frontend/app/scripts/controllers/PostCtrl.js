'use strict';
define(['frontend', 'services/postService', 'services/sessionService'], function(frontend) {

  frontend.controller('PostCtrl', function($scope, $location, $window,$rootScope, $routeParams, postService, $sessionStorage,Restangular, sessionService, $localStorage) {

    $scope.isPresent = false;
    $scope.$parent.$watch('username',function () {
      var user = sessionService.getStorageUser();
      if (user !== undefined) {
        sessionService.getCurrentUser(user.location).then(function (response) {
          $scope.username = response.data.username;
          $scope.isMod = response.data.verify;
          $scope.isAdmin = response.data.admin;
          $scope.isEnabled = response.data.enabled;
          $scope.isPresent = true;
        });
      }
    });

    $scope.getTags = function () {
      postService.getTags().then((tags => {
        $scope.names = tags.data.names;
        $scope.categories = tags.data.categories;
        $scope.types = tags.data.types;
      }));
    }

    $scope.getPost = function() {
      postService.getPost($routeParams.id).then(function(response) {
        $scope.post = response.data;
        $scope.namesChosen = [];
        $scope.categoriesChosen = [];
        $scope.typesChosen = [];
        $scope.postTitleInput = $scope.post.postTitle;
        $scope.postDescriptionInput = $scope.post.postDescription;
        $scope.getTags();
        $scope.post.postTags.forEach( tag => {
          if( tag.type === 'tech_name') {
            $scope.namesChosen.push(tag.tagName);
            _.remove($scope.names, (value) => {
              return value === tag.tagName;
            })
          } else if ( tag.type === 'tech_category') {
            $scope.categoriesChosen.push(tag.tagName);
            _.remove($scope.categories, (value) => {
              return value === tag.tagName;
            })
          } else {
            $scope.typesChosen.push(tag.tagName);
            _.remove($scope.types, (value) => {
              return value === tag.tagName;
            })
          }
        });

      }).catch( function () {
        $window.location.href = '#/404';
      });
    };


    $scope.getAnswers = function() {
      postService.getAnswers($routeParams.id).then(function(response) {
        $scope.answers = response.data;
        $scope.pagingLinks = response.headers('link');
      });

    };

    $scope.redirectToExplore = function(tag, type) {

      $rootScope.tagToSearch = tag;
      $rootScope.tagType = type;
      $window.location.href = '#/explore';
    };

    $scope.setDel = function (url) {
      $scope.toDel = url;
    };
    $scope.cleanDel = function () {
      $scope.toDel = undefined;
    };

    $scope.deletePost = function() {
      postService.deletePost($scope.toDel).then(function() {
        $('#deletePostModal').modal('hide');
        $scope.cleanDel();
        $window.location.href = '#/posts';
      });


    };

    $scope.deleteAnswer = function() {
      postService.deletePost($scope.toDel).then(function() {
        $('#deleteCommentModal').modal('hide');
        $scope.getPost();
        $scope.getAnswers();
        $scope.cleanDel();

      });
    };

    $scope.upVote = function() {
      postService.upVote($scope.post.location).then(function() {
        $scope.getPost();
      });
    };

    $scope.downVote = function(location) {
      postService.downVote($scope.post.location).then(function() {
        $scope.getPost();
      });
    };

    $scope.upVoteAnswer = function(location) {
      postService.upVoteAnswer(location).then(function() {
        $scope.getAnswers();
      });
    };

    $scope.downVoteAnswer = function(location) {
      postService.downVoteAnswer(location).then(function() {
        $scope.getAnswers();
      });
    };

    $scope.commentPost = function(answer) {
      postService.commentPost($routeParams.id, answer).then(function () {
        $("#commentBox").val('');
        $scope.getPost();
        $scope.getAnswers();
      });
    };

    $scope.setData = function(response) {
      $scope.answers = response.data;
      $scope.pagingLinks = response.headers('link');
    };

    $('#deletePostModal').on('hide.bs.modal',function () {
      $scope.cleanDel();
    });

    $('#deleteCommentModal').on('hide.bs.modal',function () {
      $scope.cleanDel();
    });


    $scope.tagsEmpty = () => {
      return _.isEmpty($scope.namesChosen) && _.isEmpty($scope.categoriesChosen) && _.isEmpty($scope.typesChosen)
    }


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



    $scope.editPost = () => {
      let post = {
        'title' : $scope.postTitleInput,
        'description' : $scope.postDescriptionInput,
        'names' : $scope.namesChosen,
        'types' : $scope.typesChosen,
        'categories' : $scope.categoriesChosen
      }
      postService.editPost(post, $scope.post.location).then((response) => {
        $location.path('/posts/' + $routeParams.id);
      });
    }

    $scope.getPost();
    $scope.getAnswers();


    // Form Validations
    $scope.postTitleValidator = {
      minLen: 3,
      maxLen: 200,
    };

    $scope.postDescriptionValidator = {
      minLen: 1,
      maxLen: 5000,
    };

  });

});
