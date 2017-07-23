angular.module('product')
.component('productList', {
  templateUrl: 'app/product/product-list.template.html',
  controller: function PhoneListController (ProductResource, popupService) {
    this.products = ProductResource.query(
      function (data) {
        if (data.length !== 0) {
          popupService.popupInfo('Ok', 'Products loaded')
        }
      },
      function () {
        popupService.popupError('Error', 'Products load failed')
      });
  }
})
.component('productNew', {
  templateUrl: 'app/product/product-add.template.html',
  controller: function ($scope, $location, ProductResource, ItemResource, popupService) {
    $scope.product = new ProductResource();
    $scope.addProduct = function () {
      $scope.product.items = [];
      $scope.selectedItemsModel.forEach(function (item) {
        $scope.product.items.push({id: item.id});
      }, this);
      $scope.product.$save(
        function () {
          popupService.popupSuccess('Ok', 'Product created')
          $location.path('/products');
        },
        function () {
          popupService.popupError('Error', 'Product save failed')
        });
    }
    this.loadItems = function () {
      $scope.itemsSettings = { searchField: 'label', enableSearch: true }
      $scope.availableItemsModel = [];
      $scope.selectedItemsModel = [];
      ItemResource.query(
        function (data) {
          data.forEach(function (item) {
            $scope.availableItemsModel.push({id: item.id, label: item.name});
          }, this);
        },
        function () {
          console.log('Error loading available items');
        });
    };
    this.loadItems(); 
  }
})
.component('productEdit', {
  templateUrl: 'app/product/product-edit.template.html',
  controller: function ($scope, $location, $routeParams, $http, popupService, ProductResource, ItemResource) {
    $scope.updateProduct = function () {
      $scope.product.items = [];
      $scope.selectedItemsModel.forEach(function (item) {
        $scope.product.items.push({id: item.id});
      }, this);
      $scope.product.$update( 
        function () {
          popupService.popupSuccess('Ok', 'Product updated')
          $location.path('/products')
        },
        function () {
          popupService.popupError('Error', 'Product update failed')
        });
    };
    $scope.deleteProduct = function () {
      if (popupService.showConfirmPopup('Really delete this?')) {
        ProductResource.delete(
          {id: $routeParams.id}, 
          function () {
            popupService.popupSuccess('Ok', 'Product deleted')
            $location.path('/products')
          },
          function () {
            popupService.popupError('Error', 'Product delete failed')
          })
        $location.path('/products')
      }
    }
    this.loadProduct = function () {
      $scope.itemsSettings = { searchField: 'label', enableSearch: true }
      $scope.product = ProductResource.get(
        { id: $routeParams.id },
        function () {
          popupService.popupInfo('Ok', 'Product loaded')
        },
        function () {
          popupService.popupError('Error', 'Product load failed')
        });
      $scope.availableItemsModel = [];
      $scope.selectedItemsModel = [];
      ItemResource.query(
        function (data) {
          data.forEach(function (item) {
            $scope.availableItemsModel.push({id: item.id, label: item.name});
          }, this);
          $http.get('/api/v1.0/items/product/' + $routeParams.id)
            .success(function (data) {
              data.forEach(function (item) {
                function byId (element) {
                  return element.id === item.id
                }
                $scope.selectedItemsModel.push($scope.availableItemsModel.find(byId))
              }, this);
              if (data.length !== 0) {
                popupService.popupInfo('Ok', 'Item per product loaded')
              }
            })
            .error(function () {
              popupService.popupWarning('Warning', 'Item per product load failed')
            });
      },
      function () {
        popupService.popupError('Error', 'Items load failed')
      })
    };
    this.loadProduct(); 
  }
})
.config(['$locationProvider', '$routeProvider',
  function config ($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!')
    $routeProvider
      .when('/products', {
        template: '<product-list></product-list>'
      })
      .when('/products/:id', {
        template: '<product-edit></product-edit>'
      })
      .when('/products_new', {
        template: '<product-new></product-new>'
      })
  }
]);