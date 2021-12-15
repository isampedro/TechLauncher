'use strict';
define(['frontend'], function(frontend) {

	frontend.service('exploreService', function(Restangular) {


    this.getTechs = function() {
      return Restangular.one('explore/techs').get();
    };

    this.getPosts = function() {
      return Restangular.one('explore/posts?is_post=true').get();
    };

    this.search = function(tab, toSearch, categories, types, starsLeft, starsRight,nameFlag, commentAmount, lastComment, lastUpdate, groupBy, orderBy) {

      var toSearchQ = (toSearch === undefined ? '' : toSearch);
      var starsLeftQ = starsLeft === undefined ? '0' : starsLeft;
      var starsRightQ = starsRight === undefined ? '5' : starsRight;
      var commentAmountQ = commentAmount === undefined ? '' : commentAmount;
      var lastCommentQ = lastComment === undefined ? '' : lastComment;
      var lastUpdateQ = lastUpdate === undefined ? '' : lastUpdate;
      var order;
      var categoriesQ = '';
      var typesQ = '';

      angular.forEach(categories, function(category) {
        if (category.selected) {
          categoriesQ = categoriesQ.concat('&categories=' + category.category);
        }
      });

      angular.forEach(types, function(type) {
        if (type.selected) {
          typesQ = typesQ.concat('&types=' + type.type);
        }
      });

      if (groupBy !== undefined && orderBy === undefined) {
        order = groupBy;
      } else if (groupBy !== undefined && orderBy !== undefined) {
        order = groupBy * orderBy;
      } else {
        order = '0';
      }


      var url = 'explore/techs?to_search=' + toSearchQ + categoriesQ + typesQ + '&stars_left=' + starsLeftQ + '&stars_right=' + starsRightQ + '&name_flag=' + nameFlag + '&order=' + order + '&comment_amount=' + commentAmountQ + '&last_comment=' + lastCommentQ + '&last_update=' + lastUpdateQ;

      if (tab === 'P') {
        var postUrl = 'explore/posts?to_search=' + toSearchQ + categoriesQ + typesQ + '&order=' + order + '&comment_amount=' + commentAmountQ + '&last_comment=' + lastCommentQ + '&last_update=' + lastUpdateQ + '&is_post=true';
        return Restangular.one(postUrl).get();
      }

      return Restangular.one(url).get();
    };




	});
});
