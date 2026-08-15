<script setup lang="ts">
/** 图标资源取自 Spring Cloud Alibaba 官网微服务全景图 */
interface ArchNode {
  name: string
  icon: string
  /** 名称高亮前缀（如“异步调用 /”） */
  highlight?: string
}

interface PlaneGroup {
  title: string
  nodes: ArchNode[]
  /** 行内占宽比（默认 1）：组件多的平面可给更大占比 */
  grow?: number
}

const planesTop: PlaneGroup[] = [
  {
    title: '控制面',
    nodes: [
      {
        name: 'Nacos',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN01GdSxST24NB7Yl5Pdx_!!6000000007378-2-tps-80-80.png'
      },
      {
        name: 'OpenSergo',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN01oLvUis1DjSkwk53Tg_!!6000000000252-2-tps-80-80.png'
      }
    ]
  },
  {
    title: '治理面',
    /** 三个组件比控制面多一个，占宽比放大避免拥挤 */
    grow: 1.3,
    nodes: [
      {
        name: 'Sentinel',
        icon: 'https://img.alicdn.com/imgextra/i2/O1CN01bbN4uH1OXyb3Upgcg_!!6000000001716-2-tps-80-80.png'
      },
      {
        name: 'XXL-Job',
        icon: 'https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/xxl-logo.png'
      },
      {
        name: 'AppActive',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN019Nt2qs1eyZRjJMDwi_!!6000000003940-2-tps-80-80.png'
      }
    ]
  }
]

const planesBottom: PlaneGroup[] = [
  {
    title: '运维面',
    nodes: [
      {
        name: 'Docker',
        icon: 'https://cdn.simpleicons.org/docker/2496ED'
      },
      {
        name: 'Kubernetes',
        icon: 'https://img.alicdn.com/imgextra/i1/O1CN01FxpqPp1Q3rn1Xh6N1_!!6000000001921-2-tps-80-80.png'
      }
    ]
  },
  {
    title: '可观测',
    nodes: [
      {
        name: 'Loki',
        icon: 'https://raw.githubusercontent.com/grafana/loki/main/docs/sources/logo.png'
      },
      {
        name: 'OpenTelemetry',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN01hTFdm51Jor72V1UQ9_!!6000000001076-2-tps-80-80.png'
      },
      {
        name: 'Prometheus',
        icon: 'https://img.alicdn.com/imgextra/i2/O1CN01xWWOPW1YLHmKw5I1Z_!!6000000003042-2-tps-80-80.png'
      }
    ]
  }
]

/** 数据面核心链路节点 */
const iotNode: ArchNode = {
  name: '客户端',
  icon: 'https://gw.alicdn.com/imgextra/i2/O1CN014ZK8OP1msdEMGutsg_!!6000000005010-2-tps-166-160.png'
}
const gatewayNode: ArchNode = {
  name: '网关/Higress',
  icon: 'https://img.alicdn.com/imgextra/i4/O1CN01BodpHP1YS9ihnVuRB_!!6000000003057-2-tps-80-80.png'
}
const rocketmqNode: ArchNode = {
  name: 'RocketMQ',
  highlight: '异步调用 /',
  icon: 'https://img.alicdn.com/imgextra/i4/O1CN01qZ4Kh71Vfwndw8Qoa_!!6000000002681-2-tps-80-80.png'
}
const seataNode: ArchNode = {
  name: 'Seata',
  highlight: '分布式事务 /',
  icon: 'https://img.alicdn.com/imgextra/i3/O1CN01W3W4PH1qXngVzf7uP_!!6000000005506-2-tps-80-80.png'
}
const storageNode: ArchNode = {
  name: '数据存储',
  icon: 'https://gw.alicdn.com/imgextra/i1/O1CN01VfCTpe1gHdXoaboh6_!!6000000004117-2-tps-200-211.png'
}

