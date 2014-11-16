"use strict";

(function () {
	var app = angular.module('metrix');

	app.controller('mainController', ['$location', 'userInfoProvider', '$scope', '$modal', function ($location, userInfoProvider, $scope, $modal) {

		$scope.$on("INTERNAL_SERVER_ERROR", function (event, traceId) {
			$modal.open({
				templateUrl: 'components/error-template.html',
				controller: ['$scope', '$modalInstance', 'traceId', function ($scope, $modalInstance, traceId) {
					$scope.traceId = traceId;
					$scope.ok = function () {
						$modalInstance.close();
					};
				}],
				resolve: {
					traceId: function () {
						return traceId;
					}
				}
			});
		});
	}]);
})();