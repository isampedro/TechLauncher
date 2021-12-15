'use strict';
define(['frontend'], function(frontend) {

	frontend.service('postService', function(Restangular) {

    this.getPost = function(id) {
      return Restangular.one('posts',id).get();
    };

    this.getAnswers = function(id) {
      return Restangular.one('posts',id).getList('answers');
    };

    this.deletePost = function(post) {
      return Restangular.oneUrl('post',post).remove();
    };

    this.upVote = function(location) {
      return Restangular.oneUrl('vote',location + '/up_vote').post();
    };

    this.downVote = function(location) {
      return Restangular.oneUrl('vote',location + '/down_vote').post();
    };

    this.upVoteAnswer = function(location) {
      return Restangular.allUrl('vote', location + '/up_vote').post();
    };

    this.downVoteAnswer = function(location) {
      return Restangular.allUrl('vote', location + '/down_vote').post();
    };

    this.commentPost = function(id, answer) {
      var newAnswer = {
        'description': answer
      };
      return Restangular.all('posts/' + id + '/answers').post(newAnswer);
    };

    this.getPosts = function() {
      return Restangular.one('posts').get();
    };

    this.getTags = function () {
      return Restangular.one('posts/tags').get();
    }

    this.addPost = (post, location) => {
      return Restangular.one(location).customPOST(post);
    }

    this.editPost = (post, location) => {
      return Restangular.oneUrl('post',location).customPUT(post);
    }
	});
});
