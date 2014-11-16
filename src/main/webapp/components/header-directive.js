"use strict";

(function () {
	var app = angular.module('metrix');

	app.directive('abHeader', function () {
		return {
			restrict: 'E',
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