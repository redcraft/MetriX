"use strict";

(function () {
	var app = angular.module('metrix');

	app.factory('authInterceptor', ['$q', '$location', '$injector', '$rootScope', function ($q, $location, $injector, $rootScope) {
		console.log("Interceptor injected");
		return {
			response: function (response) {
				if (response.status === 401) {
					console.log("Response 401");
				}
				return response || $q.when(response);
			},
			responseError: function (rejection) {
				console.log("ERROR caught by interceptor", rejection);
				if (rejection.status === 401) {
					var userInfoProvider = $injector.get('userInfoProvider');
					userInfoProvider.invalidateUserInfo();
					$location.path('/login');
				}
				else if (rejection.status === 500) {
					$rootScope.$broadcast("INTERNAL_SERVER_ERROR", rejection.data.traceId);
				}
				return $q.reject(rejection);
			}
		}
	}]);
})();