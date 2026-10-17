var app = angular.module('sentinelDashboardApp');

app.controller('MetricCtl', ['$scope', '$stateParams', 'MetricService', '$interval', '$timeout',
  function ($scope, $stateParams, MetricService, $interval, $timeout) {
	$scope.charts = [];
    // 默认查询最近 5 分钟
    $scope.endTime = new Date();
    $scope.startTime = new Date($scope.endTime.getTime() - 1000 * 60 * 5);

    $scope.app = $stateParams.app;
    // 数据自动刷新频率
    var DATA_REFRESH_INTERVAL = 1000 * 10;
    // 结束时间距现在不超过该值时视为实时查询, 实时查询自动刷新, 历史区间查询需手动点击查询
    var REALTIME_WINDOW_MS = 1000 * 60;

    // 快捷时间范围(分钟), activeQuickMinutes 记录当前选中的快捷范围, 自定义时间时为 null
    $scope.quickRanges = [
      { minutes: 5, label: '最近5分钟' },
      { minutes: 30, label: '最近30分钟' },
      { minutes: 60, label: '最近1小时' },
      { minutes: 360, label: '最近6小时' },
      { minutes: 1440, label: '最近24小时' }
    ];
    $scope.activeQuickMinutes = 5;

    $scope.servicePageConfig = {
      pageSize: 6,
      currentPageIndex: 1,
      totalPage: 1,
      totalCount: 0,
    };
    $scope.servicesChartConfigs = [];

    $scope.pageChanged = function (newPageNumber) {
      $scope.servicePageConfig.currentPageIndex = newPageNumber;
      reInitIdentityDatas();
    };

    var searchT;
    $scope.searchService = function () {
      $timeout.cancel(searchT);
      searchT = $timeout(function () {
        reInitIdentityDatas();
      }, 600);
    }

    var intervalId;
    // 聚合粒度 N(秒), 由查询接口下发; 首次响应前以 10 秒兜底
    $scope.aggregateSeconds = 10;
    // 实时刷新周期可配: 关闭/5秒/10秒/30秒/60秒, 默认 10 秒
    $scope.refreshOptions = [
      { label: '关闭', ms: 0 },
      { label: '5秒', ms: 5000 },
      { label: '10秒', ms: 10000 },
      { label: '30秒', ms: 30000 },
      { label: '60秒', ms: 60000 }
    ];
    $scope.refreshIntervalMs = DATA_REFRESH_INTERVAL;
    $scope.changeRefreshInterval = function () {
      reInitIdentityDatas();
    };
    function reInitIdentityDatas() {
      $interval.cancel(intervalId);
      queryIdentityDatas();
      if ($scope.refreshIntervalMs > 0) {
        intervalId = $interval(function () {
          // 历史区间查询数据不会变化, 跳过自动刷新
          if ($scope.isRealtimeQuery()) {
            queryIdentityDatas();
          }
        }, $scope.refreshIntervalMs);
      }
    };

    $scope.$on('$destroy', function () {
      $interval.cancel(intervalId);
    });

    // 是否为实时查询(结束时间接近当前时间)
    $scope.isRealtimeQuery = function () {
      return $scope.endTime instanceof Date && (Date.now() - $scope.endTime.getTime()) <= REALTIME_WINDOW_MS;
    };

    // 实时查询时窗口随时间向前滑动, 保持所选的时间跨度不变
    function refreshRealtimeWindow() {
      if ($scope.isRealtimeQuery() && $scope.startTime instanceof Date) {
        var widthMs = $scope.endTime.getTime() - $scope.startTime.getTime();
        if (widthMs < 0) {
          widthMs = 0;
        }
        $scope.endTime = new Date();
        $scope.startTime = new Date($scope.endTime.getTime() - widthMs);
      }
    };

    // 按所选时间区间查询
    $scope.queryByTime = function () {
      if (!$scope.startTime || !$scope.endTime) {
        alert('请选择开始时间和结束时间');
        return;
      }
      if ($scope.startTime.getTime() >= $scope.endTime.getTime()) {
        alert('开始时间必须早于结束时间');
        return;
      }
      $scope.activeQuickMinutes = null;
      $scope.servicePageConfig.currentPageIndex = 1;
      reInitIdentityDatas();
    };

    // 快捷范围查询: 最近 N 分钟
    $scope.useQuickRange = function (minutes) {
      $scope.endTime = new Date();
      $scope.startTime = new Date($scope.endTime.getTime() - minutes * 60 * 1000);
      $scope.activeQuickMinutes = minutes;
      $scope.servicePageConfig.currentPageIndex = 1;
      reInitIdentityDatas();
    };
    $scope.initAllChart = function () {
      //revoke useless charts positively
      while($scope.charts.length > 0) {
      	let chart = $scope.charts.pop();
      	chart.destroy();
      }
      // 查询跨度超过 1 天时横轴显示日期
      var showDate = $scope.showDate;
      $.each($scope.metrics, function (idx, metric) {
        if (idx == $scope.metrics.length - 1) {
          return;
        }
        const chart = new G2.Chart({
          container: 'chart' + idx,
          forceFit: true,
          width: 100,
          height: 250,
          padding: [10, 30, 70, 50]
        });
        $scope.charts.push(chart);
        var maxQps = 0;
        for (var i in metric.data) {
          var item = metric.data[i];
          if (item.passQps > maxQps) {
            maxQps = item.passQps;
          }
          if (item.blockQps > maxQps) {
            maxQps = item.blockQps;
          }
        }
        // 数据全为 0 时避免 Y 轴退化成 [0,0] 导致曲线不可见
        maxQps = Math.max(maxQps, 1);
        // 横轴右端固定为查询窗口 endTime: 实时模式下始终是当前时间, 数据滞后/断流时
        // 曲线不到右边缘, 一眼可见; 横轴左端为窗口起点, 但与窗口相交的聚合桶(桶起点
        // 经网格对齐查询进入, 可能早于窗口起点)需延伸到最早数据点, 否则点落在轴外被裁剪
        var axisStart = $scope.startTime instanceof Date ? $scope.startTime.getTime() : null;
        if (metric.data && metric.data.length && axisStart !== null && metric.data[0].timestamp < axisStart) {
          axisStart = metric.data[0].timestamp;
        }
        chart.scale('timestamp', {
          type: 'time',
          mask: 'YYYY-MM-DD HH:mm:ss',
          min: axisStart !== null ? axisStart : undefined,
          max: $scope.endTime instanceof Date ? $scope.endTime.getTime() : undefined
        });
        chart.scale('passQps', {
          min: 0,
          max: maxQps,
          fine: true,
          alias: '通过 QPS'
          // max: 10
        });
        chart.scale('blockQps', {
          min: 0,
          max: maxQps,
          fine: true,
          alias: '拒绝 QPS',
        });
        chart.scale('rt', {
          min: 0,
          fine: true,
        });
        chart.axis('rt', {
          grid: null,
          label: null
        });
        chart.axis('blockQps', {
          grid: null,
          label: null
        });

        chart.axis('timestamp', {
          label: {
            textStyle: {
              textAlign: 'center', // 文本对齐方向，可取值为： start center end
              fill: '#404040', // 文本的颜色
              fontSize: '11', // 文本大小
              //textBaseline: 'top', // 文本基准线，可取 top middle bottom，默认为middle
            },
            autoRotate: false,
            formatter: function (text, item, index) {
              return showDate ? text.substring(5, 16) : text.substring(11, 11 + 5);
            }
          }
        });
        // 序列级显隐: 整个区间内通过/拒绝 QPS 全为 0 的序列不画(线与圆点整条不显示);
        // 序列存在时其全部数据点(含自动补 0 的点)照常画圆点。
        // 图层实例上标记 y 字段, legend 开关据此匹配(不依赖 G2 内部 API, 兼容所有构建)
        var hasPass = false;
        var hasBlock = false;
        for (var k in metric.data) {
          if (metric.data[k].passQps > 0) {
            hasPass = true;
          }
          if (metric.data[k].blockQps > 0) {
            hasBlock = true;
          }
        }
        chart.legend({
          custom: true,
          useHtml: true,
          position: 'bottom',
          allowAllCanceled: true,
          itemFormatter: function (val) {
            if ('passQps' === val) {
              return '通过 QPS';
            }
            if ('blockQps' === val) {
              return '拒绝 QPS';
            }
            return val;
          },
          items: (function () {
            // 仅列出实际绘制的序列: 通过/拒绝任一系列全 0 时, 其线、圆点与图例都不显示
            var items = [];
            if (hasPass) {
              items.push({ value: 'passQps', marker: { symbol: 'hyphen', stroke: '#1d9d74', radius: 5, lineWidth: 2 } });
            }
            if (hasBlock) {
              items.push({ value: 'blockQps', marker: { symbol: 'hyphen', stroke: '#F53F3F', radius: 5, lineWidth: 2 } });
            }
            return items;
          })(),
          onClick: function (ev) {
            const item = ev.item;
            const value = item.value;
            const checked = ev.checked;
            const geoms = chart.getAllGeoms();
            for (var i = 0; i < geoms.length; i++) {
              const geom = geoms[i];
              // 匹配图层创建时挂上的字段标记(__mrtField), 点与线一起显隐
              if (geom.__mrtField === value) {
                if (checked) {
                  geom.show();
                } else {
                  geom.hide();
                }
              }
            }
          }
        });
        // 注意: 当前 G2 构建(3.4.10)没有 geom.data()/geom.filter(), point 对 null 值
        // 不跳过(画在 0 位置), view 独立数据源又会吞掉图例。最终方案: 圆点图层留在
        // chart 层(图例正常), 数据携带 flag 字段, 通过 size/opacity 字段回调把 0 值点
        // 置为半径 0 + 完全透明(alpha=0, canvas 不产生像素); line 用原始字段, 曲线连续。
        // 空档内部补 null 断线留白(仅数据点 ±N 秒位置补 0 保持 0→数据→0 脉冲形态)
        var chartData = [];
        for (var d in metric.data) {
          var item = metric.data[d];
          chartData.push({
            timestamp: item.timestamp,
            passQps: item.passQps,
            blockQps: item.blockQps,
            passFlag: item.passQps > 0 ? 1 : 0,
            blockFlag: item.blockQps > 0 ? 1 : 0
          });
        }
        chart.source(chartData);
        if (hasPass) {
          var linePass = chart.line().position('timestamp*passQps').size(2).color('#1d9d74').opacity(0.9).shape('smooth');
          linePass.__mrtField = 'passQps';
          // 圆点图层关闭 tooltip: 其 passFlag/blockFlag 映射字段(size/opacity)会被默认
          // tooltip 收集显示出来; 线图层字段干净, 由它提供"通过 QPS"提示项
          var ptPass = chart.point().position('timestamp*passQps').shape('circle').color('#1d9d74')
            .size('passFlag', function (v) { return v > 0 ? 3 : 0; })
            .opacity('passFlag', function (v) { return v > 0 ? 0.85 : 0; })
            .tooltip(false);
          ptPass.__mrtField = 'passQps';
        }
        if (hasBlock) {
          var lineBlock = chart.line().position('timestamp*blockQps').size(2).color('#F53F3F').opacity(0.9).shape('smooth');
          lineBlock.__mrtField = 'blockQps';
          var ptBlock = chart.point().position('timestamp*blockQps').shape('circle').color('#F53F3F')
            .size('blockFlag', function (v) { return v > 0 ? 3 : 0; })
            .opacity('blockFlag', function (v) { return v > 0 ? 0.85 : 0; })
            .tooltip(false);
          ptBlock.__mrtField = 'blockQps';
        }
        //chart.line().position('timestamp*rt').size(1).color('gray').shape('smooth');
        // 悬停提示固定输出 通过/拒绝 QPS 两项: G2 默认按鼠标附近图形收集 items,
        // 悬停位置贴近某条线时另一条的项可能偶发缺失, 这里按命中记录强制补全
        chart.on('tooltip:change', function (ev) {
          var tipItems = ev.items || [];
          var record = null;
          var tipTitle = null;
          for (var ti = 0; ti < tipItems.length; ti++) {
            if (!tipTitle && tipItems[ti].title) {
              tipTitle = tipItems[ti].title;
            }
            if (!record && tipItems[ti].point && tipItems[ti].point._origin) {
              record = tipItems[ti].point._origin;
            }
          }
          if (!record) {
            return;
          }
          var fixedItems = [];
          if (hasPass) {
            fixedItems.push({ name: '通过 QPS', value: record.passQps, color: '#1d9d74', marker: true, title: tipTitle });
          }
          if (hasBlock) {
            fixedItems.push({ name: '拒绝 QPS', value: record.blockQps, color: '#F53F3F', marker: true, title: tipTitle });
          }
          tipItems.splice(0, tipItems.length);
          for (var fi = 0; fi < fixedItems.length; fi++) {
            tipItems.push(fixedItems[fi]);
          }
        });
        G2.track(false);
        chart.render();
        bindChartRangeSelection(idx);
      });
    };

    // 图表框选查询: 在图表上按住从某个时间横向拖拽到另一个时间, 松开后按所选区间
    // 联动开始/结束时间并立即查询(拖拽过短视为点击, 不触发)
    function bindChartRangeSelection(idx) {
      var containerEl = document.getElementById('chart' + idx);
      if (!containerEl || containerEl.__mrtBrush) {
        return;
      }
      containerEl.__mrtBrush = true;
      containerEl.style.position = 'relative';
      containerEl.style.cursor = 'crosshair';
      var overlay = document.createElement('div');
      overlay.className = 'mrt-brush-overlay';
      overlay.style.cssText = 'position:absolute;top:0;left:0;right:0;bottom:0;display:none;pointer-events:none;z-index:5;';
      var mask = document.createElement('div');
      mask.className = 'mrt-brush-mask';
      mask.style.cssText = 'position:absolute;background:rgba(29,157,116,.15);'
        + 'border-left:1px solid #1d9d74;border-right:1px solid #1d9d74;';
      overlay.appendChild(mask);
      containerEl.appendChild(overlay);
      var startX = -1;
      var dragging = false;
      // 拖拽开始时刻的坐标比例尺(实时刷新会重建图表实例, 松开时须用按下时的比例换算)
      var axisSnapshot = null;

      function relX(e) {
        return e.clientX - containerEl.getBoundingClientRect().left;
      }

      function snapshotAxis() {
        var chart = $scope.charts[idx];
        var plotRange = chart.get('plotRange');
        var xScale = chart.getAllGeoms()[0].getXScale();
        return { left: plotRange.tl.x, right: plotRange.tr.x, min: xScale.min, max: xScale.max,
          top: plotRange.tl.y, bottom: plotRange.bl.y };
      }

      function pxToTime(px) {
        if (px < axisSnapshot.left) {
          px = axisSnapshot.left;
        }
        if (px > axisSnapshot.right) {
          px = axisSnapshot.right;
        }
        return axisSnapshot.min + (px - axisSnapshot.left)
          / (axisSnapshot.right - axisSnapshot.left) * (axisSnapshot.max - axisSnapshot.min);
      }

      function onMove(e) {
        var cur = relX(e);
        mask.style.left = Math.min(startX, cur) + 'px';
        mask.style.width = Math.abs(cur - startX) + 'px';
      }

      function onUp(e) {
        dragging = false;
        overlay.style.display = 'none';
        document.removeEventListener('mousemove', onMove);
        document.removeEventListener('mouseup', onUp);
        var cur = relX(e);
        if (Math.abs(cur - startX) < 5) {
          return;
        }
        var t1 = Math.round(pxToTime(Math.min(startX, cur)));
        var t2 = Math.round(pxToTime(Math.max(startX, cur)));
        if (t2 - t1 < 1000) {
          return;
        }
        $scope.$apply(function () {
          $scope.startTime = new Date(t1);
          $scope.endTime = new Date(t2);
          $scope.activeQuickMinutes = null;
          $scope.servicePageConfig.currentPageIndex = 1;
          reInitIdentityDatas();
        });
      }

      containerEl.addEventListener('mousedown', function (e) {
        if (e.button !== 0) {
          return;
        }
        e.preventDefault();
        try {
          axisSnapshot = snapshotAxis();
        } catch (err) {
          return;
        }
        startX = relX(e);
        dragging = true;
        // 遮罩垂直范围 = 绘图区(最高指标刻度到 Y 轴 0 刻度线), 不覆盖横轴标签区
        mask.style.top = axisSnapshot.top + 'px';
        mask.style.height = (axisSnapshot.bottom - axisSnapshot.top) + 'px';
        mask.style.left = startX + 'px';
        mask.style.width = '0px';
        overlay.style.display = 'block';
        document.addEventListener('mousemove', onMove);
        document.addEventListener('mouseup', onUp);
      });
    }

    $scope.metrics = [];
    $scope.emptyObjs = [];
    function queryIdentityDatas() {
      refreshRealtimeWindow();
      // 时间被清空时不发起查询, 可点击"最新5分钟"恢复
      if (!($scope.startTime instanceof Date) || !($scope.endTime instanceof Date)) {
        return;
      }
      var params = {
        app: $scope.app,
        pageIndex: $scope.servicePageConfig.currentPageIndex,
        pageSize: $scope.servicePageConfig.pageSize,
        desc: $scope.isDescOrder,
        searchKey: $scope.serviceQuery,
        startTime: $scope.startTime.getTime(),
        endTime: $scope.endTime.getTime()
      };
      // 查询跨度超过 1 天时, 图表横轴和表格时间列显示日期
      $scope.showDate = (params.endTime - params.startTime) > 1000 * 60 * 60 * 24;
      MetricService.queryAppSortedIdentities(params).success(function (data) {
        $scope.metrics = [];
        $scope.emptyObjs = [];
        if (data.code === 0 && data.data) {
          // 后端下发的聚合粒度 N(秒), 首尾补零点位于数据点前/后 N 秒
          if (data.data.aggregateSeconds > 0) {
            $scope.aggregateSeconds = data.data.aggregateSeconds;
          }
          var metricsObj = data.data.metric;
          var identityNames = Object.keys(metricsObj);
          if (identityNames.length < 1) {
            $scope.emptyServices = true;
          } else {
            $scope.emptyServices = false;
          }
          $scope.servicePageConfig.totalPage = data.data.totalPage;
          $scope.servicePageConfig.pageSize = data.data.pageSize;
          var totalCount = data.data.totalCount;
          $scope.servicePageConfig.totalCount = totalCount;
          for (i = 0; i < totalCount; i++) {
            $scope.emptyObjs.push({});
          }
          $.each(identityNames, function (idx, identityName) {
            var identityDatas = metricsObj[identityName];
            var metrics = {};
            metrics.resource = identityName;
            // 图表使用补零后的完整数据
            metrics.data = fillZeros(identityDatas);
            // 表格使用原始数据点, 按时间倒序(最新在前), 分页展示整个查询区间
            metrics.rowsDesc = (identityDatas || []).slice().reverse();
            metrics.tablePage = 1;
            metrics.tblTotal = Math.max(1, Math.ceil(metrics.rowsDesc.length / $scope.tblPageSize));
            $scope.metrics.push(metrics);
          });
          // push an empty element in the last, for ng-init reasons.
          $scope.metrics.push([]);
        } else {
          $scope.emptyServices = true;
          console.log(data.msg);
        }
      });
    };
    // 按数据节奏补点: 自动识别相邻数据点的最小间隔(聚合粒度为 N 秒时即 N)。
    // 首点前/末点后 N 秒、空档毗邻数据点处补 0(0→数据→0 脉冲形态);
    // 空档内部补 null 断线留白 —— 无流量时段不画贴 0 长平线
    function fillZeros(metricData) {
      if (!metricData || metricData.length == 0) {
        return [];
      }
      function zeroAt(ts) {
        return {
          "timestamp": ts,
          "passQps": 0,
          "blockQps": 0,
          "successQps": 0,
          "exception": 0,
          "rt": 0,
          "count": 0
        };
      }
      function nullAt(ts) {
        return {
          "timestamp": ts,
          "passQps": null,
          "blockQps": null,
          "successQps": null,
          "exception": null,
          "rt": null,
          "count": null
        };
      }
      var stepMs = ($scope.aggregateSeconds > 0 ? $scope.aggregateSeconds : 10) * 1000;
      if (metricData.length == 1) {
        // 单点: 若点前/后 N 秒没有其他点, 在点前/后 N 秒各补一个 0 值点,
        // 曲线从 0 升到该点再降回 0(脉冲形态); 点恰好在窗口起点或结束时间附近时不补
        var only = metricData[0];
        var anchor = ($scope.startTime instanceof Date) ? $scope.startTime.getTime() : null;
        var endAnchor = ($scope.endTime instanceof Date) ? $scope.endTime.getTime() : null;
        var head = [];
        if (anchor !== null && anchor < only.timestamp - 1000) {
          head = [zeroAt(Math.max(only.timestamp - stepMs, anchor))];
        }
        var tail = [];
        if (endAnchor !== null && endAnchor - only.timestamp > 1000) {
          tail = [zeroAt(Math.min(only.timestamp + stepMs, endAnchor))];
        }
        return head.concat([only], tail);
      }
      var cadence = Infinity;
      for (var i = 1; i < metricData.length; i++) {
        var gap = metricData[i].timestamp / 1000 - metricData[i - 1].timestamp / 1000;
        if (gap >= 1 && gap < cadence) {
          cadence = gap;
        }
      }
      if (!isFinite(cadence)) {
        cadence = 1;
      }
      var filledData = [];
      // 首点前 N 秒没有点时, 在首点前 N 秒处补 0(不早于窗口起点),
      // 曲线从 0 直线上升到首点的值; 不在窗口起点补 0, 避免大窗口下的长斜线
      var anchorTs = ($scope.startTime instanceof Date) ? $scope.startTime.getTime() : null;
      if (anchorTs !== null && metricData[0].timestamp - anchorTs > 1000) {
        var leadZeroTs = metricData[0].timestamp - stepMs;
        if (leadZeroTs < anchorTs) {
          leadZeroTs = anchorTs;
        }
        filledData.push({
          "timestamp": leadZeroTs,
          "passQps": 0,
          "blockQps": 0,
          "successQps": 0,
          "exception": 0,
          "rt": 0,
          "count": 0
        });
      }
      filledData.push(metricData[0]);
      var lastTime = metricData[0].timestamp / 1000;
      for (var i = 1; i < metricData.length; i++) {
        var curTime = metricData[i].timestamp / 1000;
        // 点间空档按 N 秒步长补点(不设上限): 毗邻数据点的位置(前一点后 N 秒、
        // 后一点前 N 秒)补 0 完成脉冲形态; 空档内部(离两边数据点都超过 N 秒)补
        // null —— line 断开留白, 不画贴 0 长平线, 也不产生圆点
        if (curTime > lastTime + cadence) {
          var stepSec = stepMs / 1000;
          for (var j = lastTime + stepSec; j < curTime; j += stepSec) {
            var nearPrev = (j - lastTime) <= stepSec + 0.5;
            var nearCur = (curTime - j) <= stepSec + 0.5;
            filledData.push(nearPrev || nearCur ? zeroAt(j * 1000) : nullAt(j * 1000));
          }
        }
        filledData.push(metricData[i]);
        lastTime = curTime;
      }
      // 尾部: 最后数据点之后 N 秒没有点时, 在其后 N 秒处补 0(不晚于窗口结束), 曲线收尾降回 0;
      // 尾点恰好在窗口结束时间附近时无需补
      var endAnchorTs = ($scope.endTime instanceof Date) ? $scope.endTime.getTime() : null;
      var lastPt = metricData[metricData.length - 1];
      if (endAnchorTs !== null && endAnchorTs - lastPt.timestamp > 1000) {
        filledData.push(zeroAt(Math.min(lastPt.timestamp + stepMs, endAnchorTs)));
      }
      return filledData;
    }
    // 表格分页: 每个资源卡片独立分页, 展示查询区间内的全部数据点(最新在前)
    $scope.tblPageSize = 8;
    $scope.changeTablePage = function (metric, delta) {
      if (!metric || !metric.rowsDesc || !metric.rowsDesc.length) {
        return;
      }
      var next = (metric.tablePage || 1) + delta;
      if (next < 1 || next > metric.tblTotal) {
        return;
      }
      metric.tablePage = next;
    };

    $scope.isDescOrder = true;
    $scope.setDescOrder = function () {
      $scope.isDescOrder = true;
      reInitIdentityDatas();
    }
    $scope.setAscOrder = function () {
      $scope.isDescOrder = false;
      reInitIdentityDatas();
    }

    // 初始化查询: 必须放在控制器末尾执行, 此时 isRealtimeQuery 等 $scope 函数
    // 均已定义(若在构造函数开头同步调用, 会因函数尚未定义抛出 TypeError,
    // 导致整个视图模板编译中断, 页面显示原始 {{}} 且所有控件失效)
    reInitIdentityDatas();
  }]);

