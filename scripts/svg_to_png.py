#!/usr/bin/env python3
"""Rasterize SVG icon sources into ignored Tako GUI texture assets.

Usage:
  py -3 -m pip install -r tools/requirements-svg.txt
  py -3 scripts/svg_to_png.py
  py -3 scripts/svg_to_png.py path/to/icon.svg --size 32
"""

from __future__ import annotations

import argparse
from pathlib import Path
import sys


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_SOURCE = ROOT / "assets" / "icons" / "svg"
DEFAULT_OUTPUT = (
    ROOT
    / "src"
    / "main"
    / "resources"
    / "assets"
    / "tako-libs"
    / "textures"
    / "gui"
    / "icons-generated"
)


def positive_size(value: str) -> int:
    size = int(value)
    if size <= 0:
        raise argparse.ArgumentTypeError("size must be greater than zero")
    return size


def main() -> int:
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument("source", nargs="?", type=Path, default=DEFAULT_SOURCE)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--size", type=positive_size, default=32, help="square output size in pixels")
    args = parser.parse_args()

    source = args.source.resolve()
    output = args.output.resolve()
    if not source.exists():
        parser.error(f"SVG source does not exist: {source}")

    if source.is_file():
        if source.suffix.lower() != ".svg":
            parser.error(f"input file must be SVG: {source}")
        files = [source]
        source_root = source.parent
    else:
        files = sorted(source.rglob("*.svg"), key=lambda path: path.relative_to(source).as_posix().lower())
        source_root = source
    if not files:
        parser.error(f"no SVG files found under: {source}")

    try:
        import cairosvg
    except ImportError:
        print(
            "CairoSVG is required. Install it with: "
            "py -3 -m pip install -r tools/requirements-svg.txt",
            file=sys.stderr,
        )
        return 1

    for svg in files:
        relative_path = svg.relative_to(source_root).with_suffix(".png")
        png = output / relative_path
        png.parent.mkdir(parents=True, exist_ok=True)
        cairosvg.svg2png(
            url=str(svg),
            write_to=str(png),
            output_width=args.size,
            output_height=args.size,
        )
        print(f"{svg} -> {png}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
