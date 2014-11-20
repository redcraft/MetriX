"use strict";

(function () {
	var app = angular.module('metrix');

	app.directive('metrixHeader', function () {
		return {
			restrict: 'C',
			templateUrl: 'components/header-template.html',
			controller: ['$scope', 'userInfoProvider',
				function ($scope, userInfoProvider) {
					$scope.signOut = function () {
						userInfoProvider.logout();
					}
				}]
		}
	});
})();