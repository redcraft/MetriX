"use strict";

(function () {
	var app = angular.module('metrix');

	app.filter('bytes', function () {
		return function (bytes, precision) {
			if (isNaN(parseFloat(bytes)) || !isFinite(bytes) || bytes < 0.0001) return '-';
			if (typeof precision === 'undefined') precision = 1;
			var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PB'],
				number = Math.floor(Math.log(bytes) / Math.log(1024));
			return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) + ' ' + units[number];
		}
	});

	app.directive('dropbox', function () {

		return {
			restrict: 'E',
			scope: {},
			templateUrl: 'components/dropbox-template.html',
			controller: ['$scope', '$location', 'userInfoProvider', '$interval', function ($scope, $location, userInfoProvider, $interval) {

				var folderChartOptions = {
					responsive: true,
					segmentShowStroke: true,
					segmentStrokeColor: '#fff',
					segmentStrokeWidth: 2,
					percentageInnerCutout: 0,
					animationSteps: 100,
					animationEasing: 'easeOutQuart',
					animateRotate: false,
					animateScale: false,
					showTooltips: false

				};
				var globalProgressChartOptions = angular.copy(folderChartOptions);
				globalProgressChartOptions.percentageInnerCutout = 50;

				$scope.folderChartOptions = folderChartOptions;
				$scope.globalProgressChartOptions = globalProgressChartOptions;

				var getFolderInfo = function (dropbox) {
					$scope.dropbox = dropbox;
					var chartData = [];
					var pathSections = [{name: "Dropbox", path: "/"}];
					var totalSize = 0;
					var fileSize = 0;

					angular.forEach(dropbox.node.children, function (value, key) {
						if (value.type === 'DIR') {
							this.push({
								value: value.size,
								color: value.color,
								highlight: '#FF5A5E',
								label: value.path
							});
						}
						else {
							fileSize += value.size;
						}
						totalSize += value.size;
					}, chartData);
					chartData.push({
						value: fileSize,
						color: '#F7464A',
						highlight: '#FF5A5E',
						label: 'Files'
					});

					var activePath = "";
					var activeLength = 7;
					angular.forEach(dropbox.node.path.split("/"), function(value, key) {
						if(value != "") {
							activePath += ("/" + value);
							this.push({name: value, path: activePath});
							activeLength += (value.length + 3);
						}
					}, pathSections);

					if(angular.isDefined($scope.userInfo)) {
						$scope.globalProgressData = [
							{
								value: $scope.userInfo.quota.normal - $scope.dropbox.total,
								color:'#f3f9fe'
							},
							{
								value: $scope.dropbox.total,
								color: '#73bffc'
							}
						];
					}

					$scope.folderData = chartData;
					$scope.totalSize = totalSize;
					$scope.pathInfo = {sections: pathSections, length: activeLength};

				};

				var stop = undefined;
				var unwatch = undefined;
				userInfoProvider.getAsyncUserInfo().then(function (data) {
					$scope.userInfo = data;
					stop = $interval(function () {
						userInfoProvider.getDropboxStats($location.search().path).then(getFolderInfo)
					}, 3000);
					unwatch = $scope.$watch(function () {
						return $location.search()
					}, function () {
						userInfoProvider.getDropboxStats($location.search().path).then(getFolderInfo);
					}, true);
				});

				$scope.setPath = function (path) {
					$location.search("path", path);
					userInfoProvider.getDropboxStats($location.search().path).then(getFolderInfo);
				}

				$scope.signOut = function () {
					userInfoProvider.logout();
				}

				$scope.$on('$destroy', function () {
					if (angular.isDefined(stop)) {
						$interval.cancel(stop);
						stop = undefined;
					}
					if (angular.isDefined(unwatch)) {
						unwatch();
						unwatch = undefined;
					}
				});

			}]
		}
	});
})();