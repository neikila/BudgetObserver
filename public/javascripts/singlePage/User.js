/**
 * Created by neikila on 03.02.16.
 */
define(["backbone"], function (Backbone) {
    return Backbone.Model.extend({
        defaults: {
            isAuth: false,
            login: "",
            email: "",
            name: "",
            surname: "",
            defaultGroup: "",
            otherGroups: []
        },

        getFromServer: function(appState) {
            var self = this;
            $.ajax({
                type: "GET",
                url: "/userData",
                success: function(json){
                    console.log(json);
                    if (json.isAuth) {
                        console.log(self);
                        self.set("login", json.user.login);
                        self.set("email",  json.user.email);
                        self.set("name", json.user.name);
                        self.set("surname", json.user.surname);
                        self.set("defaultGroup", json.defaultGroup);
                        self.set("otherGroups", json.otherGroups);
                        self.set("isAuth", true);
                        appState.set("groupToShow", self.get("defaultGroup"))
                    }
                }
            });
        }
    });
});