angular.module('sale', ['ngRoute', 'ngResource', 'constants', 'angularjs-dropdown-multiselect'])
.factory('SaleResource', function ($resource, apiBaseUrl) {
  return $resource(apiBaseUrl + '/sale')
});
