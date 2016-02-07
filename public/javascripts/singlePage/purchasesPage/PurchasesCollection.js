/**
    * Created by neikila.
    */
define(["purchaseModel", "backbone"], function(PurchaseModel, Backbone) {
    return Backbone.Collection.extend({ // Коллекция платежей
        model: PurchaseModel
    });
});