/**
 * Created by neikila on 06.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.Model.extend({ // Модель ошибки
        defaults: {
            "strong": "Error",
            "message": ""
        }
    });
});