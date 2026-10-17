#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
dir="$root/assets/fonts"
mkdir -p "$dir"
curl -fLsS "https://raw.githubusercontent.com/google/fonts/main/ofl/dmsans/DMSans%5Bopsz,wght%5D.ttf" -o "$dir/DMSans.ttf"
curl -fLsS "https://raw.githubusercontent.com/google/fonts/main/ofl/cormorantgaramond/CormorantGaramond%5Bwght%5D.ttf" -o "$dir/CormorantGaramond.ttf"
curl -fLsS "https://raw.githubusercontent.com/google/fonts/main/ofl/dmsans/OFL.txt" -o "$dir/OFL-DMSans.txt"
curl -fLsS "https://raw.githubusercontent.com/google/fonts/main/ofl/cormorantgaramond/OFL.txt" -o "$dir/OFL-Cormorant.txt"
flutter_root="$(cd "$(dirname "$(command -v flutter)")/.." && pwd)"
cp "$flutter_root/bin/cache/artifacts/material_fonts/MaterialIcons-Regular.otf" "$dir/MaterialIcons-Regular.otf"
echo "PASS: fonts and licenses ready"
