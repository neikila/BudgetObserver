/**
    * Created by neikila.
    */
define(["backbone"], function (Backbone) {
    return Backbone.Model.extend({
        defaults: {
            login: "",
            productName: "",
            amount: 0
        }
    });
});