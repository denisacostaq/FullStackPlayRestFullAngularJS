angular.module('product', ['ngRoute', 'ngResource', 'constants', 'angularjs-dropdown-multiselect'])
.factory('ProductResource', function ($resource, apiBaseUrl) {
  return $resource(apiBaseUrl + '/product/:id', {id: '@id'}, {
    update: {
      method: 'PUT'
    }
  })
});
