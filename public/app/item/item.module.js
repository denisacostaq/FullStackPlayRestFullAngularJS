angular.module('item', ['ngRoute', 'ngResource', 'constants'])
.factory('ItemResource', function ($resource, apiBaseUrl) {
  return $resource(apiBaseUrl + '/item/:id', {id: '@id'}, {
    update: {
      method: 'PUT'
    }
  })
});
