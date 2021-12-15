'use strict';

define([], function() {
    return {
        defaultRoutePath: '/404',
        routes: {
            '/': {
                templateUrl: 'views/home.html',
                controller: 'HomeCtrl'
            },
            '/explore': {
              templateUrl: 'views/explore.html',
              controller: 'ExploreCtrl'
            },
            '/404': {
              templateUrl: 'views/404.html',
              controller: 'ErrorCtrl'
            },
            '/500': {
              templateUrl: 'views/500.html',
              controller: 'ErrorCtrl'
            },
            '/login': {
                templateUrl: 'views/session/login.html',
                controller: 'LoginCtrl'
            },
            '/register/confirm/:token' : {
              templateUrl: 'views/session/register_succesful.html',
              controller: 'RegisterConfirmCtrl'
            },
            '/users/:id/change_password': {
              templateUrl: 'views/session/change_password.html',
              controller: 'ChangePasswordCtrl'
            },
            '/register': {
              templateUrl: 'views/session/register.html',
              controller: 'RegisterCtrl'
            },
            '/register/forgot_password': {
              templateUrl: 'views/session/recover_password.html',
              controller: 'RecoverPasswordCtrl'
            },
            '/register/success/:success_type' : {
              templateUrl: 'views/session/register_succesful.html',
              controller: 'SuccessCtrl'
            },
            '/posts/add': {
              templateUrl: "views/posts/add_post.html",
              controller: 'PostsCtrl'
            },
            '/posts/:id/edit': {
              templateUrl: 'views/posts/edit_post.html',
              controller: 'PostCtrl'
            },
            '/posts/:id': {
                templateUrl: 'views/posts/post.html',
                controller: 'PostCtrl'
            },
            '/techs': {
              templateUrl: 'views/techs/techs.html',
              controller: 'TechsCtrl'
            },
            '/techs/category/:category': {
              templateUrl: 'views/techs/techs_cats.html',
              controller: 'TechsCatCtrl'
            },
            '/techs/add_tech': {
              templateUrl: 'views/techs/add_tech.html',
              controller: 'EditTechCtrl'
            },
            '/techs/:id/edit_tech': {
              templateUrl: 'views/techs/edit_tech.html',
              controller: 'EditTechCtrl'
            },
            '/techs/:id': {
              templateUrl: 'views/techs/tech.html',
              controller: 'TechCtrl'
            },
            '/users/:id': {
              templateUrl: 'views/session/userProfile.html',
              controller: 'UserCtrl'
            },
            '/posts': {
              templateUrl: 'views/posts/posts.html',
              controller: 'PostsCtrl'
            },
            '/mod': {
              templateUrl: 'views/session/mod_page.html',
              controller: 'ModCtrl'
            }
            /* ===== yeoman hook ===== */
            /* Do not remove these commented lines! Needed for auto-generation */
        }
    };
});
