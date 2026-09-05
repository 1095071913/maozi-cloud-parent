<script setup lang="ts">
/** 图标资源取自 Spring Cloud Alibaba 官网微服务全景图 */
interface ArchNode {
  name: string
  icon: string
  /** 名称高亮前缀（如“异步调用 /”） */
  highlight?: string
  /** 横版 logo：按更宽的比例显示 */
  wide?: boolean
  /** 浮窗信息：官网 / GitHub / 框架描述 */
  site?: string
  github?: string
  desc?: string
}

interface PlaneGroup {
  title: string
  nodes: ArchNode[]
  /** 行内占宽比（默认 1）：组件多的平面可给更大占比 */
  grow?: number
}

const planesTop: PlaneGroup[] = [
  {
    title: '治理面',
    /** 五个组件，占宽比放大避免拥挤 */
    grow: 1.8,
    nodes: [
      {
        name: 'Nacos',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN01GdSxST24NB7Yl5Pdx_!!6000000007378-2-tps-80-80.png',
        site: 'https://nacos.io/',
        github: 'https://github.com/alibaba/nacos',
        desc: '更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。'
      },
      {
        name: 'Seata',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN01W3W4PH1qXngVzf7uP_!!6000000005506-2-tps-80-80.png',
        site: 'https://seata.io/',
        github: 'https://github.com/seata/seata',
        desc: '开源的分布式事务解决方案，提供高性能、简单易用的分布式事务服务。'
      },
      {
        name: 'Sentinel',
        icon: 'https://img.alicdn.com/imgextra/i2/O1CN01bbN4uH1OXyb3Upgcg_!!6000000001716-2-tps-80-80.png',
        site: 'https://sentinelguard.io/',
        github: 'https://github.com/alibaba/Sentinel',
        desc: '面向分布式、多语言异构化服务架构的流量治理组件。'
      },
      {
        name: 'Snail-Job',
        icon: 'https://snailjob.opensnail.com/logo.svg',
        site: 'https://snailjob.opensnail.com/',
        github: 'https://github.com/aizuda/snail-job',
        desc: '灵活、可靠且高效的分布式任务重试与任务调度平台，支持可视化任务编排与多样化告警监控。'
      },
      {
        name: 'RocketMQ',
        icon: 'https://img.alicdn.com/imgextra/i4/O1CN01qZ4Kh71Vfwndw8Qoa_!!6000000002681-2-tps-80-80.png',
        site: 'https://rocketmq.apache.org/',
        github: 'https://github.com/apache/rocketmq',
        desc: '云原生“消息、事件、流”实时数据处理平台，覆盖云边端一体化场景。'
      }
    ]
  },
  {
    title: '控制面',
    nodes: [
      {
        name: 'Spring Boot Admin',
        icon: 'https://raw.githubusercontent.com/codecentric/spring-boot-admin/master/spring-boot-admin-server-ui/src/main/frontend/assets/img/icon-spring-boot-admin.svg',
        site: 'https://codecentric.github.io/spring-boot-admin/',
        github: 'https://github.com/codecentric/spring-boot-admin',
        desc: '用于监控与管理 Spring Boot 应用的社区开源项目，实时查看健康、指标与日志。'
      },
      {
        name: 'Arthas',
        wide: true,
        icon: 'https://raw.githubusercontent.com/alibaba/arthas/master/site/docs/.vuepress/public/images/arthas.png',
        site: 'https://arthas.aliyun.com/',
        github: 'https://github.com/alibaba/arthas',
        desc: 'Alibaba 开源的 Java 诊断工具，无需重启即可在线排查线上问题。'
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
        icon: 'https://cdn.simpleicons.org/docker/2496ED',
        site: 'https://www.docker.com/',
        github: 'https://github.com/moby/moby',
        desc: '开源的应用容器引擎，将应用及其依赖打包成标准化镜像随处运行。'
      },
      {
        name: 'Kubernetes',
        icon: 'https://img.alicdn.com/imgextra/i1/O1CN01FxpqPp1Q3rn1Xh6N1_!!6000000001921-2-tps-80-80.png',
        site: 'https://kubernetes.io/',
        github: 'https://github.com/kubernetes/kubernetes',
        desc: '开源的容器编排引擎，负责容器化应用的自动化部署、扩缩与管理。'
      }
    ]
  },
  {
    title: '可观测',
    /** 五个组件，占宽比放大避免拥挤 */
    grow: 1.8,
    nodes: [
      {
        name: 'Loki',
        icon: 'https://raw.githubusercontent.com/grafana/loki/main/docs/sources/logo.png',
        site: 'https://grafana.com/oss/loki/',
        github: 'https://github.com/grafana/loki',
        desc: 'Grafana 出品的高性能日志聚合系统，像操作对象存储一样低成本。'
      },
      {
        name: 'Grafana Tempo',
        icon: 'https://raw.githubusercontent.com/grafana/tempo/main/cmd/tempo/app/static/tempo-icon.png',
        site: 'https://grafana.com/oss/tempo/',
        github: 'https://github.com/grafana/tempo',
        desc: '高容量、低成本的分布式链路追踪后端，与 Grafana 深度集成。'
      },
      {
        name: 'Prometheus',
        icon: 'https://img.alicdn.com/imgextra/i2/O1CN01xWWOPW1YLHmKw5I1Z_!!6000000003042-2-tps-80-80.png',
        site: 'https://prometheus.io/',
        github: 'https://github.com/prometheus/prometheus',
        desc: '开源的系统监控与告警工具包，拉取式指标采集的事实标准。'
      },
      {
        name: 'Grafana',
        icon: 'https://cdn.simpleicons.org/grafana/F46800',
        site: 'https://grafana.com/',
        github: 'https://github.com/grafana/grafana',
        desc: '开源的可视化分析平台，将指标、日志、追踪统一为图表与大屏。'
      },
      {
        name: 'OpenTelemetry',
        icon: 'https://img.alicdn.com/imgextra/i3/O1CN01hTFdm51Jor72V1UQ9_!!6000000001076-2-tps-80-80.png',
        site: 'https://opentelemetry.io/',
        github: 'https://github.com/open-telemetry',
        desc: '高质量、可移植的可观测性标准与工具集，统一采集指标、日志与链路。'
      }
    ]
  }
]

