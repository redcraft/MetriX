"use strict";


(function () {
	var app = angular.module('userInfo', []);

	app.factory('userInfoProvider', ['$http', '$q', '$window', function ($http, $q, $window) {
		var userInfo = undefined;

		var getAsyncUserInfo = function (force) {
			var deferred = $q.defer();
			deferred.notify("Getting user info");
			if (userInfo === undefined || !!force) {
				$http({method: 'GET', url: '/api/user'}).
					success(function (data) {
						userInfo = data;
						deferred.resolve(userInfo);
					}).
					error(function (data, status) {
						console.log("ERROR: ", status, data);
						deferred.reject(data);
					});
			}
			else {
				deferred.resolve(userInfo);
			}
			return deferred.promise;
		};

		var invalidateUserInfo = function () {
			userInfo = undefined;
		};

		var login = function () {
			$window.location.href = '/api/auth';
		};

		var logout = function () {
			$window.location.href = '/api/auth/logout';

		};

		var getDropboxStats = function (path) {
			var path = angular.isDefined(path) ? path : "/";
			var deferred = $q.defer();
			deferred.notify("Getting dropbox");
			$http({method: 'GET', url: '/api/user/stats?path=' + path}).
				success(function (data) {
					deferred.resolve(data);
				}).
				error(function (data, status) {
					console.log("ERROR: ", status, data);
					deferred.reject(data);
				});
			return deferred.promise;
		};


		return {
			invalidateUserInfo: invalidateUserInfo,
			getAsyncUserInfo: getAsyncUserInfo,
			login: login,
			logout: logout,
			getDropboxStats: getDropboxStats
		}
	}]);

})();
