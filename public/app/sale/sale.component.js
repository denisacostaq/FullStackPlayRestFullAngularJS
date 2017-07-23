angular.module('sale')
.component('saleList', {
  templateUrl: 'app/sale/sale-list.template.html',
  controller: function PhoneListController ($scope, SaleResource, popupService) {
    this.sales = SaleResource.query(
      function (data) {
        if (data.length !== 0) {
          popupService.popupInfo('Ok', 'Sales loaded')
        }
      },
      function () {
        popupService.popupError('Error', 'Sales load failed')
      });
  }
})
.component('saleNew', {
  templateUrl: 'app/sale/sale-add.template.html',
  controller: function ($scope, $location, SaleResource, ProductResource, popupService) {
    $scope.sale = new SaleResource();
    $scope.addSale = function () {
      $scope.sale.product = {};
      $scope.selectedProductsModel.forEach(function (item) {
        $scope.sale.product = {id: item.id};
      }, this);
      $scope.sale.$save(
        function () {
          popupService.popupSuccess('Ok', 'Sale created')
          $location.path('/sales');
        },
        function () {
          popupService.popupError('Error', 'Sale save failed')
        });
    }
    this.loadProducts = function () {
      $scope.productsSettings = { searchField: 'label', enableSearch: true, selectionLimit: 1 }
      $scope.availableProductsModel = [];
      $scope.selectedProductsModel = [];
      ProductResource.query(
        function (data) {
          data.forEach(function (product) {
            $scope.availableProductsModel.push({id: product.id, label: product.name});
          }, this);
        },
        function () {
          console.log('Error loading available products');
        });
    };
    this.loadProducts();
  }
})
.config(['$locationProvider', '$routeProvider',
  function config ($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!')
    $routeProvider
      .when('/sales', {
        template: '<sale-list></sale-list>'
      })
      .when('/sales_new', {
        template: '<sale-new></sale-new>'
      })
  }
]);