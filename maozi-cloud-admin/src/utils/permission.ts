import {type PermissionItem, type PermissionTreeNode, PermissionType} from '@/types/api'

/** 判断 id 是否为根（无父节点） */
function isRootId(id: string | number): boolean {
  return id === 0 || id === '0' || id === '' || id === null || id === undefined
}

/** 可构建树的最小节点结构（至少含 id、parentId，可选 sort） */
export interface TreeNodeBase {
  id: string | number
  parentId: string | number
  sort?: number
}

/** 通用树节点：在原数据基础上追加可选 children */
export type Tree<T> = T & { children?: Tree<T>[] }

/**
 * 将扁平权限列表构建为树。
 * @param list 全量权限列表
 * @param keep 过滤函数，仅保留返回 true 的节点
 */
export function buildPermissionTree(
  list: PermissionItem[],
  keep: (item: PermissionItem) => boolean
): PermissionTreeNode[] {
  // 1) 过滤
  const filtered = list.filter(keep)
  const nodeMap = new Map<string | number, PermissionTreeNode>()
  filtered.forEach((item) => {
    nodeMap.set(item.id, { ...item, children: [] })
  })

  const roots: PermissionTreeNode[] = []
  // 2) 组装父子关系
  filtered.forEach((item) => {
    const node = nodeMap.get(item.id)!
    if (isRootId(item.parentId) || !nodeMap.has(item.parentId)) {
      // 父节点不存在（或为根）→ 作为根节点
      roots.push(node)
    } else {
      const parent = nodeMap.get(item.parentId)!
      parent.children!.push(node)
    }
  })

  // 3) 剪掉没有子节点的空目录（type=0 且 children 为空），并按 sort 升序排序
  const prune = (nodes: PermissionTreeNode[]): PermissionTreeNode[] => {
    return nodes
      .filter((n) => {
        if (n.type === PermissionType.DIRECTORY) {
          n.children = prune(n.children || [])
          return n.children.length > 0
        }
        return true
      })
      .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  }
  return prune(roots)
}

/**
 * 将扁平列表构建为完整树（不过滤、不剪枝），用于权限管理树表与角色权限选择。
 * 各层级按 sort 升序排序。泛型 T 只要求包含 id、parentId（sort 可选），
 * 因此可同时承接全量权限（PermissionItem）与下拉权限（PermissionOptionItem）。
 */
export function buildFullPermissionTree<T extends TreeNodeBase>(
  list: T[]
): Tree<T>[] {
  const nodeMap = new Map<string | number, Tree<T>>()
  list.forEach((item) => {
    nodeMap.set(item.id, { ...item, children: [] })
  })
  const roots: Tree<T>[] = []
  list.forEach((item) => {
    const node = nodeMap.get(item.id)!
    if (isRootId(item.parentId) || !nodeMap.has(item.parentId)) {
      roots.push(node)
    } else {
      nodeMap.get(item.parentId)!.children!.push(node)
    }
  })
  const sortNodes = (nodes: Tree<T>[]): Tree<T>[] => {
    nodes.forEach((n) => {
      if (n.children?.length) {
        n.children = sortNodes(n.children)
      }
    })
    return nodes.sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
  }
  return sortNodes(roots)
}

/** 计算每个节点相对于根的深度（根为 0），返回 id -> level 映射 */
export function buildPermissionLevelMap(
  list: PermissionItem[]
): Map<string | number, number> {
  const levelMap = new Map<string | number, number>()
  const childMap = new Map<string | number, PermissionItem[]>()
  const roots: PermissionItem[] = []
  list.forEach((item) => {
    if (isRootId(item.parentId) || !list.some((x) => x.id === item.parentId)) {
      roots.push(item)
    } else {
      const arr = childMap.get(item.parentId) || []
      arr.push(item)
      childMap.set(item.parentId, arr)
    }
  })
  const walk = (node: PermissionItem, level: number) => {
    levelMap.set(node.id, level)
    ;(childMap.get(node.id) || []).forEach((c) => walk(c, level + 1))
  }
  roots.forEach((r) => walk(r, 0))
  return levelMap
}

/**
 * 由 mark 推导前端路由路径，如 system:user -> /system/user
 */
export function markToPath(mark: string): string {
  return '/' + mark.replace(/:/g, '/')
}
