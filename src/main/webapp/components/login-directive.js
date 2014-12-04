"use strict";

(function () {
	var app = angular.module('metrix');

	app.directive('login', function () {
		return {
			restrict: 'E',
			templateUrl: 'components/login-template.html',
			controller: ['$scope', 'userInfoProvider', function ($scope, userInfoProvider) {
				$scope.login = function () {
					$scope.showAuthLoader = true;
					userInfoProvider.login();
				};
			}]
		}
	});

})();