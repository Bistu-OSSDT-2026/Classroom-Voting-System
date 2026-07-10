#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

# 自动查找 Java
find_java() {
  # 1) JAVA_HOME
  if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    echo "$JAVA_HOME/bin/java"
    return
  fi
  # 2) PATH
  if command -v java &>/dev/null; then
    echo "java"
    return
  fi
  # 3) 常见路径
  for dir in /usr/lib/jvm/java-21-* /usr/lib/jvm/jdk-21* /usr/local/opt/openjdk@21/bin; do
    if [ -x "$dir/bin/java" ]; then
      echo "$dir/bin/java"
      return
    fi
  done
  echo ""
}

JAVA_CMD=$(find_java)

if [ -z "$JAVA_CMD" ]; then
  echo "❌ 未找到 Java，请先安装 JDK 21"
  echo "下载: https://adoptium.net/download/"
  exit 1
fi

echo "✅ Java found: $JAVA_CMD"
"$JAVA_CMD" -version 2>&1 | head -1

# 首次构建
if [ ! -f "target/cvs-app.jar" ]; then
  echo "🔨 首次运行，正在构建项目..."
  chmod +x mvnw 2>/dev/null || true
  ./mvnw clean package -DskipTests -q
  echo "✅ 构建完成！"
fi

# 启动
echo ""
echo "╔══════════════════════════════════════╗"
echo "║  课堂投票系统启动！                  ║"
echo "║  访问: http://localhost:8080          ║"
echo "║  教师: teacher1 / 123456             ║"
echo "║  学生: student1 / 123456             ║"
echo "╚══════════════════════════════════════╝"
echo ""

"$JAVA_CMD" -jar target/cvs-app.jar