/**
 * 自定义日期时间选择器, 替代原生 datetime-local:
 * 1. 弹出面板样式与实时监控页面设计语言统一(原生弹出日历无法定制样式);
 * 2. 原生 input 有浏览器内置校验(Chrome 默认 step=60, 秒数非 0 触发
 *    "请输入有效值" 拦截表单提交), 自定义组件不受其影响;
 * 3. ng-model 绑定 Date 对象(分钟精度), 与页面控制器逻辑保持一致。
 */
app.directive('mrtDatetime', function () {
  return {
    restrict: 'E',
    scope: {
      // 注意: 空的隔离作用域会切断 ng-model 表达式与父作用域的联系,
      // 必须显式声明 ngModel 双向绑定, 否则无法回显和写回所选时间
      ngModel: '='
    },
    template: ''
      + '<div class="mrt-dt" ng-class="{open: open}">'
      + '  <input class="mrt-dt-input" type="text" readonly="readonly" ng-click="toggle($event)"'
      + '    ng-model="viewValue" placeholder="请选择时间">'
      + '  <i class="fa fa-calendar-check-o mrt-dt-icon"></i>'
      + '  <div class="mrt-dt-panel" ng-if="open" ng-click="$event.stopPropagation()">'
      + '    <div class="mrt-dt-head">'
      + '      <button type="button" class="mrt-dt-nav" ng-click="addMonths(-1)"><i class="fa fa-chevron-left"></i></button>'
      + '      <span class="mrt-dt-title">{{view.year}} 年 {{view.month + 1}} 月</span>'
      + '      <button type="button" class="mrt-dt-nav" ng-click="addMonths(1)"><i class="fa fa-chevron-right"></i></button>'
      + '    </div>'
      + '    <div class="mrt-dt-weeks"><span ng-repeat="w in weekNames track by $index">{{w}}</span></div>'
      + '    <div class="mrt-dt-days">'
      + '      <button type="button" class="mrt-dt-day" ng-repeat="d in days track by $index"'
      + '        ng-class="{muted: d.out, today: d.today, selected: d.selected}" ng-click="pickDay(d)">{{d.label}}</button>'
      + '    </div>'
      + '    <div class="mrt-dt-time">'
      + '      <i class="fa fa-clock-o"></i>'
      + '      <select ng-model="view.hour" ng-options="h for h in hours" ng-change="pickTime()"></select>'
      + '      <span class="mrt-dt-colon">:</span>'
      + '      <select ng-model="view.minute" ng-options="m for m in minutes" ng-change="pickTime()"></select>'
      + '    </div>'
      + '    <div class="mrt-dt-foot">'
      + '      <button type="button" class="mrt-dt-btn" ng-click="setNow()">此刻</button>'
      + '      <button type="button" class="mrt-dt-btn mrt-dt-ok" ng-click="close()">确定</button>'
      + '    </div>'
      + '  </div>'
      + '</div>',
    link: function (scope, element) {
      scope.open = false;
      scope.weekNames = ['日', '一', '二', '三', '四', '五', '六'];
      scope.hours = [];
      scope.minutes = [];
      for (var h = 0; h < 24; h++) {
        scope.hours.push(h < 10 ? '0' + h : '' + h);
      }
      for (var m = 0; m < 60; m++) {
        scope.minutes.push(m < 10 ? '0' + m : '' + m);
      }

      function pad(n) {
        return n < 10 ? '0' + n : '' + n;
      }

      function fmt(d) {
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate())
          + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes());
      }

      function validDate(d) {
        return d instanceof Date && !isNaN(d.getTime());
      }

      // 模型变化(外部快捷范围/实时窗口滑动/面板选择)时刷新输入框显示
      scope.$watch('ngModel', function (d) {
        scope.viewValue = validDate(d) ? fmt(d) : '';
        if (scope.open && validDate(d)) {
          scope.view.year = d.getFullYear();
          scope.view.month = d.getMonth();
          scope.view.hour = pad(d.getHours());
          scope.view.minute = pad(d.getMinutes());
          buildDays(d);
        }
      });

      function buildDays(selDate) {
        var first = new Date(scope.view.year, scope.view.month, 1);
        var start = new Date(first.getFullYear(), first.getMonth(), first.getDate() - first.getDay());
        var today = new Date();
        today = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        var selKey = validDate(selDate)
          ? new Date(selDate.getFullYear(), selDate.getMonth(), selDate.getDate()).getTime() : null;
        scope.days = [];
        for (var i = 0; i < 42; i++) {
          var cur = new Date(start.getFullYear(), start.getMonth(), start.getDate() + i);
          scope.days.push({
            label: cur.getDate(),
            out: cur.getMonth() !== scope.view.month,
            today: cur.getTime() === today.getTime(),
            selected: selKey !== null && cur.getTime() === selKey,
            date: cur
          });
        }
      }

      function syncFromModel() {
        var d = scope.ngModel;
        if (!validDate(d)) {
          d = new Date();
        }
        scope.view = {
          year: d.getFullYear(),
          month: d.getMonth(),
          hour: pad(d.getHours()),
          minute: pad(d.getMinutes())
        };
        buildDays(d);
      }

      scope.selected = null;

      function applyPick(date) {
        // 分钟精度, 秒/毫秒清零
        var picked = new Date(date.getFullYear(), date.getMonth(), date.getDate(),
          parseInt(scope.view.hour, 10), parseInt(scope.view.minute, 10), 0, 0);
        scope.selected = picked;
        // 通过双向绑定写回父作用域的 startTime/endTime
        scope.ngModel = new Date(picked.getTime());
        scope.view.year = picked.getFullYear();
        scope.view.month = picked.getMonth();
        buildDays(picked);
      }

      scope.addMonths = function (n) {
        var d = new Date(scope.view.year, scope.view.month + n, 1);
        scope.view.year = d.getFullYear();
        scope.view.month = d.getMonth();
        buildDays(scope.selected);
      };
      scope.pickDay = function (d) {
        applyPick(d.date);
      };
      scope.pickTime = function () {
        var base = scope.selected || new Date();
        applyPick(new Date(base.getFullYear(), base.getMonth(), base.getDate()));
      };
      scope.setNow = function () {
        var n = new Date();
        scope.view.hour = pad(n.getHours());
        scope.view.minute = pad(n.getMinutes());
        applyPick(n);
      };
      // 注意: 这里不 stopPropagation, 让事件冒泡到 document 监听器,
      // 由它关闭其他已打开的选择器面板, 保证同时最多只有一个面板打开
      scope.toggle = function () {
        scope.open = !scope.open;
        if (scope.open) {
          syncFromModel();
        }
      };
      scope.close = function () {
        scope.open = false;
      };

      // 点击组件外部时收起面板
      function onDocClick(e) {
        if (!scope.open) {
          return;
        }
        if (element[0].contains(e.target)) {
          return;
        }
        scope.$apply(function () {
          scope.open = false;
        });
      }
      document.addEventListener('click', onDocClick);
      scope.$on('$destroy', function () {
        document.removeEventListener('click', onDocClick);
      });
    }
  };
});
