/**
 * Created by neikila.
 */
define(["backbone", "utils", "chart"], function(Backbone, Utils, Chart) {
    return Backbone.View.extend({

        pieResize: function() {
            var pieholder = $(this.el);
            var pieLength = pieholder.width();
            if (pieLength > 400) {
                pieLength = 400
            }
            pieholder.empty().append("<canvas id='graph'></canvas>");
            this.pieChart = null;
            var pie = this.$("canvas");
            pie.attr("width", pieLength);
            pie.attr("height", pieLength);
        },

        initialize: function () {
            this.time = 100;
            var self = this;
            $(window).on("resize", function() {
                self.render();
            });
            this.pieChart = null;
            this.listenTo(this.model, "reset", this.render);
            this.listenTo(this.model, "add", this.render);
            this.listenTo(this.model, "remove", this.render);
            this.listenTo(this.model, "change", this.render);
        },

        drawPieChart: function(num) {
            // TODO refactor.
            var temp = document.getElementById('graph');
            if (temp != null) {
                var graph = temp.getContext('2d');
                var pieOptions = {
                    animationSteps: num,
                    animationEasing: 'easeInOutQuart'
                };
                if (this.pieChart != null) {
                    this.pieChart.destroy();
                }
                this.pieChart = new Chart(graph).Pie(this.model.toJSON(), pieOptions);
            }
        },

        render: function() {
            if (this.model.length > 0) {
                this.pieResize();
                this.drawPieChart(this.time);
                this.time = 0;
            }
        }
    })
});