/** 数据面核心链路节点 */
const iotNode: ArchNode = {
  name: '客户端',
  icon: 'https://gw.alicdn.com/imgextra/i2/O1CN014ZK8OP1msdEMGutsg_!!6000000005010-2-tps-166-160.png',
  desc: 'IoT / PC / Mobile 多端流量入口。'
}
const gatewayNode: ArchNode = {
  name: '网关/Higress',
  icon: 'https://img.alicdn.com/imgextra/i4/O1CN01BodpHP1YS9ihnVuRB_!!6000000003057-2-tps-80-80.png',
  site: 'https://higress.cn/',
  github: 'https://github.com/alibaba/higress',
  desc: '遵循开源 Ingress/Gateway API 标准的云原生网关，流量调度、服务治理、安全防护三合一。'
}
const rocketmqNode: ArchNode = {
  name: 'RocketMQ',
  highlight: '异步调用 /',
  icon: 'https://img.alicdn.com/imgextra/i4/O1CN01qZ4Kh71Vfwndw8Qoa_!!6000000002681-2-tps-80-80.png',
  site: 'https://rocketmq.apache.org/',
  github: 'https://github.com/apache/rocketmq',
  desc: '链路间的异步调用通过 RocketMQ 消息解耦，削峰填谷。'
}
const seataNode: ArchNode = {
  name: 'Seata',
  highlight: '分布式事务 /',
  icon: 'https://img.alicdn.com/imgextra/i3/O1CN01W3W4PH1qXngVzf7uP_!!6000000005506-2-tps-80-80.png',
  site: 'https://seata.io/',
  github: 'https://github.com/seata/seata',
  desc: '保障跨微服务集群间调用的一致性，AT 模式对业务近乎无侵入。'
}
const storageNode: ArchNode = {
  name: '数据存储',
  icon: 'https://gw.alicdn.com/imgextra/i1/O1CN01VfCTpe1gHdXoaboh6_!!6000000004117-2-tps-200-211.png',
  desc: '业务数据最终落地存储（MySQL / Redis 等）。'
}

