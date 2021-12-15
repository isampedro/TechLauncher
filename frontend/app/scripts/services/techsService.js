'use strict';
define(['frontend'], function(frontend) {

  frontend.service('techsService', function(Restangular) {
    this.getHomeInfo = function() {
      return Restangular.one('techs').get();
    };

    this.getByCategory = function (category) {
      return Restangular.one('techs/category/' + category).get();
    };

    this.getCategories = function() {
      return Restangular.one('techs/category').get();
    };
    this.getTypes = function() {
      return Restangular.one('techs/types').get();
    };

    this.getTech = function(id) {
      return Restangular.one('techs',id).get();
    };

    this.getPicture = function(id) {
      return Restangular.one('techs/' + id + '/image').get();
    };

    this.getData = function(url) {
      return Restangular.oneUrl('data',url).get();
    };

    this.deleteData = function(url) {
      return Restangular.oneUrl('data',url).remove();
    };

    this.addContent = function (id, title, type, link) {
      var content = {
        'id': id,
        'title': title,
        'type': type,
        'link': link
      };
      return Restangular.all('techs/' + id + '/content').post(content);
    };

    this.addComment = function (id, input) {
      var comment = {
        'description': input
      };
      return Restangular.all('techs/' + id + '/comment').post(comment);
    };

    this.addReply = function (location, input) {
      var reply = {
        'description': input
      };
      return Restangular.allUrl('comment',location).post(reply);
    };

    this.report = function (url, description) {
      var report = {
        'description': description
      };
      return Restangular.allUrl('report',url).post(report);
    };

    this.vote = function (url, type) {
      switch(type) {
        case 'up':
          return Restangular.oneUrl('comments',url+'/upvote').post();
        case 'down':
          return Restangular.oneUrl('comments',url+'/downvote').post();
        default:
          break;
      }
    }

    this.rate = function (id, stars) {
      var vote = {
        'value': stars
      }
      return Restangular.all('techs/'+id+'/stars').post(vote);
    }

    this.addTech = function(add) {
      return Restangular.all('techs').post(add);
    };

    this.addTech = function(add) {
      var fd = new FormData();

      var body = {
        'name': add.name,
        'category': add.category,
        'type': add.type,
        'description': add.description,
        'introduction': add.introduction,
      }

      var bodyBlob = new Blob(
        [angular.toJson(body)],
        {type: 'application/json'}
      );

      fd.append('body', bodyBlob);
      fd.append('picture', add.picture);

      return Restangular.one('techs/').withHttpConfig({transformRequest: angular.identity})
        .customPOST(fd, '', undefined, {'Content-Type': undefined});
    };

    this.editTech = function(id, edit) {
      var fd = new FormData();

      var body = {
        'id': id,
        'name': edit.name,
        'category': edit.category,
        'type': edit.type,
        'description': edit.description,
        'introduction': edit.introduction,
      }

      var bodyBlob = new Blob(
        [angular.toJson(body)],
        {type: 'application/json'}
      );

      fd.append('body', bodyBlob);

      if (edit.picture !== undefined) {
        fd.append('picture', edit.picture);
      }

      return Restangular.one('techs/'+id).withHttpConfig({transformRequest: angular.identity})
        .customPUT(fd, '', undefined, {'Content-Type': undefined});
    };

    this.applyForMod = function (location) {
      return Restangular.allUrl('mod',location).post();
    };

    this.stopBeingAMod = function (location) {
      return Restangular.oneUrl('mod',location).remove();
    };

  });
});
