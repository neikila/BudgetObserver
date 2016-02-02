/**
 * Created by neikila on 03.02.16.
 */
define(["backbone"], function (Backbone) {
    return Backbone.Model.extend({
        defaults: {
            isAuth: false,
            user: "asd",
            email: "test",
            name: "testName",
            defaultGroup: "first",
            groups: ["first", "second"]
        }
    });
});