const clusterNodes: ArchNode[] = [
  {
    name: 'Dubbo',
    icon: 'https://img.alicdn.com/imgextra/i2/O1CN01TIWcnX1inMDF8jH9J_!!6000000004457-2-tps-80-80.png',
    site: 'https://cn.dubbo.apache.org/',
    github: 'https://github.com/apache/dubbo',
    desc: '高性能 RPC 服务框架，为大规模微服务提供通信、流量治理与可观测性。'
  },
  {
    name: 'SCA',
    icon: 'https://img.alicdn.com/imgextra/i2/O1CN01kjTZ8b1d4remhTuM6_!!6000000003683-2-tps-80-80.png',
    site: 'https://sca.aliyun.com/',
    github: 'https://github.com/alibaba/spring-cloud-alibaba',
    desc: 'Spring Cloud Alibaba，一站式的分布式应用开发框架。'
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
            <el-popover
              v-for="n in plane.nodes"
              :key="n.name"
              placement="top"
              trigger="hover"
              :width="280"
              :offset="12"
            >
              <template #reference>
                <div
                  class="arch-node"
                  :class="{ 'arch-node--wide': n.wide }"
                >
                  <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
                  <p class="arch-node__name">{{ n.name }}</p>
                </div>
              </template>
              <div class="arch-pop">
                <div class="arch-pop__head">
                  <img
                    class="arch-pop__icon"
                    :class="{ 'arch-pop__icon--wide': n.wide }"
                    :src="n.icon"
                    :alt="n.name"
                  />
                  <div class="arch-pop__title">{{ n.name }}</div>
                </div>
                <p class="arch-pop__desc">{{ n.desc }}</p>
                <div class="arch-pop__links">
                  <a v-if="n.site" :href="n.site" target="_blank" rel="noopener">
                    官网
                  </a>
                  <a v-if="n.github" :href="n.github" target="_blank" rel="noopener">
                    GitHub
                  </a>
                </div>
              </div>
            </el-popover>
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
          <el-popover placement="top" trigger="hover" :width="280" :offset="12">
            <template #reference>
              <div class="arch-node">
                <img class="arch-node__icon" :src="iotNode.icon" :alt="iotNode.name" />
                <p class="arch-node__name">{{ iotNode.name }}</p>
              </div>
            </template>
            <div class="arch-pop">
              <div class="arch-pop__head">
                <img class="arch-pop__icon" :src="iotNode.icon" :alt="iotNode.name" />
                <div class="arch-pop__title">{{ iotNode.name }}</div>
              </div>
              <p class="arch-pop__desc">{{ iotNode.desc }}</p>
            </div>
          </el-popover>

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

          <el-popover placement="top" trigger="hover" :width="280" :offset="12">
            <template #reference>
              <div class="arch-node">
                <img class="arch-node__icon" :src="gatewayNode.icon" :alt="gatewayNode.name" />
                <p class="arch-node__name">{{ gatewayNode.name }}</p>
              </div>
            </template>
            <div class="arch-pop">
              <div class="arch-pop__head">
                <img class="arch-pop__icon" :src="gatewayNode.icon" :alt="gatewayNode.name" />
                <div class="arch-pop__title">{{ gatewayNode.name }}</div>
              </div>
              <p class="arch-pop__desc">{{ gatewayNode.desc }}</p>
              <div class="arch-pop__links">
                <a :href="gatewayNode.site" target="_blank" rel="noopener">官网</a>
                <a :href="gatewayNode.github" target="_blank" rel="noopener">GitHub</a>
              </div>
            </div>
          </el-popover>

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
              <el-popover placement="top" trigger="hover" :width="280" :offset="12">
                <template #reference>
                  <div class="arch-node arch-node--sm">
                    <img class="arch-node__icon" :src="rocketmqNode.icon" :alt="rocketmqNode.name" />
                    <p class="arch-node__name">
                      <span class="arch-node__highlight">{{ rocketmqNode.highlight }}</span>
                      {{ rocketmqNode.name }}
                    </p>
                  </div>
                </template>
                <div class="arch-pop">
                  <div class="arch-pop__head">
                    <img class="arch-pop__icon" :src="rocketmqNode.icon" :alt="rocketmqNode.name" />
                    <div class="arch-pop__title">{{ rocketmqNode.name }}</div>
                  </div>
                  <p class="arch-pop__desc">{{ rocketmqNode.desc }}</p>
                  <div class="arch-pop__links">
                    <a :href="rocketmqNode.site" target="_blank" rel="noopener">官网</a>
                    <a :href="rocketmqNode.github" target="_blank" rel="noopener">GitHub</a>
                  </div>
                </div>
              </el-popover>
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
                  <el-popover
                    v-for="n in clusterNodes"
                    :key="`a-${n.name}`"
                    placement="top"
                    trigger="hover"
                    :width="280"
                    :offset="8"
                  >
                    <template #reference>
                      <div class="arch-node arch-node--xs">
                        <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
                        <p class="arch-node__name">{{ n.name }}</p>
                      </div>
                    </template>
                    <div class="arch-pop">
                      <div class="arch-pop__head">
                        <img class="arch-pop__icon" :src="n.icon" :alt="n.name" />
                        <div class="arch-pop__title">{{ n.name }}</div>
                      </div>
                      <p class="arch-pop__desc">{{ n.desc }}</p>
                      <div class="arch-pop__links">
                        <a v-if="n.site" :href="n.site" target="_blank" rel="noopener">
                          官网
                        </a>
                        <a v-if="n.github" :href="n.github" target="_blank" rel="noopener">
                          GitHub
                        </a>
                      </div>
                    </div>
                  </el-popover>
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
                  <el-popover
                    v-for="n in clusterNodes"
                    :key="`b-${n.name}`"
                    placement="top"
                    trigger="hover"
                    :width="280"
                    :offset="8"
                  >
                    <template #reference>
                      <div class="arch-node arch-node--xs">
                        <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
                        <p class="arch-node__name">{{ n.name }}</p>
                      </div>
                    </template>
                    <div class="arch-pop">
                      <div class="arch-pop__head">
                        <img class="arch-pop__icon" :src="n.icon" :alt="n.name" />
                        <div class="arch-pop__title">{{ n.name }}</div>
                      </div>
                      <p class="arch-pop__desc">{{ n.desc }}</p>
                      <div class="arch-pop__links">
                        <a v-if="n.site" :href="n.site" target="_blank" rel="noopener">
                          官网
                        </a>
                        <a v-if="n.github" :href="n.github" target="_blank" rel="noopener">
                          GitHub
                        </a>
                      </div>
                    </div>
                  </el-popover>
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
              <el-popover placement="top" trigger="hover" :width="280" :offset="12">
                <template #reference>
                  <div class="arch-node arch-node--sm">
                    <img class="arch-node__icon" :src="seataNode.icon" :alt="seataNode.name" />
                    <p class="arch-node__name">
                      <span class="arch-node__highlight">{{ seataNode.highlight }}</span>
                      {{ seataNode.name }}
                    </p>
                  </div>
                </template>
                <div class="arch-pop">
                  <div class="arch-pop__head">
                    <img class="arch-pop__icon" :src="seataNode.icon" :alt="seataNode.name" />
                    <div class="arch-pop__title">{{ seataNode.name }}</div>
                  </div>
                  <p class="arch-pop__desc">{{ seataNode.desc }}</p>
                  <div class="arch-pop__links">
                    <a :href="seataNode.site" target="_blank" rel="noopener">官网</a>
                    <a :href="seataNode.github" target="_blank" rel="noopener">GitHub</a>
                  </div>
                </div>
              </el-popover>
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

          <el-popover placement="top" trigger="hover" :width="280" :offset="12">
            <template #reference>
              <div class="arch-node">
                <img class="arch-node__icon" :src="storageNode.icon" :alt="storageNode.name" />
                <p class="arch-node__name">{{ storageNode.name }}</p>
              </div>
            </template>
            <div class="arch-pop">
              <div class="arch-pop__head">
                <img class="arch-pop__icon" :src="storageNode.icon" :alt="storageNode.name" />
                <div class="arch-pop__title">{{ storageNode.name }}</div>
              </div>
              <p class="arch-pop__desc">{{ storageNode.desc }}</p>
            </div>
          </el-popover>
        </div>
      </div>
      <div class="arch-plane__title">数据面</div>
    </div>

    <!-- 运维面 / 可观测 -->
    <div class="arch-row">
      <div
        v-for="plane in planesBottom"
        :key="plane.title"
        class="arch-plane"
        :style="plane.grow ? { flex: plane.grow } : undefined"
      >
        <div class="arch-plane__box">
          <div class="arch-plane__bg" />
          <div class="arch-plane__content">
            <el-popover
              v-for="n in plane.nodes"
              :key="n.name"
              placement="top"
              trigger="hover"
              :width="280"
              :offset="12"
            >
              <template #reference>
                <div
                  class="arch-node"
                  :class="{ 'arch-node--wide': n.wide }"
                >
                  <img class="arch-node__icon" :src="n.icon" :alt="n.name" />
                  <p class="arch-node__name">{{ n.name }}</p>
                </div>
              </template>
              <div class="arch-pop">
                <div class="arch-pop__head">
                  <img
                    class="arch-pop__icon"
                    :class="{ 'arch-pop__icon--wide': n.wide }"
                    :src="n.icon"
                    :alt="n.name"
                  />
                  <div class="arch-pop__title">{{ n.name }}</div>
                </div>
                <p class="arch-pop__desc">{{ n.desc }}</p>
                <div class="arch-pop__links">
                  <a v-if="n.site" :href="n.site" target="_blank" rel="noopener">
                    官网
                  </a>
                  <a v-if="n.github" :href="n.github" target="_blank" rel="noopener">
                    GitHub
                  </a>
                </div>
              </div>
            </el-popover>
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

/** 组件节点：悬停时图标与文字整体放大突出 */
.arch-node {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
  transition: transform 0.25s ease;

  &:hover {
    z-index: 1;
    transform: scale(1.18);
  }
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

/** 横版 logo：Arthas 官方图字母偏上、底部带装饰线（占位不下沉），
 *  故按字母区域比例裁掉底部装饰线，并下移补偿与相邻 56px 方形图标对齐 */
.arch-node--wide .arch-node__icon {
  width: 108px;
  height: 31px;
  margin-top: 23px;
  object-fit: cover;
  object-position: left top;
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

/** 悬停浮窗内容：图标 + 框架名 / 描述 / 官网与 GitHub 链接 */
.arch-pop__head {
  display: flex;
  gap: 8px;
  align-items: center;
}

.arch-pop__icon {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  object-fit: contain;
}

/** 横版 logo（Arthas）按更宽比例显示 */
.arch-pop__icon--wide {
  width: 56px;
  height: 16px;
  object-fit: cover;
  object-position: left top;
}

.arch-pop__title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2d3d;
}

.arch-pop__desc {
  margin: 8px 0;
  font-size: 12px;
  line-height: 1.6;
  color: #606266;
}

.arch-pop__links {
  display: flex;
  gap: 8px;

  a {
    padding: 2px 12px;
    font-size: 12px;
    color: $highlight;
    background: #d6ead8;
    border: 1px solid $arrow;
    border-radius: 4px;
    text-decoration: none;

    &:hover {
      opacity: 0.8;
    }
  }
}
</style>
