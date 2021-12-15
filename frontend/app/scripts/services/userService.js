'use strict';
define(['frontend'], function(frontend) {

  frontend.service('userService', function(Restangular) {
    this.getUser = function(id) {
      return Restangular.one('users',id).get();
    };
    this.getPicture = function(id) {
      return Restangular.one('users/' + id + '/image').get();
    };
    this.getData = function(url) {
      return Restangular.oneUrl('data',url).get();
    };
    this.setMod = function(url,set) {
      return Restangular.oneUrl('data',url + set).put();
    };
    this.update = function(file,description,id) {
      var fd = new FormData();

      if (file !== undefined) {
        fd.append('picture', file);
      }

      if (description !== undefined) {
        var body = {
          'description': description
        }

        var bodyBlob = new Blob(
          [angular.toJson(body)],
          {type: 'application/json'}
        );

        fd.append('body', bodyBlob);
      }

      return Restangular.one('users/' + id).withHttpConfig({transformRequest: angular.identity})
        .customPUT(fd, '', undefined, {'Content-Type': undefined});
    };

    this.getCurrentMods = () => {
      return Restangular.one('mod/moderators').get();
    };

    this.getCurrentApplicants = () => {
      return Restangular.one('mod/applicants').get();
    };

    this.getVerified = () => {
      return Restangular.one('mod/verified').get();
    };

    this.getReportedComments = () => {
      return Restangular.one('mod/reported_comments').get();
    };

    this.getReportedContents = () => {
      return Restangular.one('mod/reported_contents').get();
    };

    this.deleteMod = (mod) => {
      return Restangular.oneUrl('mod',mod).remove();
    };

    this.acceptMod = (mod) => {
      return Restangular.oneUrl('mod',mod).post()
    };

    this.rejectMod = (mod) => {
      return Restangular.oneUrl('mod',mod).remove();
    };

    this.applyForTech = (tech) => {
      return Restangular.oneUrl('tech',tech).post();
    };

    this.quitTech = (tech) => {
      return Restangular.oneUrl('tech',tech).remove();
    };

    this.deleteCommentReport = (reportedComment) => {
      return Restangular.oneUrl('report',reportedComment ).remove();
    };

    this.acceptCommentReport = (reportedComment) => {
      return Restangular.oneUrl('report',reportedComment).post();
    }

    this.deleteContentReport = (reportedContent) => {
      return Restangular.oneUrl('report',reportedContent).remove();
    }

    this.acceptContentReport = (reportedContent) => {
      return Restangular.oneUrl('report',reportedContent).post();
    }
  });
});
