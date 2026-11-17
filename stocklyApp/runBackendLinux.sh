#!/usr/bin/env sh

set -u

android_project=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd) || {
    echo "No se pudo localizar la carpeta del proyecto Android." >&2
    exit 1
}
repo_root=$(CDPATH= cd -- "$android_project/.." && pwd) || {
    echo "No se pudo localizar la carpeta raiz del proyecto." >&2
    exit 1
}
backend_dir="$repo_root/StocklyBackend"
gradle_wrapper="$android_project/gradlew"
maven_wrapper="$backend_dir/mvnw"

if [ ! -f "$gradle_wrapper" ]; then
    echo "No se encontro gradlew en: $gradle_wrapper" >&2
    exit 1
fi

if [ ! -f "$maven_wrapper" ]; then
    echo "No se encontro mvnw en: $maven_wrapper" >&2
    exit 1
fi

if ! sh "$gradle_wrapper" -p "$android_project" :app:reverseBackendPort; then
    echo "No se pudo configurar adb reverse. Verifica que el dispositivo este conectado y autorizado." >&2
    exit 1
fi

cd "$backend_dir" || {
    echo "No se pudo acceder a la carpeta del backend: $backend_dir" >&2
    exit 1
}

exec sh "$maven_wrapper" spring-boot:run
