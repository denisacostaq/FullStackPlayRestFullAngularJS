angular.module('companyApp', [
  'ngAnimate',
  'item',
  'product',
  'sale',
  'toaster'
])
.component('appIndex', {
  template: '<p>Welcome</p>',
  controller: function PhoneListController (popupService) {
    popupService.popupInfo('Ok', 'Welcome')
  }
})
.service('popupService', function ($window, toaster) {
  this.popupError = function (title, message) {
    toaster.pop('error', title, message);
  }
  this.popupWarning = function (title, message) {
    toaster.pop('warning', title, message);
  }
  this.popupInfo = function (title, message) {
    toaster.pop('info', title, message);
  }
  this.popupSuccess = function (title, message) {
    toaster.pop('success', title, message);
  }
  this.showConfirmPopup = function (message) {
    return $window.confirm(message);
  }
})
.config(['$locationProvider', '$routeProvider',
  function config ($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!')
    $routeProvider
      .when('/app', {
        template: '<app-index></app-index>'
      })
      .otherwise('/app')
  }
]);