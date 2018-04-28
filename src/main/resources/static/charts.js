$.getJSON(
  'http://asw-i3a-zuul-eu-west-1.guill.io/sensor_data_mining_service/sensor/5ad12d5d71b7a726c78124c5',
  function(data) {
    var units = ['Celsius', '%', 'Wats']
    var compUnits = ''
    if (data['metric'].toUpperCase() == 'TEMPERATURE') {
      compUnits = units[0]
    } else if (data['metric'].toUpperCase() == 'HUMIDITY') {
      compUnits = units[1]
    } else if (data['metric'].toUpperCase() == 'HUMIDITY') {
      compUnits = units[2]
    } else {
      compUnits = ''
    }

    Highcharts.chart('container', {
      chart: {
        zoomType: 'x'
      },
      title: {
        text: data['metric'].charAt(0).toUpperCase() + data['metric'].slice(1) + ' evolution'
      },
      subtitle: {
        text: document.ontouchstart === undefined ?
          'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
      },
      xAxis: {
        type: 'datetime'
      },
      yAxis: {
        title: {
          text: data['metric']
        },
        plotLines: [{
          value: data['data'][1][1],
          color: 'red',
          width: 2

        }]
      },
      legend: {
        enabled: false
      },
      plotOptions: {
        area: {
          fillColor: {
            linearGradient: {
              x1: 0,
              y1: 0,
              x2: 0,
              y2: 1
            },
            stops: [
              [0, Highcharts.getOptions().colors[0]],
              [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
            ]
          },
          marker: {
            radius: 2
          },
          lineWidth: 1,
          states: {
            hover: {
              lineWidth: 1
            }
          },
          threshold: null
        }
      },

      series: [{
        type: 'area',
        name: compUnits,
        data: data['data']
      }]
    });
  }
);
