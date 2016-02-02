/**
 * Created by neikila on 03.02.16.
 */
define(["userNameModel", "backbone"], function(UserNameModel, Backbone) {
    return Backbone.Collection.extend({ // Коллекция пользователей
        model: UserNameModel,

        checkUser: function (username) { // Проверка пользователя
            var findResult = this.find(function (user) { return user.get("Name") == username });
            return findResult != null;
        }

    });
});