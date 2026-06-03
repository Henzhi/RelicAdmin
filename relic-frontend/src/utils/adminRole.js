/** 与后端 AdminUserVO.roleIds / roleNames 对齐 */

export function parseRoleIdList(row) {
  if (!row?.roleIds) return []
  return String(row.roleIds)
    .split(',')
    .map((s) => Number(s.trim()))
    .filter((n) => !Number.isNaN(n))
}

export function roleType(roleId) {
  switch (roleId) {
    case 1: return 'danger'
    case 4: return 'success'
    case 5: return 'warning'
    default: return 'info'
  }
}

export function roleLabel(roleId) {
  switch (roleId) {
    case 1: return '超级审核员'
    case 4: return '内容审核员'
    case 5: return '数据管理员'
    default: return `角色#${roleId}`
  }
}

/** 与角色管理 /role/list 返回项对齐，按 id 查找 */
export function findRoleById(roleId, roles = []) {
  if (roleId == null || roleId === '') return null
  const id = Number(roleId)
  if (Number.isNaN(id)) return null
  return roles.find((r) => r.id === id) || null
}

/** 用角色管理同源数据解析显示名称（displayName） */
export function roleTagFromList(roleId, roles = []) {
  if (roleId == null || roleId === '') return null
  const role = findRoleById(roleId, roles)
  if (role) {
    return {
      label: role.displayName || role.name,
      type: roleType(role.id)
    }
  }
  return {
    label: roleLabel(roleId),
    type: roleType(roleId)
  }
}

/** 个人中心等单 roleId 场景 */
export function buildProfileRoleTags(roleId, roles = []) {
  const tag = roleTagFromList(roleId, roles)
  return tag ? [tag] : []
}

/** @returns {{ label: string, type: string }[]} */
export function buildRoleTags(row) {
  if (row?.roleNames) {
    const names = String(row.roleNames).split('、').filter(Boolean)
    const ids = parseRoleIdList(row)
    return names.map((label, index) => ({
      label,
      type: roleType(ids[index])
    }))
  }
  return parseRoleIdList(row).map((id) => ({
    label: roleLabel(id),
    type: roleType(id)
  }))
}
