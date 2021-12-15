'use strict';
define(['frontend', 'services/exploreService', 'services/techsService'], function(frontend) {

    frontend.controller('ExploreCtrl', function($location, $scope, $rootScope, exploreService,techsService) {

      /* Hide search bar in navbar */
      $scope.$parent.searchPage = true;

      /* Explore */

     if ($scope.$parent.navbarSearch !== undefined) {
       $scope.isExplore = false;
       exploreService.search('T', $scope.$parent.navbarSearch, $scope.categories, $scope.types, $scope.starsLeft, $scope.starsRight, $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {
         $scope.matchingTechs = response.data;
         $scope.techsPaging = response.headers('link');
         $scope.navbarNameToSearch = $scope.$parent.navbarSearch;
       }).catch((error) => {
         if(error.status === 404) {
           $location.path('/404');
         }
         else {
           $location.path('/500');
         }
       });
     } else if ($rootScope.tagToSearch !== undefined) {
       $scope.isExplore = false;
       var techTab = document.getElementById('techs');
       var postTab = document.getElementById('posts');
       techTab.classList.remove('active');
       postTab.classList.add('active');

       switch ($rootScope.tagType) {
         case 'tech_type':
           exploreService.search('P', '', $scope.categories, $rootScope.tagToSearch, $scope.starsLeft, $scope.starsRight, $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {
             $scope.posts = response.data;
             $scope.postsPaging = response.headers('link');
           }).catch((error) => {
             if(error.status === 404) {
               $location.path('/404');
             }
             else {
               $location.path('/500');
             }
           });
           break;
         case 'tech_category':
           exploreService.search('P', '', $rootScope.tagToSearch, $scope.types, $scope.starsLeft, $scope.starsRight, $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {
             $scope.posts = response.data;
             $scope.postsPaging = response.headers('link');
           }).catch((error) => {
             if(error.status === 404) {
               $location.path('/404');
             }
             else {
               $location.path('/500');
             }
           });
           break;
         default:
           exploreService.search('P', $rootScope.tagToSearch, $scope.categories , $scope.types, $scope.starsLeft, $scope.starsRight, $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {
             $scope.posts = response.data;
             $scope.postsPaging = response.headers('link');
             $scope.navbarNameToSearch = $rootScope.tagToSearch;
           }).catch((error) => {
             if(error.status === 404) {
               $location.path('/404');
             }
             else {
               $location.path('/500');
             }
           });

       }


     } else {
       $scope.isExplore = true;
       exploreService.getTechs().then(function (response) {
         $scope.matchingTechs = response.data;
         $scope.techsPaging = response.headers('link');
       }).catch((error) => {
         $location.path('/404');
       });

       exploreService.getPosts().then(function (response) {
         $scope.posts = response.data;
         $scope.postsPaging = response.headers('link');
       }).catch((error) => {
         if(error.status === 404) {
           $location.path('/404');
         }
         else {
           $location.path('/500');
         }
       });
     }

     /* Set active tab in order to search techs or posts */

      $scope.activeTab = 'T';
      $scope.setActiveTab = function(tab) {
        $scope.isExplore = false;
       if ($scope.activeTab === 'P' && tab === 'T') {
         $scope.activeTab = 'T';
         exploreService.search($scope.activeTab, $scope.nameToSearch, $scope.categories, $scope.types, $scope.starsLeft, $scope.starsRight, $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {
           $scope.matchingTechs = response.data;
           $scope.techsPaging = response.headers('link');
         }).catch((error) => {
           if(error.status === 404) {
             $location.path('/404');
           }
           else {
             $location.path('/500');
           }
         });
       } else if ($scope.activeTab === 'T' && tab === 'P') {
         $scope.activeTab = 'P';
         exploreService.search($scope.activeTab, $scope.nameToSearch, $scope.categories, $scope.types, $scope.starsLeft, $scope.starsRight, $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {
           $scope.posts = response.data;
           $scope.postsPaging = response.headers('link');
         }).catch((error) => {
           if(error.status === 404) {
             $location.path('/404');
           }
           else {
             $location.path('/500');
           }
         });
       }
     };


      /* Search Results */

      $scope.search = function () {

        $scope.isExplore = false;
        exploreService.search($scope.activeTab, $scope.nameToSearch, $scope.categories, $scope.types, $('#stars-dropdown-1').val(), $('#stars-dropdown-2').val(), $scope.nameFlag === undefined ? false : $scope.nameFlag.selected, $scope.commentAmount, $scope.lastComment, $scope.lastUpdate, $scope.groupBy, $scope.orderValue).then(function (response) {

          if ($scope.activeTab === 'T') {
            $scope.matchingTechs = response.data;
            $scope.techsPaging = response.headers('link');
          } else {
            $scope.posts = response.data;
            $scope.postsPaging = response.headers('link');
          }

          $scope.navbarNameToSearch = undefined;
          $rootScope.tagToSearch = undefined;
        }).catch((error) => {
          if(error.status === 404) {
            $location.path('/404');
          }
          else {
            $location.path('/500');
          }
        });
      };

      /* Get categories and types */

      techsService.getCategories().then(function (response) {
        $scope.categories = response.data;
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });
      techsService.getTypes().then(function (response) {
        $scope.types = response.data;
      }).catch((error) => {
        if(error.status === 404) {
          $location.path('/404');
        }
        else {
          $location.path('/500');
        }
      });

      /* Show more or less categories and types */

      $scope.showMore = function (element) {
        if (element === 'Types') {
          $scope.typesVisible = true;
          $scope.typesShowMoreBtnVisible = true;
          $scope.typesShowLessBtnVisible = true;
        } else {
          $scope.categoriesVisible = true;
          $scope.categoriesShowMoreBtnVisible = true;
          $scope.categoriesShowLessBtnVisible = true;
        }
      };

      $scope.showLess = function(element) {
        if (element === 'Types') {
          $scope.typesVisible = false;
          $scope.typesShowMoreBtnVisible = true;
          $scope.typesShowLessBtnVisible = true;
        } else {
          $scope.categoriesVisible = false;
          $scope.categoriesShowMoreBtnVisible = true;
          $scope.categoriesShowLessBtnVisible = true;
        }
      };

      /* Show search bar when going to another page */
      $scope.$on('$destroy',function() {
         $scope.$parent.searchPage = false;
         $scope.$parent.navbarSearch = undefined;
         $rootScope.tagToSearch = undefined;
      });


      /* Pagination */
      $scope.setData = function(response,id) {
        switch (id) {
          case 'tech':
            $scope.matchingTechs = response.data;
            $scope.techsPaging = response.headers('link');
            break;
          case 'post':
            $scope.posts = response.data;
            $scope.postsPaging = response.headers('link');
            break;
          default:
            break;
        }
      };

    });
});
