angular.module('item')
.component('itemList', {
  templateUrl: 'app/item/item-list.template.html',
  controller: function PhoneListController ($scope, ItemResource, popupService) {
    this.items = ItemResource.query(
      function (data) {
        if (data.length !== 0) {
          popupService.popupInfo('Ok', 'Items loaded')
        }
      },
      function () {
        popupService.popupError('Error', 'Items load failed')
      }
    );
  }
})
.component('itemNew', {
  templateUrl: 'app/item/item-add.template.html',
  controller: function ($scope, $location, ItemResource, popupService) {
    $scope.item = new ItemResource();
    $scope.addItem = function () {
      $scope.item.$save(
        function () {
          popupService.popupSuccess('Ok', 'Item created')
          $location.path('/items');
        },
        function () {
          popupService.popupError('Error', 'Items create failed')
        });
    }
  }
})
.component('itemEdit', {
  templateUrl: 'app/item/item-edit.template.html',
  controller: function ($routeParams, $scope, $location, ItemResource, popupService) {
    $scope.updateItem = function () {
      $scope.item.$update( 
        function () {
          popupService.popupSuccess('Ok', 'Item updated')
          $location.path('/items')
        },
        function () {
          popupService.popupError('Error', 'Item update failed')
        });
    };
    $scope.deleteItem = function () {
      if (popupService.showConfirmPopup('Really delete this?')) {
        ItemResource.delete(
          {id: $routeParams.id}, 
          function () {
            popupService.popupSuccess('Ok', 'Item deleted')
            $location.path('/items')
          },
          function () {
            popupService.popupError('Error', 'Item delete failed')
          });        
      }
    }
    $scope.loadItem = function () {
      $scope.item = ItemResource.get(
        { id: $routeParams.id }, 
        function () {
          popupService.popupInfo('Ok', 'Item loaded')
        },
        function () {
          popupService.popupError('Error', 'Item load failed')
        });
    };
    $scope.loadItem(); 
  }
})
.config(['$locationProvider', '$routeProvider',
  function config ($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!')
    $routeProvider
      .when('/items', {
        template: '<item-list></item-list>'
      })
      .when('/items/:id', {
        template: '<item-edit></item-edit>'
      })
      .when('/items_new', {
        template: '<item-new></item-new>'
      })
  }
]);