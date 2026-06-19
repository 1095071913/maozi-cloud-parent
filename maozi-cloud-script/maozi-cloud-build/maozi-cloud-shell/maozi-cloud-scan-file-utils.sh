# ============================================================
# 本文件用于被 source 执行, 对应 maozi-cloud-bat/maozi-cloud-scan-file-utils.bat
# 请勿直接运行, 也不要使用 exit (否则会退出调用方 shell)。
# ------------------------------------------------------------
# 作用: 基于分支与提交增量判断需要构建的范围
#   通过脚本目录下的 {工程名}-env 文件夹记录上次构建状态:
#     CURRENT_BRANCH  上次构建时的分支
#     CURRENT_SHA     上次构建时的提交 SHA
#   并据此输出两种结果之一给 jar-utils:
#     1. 全量构建 -> 直接设置 buildFiles 为 "." 或模块名
#     2. 增量构建 -> 把变更文件路径写入 array, 由 jar-utils 归并
# ------------------------------------------------------------
# 三种判定:
#   A. 无 CURRENT_BRANCH 记录: 首次构建, 全量
#   B. 分支与记录不一致:       切换了分支, 全量
#   C. 分支一致但 SHA 变化:    同分支有新提交, 增量
# ------------------------------------------------------------
# 依赖外部变量: current_directory  (由调用方 jar-utils 传入的脚本目录)
# 修改外部变量: buildFiles, array_index, array[], current_build_directory,
#               current_env_build_directory, current_branch
# ============================================================

# 取当前源码工程目录名, 如 maozi-cloud-services
current_build_directory="$(basename "$(pwd)")"
# env 状态目录名: {工程名}-env, 存放分支与 SHA 记录
current_env_build_directory="${current_build_directory}-env"

# 读取当前 git 分支
current_branch="$(git branch --show-current)"

# 确保状态目录存在 (mkdir -p 等价于 bat 的 if not exist + mkdir)
env_directory="$current_directory/$current_env_build_directory"
mkdir -p "$env_directory"

branch_file="$env_directory/CURRENT_BRANCH"
sha_file="$env_directory/CURRENT_SHA"

if [ -f "$branch_file" ]; then

    # 读取上次记录的分支并去除空白 (tr -d '[:space:]' 等价 bat 的 !var: =!)
    file_branch="$(tr -d '[:space:]' < "$branch_file")"

    if [ "$current_branch" != "$file_branch" ]; then

        # ---- B. 分支不一致: 切换了分支, 全量构建 ----
        echo "$current_branch" > "$branch_file"

        git rev-parse HEAD > "$sha_file"

        # 上级有 pom.xml 说明本目录是聚合下的子模块, 构建该子模块; 否则全量 reactor
        if [ -f "../pom.xml" ]; then
            buildFiles="${buildFiles},${current_build_directory}"
        else
            buildFiles="."
        fi

    else

        # ---- C. 分支一致: 比较 SHA 判断是否有新提交 ----
        current_sha="$(git rev-parse HEAD)"

        file_sha="$(tr -d '[:space:]' < "$sha_file")"

        if [ "$current_sha" != "$file_sha" ]; then

            # 有新提交: 更新 SHA, 用 git diff 列出变更文件写入 array
            echo "$current_sha" > "$sha_file"

            while IFS= read -r line; do
                [ -z "$line" ] && continue
                array_index=$((array_index + 1))

                # 子模块追加模块名前缀; 仓库根则直接用相对路径
                if [ -f "../pom.xml" ]; then
                    array[$array_index]="${current_build_directory}/${line}"
                else
                    array[$array_index]="$line"
                fi
            done < <(git diff --name-only "$file_sha..$current_sha")

        fi

    fi

else

    # ---- A. 无记录: 首次构建, 全量 ----
    echo "$current_branch" > "$branch_file"

    git rev-parse HEAD > "$sha_file"

    if [ -f "../pom.xml" ]; then
        buildFiles="${buildFiles},${current_build_directory}"
    else
        buildFiles="."
    fi

fi
