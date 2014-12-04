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

				$scope.chartData = [];
				$scope.chartOptions = {
					responsive: true,
					segmentShowStroke: true,
					segmentStrokeColor: '#fff',
					segmentStrokeWidth: 2,
					percentageInnerCutout: 0, // This is 0 for Pie charts
					animationSteps: 100,
					animationEasing: 'easeOutQuart',
					animateRotate: false,
					animateScale: false

				};

				$scope.chartOptions2 = {
					responsive: true,
					segmentShowStroke : true,
					segmentStrokeColor : '#fff',
					segmentStrokeWidth : 2,
					percentageInnerCutout : 50, // This is 0 for Pie charts
					animationSteps : 100,
					animationEasing : 'easeOutQuart',
					animateRotate : false,
					animateScale : false

				};

				$scope.chartData2 = [

				];

				var getFolderInfo = function (dropbox) {
					$scope.dropbox = dropbox;
					var chartData = [];
					var pathSections = [{name: "Dropbox Root", path: "/"}];
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
					angular.forEach(dropbox.node.path.split("/"), function(value, key) {
						if(value != "") {
							activePath += ("/" + value);
							this.push({name: value, path: activePath});
						}
					}, pathSections);

					$scope.chartData = chartData;
					$scope.totalSize = totalSize;
					$scope.pathSections = pathSections;

					$scope.chartData2 = [
						{
							value: $scope.userInfo.quota.normal - $scope.dropbox.total,
							color:'#f3f9fe',
							highlight: '#FF5A5E',
							label: 'Red'
						},
						{
							value: $scope.dropbox.total,
							color: '#73bffc',
							highlight: '#5AD3D1',
							label: 'Green'
						}
					];

				};

				userInfoProvider.getAsyncUserInfo().then(function (data) {
					$scope.userInfo = data;
				});

				$scope.setPath = function (path) {
					$location.search("path", path);
					userInfoProvider.getDropboxStats($location.search().path).then(getFolderInfo);
				}

				$scope.resetStats = function () {
					userInfoProvider.resetStats();
				}

				$scope.signOut = function () {
					userInfoProvider.logout();
				}

				var stop = $interval(function () {
					userInfoProvider.getDropboxStats($location.search().path).then(getFolderInfo)
				}, 3000);

				var unwatch = $scope.$watch(function () {
					return $location.search()
				}, function () {
					userInfoProvider.getDropboxStats($location.search().path).then(getFolderInfo);
				}, true);

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