const clusterNodes: ArchNode[] = [
  {
    name: 'Dubbo',
    icon: 'https://img.alicdn.com/imgextra/i2/O1CN01TIWcnX1inMDF8jH9J_!!6000000004457-2-tps-80-80.png'
  },
  {
    name: 'SCA',
    icon: 'https://img.alicdn.com/imgextra/i2/O1CN01kjTZ8b1d4remhTuM6_!!6000000003683-2-tps-80-80.png'
  }
]
</script>

<template>
  <div class="arch">
    <!-- 共享箭头头部定义 -->
    <svg width="0" height="0" class="arch-defs">
      <defs>
        <marker
          id="arch-arrow"
          markerWidth="10"
          markerHeight="5"
          refX="0"
          refY="2.5"
          orient="auto"
        >
          <polygon points="0 0, 5 2.5, 0 5" fill="#5dc07e" />
        </marker>
      </defs>
    </svg>

    <!-- 标题：全景概览 / 微服务全景图 -->
    <div class="arch-header">
      <span class="arch-header__sub">全景概览</span>
      <span class="arch-header__title">微服务全景图</span>
    </div>

    <!-- 控制面 / 治理面 -->
    <div class="arch-row">
      <div
        v-for="plane in planesTop"
        :key="plane.title"
        class="arch-plane"
        :style="plane.grow ? { flex: plane.grow } : undefined"
      >
        <div class="arch-plane__box">
          <div class="arch-plane__bg" />
          <div class="arch-plane__content">
            <div v-for="n in plane.nodes" :key="n.name" class="arch-node">
              <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
              <p class="arch-node__name">{{ n.name }}</p>
            </div>
          </div>
        </div>
        <div class="arch-plane__title">{{ plane.title }}</div>
      </div>
    </div>

    <!-- 数据面：核心链路 -->
    <div class="arch-plane arch-plane--wide">
      <div class="arch-plane__box">
        <div class="arch-plane__bg" />
        <div class="arch-plane__content arch-flow">
          <div class="arch-node">
            <img class="arch-node__icon" :src="iotNode.icon" :alt="iotNode.name" />
            <p class="arch-node__name">{{ iotNode.name }}</p>
          </div>

          <svg viewBox="0 0 100 100" class="arch-arrow">
            <path d="M0 50 H80" stroke="#5dc07e" stroke-width="5" opacity="0.12" fill="none" />
            <path
              d="M0 50 H80"
              class="arch-dash"
              stroke="#5dc07e"
              stroke-width="2"
              stroke-dasharray="5"
              fill="none"
              marker-end="url(#arch-arrow)"
            />
          </svg>

          <div class="arch-node">
            <img class="arch-node__icon" :src="gatewayNode.icon" :alt="gatewayNode.name" />
            <p class="arch-node__name">{{ gatewayNode.name }}</p>
          </div>

          <svg viewBox="0 0 100 100" class="arch-arrow">
            <path d="M0 50 H80" stroke="#5dc07e" stroke-width="5" opacity="0.12" fill="none" />
            <path
              d="M0 50 H80"
              class="arch-dash"
              stroke="#5dc07e"
              stroke-width="2"
              stroke-dasharray="5"
              fill="none"
              marker-end="url(#arch-arrow)"
            />
          </svg>

          <!-- 集群区：异步调用 / 同步调用 / 分布式事务 -->
          <div class="arch-mid">
            <div class="arch-mid__side">
              <svg viewBox="0 0 100 100" class="arch-curve">
                <path
                  d="M10 100 V60 A10 10 0 0 1 20 50 H80"
                  class="arch-dash"
                  stroke="#5dc07e"
                  stroke-width="2"
                  stroke-dasharray="5"
                  fill="none"
                  marker-end="url(#arch-arrow)"
                />
              </svg>
              <div class="arch-node arch-node--sm">
                <img class="arch-node__icon" :src="rocketmqNode.icon" :alt="rocketmqNode.name" />
                <p class="arch-node__name">
                  <span class="arch-node__highlight">{{ rocketmqNode.highlight }}</span>
                  {{ rocketmqNode.name }}
                </p>
              </div>
              <svg viewBox="0 0 100 100" class="arch-curve">
                <path
                  d="M90 100 V60 A10 10 0 0 0 80 50 H10"
                  class="arch-dash"
                  stroke="#5dc07e"
                  stroke-width="2"
                  stroke-dasharray="5"
                  fill="none"
                  marker-end="url(#arch-arrow)"
                />
              </svg>
            </div>

            <div class="arch-mid__clusters">
              <div class="arch-cluster">
                <div class="arch-cluster__bg" />
                <div class="arch-cluster__content">
                  <div
                    v-for="n in clusterNodes"
                    :key="`a-${n.name}`"
                    class="arch-node arch-node--xs"
                  >
                    <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
                    <p class="arch-node__name">{{ n.name }}</p>
                  </div>
                </div>
                <div class="arch-cluster__label">微服务集群A</div>
              </div>

              <svg viewBox="0 0 200 100" class="arch-arrow arch-arrow--sync">
                <path d="M0 50 H180" stroke="#5dc07e" stroke-width="5" opacity="0.12" fill="none" />
                <path
                  d="M0 50 H180"
                  class="arch-dash"
                  stroke="#5dc07e"
                  stroke-width="2"
                  stroke-dasharray="5"
                  fill="none"
                  marker-end="url(#arch-arrow)"
                />
                <text x="50%" y="70" text-anchor="middle" class="arch-arrow__label">
                  同步调用
                </text>
              </svg>

              <div class="arch-cluster">
                <div class="arch-cluster__bg" />
                <div class="arch-cluster__content arch-cluster__content--right">
                  <div
                    v-for="n in clusterNodes"
                    :key="`b-${n.name}`"
                    class="arch-node arch-node--xs"
                  >
                    <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
                    <p class="arch-node__name">{{ n.name }}</p>
                  </div>
                </div>
                <div class="arch-cluster__label">微服务集群B</div>
              </div>
            </div>

            <div class="arch-mid__side">
              <svg viewBox="0 0 100 100" class="arch-curve">
                <path
                  d="M10 0 V40 A10 10 0 0 0 20 50 H80"
                  class="arch-dash"
                  stroke="#5dc07e"
                  stroke-width="2"
                  stroke-dasharray="5"
                  fill="none"
                  marker-end="url(#arch-arrow)"
                />
              </svg>
              <div class="arch-node arch-node--sm">
                <img class="arch-node__icon" :src="seataNode.icon" :alt="seataNode.name" />
                <p class="arch-node__name">
                  <span class="arch-node__highlight">{{ seataNode.highlight }}</span>
                  {{ seataNode.name }}
                </p>
              </div>
              <svg viewBox="0 0 100 100" class="arch-curve">
                <path
                  d="M90 0 V40 A10 10 0 0 1 80 50 H10"
                  class="arch-dash"
                  stroke="#5dc07e"
                  stroke-width="2"
                  stroke-dasharray="5"
                  fill="none"
                  marker-end="url(#arch-arrow)"
                />
              </svg>
            </div>
          </div>

          <svg viewBox="0 0 100 100" class="arch-arrow">
            <path d="M0 50 H80" stroke="#5dc07e" stroke-width="5" opacity="0.12" fill="none" />
            <path
              d="M0 50 H80"
              class="arch-dash"
              stroke="#5dc07e"
              stroke-width="2"
              stroke-dasharray="5"
              fill="none"
              marker-end="url(#arch-arrow)"
            />
          </svg>

          <div class="arch-node">
            <img class="arch-node__icon" :src="storageNode.icon" :alt="storageNode.name" />
            <p class="arch-node__name">{{ storageNode.name }}</p>
          </div>
        </div>
      </div>
      <div class="arch-plane__title">数据面</div>
    </div>

    <!-- 运维面 / 可观测 -->
    <div class="arch-row">
      <div v-for="plane in planesBottom" :key="plane.title" class="arch-plane">
        <div class="arch-plane__box">
          <div class="arch-plane__bg" />
          <div class="arch-plane__content">
            <div v-for="n in plane.nodes" :key="n.name" class="arch-node">
              <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
              <p class="arch-node__name">{{ n.name }}</p>
            </div>
          </div>
        </div>
        <div class="arch-plane__title">{{ plane.title }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
/** 配色取自 SCA 全景图主题 */
$arrow: #5dc07e;
$highlight: #418b47;
$normal: #9295a5;

.arch {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.arch-defs {
  position: absolute;
}

/** 标题 */
.arch-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}

.arch-header__sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  letter-spacing: 4px;
}

