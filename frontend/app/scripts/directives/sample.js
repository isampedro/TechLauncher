'use strict';
define(['angular'], function(angular) {

	angular.module('unicorn').directive('sample', function() {
		return {
			restrict: 'E',
			template: '<a>This is Sample</a>'
		};
	});
});
