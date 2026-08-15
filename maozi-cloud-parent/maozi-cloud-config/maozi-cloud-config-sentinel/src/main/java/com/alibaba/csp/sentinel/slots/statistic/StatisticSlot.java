/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.statistic;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotEntryCallback;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlotExitCallback;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.PriorityWaitException;
import com.alibaba.csp.sentinel.spi.Spi;
import com.alibaba.csp.sentinel.util.TimeUtil;

import java.util.Collection;

/**
 * 实时统计数据处理器插槽
 * <p>
 * 该插槽是 Sentinel 责任链中的核心统计模块，专门用于实时数据采集和统计。
 * 当请求进入此插槽时，需要分别统计以下维度的信息：
 * <ul>
 * <li>{@link ClusterNode}：基于资源 ID 的集群节点总统计数据</li>
 * <li>Origin node：来自不同调用方/来源的集群节点统计数据</li>
 * <li>{@link DefaultNode}：特定上下文中特定资源名称的统计数据</li>
 * <li>所有入口的总统计数据</li>
 * </ul>
 * </p>
 * <p>
 * <b>注：</b>本类为复制自 sentinel-core 1.8.6 的
 * {@code com.alibaba.csp.sentinel.slots.statistic.StatisticSlot} 的本地覆盖副本
 * （同包同名类在 classpath 上优先于 jar 内类加载），
 * 修改点：entry 通过分支中当前资源节点（DefaultNode）的 {@code node.addPassRequest(count)}
 * 调用被注释禁用，通过请求数仅统计到来源节点与全局入口节点（调整统计口径）；
 * 升级 sentinel-core 版本时需同步比对原生类变更。
 * </p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
@Spi(order = Constants.ORDER_STATISTIC_SLOT)
public class StatisticSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    /**
     * 请求进入资源时的统计处理
     * <p>
     * 该方法在请求进入时执行，通过调用链式处理（fireEntry）后，根据不同结果进行统计：
     * <ul>
     *   <li>通过（Pass）：当前资源节点（{@code node}）仅增加线程数，
     *       该节点的 {@code addPassRequest} 已被注释禁用（见方法体内注释）；
     *       来源节点和入口节点仍正常增加线程数和通过请求数</li>
     *   <li>优先等待（PriorityWait）：仅增加线程数</li>
     *   <li>被阻塞（BlockException）：增加阻塞 QPS</li>
     *   <li>异常（Throwable）：记录异常信息</li>
     * </ul>
     * </p>
     *
     * @param context        当前调用上下文
     * @param resourceWrapper 资源包装器，包含资源名称和入口类型等信息
     * @param node           默认节点，用于统计特定上下文中该资源的数据
     * @param count          本次请求占用的令牌数/请求数
     * @param prioritized    是否为优先请求
     * @param args           额外参数
     * @throws Throwable 可能抛出的异常，包括 BlockException 等
     */
    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {
        try {
            // 先执行后续插槽的检查逻辑（如规则校验）
            fireEntry(context, resourceWrapper, node, count, prioritized, args);

            // 请求通过检查，增加当前资源的线程数
            node.increaseThreadNum();
            // 注：当前资源节点（node）的 addPassRequest 调用已被禁用，
            // 仅来源节点和入口节点会统计通过请求数（见下方分支）。原因为统计口径调整，具体历史不详
//            node.addPassRequest(count);

            // 如果存在来源节点（调用方），增加来源维度的线程数和通过请求数
            if (context.getCurEntry().getOriginNode() != null) {
                context.getCurEntry().getOriginNode().increaseThreadNum();
                context.getCurEntry().getOriginNode().addPassRequest(count);
            }

            // 如果是入口流量（EntryType.IN），增加全局入口节点的统计数据
            if (resourceWrapper.getEntryType() == EntryType.IN) {
                Constants.ENTRY_NODE.increaseThreadNum();
                Constants.ENTRY_NODE.addPassRequest(count);
            }

            // 触发已注册的入口回调处理器（通过事件）
            for (ProcessorSlotEntryCallback<DefaultNode> handler : StatisticSlotCallbackRegistry.getEntryCallbacks()) {
                handler.onPass(context, resourceWrapper, node, count, args);
            }
        } catch (PriorityWaitException ex) {
            // 优先等待异常：请求因优先级等待而通过，仅增加线程数，不计入通过请求数
            node.increaseThreadNum();
            if (context.getCurEntry().getOriginNode() != null) {
                // 增加来源节点的线程数
                context.getCurEntry().getOriginNode().increaseThreadNum();
            }

            if (resourceWrapper.getEntryType() == EntryType.IN) {
                // 增加全局入口节点的线程数
                Constants.ENTRY_NODE.increaseThreadNum();
            }
            // 触发已注册的入口回调处理器（通过事件）
            for (ProcessorSlotEntryCallback<DefaultNode> handler : StatisticSlotCallbackRegistry.getEntryCallbacks()) {
                handler.onPass(context, resourceWrapper, node, count, args);
            }
        } catch (BlockException e) {
            // 被流控规则阻塞：设置阻塞异常到当前入口，并增加各维度的阻塞 QPS
            context.getCurEntry().setBlockError(e);

            // 增加当前资源节点的阻塞 QPS
            node.increaseBlockQps(count);
            if (context.getCurEntry().getOriginNode() != null) {
                // 增加来源节点的阻塞 QPS
                context.getCurEntry().getOriginNode().increaseBlockQps(count);
            }

            if (resourceWrapper.getEntryType() == EntryType.IN) {
                // 增加全局入口节点的阻塞 QPS
                Constants.ENTRY_NODE.increaseBlockQps(count);
            }

            // 触发已注册的入口回调处理器（阻塞事件）
            for (ProcessorSlotEntryCallback<DefaultNode> handler : StatisticSlotCallbackRegistry.getEntryCallbacks()) {
                handler.onBlocked(e, context, resourceWrapper, node, count, args);
            }

            throw e;
        } catch (Throwable e) {
            // 意外的内部错误：设置错误到当前入口
            context.getCurEntry().setError(e);

            throw e;
        }
    }

    /**
     * 请求退出资源时的统计处理
     * <p>
     * 该方法在请求完成（退出）时执行，主要完成以下统计：
     * <ul>
     *   <li>计算并记录响应时间（RT）</li>
     *   <li>记录成功请求数</li>
     *   <li>减少线程数</li>
     *   <li>记录异常数（如果有非阻塞异常）</li>
     * </ul>
     * </p>
     *
     * @param context        当前调用上下文
     * @param resourceWrapper 资源包装器
     * @param count          本次请求占用的令牌数/请求数
     * @param args           额外参数
     */
    @Override
    public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
        Node node = context.getCurNode();

        // 如果请求未被阻塞，则记录响应时间和成功计数
        if (context.getCurEntry().getBlockError() == null) {
            // 计算响应时间 = 完成时间 - 创建时间
            long completeStatTime = TimeUtil.currentTimeMillis();
            context.getCurEntry().setCompleteTimestamp(completeStatTime);
            long rt = completeStatTime - context.getCurEntry().getCreateTimestamp();

            Throwable error = context.getCurEntry().getError();

            // 分别为当前节点、来源节点和全局入口节点记录响应时间和成功计数
            recordCompleteFor(node, count, rt, error);
            recordCompleteFor(context.getCurEntry().getOriginNode(), count, rt, error);
            if (resourceWrapper.getEntryType() == EntryType.IN) {
                recordCompleteFor(Constants.ENTRY_NODE, count, rt, error);
            }
        }

        // 触发已注册的退出回调处理器
        Collection<ProcessorSlotExitCallback> exitCallbacks = StatisticSlotCallbackRegistry.getExitCallbacks();
        for (ProcessorSlotExitCallback handler : exitCallbacks) {
            handler.onExit(context, resourceWrapper, count, args);
        }

        // 修复 bug: https://github.com/alibaba/Sentinel/issues/2374
        // 继续执行后续插槽的退出逻辑
        fireExit(context, resourceWrapper, count, args);
    }

    /**
     * 为指定节点记录请求完成的统计数据
     * <p>
     * 记录响应时间和成功请求数，减少线程数，如果是非阻塞异常则增加异常 QPS
     * </p>
     *
     * @param node       目标统计节点，如果为 null 则不做任何处理
     * @param batchCount 批次请求数量
     * @param rt         响应时间（毫秒）
     * @param error      请求过程中发生的异常，可能为 null
     */
    private void recordCompleteFor(Node node, int batchCount, long rt, Throwable error) {
        if (node == null) {
            return;
        }
        // 记录响应时间和成功请求数
        node.addRtAndSuccess(rt, batchCount);
        // 减少线程数（请求已完成，释放占用线程）
        node.decreaseThreadNum();

        // 如果存在异常且不是阻塞异常，则增加异常 QPS
        if (error != null && !(error instanceof BlockException)) {
            node.increaseExceptionQps(batchCount);
        }
    }
}
