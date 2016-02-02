/**
 * Created by neikila on 03.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.Model.extend({ // Модель пользователя
        defaults: {
            "Name": ""
        }
    });
});