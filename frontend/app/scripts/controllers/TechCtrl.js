'use strict';
define(['frontend', 'services/techsService', 'services/sessionService'], function(frontend) {

  frontend.controller('TechCtrl', function($scope, $location, $window, $routeParams, techsService, $sessionStorage,Restangular, sessionService, $localStorage, $rootScope) {

    $scope.isPresent = false;
    $scope.isVerify = false;
    $scope.verifyPending = false;
    $scope.isOwner = false;

    $scope.getUser = function () {
      var user = sessionService.getStorageUser();

      if (user !== undefined) {
        sessionService.getCurrentUser(user.location).then(function (response) {
          $scope.username = response.data.username;
          $scope.isMod = response.data.verify;
          $scope.isAdmin = response.data.admin;
          $scope.isEnable = response.data.enabled;
          $scope.allowMod = response.data.allowedModeration;
          $scope.isPresent = true;
          $scope.userVerifications = response.data.applications;
          $scope.userVotes = response.data.techVotes;

          if ($scope.userVerifications !== undefined && $scope.userVerifications.length > 0) {
            let verification;
            for (verification of $scope.userVerifications) {
              if (verification.frameworkName === $scope.tech.name) {
                if (!verification.pending) {
                  $scope.isVerify = true;
                  $scope.verifyPending = false;
                } else {
                  $scope.isVerify = false;
                  $scope.verifyPending = true;
                }
              }

            }
          }

          if ($scope.userVotes !== undefined && $scope.userVotes.length > 0) {
            let vote;
            for (vote of $scope.userVotes) {
              if (vote.techName === $scope.tech.name) {
                $scope.star = vote.vote;
              }

            }
          }
        }).catch((error) => {
            $location.path('/404');
        });
      }
    }

    $scope.getTech = function() {

      techsService.getTech($routeParams.id).then(function (tech) {
        $scope.tech = tech.data;
        $scope.getUser();
        if ($scope.tech.author === $scope.username) {
          $scope.isOwner = true;
        }

        if ($scope.userVerifications !== undefined && $scope.userVerifications.length > 0) {
          let verification;
          for (verification of $scope.userVerifications) {
            if (verification.frameworkName === $scope.tech.name) {
              if (!verification.pending) {
                $scope.isVerify = true;
                $scope.verifyPending = false;
              } else {
                $scope.isVerify = false;
                $scope.verifyPending = true;
              }
            }

          }
        }

        if ($scope.userVotes !== undefined && $scope.userVotes.length > 0) {
          let vote;
          for (vote of $scope.userVotes) {
            if (vote.techName === $scope.tech.name) {
              $scope.star = vote.vote;
            }

          }
        }

        techsService.getData($scope.tech.comments).then(function (comments) {
          $scope.tech.comments = comments.data;
          $scope.commentsPaging = comments.headers('link');
        }).catch((error) => {
          $location.path('/404');
        });

        techsService.getData($scope.tech.books).then(function (books) {
          $scope.tech.books = books.data;
          $scope.booksPaging = books.headers('link');
        }).catch((error) => {
          $location.path('/404');
        });

        techsService.getData($scope.tech.courses).then(function (courses) {
          $scope.tech.courses = courses.data;
          $scope.coursesPaging = courses.headers('link');
        }).catch((error) => {
          $location.path('/404');
        });

        techsService.getData($scope.tech.tutorials).then(function (tutorials) {
          $scope.tech.tutorials = tutorials.data;
          $scope.tutorialsPaging = tutorials.headers('link');
        }).catch((error) => {
          $location.path('/404');
        });

        techsService.getData($scope.tech.competitors).then(function (competitors) {
          $scope.tech.competitors = competitors.data;
        }).catch((error) => {
          $location.path('/404');
        });

        if ($scope.tech.stars !== undefined) {
          $scope.tech.stars = $scope.tech.stars.toFixed(2);
        }

      }).catch(function () {
        $window.location.href = '#/404';
      });
    };

    $scope.getTech();

    $scope.setData = function(response,id) {
      switch (id) {
        case 'comments':
          $scope.tech.comments = response.data;
          $scope.commentsPaging = response.headers('link');
          break;
        case 'books':
          $scope.tech.books = response.data;
          $scope.booksPaging = response.headers('link');
          break;
        case 'courses':
          $scope.tech.courses = response.data;
          $scope.coursesPaging = response.headers('link');
          break;
        case 'tutorials':
          $scope.tech.tutorials = response.data;
          $scope.tutorialsPaging = response.headers('link');
          break;
        case 'competitors':
          $scope.tech.competitors = response.data;
          break;
        default:
          break;
      }
    };

    $scope.redirectToExplore = function(tag) {
      $rootScope.tagToSearch = tag;
      $rootScope.tagType = 'tech_name';
      $window.location.href = '#/explore';
    };

    $scope.isReporter = function (data, username) {
      let report;
      if (data.reports !== undefined && data.reports.length > 0) {
        for (report of data.reports) {
          if (report.reported === username) {
            return true;
          }
        }
      }
      return false;
    }

    $scope.setDel = function (url) {
      $scope.toDel = url;
    };

    $scope.cleanDel = function () {
      $scope.toDel = undefined;
    };

    $scope.setReport = function (url) {
      $scope.toReport = url;
    };

    $scope.cleanReport = function () {
      $scope.toReport = undefined;
    };

    $scope.deleteTech = function() {
      $('#deleteTechModal').modal('hide');
      techsService.deleteData($scope.toDel).then(function() {
        $location.path('/techs');
      }).catch((error) => {
        $location.path('/404');
      });
    };

    $scope.deleteContent = function() {
      techsService.deleteData($scope.toDel).then(function() {
        $scope.getTech();
        $('#deleteContentModal').modal('hide');
      }).catch((error) => {
        $location.path('/404');
      });
    };

    $scope.deleteComment = function() {
      techsService.deleteData($scope.toDel).then(function() {
        $scope.getTech();
        $('#deleteCommentModal').modal('hide');
      }).catch((error) => {
        $location.path('/404');
      });
    };

    $scope.addContent = function (title, type, link) {
      $scope.error = false;
      $scope.errorDetails = undefined;
          techsService.addContent($routeParams.id, title, type, link).then(function () {
            $scope.getTech();
            $('#addContentModal').modal('hide');
            $('#addContentTitle').val('');
            $('#addContentType').val('');
            $('#addContentLink').val('');
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


    $scope.addComment = function(input) {
      techsService.addComment($routeParams.id, input).then(function () {
        $scope.getTech();
        $scope.commentInput = '';
      });
    }

    $scope.replyComment = function (location, input, index) {
      techsService.addReply(location, input).then(function () {
        $scope.getTech();
        $("#replyComment--" + index).val('');
        $scope.commentInput = '';
      });
    }

    $scope.reportContent = function(description) {
      techsService.report($scope.toReport, description).then(function () {
        $scope.getTech();
        $('#reportContentModal').modal('hide');
      }).catch((error) => {
        $location.path('/404');
      });
    }

    $scope.reportComment = function(description) {
      techsService.report($scope.toReport, description).then(function () {
        $scope.getTech();
        $('#reportCommentModal').modal('hide');
      }).catch((error) => {
        $location.path('/404');
      });
    }

    $scope.upVote = function (location) {
      techsService.vote(location, 'up').then(function () {
        $scope.getTech();
      }).catch((error) => {
        $location.path('/404');
      });
    }

    $scope.downVote = function (location) {
      techsService.vote(location, 'down').then(function () {
        $scope.getTech();
      }).catch((error) => {
        $location.path('/404');
      });
    }

    $scope.rateTech = function (stars) {
      techsService.rate($routeParams.id, stars).then(function () {
        $scope.getTech();
        $scope.star = stars;
      }).catch((error) => {
        $location.path('/404');
      });
    }

    $scope.hasUserVotedUp = function (comment, username) {
      let commentVote;
      if (comment.votes == null) {
        return false;
      }
      for (commentVote of comment.votes) {
        if (commentVote.voter === username && commentVote.value > 0) {
          return true;
        }
      }
      return false;
    }

    $scope.hasUserVotedDown = function (comment, username) {
      let commentVote;
      if (comment.votes == null) {
        return false;
      }

      for (commentVote of comment.votes) {
        if (commentVote.voter === username && commentVote.value < 0) {
          return true;
        }
      }
      return false;
    }

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

    $scope.setPic = function(file) {
      $scope.add.picture = file;
    };

    $scope.applyForMod = function (location) {
      techsService.applyForMod(location).then(function (response) {
        if (response.status === 200) {
          $scope.getUser();
          $scope.getTech();
        }
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      })
    };

    $scope.stopBeingAMod = function (location) {
      techsService.stopBeingAMod(location).then(function (response) {
        if (response.status === 200) {
          $scope.getTech();
          $scope.getUser();
          $scope.isVerify = false;
        }
      }).catch( () =>
        $location.path($scope.tech.location)
      );
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
