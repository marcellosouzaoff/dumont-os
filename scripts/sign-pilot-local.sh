#!/usr/bin/env bash
# Sign DUMONT Pilot locally with a stable development key; NEVER commit key material.
set -euo pipefail
umask 077
if (( $# != 2 )); then
    echo "Usage: $0 <ci-debug.apk> <output-signed.apk>" >&2
    exit 2
fi
input="$1"
output="$2"
keystore="$HOME/.dumont-private-signing/dumont-debug-signing.p12"
password_file="$HOME/.dumont-private-signing/password.txt"
certificate="$HOME/.dumont-private-signing/public-cert.pem"
test -s "$input" && test -s "$keystore" && test -s "$password_file" && test -s "$certificate"
command -v apksigner >/dev/null
command -v openssl >/dev/null
input_abs="$(realpath "$input")"
output_abs="$(realpath -m "$output")"
if [[ "$input_abs" == "$output_abs" ]]; then
    echo "Refusing to overwrite source" >&2
    exit 3
fi
mkdir -p "$(dirname "$output_abs")"
tmp="$output_abs.partial-$$"
trap 'rm -f "$tmp" "$tmp.verify"' EXIT
apksigner sign \
    --ks "$keystore" \
    --ks-type PKCS12 \
    --ks-key-alias dumont-ci-debug \
    --ks-pass "file:$password_file" \
    --key-pass "file:$password_file" \
    --out "$tmp" "$input_abs"
apksigner verify --verbose --print-certs "$tmp" > "$tmp.verify"
expected="$(openssl x509 -in "$certificate" -outform DER | sha256sum | cut -d' ' -f1)"
actual="$(sed -n 's/^Signer #1 certificate SHA-256 digest: //p' "$tmp.verify" | tr 'A-F' 'a-f' | head -n1)"
if [[ -z "$actual" || "$actual" != "$expected" ]]; then
    echo "Certificate fingerprint mismatch; output discarded" >&2
    exit 4
fi
mv "$tmp" "$output_abs"
echo "PASS: stable development signature verified"
echo "Public cert SHA-256: $actual"
sha256sum "$output_abs"
