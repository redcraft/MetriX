'use strict';

var app = angular.module(
    'metrix',
    [
        'ui.bootstrap', 'tc.chartjs', 'ngRoute',
        'userInfo'
    ]
);

app.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
    $routeProvider.
        when('/', {template: '<dropbox></dropbox>', reloadOnSearch: false}).
        when('/login', {template: '<aisling-login></aisling-login>', reloadOnSearch: false}).
        when('/error', {template: 'error', reloadOnSearch: false}).
        otherwise({ redirectTo: '/login' });
    //$httpProvider.interceptors.push('authInterceptor');
}]);

app.run(['$rootScope', '$location', 'userInfoProvider', function($rootScope, $location, userInfoProvider) {
    $rootScope.$on('$routeChangeStart', function (event, next, current) {
        if ($location.path() != '/error') {
            if ($location.path() === '/') {
                userInfoProvider.getAsyncUserInfo().then(function () {
                }, function () {
                    $location.url('/login')
                });
            } else if ($location.path() === '/login') {
                userInfoProvider.getAsyncUserInfo().then(function () {
                    $location.url('/')
                });
            }
        }
    });
}]);