.arch-header__title {
  font-size: 30px;
  font-weight: 500;
  color: #fff;
}

/** 平面行 */
.arch-row {
  display: flex;
  gap: 20px;
  justify-content: space-around;
}

.arch-plane {
  flex: 1;

  &--wide {
    flex: none;
    width: 100%;
  }
}

/** 3D 底板容器：背景层倾斜、内容层保持平面 */
.arch-plane__box {
  position: relative;
  padding: 12px 16px;
}

/** 子面板底板：平直卡片（渐变底 + 圆角 + 柔和投影），边角不再倾斜 */
.arch-plane__bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(0deg, #fbfdff 0%, #f4f8ff 100%);
  border-radius: 12px;
  box-shadow: 0 4px 14px 0 rgb(73 101 120 / 16%);
}

.arch-plane__content {
  position: relative;
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: space-evenly;
}

.arch-plane__title {
  margin-top: 4px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.92);
  text-align: center;
  text-shadow: 0 1px 3px rgb(0 0 0 / 25%);
}

/** 组件节点 */
.arch-node {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
}

.arch-node__icon {
  width: 56px;
  height: 56px;
  object-fit: contain;
}

.arch-node__name {
  font-size: 13px;
  color: $normal;
  text-align: center;
  white-space: nowrap;
}

