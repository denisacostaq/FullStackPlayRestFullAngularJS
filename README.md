Get it working:
===============

Environment set up
------------------

- [Installing Play](https://playframework.com/documentation/2.2.x/Installing#Installing-Play)
- [Install NodeJs](https://nodejs.org/es/download/)

Build and run
-------------

```{.bash}
git clone https://github.com/denisacostaq/FullStackPlayRestFullAngularJS.git
cd FullStackPlayRestFullAngularJS/public
npm install 
cd .. 
play run
```

Know bugs:
==========

Frontend
--------

- List continue showing a recently removed element (async operations) after the redirection.
- On [`this.loadProduct`](https://github.com/denisacostaq/FullStackPlayRestFullAngularJS/blob/master/public/app/product/product.component.js#L82) it should get a single result from the async calls to [`ProductResource.get`](https://github.com/denisacostaq/FullStackPlayRestFullAngularJS/blob/master/public/app/product/product.component.js#L84) and [`ItemResource.query`](https://github.com/denisacostaq/FullStackPlayRestFullAngularJS/blob/master/public/app/product/product.component.js#L94)