.arch-node__highlight {
  color: $highlight;
}

.arch-node--sm .arch-node__icon {
  width: 46px;
  height: 46px;
}

.arch-node--xs .arch-node__icon {
  width: 36px;
  height: 36px;
}

/** 数据面核心链路 */
.arch-flow {
  gap: 4px;
}

/** 箭头允许按比例收缩（配 min-width 兜底），避免链路总宽超出面板把末尾节点挤出 */
.arch-arrow {
  width: 112px;
  min-width: 72px;
  height: 104px;
}

.arch-arrow--sync {
  width: 190px;
  min-width: 128px;
}

.arch-arrow__label {
  font-size: 14px;
  fill: $highlight;
}

/** 虚线流动动画（放缓） */
.arch-dash {
  animation: arch-dash 9s linear infinite;
}

@keyframes arch-dash {
  to {
    stroke-dashoffset: -100;
  }
}

/** 集群区：异步 / 同步 / 分布式事务 */
.arch-mid {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 2px;
  align-items: stretch;
  min-width: 0;
}

.arch-mid__side {
  display: flex;
  align-items: center;
  justify-content: center;
}

.arch-curve {
  flex: 1;
  height: 76px;
  min-width: 52px;
}

.arch-mid__clusters {
  display: flex;
  gap: 6px;
  align-items: center;
}

.arch-cluster {
  position: relative;
  flex: 1;
  /** 下限收窄：空间不足时先压缩卡片留白，保证链路整体不超出数据面面板 */
  min-width: 130px;
  padding: 8px 16px;
}

/** 集群底板：与平面底板同款平直卡片 */
.arch-cluster__bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(0deg, #fbfdff 0%, #f4f8ff 100%);
  border-radius: 10px;
  box-shadow: 0 3px 10px 0 rgb(73 101 120 / 12%);
}

.arch-cluster__content {
  position: relative;
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: center;

  &--right {
    justify-content: center;
  }
}

.arch-cluster__label {
  position: relative;
  margin-top: 2px;
  font-size: 12px;
  color: $normal;
  text-align: center;
}
</style>
